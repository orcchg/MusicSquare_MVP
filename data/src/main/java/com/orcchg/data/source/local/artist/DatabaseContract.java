package com.orcchg.data.source.local.artist;

/**
 * Represents the schema in the {@link DatabaseSourceImpl}.
 */
class DatabaseContract {

    DatabaseContract() {
        // protect from accidental instantiation
    }

    static class ArtistsTable {
        static final String TABLE_NAME = "Artists";
        static final String TABLE_SMALL_NAME = "ArtistsSmall";
        static final String COLUMN_NAME_ID = "id";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_GENRES = "genres";
        static final String COLUMN_NAME_TRACKS_COUNT = "tracks_count";
        static final String COLUMN_NAME_ALBUMS_COUNT = "albums_count";
        static final String COLUMN_NAME_LINK = "link";
        static final String COLUMN_NAME_DESCRIPTION = "description";
        static final String COLUMN_NAME_COVER_LARGE = "cover_big";
        static final String COLUMN_NAME_COVER_SMALL = "cover_small";
    }

    static final String CREATE_TABLE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS " + ArtistsTable.TABLE_NAME + " (" +
            ArtistsTable.COLUMN_NAME_ID + " INTEGER UNIQUE, " +
            ArtistsTable.COLUMN_NAME_NAME + " TEXT DEFAULT \"\", " +
            ArtistsTable.COLUMN_NAME_GENRES + " TEXT DEFAULT \"\", " +
            ArtistsTable.COLUMN_NAME_TRACKS_COUNT + " INTEGER DEFAULT 0, " +
            ArtistsTable.COLUMN_NAME_ALBUMS_COUNT + " INTEGER DEFAULT 0, " +
            ArtistsTable.COLUMN_NAME_LINK + " TEXT DEFAULT \"\", " +
            ArtistsTable.COLUMN_NAME_DESCRIPTION + " TEXT DEFAULT \"\", " +
            ArtistsTable.COLUMN_NAME_COVER_LARGE + " TEXT DEFAULT \"\", " +
            ArtistsTable.COLUMN_NAME_COVER_SMALL + " TEXT DEFAULT \"\");";

    static final String CREATE_TABLE_SMALL_STATEMENT =
            "CREATE TABLE IF NOT EXISTS " + ArtistsTable.TABLE_SMALL_NAME + " (" +
                    ArtistsTable.COLUMN_NAME_ID + " INTEGER UNIQUE, " +
                    ArtistsTable.COLUMN_NAME_NAME + " TEXT DEFAULT \"\", " +
                    ArtistsTable.COLUMN_NAME_COVER_SMALL + " TEXT DEFAULT \"\");";

    static final String CLEAR_TABLE_STATEMENT =
            "DROP TABLE IF EXISTS " + ArtistsTable.TABLE_NAME + ";";

    static final String CLEAR_TABLE_SMALL_STATEMENT =
            "DROP TABLE IF EXISTS " + ArtistsTable.TABLE_SMALL_NAME + ";";

    static final String READ_ALL_STATEMENT =
            "SELECT * FROM " + ArtistsTable.TABLE_NAME + ";";

    static final String READ_ALL_SMALL_STATEMENT =
            "SELECT " + ArtistsTable.COLUMN_NAME_ID + "," +
                    ArtistsTable.COLUMN_NAME_NAME + "," +
                    ArtistsTable.COLUMN_NAME_COVER_SMALL +
            " FROM " + ArtistsTable.TABLE_NAME + ";";

    static final String READ_STATEMENT = READ_ALL_STATEMENT + " WHERE %s;";
    static final String READ_SMALL_STATEMENT = READ_ALL_SMALL_STATEMENT + " WHERE %s;";

    static final String CONTAINS_STATEMENT =
            "SELECT EXISTS(SELECT 1 FROM " + ArtistsTable.TABLE_NAME +
                    " WHERE " + ArtistsTable.COLUMN_NAME_ID + " == %s LIMIT 1);";

    static final String CONTAINS_SMALL_STATEMENT =
            "SELECT EXISTS(SELECT 1 FROM " + ArtistsTable.TABLE_SMALL_NAME +
                    " WHERE " + ArtistsTable.COLUMN_NAME_ID + " == %s LIMIT 1);";


    static final String INSERT_STATEMENT =
            "INSERT OR REPLACE INTO " + ArtistsTable.TABLE_NAME + " (" +
                    ArtistsTable.COLUMN_NAME_ID + ", " +
                    ArtistsTable.COLUMN_NAME_NAME + ", " +
                    ArtistsTable.COLUMN_NAME_GENRES + ", " +
                    ArtistsTable.COLUMN_NAME_TRACKS_COUNT + ", " +
                    ArtistsTable.COLUMN_NAME_ALBUMS_COUNT + ", " +
                    ArtistsTable.COLUMN_NAME_LINK + ", " +
                    ArtistsTable.COLUMN_NAME_DESCRIPTION + ", " +
                    ArtistsTable.COLUMN_NAME_COVER_LARGE + ", " +
                    ArtistsTable.COLUMN_NAME_COVER_SMALL + ") " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";

    static final String INSERT_SMALL_STATEMENT =
            "INSERT OR REPLACE INTO " + ArtistsTable.TABLE_SMALL_NAME + " (" +
                    ArtistsTable.COLUMN_NAME_ID + ", " +
                    ArtistsTable.COLUMN_NAME_NAME + ", " +
                    ArtistsTable.COLUMN_NAME_COVER_SMALL + ") " +
                    "VALUES(?, ?, ?);";

    static final String DELETE_ALL_STATEMENT = "DELETE FROM " + ArtistsTable.TABLE_NAME + ";";
    static final String DELETE_ALL_SMALL_STATEMENT = "DELETE FROM " + ArtistsTable.TABLE_SMALL_NAME + ";";

    static final String DELETE_STATEMENT = "DELETE FROM " + ArtistsTable.TABLE_NAME + " WHERE %s;";
    static final String DELETE_SMALL_STATEMENT = "DELETE FROM " + ArtistsTable.TABLE_SMALL_NAME + " WHERE %s;";
}
