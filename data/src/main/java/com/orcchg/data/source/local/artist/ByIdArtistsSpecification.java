package com.orcchg.data.source.local.artist;

import com.orcchg.data.entity.ArtistEntity;

/**
 * Specification allows to query {@link ArtistEntity} items
 * from the storage by {@link ArtistEntity#id} value.
 */
class ByIdArtistsSpecification implements ArtistsSpecification {
    private final long id;

    ByIdArtistsSpecification(long id) {
        this.id = id;
    }

    @Override
    public String getSelectionArgs() {
        return ArtistDatabaseContract.ArtistsTable.COLUMN_NAME_ID + " = " + Long.toString(this.id);
    }
}
