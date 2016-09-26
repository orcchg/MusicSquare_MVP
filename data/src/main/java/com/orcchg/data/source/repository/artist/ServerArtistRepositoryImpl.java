package com.orcchg.data.source.repository.artist;

import android.support.annotation.IntDef;

import com.domain.model.Artist;
import com.domain.repository.IArtistRepository;
import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;
import com.orcchg.data.entity.mapper.ArtistMapper;
import com.orcchg.data.entity.mapper.SmallArtistMapper;
import com.orcchg.data.source.local.artist.ArtistLocalSource;
import com.orcchg.data.source.remote.artist.ArtistDataSource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class ServerArtistRepositoryImpl implements IArtistRepository {
    private static final int SOURCE_REMOTE = 0;
    private static final int SOURCE_LOCAL = 1;
    @IntDef({SOURCE_REMOTE, SOURCE_LOCAL})
    @Retention(RetentionPolicy.SOURCE)
    private @interface SourceType {}

    private final ArtistDataSource cloudSource;
    private final ArtistLocalSource localSource;
    private final SmallArtistMapper smallArtistMapper;
    private final ArtistMapper artistMapper;
    private @SourceType int source;

    @Inject
    ServerArtistRepositoryImpl(@Named("serverCloud") ArtistDataSource cloudSource, ArtistLocalSource localSource,
                               SmallArtistMapper smallArtistMapper, ArtistMapper artistMapper) {
        this.cloudSource = cloudSource;
        this.localSource = localSource;
        this.smallArtistMapper = smallArtistMapper;
        this.artistMapper = artistMapper;
    }

    @Override
    public List<Artist> artists() {
        return artists(-1, 0);
    }

    @Override
    public List<Artist> artists(int limit, int offset) {
        return artists(limit, offset, null);
    }

    @Override
    public List<Artist> artists(List<String> genres) {
        return artists(-1, 0, genres);
    }

    @Override
    public List<Artist> artists(int limit, int offset, List<String> genres) {
        return processListOfEntities(this.getDataSource().artists(limit, offset, genres));
    }

    @Override
    public Artist artist(long artistId) {
        ArtistEntity artistEntity = this.getDataSource(artistId).artist(artistId);
        if (this.source == SOURCE_REMOTE &&
            (this.checkCacheStaled() || !this.localSource.hasArtist(artistId))) {
            List<ArtistEntity> artistEntities = new ArrayList<>();
            artistEntities.add(artistEntity);
            this.localSource.updateArtists(artistEntities);
        }
        return this.artistMapper.map(artistEntity);
    }

    @Override
    public boolean clear() {
        this.localSource.clear();
        return true;
    }

    private boolean checkCacheStaled() {
        return this.localSource.isEmpty() || this.localSource.isExpired();
    }

    private ArtistDataSource getDataSource() {
        return getDataSource(-1);
    }

    private ArtistDataSource getDataSource(long artistId) {
        if (this.checkCacheStaled() || (artistId >= 0 && !this.localSource.hasArtist(artistId))) {
            this.source = SOURCE_REMOTE;
            return this.cloudSource;
        } else {
            this.source = SOURCE_LOCAL;
            return this.localSource;
        }
    }

    private List<Artist> processListOfEntities(List<SmallArtistEntity> data) {
        if (this.source == SOURCE_REMOTE && this.checkCacheStaled()) {
            this.localSource.updateSmallArtists(data);
        }
        List<Artist> artists = new ArrayList<>();
        for (SmallArtistEntity entity : data) {
            artists.add(this.smallArtistMapper.map(entity));
        }
        return artists;
    }
}
