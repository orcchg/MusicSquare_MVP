package com.orcchg.data.source.repository.artist;

import android.support.annotation.Nullable;

import com.domain.model.Artist;
import com.domain.model.TotalValue;
import com.domain.repository.IArtistRepository;
import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;
import com.orcchg.data.entity.TotalValueEntity;
import com.orcchg.data.entity.mapper.ArtistMapper;
import com.orcchg.data.entity.mapper.SmallArtistMapper;
import com.orcchg.data.entity.mapper.TotalValueMapper;
import com.orcchg.data.source.local.artist.ArtistLocalSource;
import com.orcchg.data.source.remote.artist.ArtistDataSource;
import com.orcchg.data.source.repository.RepoUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import hugo.weaving.DebugLog;
import timber.log.Timber;

@Singleton
public class ServerArtistRepositoryImpl implements IArtistRepository {

    private final ArtistDataSource cloudSource;
    private final ArtistLocalSource localSource;
    private final SmallArtistMapper smallArtistMapper;
    private final ArtistMapper artistMapper;
    private final TotalValueMapper totalValueMapper;

    private @RepoUtils.SourceType int source;
    private boolean sequentialItems;

    @Inject
    ServerArtistRepositoryImpl(@Named("serverCloud") ArtistDataSource cloudSource, ArtistLocalSource localSource,
                               SmallArtistMapper smallArtistMapper, ArtistMapper artistMapper,
                               TotalValueMapper totalValueMapper) {
        this.cloudSource = cloudSource;
        this.localSource = localSource;
        this.smallArtistMapper = smallArtistMapper;
        this.artistMapper = artistMapper;
        this.totalValueMapper = totalValueMapper;
    }

    @DebugLog @Override
    public List<Artist> artists() {
        return artists(-1, 0);
    }

    @DebugLog @Override
    public List<Artist> artists(int limit, int offset) {
        return artists(limit, offset, null);
    }

    @DebugLog @Override
    public List<Artist> artists(@Nullable List<String> genres) {
        return artists(-1, 0, genres);
    }

    @DebugLog @Override
    public List<Artist> artists(int limit, int offset, @Nullable List<String> genres) {
        return processListOfEntities(getArtists(limit, offset, genres));
    }

    @DebugLog @Override
    public Artist artist(long artistId) {
        ArtistEntity artistEntity = getDataSource(artistId).artist(artistId);
        if (source == RepoUtils.SOURCE_REMOTE &&
            (checkCacheStaled() || !localSource.hasArtist(artistId))) {
            List<ArtistEntity> artistEntities = new ArrayList<>();
            artistEntities.add(artistEntity);
            localSource.updateArtists(artistEntities);
        }
        return artistMapper.map(artistEntity);
    }

    @DebugLog @Override
    public boolean clear() {
        localSource.clear();
        return true;
    }

    @DebugLog @Override
    public TotalValue total() {
        return total(null);
    }

    @DebugLog @Override
    public TotalValue total(@Nullable List<String> genres) {
        // total items count is always fetched from remote cloud to be actual
        TotalValueEntity totalValueEntity = cloudSource.total(genres);
        return totalValueMapper.map(totalValueEntity);
    }

    /* Internal */
    // --------------------------------------------------------------------------------------------
    @DebugLog
    private boolean checkCacheStaled() {
        return localSource.isEmpty() || localSource.isExpired();
    }

    @DebugLog
    private ArtistDataSource getDataSource() {
        return getDataSource(-1);
    }

    @DebugLog
    private ArtistDataSource getDataSource(long artistId) {
        if (checkCacheStaled() || !localSource.hasArtist(artistId)) {
            source = RepoUtils.SOURCE_REMOTE;
            return cloudSource;
        } else {
            source = RepoUtils.SOURCE_LOCAL;
            return localSource;
        }
    }

    @DebugLog
    private List<SmallArtistEntity> getArtists(int limit, int offset, @Nullable List<String> genres) {
        /**
         * If there are genres provided, then always fetch models selected by genres from the Cloud,
         * because we rely on the fact that Cache and Cloud are synchronized and contain absolutely
         * the same sequences of items (with absolutely the same ordering), otherwise paging will
         * malfunction, because it is based on total items count in Cache which is not relevant to
         * the total items sharing the same genre. Moreover, repository implementation tends to
         * update Cache with new data available from Cloud, so when Cloud is asked for items sharing
         * any particular genre or set of genres, it will give items in a random ordering, but then
         * repository will try to store them in Cache sequentially. When a new page of items will
         * then be asked for, items will be fetched partially from Cache and from Cloud leading some weird
         * gaps and possible duplicates could occur in the final set of items.
         *
         * Thus, neither selecting items by genres from Cache, nor storing them in Cache when received
         * from Cloud in a random ordering. The ordering in Cache must always coincide with ordering in
         * Cloud, otherwise Cache should be considered 'expired' and to be then cleaned and re-populated.
         */
        if (genres != null && !genres.isEmpty()) {
            Timber.v("Fetch items by genres from cloud");
            sequentialItems = false;  // items selected by genres have a random ordering
            return cloudSource.artists(limit, offset, genres);
        }

        sequentialItems = true;  // items have a direct ordering

        int total = localSource.totalItems();
        int available = total - offset;
        int needed = limit + offset - total;
        Timber.v("Total %s, Available %s, Needed %s", total, available, needed);

        // case 0: total is enough - get all items from local cache.
        if (needed <= 0) {
            Timber.d("Case 1: get all from local cache");
            source = RepoUtils.SOURCE_LOCAL;
            return localSource.artists(limit, offset, genres);
        }

        // case 2: total is less - get all items as requested from remote cloud.
        if (available <= 0) {
            limit -= available;
            offset += available;
            Timber.d("Case 2: get all from cloud, limit = %s, offset = %s", limit, offset);
            source = RepoUtils.SOURCE_REMOTE;
            return cloudSource.artists(limit, offset, genres);
        }

        // case 3: get available items from local cache and the rest from remote cloud,
        // adjusting limit and offset in order to make them aligned with the initial request.
        List<SmallArtistEntity> local = new ArrayList<>();
        if (available > 0 && needed > 0) {
            source = RepoUtils.SOURCE_REMOTE;  // force update local cache
            int newOffset = offset + available;
            Timber.d("Case 3: get (%s, %s) from local cache and (%s, %s) from cloud", available, offset, needed, newOffset);
            local = localSource.artists(available, offset, genres);
            List<SmallArtistEntity> remote = cloudSource.artists(needed, newOffset, genres);
            local.addAll(remote);
        }
        return local;
    }

    private List<Artist> processListOfEntities(List<SmallArtistEntity> data) {
        if (source == RepoUtils.SOURCE_REMOTE && sequentialItems) {
            localSource.updateSmallArtists(data);
        }
        List<Artist> artists = new ArrayList<>();
        for (SmallArtistEntity entity : data) {
            artists.add(smallArtistMapper.map(entity));
        }
        return artists;
    }
}
