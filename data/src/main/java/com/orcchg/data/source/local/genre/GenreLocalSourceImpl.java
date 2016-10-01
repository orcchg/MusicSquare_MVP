package com.orcchg.data.source.local.genre;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.orcchg.data.entity.GenreEntity;
import com.orcchg.data.entity.TotalValueEntity;
import com.orcchg.data.entity.util.GenreUtils;
import com.orcchg.data.source.local.DatabaseHelper;
import com.orcchg.data.source.local.base.BaseLocalSourceImpl;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;
import timber.log.Timber;

public class GenreLocalSourceImpl extends BaseLocalSourceImpl implements GenreLocalSource {

    private boolean isEmpty;

    public GenreLocalSourceImpl(DatabaseHelper database) {
        super(database);
        this.isEmpty = true;
    }

    /* Lifecycle */
    // --------------------------------------------------------------------------------------------
    @DebugLog
    @Override
    public void onCreate() {
        super.onCreate();
        database.open();
        database.execSql(GenreDatabaseContract.CREATE_TABLE_STATEMENT);
        database.close();
    }

    @DebugLog @Override
    public void onUpgrade() {
        drop();
        onCreate();
    }

    @DebugLog @Override
    public void onDowngrade() {
        drop();
        onCreate();
    }

    @DebugLog
    private void drop() {
        database.open();
        database.execSql(GenreDatabaseContract.DELETE_TABLE_STATEMENT);
        database.close();
    }

    /* Cache stuff */
    // ------------------------------------------
    @DebugLog @Override
    public boolean isEmpty() {
        if (!isEmpty) return false;

        database.open();
        Cursor cursor = database.rawQuery(GenreDatabaseContract.COUNT_ALL_STATEMENT);
        isEmpty = true;
        if (cursor.moveToFirst()) {
            int total = cursor.getInt(0);
            Timber.i("Total in cache: %s", total);
            isEmpty = total == 0;
        }
        cursor.close();
        database.close();
        return isEmpty;
    }

    @DebugLog @Override
    public boolean isExpired() {
        return false;  // TODO: fix:   expired;
    }

    @DebugLog @Override
    public void clear() {
        database.open();
        database.execSql(GenreDatabaseContract.CLEAR_TABLE_STATEMENT);
        database.close();
        isEmpty = true;
    }

    @DebugLog @Override
    public int totalItems() {
        String statement = GenreDatabaseContract.COUNT_ALL_STATEMENT;
        return intStatement(statement);
    }

    /* Repository */
    // --------------------------------------------------------------------------------------------
    @DebugLog @Override
    public boolean hasGenre(@Nullable String name) {
        if (TextUtils.isEmpty(name)) return false;
        String statement = String.format(GenreDatabaseContract.CONTAINS_STATEMENT, name);
        return checkStatement(statement);
    }

    @DebugLog @Override
    public void addGenre(GenreEntity genre) {
        database.open();
        database.beginTransaction();
        SQLiteStatement insert = database.compileStatement(GenreDatabaseContract.INSERT_STATEMENT);

        insertGenre(insert, genre);

        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }

    @DebugLog @Override
    public void addGenres(List<GenreEntity> genres) {
        database.open();
        database.beginTransaction();
        SQLiteStatement insert = database.compileStatement(GenreDatabaseContract.INSERT_STATEMENT);

        for (GenreEntity genre : genres) {
            insertGenre(insert, genre);
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }

    @DebugLog @Override
    public void updateGenres(List<GenreEntity> genres) {
        addGenres(genres);  // using REPLACE clause
    }

    @DebugLog @Override
    public void removeGenres(GenreSpecification specification) {
        String statement = specification == null ? GenreDatabaseContract.DELETE_ALL_STATEMENT :
                String.format(GenreDatabaseContract.DELETE_STATEMENT, specification.getSelectionArgs());

        executeStatementIgnoreResult(statement);
    }

    @DebugLog @Override
    public List<GenreEntity> queryGenres(GenreSpecification specification) {
        final String statement = specification == null ? GenreDatabaseContract.READ_ALL_STATEMENT :
                String.format(GenreDatabaseContract.READ_STATEMENT, specification.getSelectionArgs());

        database.open();
        Cursor cursor = database.rawQuery(statement);
        List<GenreEntity> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            GenreEntity genre = createGenreFromCursor(cursor);
            list.add(genre);
        }
        cursor.close();
        database.close();
        return list;
    }

    /* Data source implementation */
    // ------------------------------------------
    @DebugLog @Override
    public List<GenreEntity> genres() {
        return queryGenres(null);
    }

    @DebugLog @Nullable @Override
    public GenreEntity genre(String name) {
        List<GenreEntity> list = queryGenres(new ByNameGenreSpecification(name));
        if (!list.isEmpty()) return list.get(0);
        return null;
    }

    @DebugLog @Override
    public TotalValueEntity total() {
        String statement = GenreDatabaseContract.COUNT_ALL_STATEMENT;
        return new TotalValueEntity.Builder(intStatement(statement)).build();
    }

    /* Internal */
    // --------------------------------------------------------------------------------------------
    /* Insertion */
    // ------------------------------------------
    private void insertGenre(SQLiteStatement insert, GenreEntity genre) {
        insert.bindString(1, genre.getName());
        insert.bindString(2, GenreUtils.genresToString(genre));
        insert.execute();
    }

    /* Mapping */
    // ------------------------------------------
    private static GenreEntity createGenreFromCursor(Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex(GenreDatabaseContract.GenresTable.COLUMN_NAME_NAME));
        String genres = cursor.getString(cursor.getColumnIndex(GenreDatabaseContract.GenresTable.COLUMN_NAME_GENRES));

        return new GenreEntity.Builder(name)
                .setGenres(GenreUtils.stringToGenres(genres))
                .build();
    }
}
