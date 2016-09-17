package com.orcchg.data.source.local;

import android.support.annotation.NonNull;

import com.orcchg.data.entity.ArtistEntity;

/**
 * Filtering interface to query certain items from the storage.
 */
public interface ArtistsSpecification {

    boolean specified(@NonNull ArtistEntity artist);

    String getSelectionArgs();
}
