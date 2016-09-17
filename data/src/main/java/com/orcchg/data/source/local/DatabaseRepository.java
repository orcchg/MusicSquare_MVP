package com.orcchg.data.source.local;

import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;

import java.util.List;

/**
 * Common operations with the storage of {@link ArtistEntity} items.
 */
public interface DatabaseRepository {

    void addArtists(List<ArtistEntity> musicians);
    void updateArtists(List<ArtistEntity> musicians);
    void removeArtists(ArtistsSpecification specification);

    List<ArtistEntity> queryArtists(ArtistsSpecification specification);
    List<SmallArtistEntity> querySmallArtists(ArtistsSpecification specification);
}
