package com.orcchg.data.source.local.genre;

class ByNameGenreSpecification implements GenreSpecification {
    private final String name;

    ByNameGenreSpecification(String name) {
        this.name = name;
    }

    @Override
    public String getSelectionArgs() {
        return GenreDatabaseContract.GenresTable.COLUMN_NAME_NAME + " LIKE " + name;
    }
}
