package com.orcchg.data.source.local.artist;

/**
 * Filtering interface to query certain items from the storage.
 */
interface ArtistsSpecification {
    String getSelectionArgs();
}
