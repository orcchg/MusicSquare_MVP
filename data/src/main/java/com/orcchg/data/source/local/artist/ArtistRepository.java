package com.orcchg.data.source.local.artist;

import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;

import java.util.List;

/**
 * Common operations with the storage of {@link ArtistEntity} items.
 */
interface ArtistRepository {

    boolean hasArtist(long artistId);
    void addArtist(ArtistEntity artist);
    void addArtists(List<ArtistEntity> artists);
    void updateArtists(List<ArtistEntity> artists);
    void removeArtists(ArtistSpecification specification);
    List<ArtistEntity> queryArtists(ArtistSpecification specification);

    boolean hasSmallArtist(long artistId);
    void addSmallArtist(SmallArtistEntity artist);
    void addSmallArtists(List<SmallArtistEntity> artists);
    void updateSmallArtists(List<SmallArtistEntity> artists);
    void removeSmallArtists(ArtistSpecification specification);
    List<SmallArtistEntity> querySmallArtists(ArtistSpecification specification);
}
