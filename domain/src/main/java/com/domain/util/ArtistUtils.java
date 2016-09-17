package com.domain.util;

import com.domain.model.Artist;

public class ArtistUtils {
    public static final long BAD_ARTIST_ID = -1;

    /**
     * Calculates a grade of a given {@link Artist} as a ratio between
     * {@link Artist#tracksCount} and {@link Artist#albumsCount}.
     * The more tracks and less albums a certain musician has the better
     * grade it has been given.
     *
     * @param artist Input model
     * @return grade of musician
     */
    public static int calculateGrade(Artist artist) {
        float ratio = artist.getTracksCount() / (float) artist.getAlbumsCount();
        if (ratio > 10.0f) {
            return 10;
        }
        return (int) ratio;
    }
}
