package com.orcchg.data.source.local.artist;

import android.support.annotation.NonNull;

import com.orcchg.data.entity.ArtistEntity;

/**
 * Specification allows to query {@link com.orcchg.musicsquare.data.model.Musician} items
 * from the storage by {@link com.orcchg.musicsquare.data.model.Musician#mId} value.
 */
public class ByIdArtistsSpecification implements ArtistsSpecification {
    private final long id;

    public ByIdArtistsSpecification(long id) {
        this.id = id;
    }

    @Override
    public boolean specified(@NonNull ArtistEntity artist) {
        return artist.getId() == this.id;
    }

    @Override
    public String getSelectionArgs() {
        return DatabaseContract.ArtistsTable.COLUMN_NAME_ID + " = " + Long.toString(this.id);
    }
}
