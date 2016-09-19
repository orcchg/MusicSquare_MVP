package com.orcchg.data.source.repository.artist;

import com.domain.model.Artist;
import com.domain.repository.IArtistRepository;
import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;
import com.orcchg.data.entity.mapper.ArtistMapper;
import com.orcchg.data.entity.mapper.SmallArtistMapper;
import com.orcchg.data.source.local.artist.ArtistLocalSource;
import com.orcchg.data.source.remote.artist.ArtistDataSource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class ServerArtistRepositoryImpl implements IArtistRepository {

    private final ArtistDataSource cloudSource;
    private final ArtistLocalSource localSource;
    private final SmallArtistMapper smallArtistMapper;
    private final ArtistMapper artistMapper;

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
        List<Artist> artists = new ArrayList<>();
        List<SmallArtistEntity> data = this.cloudSource.artists();//this.getDataSource().artists();
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
        ArtistEntity artistEntity = this.cloudSource.artist(artistId);//this.getDataSource(artistId).artist(artistId);
//        if (this.checkCacheStaled() || !this.localSource.hasArtist(artistId)) {
//            List<ArtistEntity> artistEntities = new ArrayList<>();
//            artistEntities.add(artistEntity);
//            this.localSource.updateArtists(artistEntities);
//        }
        return this.artistMapper.map(artistEntity);
    }

    @Override
    public boolean clear() {
//        this.localSource.clear();
        return true;
    }

    private boolean checkCacheStaled() {
        return this.localSource.isEmpty() || this.localSource.isExpired();
    }

    private ArtistDataSource getDataSource() {
        return this.checkCacheStaled() ? this.cloudSource : this.localSource;
    }

    private ArtistDataSource getDataSource(long artistId) {
        return this.checkCacheStaled() ||
              !this.localSource.hasArtist(artistId) ? this.cloudSource : this.localSource;
    }
}
