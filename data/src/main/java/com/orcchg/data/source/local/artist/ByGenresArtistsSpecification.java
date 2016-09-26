package com.orcchg.data.source.local.artist;

import java.util.List;

class ByGenresArtistsSpecification implements ArtistsSpecification {
    private final List<String> genres;

    ByGenresArtistsSpecification(List<String> genres) {
        this.genres = genres;
    }

    @Override
    public String getSelectionArgs() {
        String delimiter = "";
        StringBuilder builder = new StringBuilder();
        for (String genre : this.genres) {
            builder.append(delimiter)
                    .append(ArtistDatabaseContract.ArtistsTable.COLUMN_NAME_GENRES)
                    .append(" LIKE '%").append(genre).append("%' ");
            delimiter = " OR ";
        }
        return builder.toString();
    }
}
