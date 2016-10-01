package com.orcchg.data.source.local.genre;

import com.orcchg.data.source.local.base.DatabaseContract;

public class GenreDatabaseContract extends DatabaseContract {

    GenreDatabaseContract() {
        // protect from accidental instantiation
    }

    static class GenresTable {
        static final String TABLE_NAME = "Genres";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_GENRES = "genres";
    }

    static final String CREATE_TABLE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS " + GenresTable.TABLE_NAME + " (" +
                    GenresTable.COLUMN_NAME_NAME + " TEXT DEFAULT \"\", " +
                    GenresTable.COLUMN_NAME_GENRES + " TEXT DEFAULT \"\")";

    static final String DELETE_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + GenresTable.TABLE_NAME;

    static final String CLEAR_TABLE_STATEMENT = "DELETE FROM " + GenresTable.TABLE_NAME;

    static final String COUNT_ALL_STATEMENT = "SELECT COUNT(*) FROM " + GenresTable.TABLE_NAME;
    static final String COUNT_ALL_STATEMENT_WHERE = COUNT_ALL_STATEMENT + " WHERE %s ";

    static final String READ_ALL_STATEMENT = "SELECT * FROM " + GenresTable.TABLE_NAME;
    static final String READ_STATEMENT = READ_ALL_STATEMENT + " WHERE %s ";
    static final String READ_STATEMENT_LIMIT = READ_ALL_STATEMENT + LIMIT_OFFSET;

    static final String CONTAINS_STATEMENT =
            "SELECT EXISTS(SELECT 1 FROM " + GenresTable.TABLE_NAME +
                    " WHERE " + GenresTable.COLUMN_NAME_NAME + " LIKE %s LIMIT 1)";

    static final String INSERT_STATEMENT =
            "INSERT OR REPLACE INTO " + GenresTable.TABLE_NAME + " (" +
                    GenresTable.COLUMN_NAME_NAME + ", " +
                    GenresTable.COLUMN_NAME_GENRES + ") " + "VALUES(?, ?)";

    static final String DELETE_ALL_STATEMENT = CLEAR_TABLE_STATEMENT;
    static final String DELETE_STATEMENT = "DELETE FROM " + GenresTable.TABLE_NAME + " WHERE %s ";
}
