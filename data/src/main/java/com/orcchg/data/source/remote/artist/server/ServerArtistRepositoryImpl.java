package com.orcchg.data.source.remote.artist.server;

import com.domain.model.Artist;
import com.domain.repository.ArtistRepository;
import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;
import com.orcchg.data.entity.mapper.ArtistMapper;
import com.orcchg.data.entity.mapper.SmallArtistMapper;
import com.orcchg.data.source.local.artist.LocalSource;
import com.orcchg.data.source.remote.artist.DataSource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class ServerArtistRepositoryImpl implements ArtistRepository {

    protected final DataSource cloudSource;
    protected final LocalSource localSource;
    protected final SmallArtistMapper smallArtistMapper;
    protected final ArtistMapper artistMapper;

    @Inject
    public ServerArtistRepositoryImpl(@Named("serverCloud") DataSource cloudSource, LocalSource localSource,
                                      SmallArtistMapper smallArtistMapper, ArtistMapper artistMapper) {
        this.cloudSource = cloudSource;
        this.localSource = localSource;
        this.smallArtistMapper = smallArtistMapper;
        this.artistMapper = artistMapper;
    }

    @Override
    public List<Artist> artists() {
        List<Artist> artists = new ArrayList<>();
        List<SmallArtistEntity> data = this./*getDataSource()*/cloudSource.artists();
//        if (this.checkCacheStaled()) {
//            this.localSource.updateSmallArtists(data);
//        }
        for (SmallArtistEntity entity : data) {
            artists.add(this.smallArtistMapper.map(entity));
        }
        return artists;
    }

    @Override
    public Artist artist(long artistId) {
        ArtistEntity artistEntity = this./*getDataSource(artistId)*/cloudSource.artist(artistId);
//        if (this.checkCacheStaled() || !this.localSource.hasArtist(artistId)) {
//            List<ArtistEntity> artistEntities = new ArrayList<>();
//            artistEntities.add(artistEntity);
//            this.localSource.updateArtists(artistEntities);
//        }
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

    private DataSource getDataSource() {
        return this.checkCacheStaled() ? this.cloudSource : this.localSource;
    }

    private DataSource getDataSource(long artistId) {
        return this.checkCacheStaled() ||
              !this.localSource.hasArtist(artistId) ? this.cloudSource : this.localSource;
    }
}
