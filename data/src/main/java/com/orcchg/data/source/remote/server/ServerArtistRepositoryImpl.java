package com.orcchg.data.source.remote.server;

import com.domain.model.Artist;
import com.domain.repository.ArtistRepository;
import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;
import com.orcchg.data.entity.mapper.ArtistMapper;
import com.orcchg.data.entity.mapper.SmallArtistMapper;
import com.orcchg.data.source.DataSource;
import com.orcchg.data.source.local.LocalSource;

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
        List<SmallArtistEntity> data = /*getDataSource()*/this.cloudSource.artists();
//        fillLocalStorageIfNeed(data);
        for (SmallArtistEntity entity : data) {
            artists.add(this.smallArtistMapper.map(entity));
        }
        return artists;
    }

    @Override
    public Artist artist(long artistId) {
        return this.artistMapper.map(/*getDataSource()*/this.cloudSource.artist(artistId));
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

    private void fillLocalStorageIfNeed(List<ArtistEntity> data) {
        if (this.checkCacheStaled()) {
            this.localSource.updateArtists(data);
        }
    }
}
