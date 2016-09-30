package com.orcchg.data.source.local.artist;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.Nullable;

import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;
import com.orcchg.data.entity.TotalValueEntity;
import com.orcchg.data.entity.util.ArtistUtils;
import com.orcchg.data.source.local.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import hugo.weaving.DebugLog;
import timber.log.Timber;

import static com.orcchg.data.source.local.artist.ArtistDatabaseContract.COUNT_ALL_SMALL_STATEMENT;

/**
 * Cache that stores {@link ArtistEntity} models locally.
 */
@Singleton
public class ArtistLocalSourceImpl implements ArtistLocalSource, DatabaseHelper.LifeCycleCallback {

    private static final String SETTINGS_KEY_LAST_CACHE_UPDATE = "last_cache_update";
    private static final long EXPIRATION_TIME = 60 * 10 * 1000;

    private final DatabaseHelper database;

    public ArtistLocalSourceImpl(DatabaseHelper database) {
        this.database = database;
        this.database.setLifeCycleCallback(this);
    }

    /* Lifecycle */
    // --------------------------------------------------------------------------------------------
    @DebugLog @Override
    public void create() {
        this.database.open();
        this.database.execSql(ArtistDatabaseContract.CREATE_TABLE_STATEMENT);
        this.database.execSql(ArtistDatabaseContract.CREATE_TABLE_SMALL_STATEMENT);
        this.database.close();
    }

    @DebugLog @Override
    public void onCreate() {
        create();
    }

    @DebugLog @Override
    public void onUpgrade() {
        clear();
        create();
    }

    @DebugLog @Override
    public void onDowngrade() {
        clear();
        create();
    }

    /* Cache stuff */
    // ------------------------------------------
    @DebugLog @Override
    public boolean isEmpty() {
        this.database.open();
        Cursor cursor = this.database.rawQuery(COUNT_ALL_SMALL_STATEMENT);
        boolean result = true;
        if (cursor.moveToFirst()) {
            int total = cursor.getInt(0);
            Timber.v("Total in cache: %s", total);
            result = total == 0;
        }
        cursor.close();
        this.database.close();
        return result;
    }

    @DebugLog @Override
    public boolean isExpired() {
//        long currentTime = System.currentTimeMillis();
//        long lastUpdateTime = this.database.getLastCacheUpdateTimeMillis(SETTINGS_KEY_LAST_CACHE_UPDATE);
//        boolean expired = ((currentTime - lastUpdateTime) > EXPIRATION_TIME);
//        if (expired) {
//            this.clear();
//        }
        return false;  // TODO: fix:   expired;
    }

    @DebugLog @Override
    public void clear() {
        this.database.open();
        this.database.execSql(ArtistDatabaseContract.CLEAR_TABLE_STATEMENT);
        this.database.execSql(ArtistDatabaseContract.CLEAR_TABLE_SMALL_STATEMENT);
        this.database.close();
    }

    @DebugLog @Override
    public int totalItems() {
        String statement = ArtistDatabaseContract.COUNT_ALL_SMALL_STATEMENT;
        return intStatement(statement);
    }

    /* Repository */
    // --------------------------------------------------------------------------------------------
    /* Artists */
    // ------------------------------------------
    @DebugLog @Override
    public boolean hasArtist(long artistId) {
        String statement = String.format(ArtistDatabaseContract.CONTAINS_STATEMENT, artistId);
        return checkStatement(statement);
    }

    @DebugLog @Override
    public void addArtist(ArtistEntity artist) {
        this.database.open();
        this.database.beginTransaction();
        SQLiteStatement insert = this.database.compileStatement(ArtistDatabaseContract.INSERT_STATEMENT);

        insertArtist(insert, artist);

        this.database.setTransactionSuccessful();
        this.database.endTransaction();
        this.database.close();
    }

    @DebugLog @Override
    public void addArtists(List<ArtistEntity> artists) {
        this.database.open();
        this.database.beginTransaction();
        SQLiteStatement insert = this.database.compileStatement(ArtistDatabaseContract.INSERT_STATEMENT);

        for (ArtistEntity artist : artists) {
            insertArtist(insert, artist);
        }

        this.database.setTransactionSuccessful();
        this.database.endTransaction();
        this.database.close();
    }

    @DebugLog @Override
    public void updateArtists(List<ArtistEntity> artists) {
        addArtists(artists);  // using REPLACE clause
    }

    @DebugLog @Override
    public void removeArtists(ArtistsSpecification specification) {
        String statement = specification == null ? ArtistDatabaseContract.DELETE_ALL_STATEMENT :
                String.format(ArtistDatabaseContract.DELETE_STATEMENT, specification.getSelectionArgs());

        executeStatementIgnoreResult(statement);
    }

    @DebugLog @Override
    public List<ArtistEntity> queryArtists(ArtistsSpecification specification) {
        final String statement = specification == null ? ArtistDatabaseContract.READ_ALL_STATEMENT :
                String.format(ArtistDatabaseContract.READ_STATEMENT, specification.getSelectionArgs());

        this.database.open();
        Cursor cursor = this.database.rawQuery(statement);
        List<ArtistEntity> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            ArtistEntity artist = createArtistFromCursor(cursor);
            list.add(artist);
//            Timber.v(artist.toString());
        }
        cursor.close();
        this.database.close();
        return list;
    }

    /* Small artists */
    // ------------------------------------------
    @DebugLog @Override
    public boolean hasSmallArtist(long artistId) {
        String statement = String.format(ArtistDatabaseContract.CONTAINS_SMALL_STATEMENT, artistId);
        return checkStatement(statement);
    }

    @DebugLog @Override
    public void addSmallArtist(SmallArtistEntity artist) {
        this.database.open();
        this.database.beginTransaction();
        SQLiteStatement insert = this.database.compileStatement(ArtistDatabaseContract.INSERT_SMALL_STATEMENT);

        insertSmallArtist(insert, artist);

        this.database.setTransactionSuccessful();
        this.database.endTransaction();
        this.database.close();
    }

    @DebugLog @Override
    public void addSmallArtists(List<SmallArtistEntity> artists) {
        this.database.open();
        this.database.beginTransaction();
        SQLiteStatement insert = this.database.compileStatement(ArtistDatabaseContract.INSERT_SMALL_STATEMENT);

        for (SmallArtistEntity artist : artists) {
            insertSmallArtist(insert, artist);
        }

        this.database.setTransactionSuccessful();
        this.database.endTransaction();
        this.database.close();
    }

    @DebugLog @Override
    public void updateSmallArtists(List<SmallArtistEntity> artists) {
        addSmallArtists(artists);  // using REPLACE clause
    }

    @DebugLog @Override
    public void removeSmallArtists(ArtistsSpecification specification) {
        String statement = specification == null ? ArtistDatabaseContract.DELETE_ALL_SMALL_STATEMENT :
                String.format(ArtistDatabaseContract.DELETE_SMALL_STATEMENT, specification.getSelectionArgs());

        executeStatementIgnoreResult(statement);
    }

    @DebugLog @Override
    public List<SmallArtistEntity> querySmallArtists(ArtistsSpecification specification) {
        String statement = specification == null ? ArtistDatabaseContract.READ_ALL_SMALL_STATEMENT :
            String.format(ArtistDatabaseContract.READ_SMALL_STATEMENT, specification.getSelectionArgs());

        return executeSelectionBySpecifiedQuery(statement);
    }

    /* Data source implementation */
    // ------------------------------------------
    @DebugLog @Override
    public List<SmallArtistEntity> artists() {
        return querySmallArtists(null);
    }

    @DebugLog @Override
    public List<SmallArtistEntity> artists(int limit, int offset) {
        String statement = String.format(ArtistDatabaseContract.READ_ALL_SMALL_STATEMENT_LIMIT, limit, offset);
        return executeSelectionBySpecifiedQuery(statement);
    }

    @DebugLog @Override
    public List<SmallArtistEntity> artists(@Nullable List<String> genres) {
        if (genres != null) {
            ArtistsSpecification specification = new ByGenresArtistsSpecification(genres);
            String statement = String.format(ArtistDatabaseContract.READ_SMALL_STATEMENT, specification.getSelectionArgs());
            return executeSelectionBySpecifiedQuery(statement);
        }
        return artists();
    }

    @DebugLog @Override
    public List<SmallArtistEntity> artists(int limit, int offset, @Nullable List<String> genres) {
        if (genres != null) {
            ArtistsSpecification specification = new ByGenresArtistsSpecification(genres);
            String statement = String.format(ArtistDatabaseContract.READ_SMALL_STATEMENT_LIMIT, specification.getSelectionArgs(), limit, offset);
            return executeSelectionBySpecifiedQuery(statement);
        }
        return artists(limit, offset);
    }

    @DebugLog @Nullable @Override
    public ArtistEntity artist(long artistId) {
        List<ArtistEntity> list = queryArtists(new ByIdArtistsSpecification(artistId));
        if (!list.isEmpty()) return list.get(0);
        return null;
    }

    @Override
    public TotalValueEntity total() {
        return total(null);
    }

    @Override
    public TotalValueEntity total(@Nullable List<String> genres) {
        String statement = ArtistDatabaseContract.COUNT_ALL_SMALL_STATEMENT;
        if (genres != null) {
            ArtistsSpecification specification = new ByGenresArtistsSpecification(genres);
            statement = String.format(ArtistDatabaseContract.COUNT_ALL_SMALL_STATEMENT_WHERE, specification.getSelectionArgs());
        }
        return new TotalValueEntity.Builder(intStatement(statement)).build();
    }

    /* Internal */
    // --------------------------------------------------------------------------------------------
    interface ProcessCursor<Result> {
        Result process(Cursor cursor);
    }

    /* Insertion */
    // ------------------------------------------
    private void insertArtist(SQLiteStatement insert, ArtistEntity artist) {
        String link = artist.getWebLink();
        String description = artist.getDescription();

        insert.bindLong(1, artist.getId());
        insert.bindString(2, artist.getName());
        insert.bindString(3, ArtistUtils.genresToString(artist));
        insert.bindLong(4, artist.getTracksCount());
        insert.bindLong(5, artist.getAlbumsCount());
        insert.bindString(6, link == null ? "" : link);  // could be null
        insert.bindString(7, description == null ? "" : description);  // could be null
        insert.bindString(8, artist.getCoverLarge());
        insert.bindString(9, artist.getCoverSmall());
        insert.execute();
    }

    private void insertSmallArtist(SQLiteStatement insert, SmallArtistEntity artist) {
        insert.bindLong(1, artist.getId());
        insert.bindString(2, artist.getName());
        insert.bindString(3, artist.getCover());
        insert.execute();
    }

    /* Execution */
    // ------------------------------------------
    private List<SmallArtistEntity> executeSelectionBySpecifiedQuery(String statement) {
        return performLoopStatement(statement, ArtistLocalSourceImpl::createSmallArtistFromCursor);
    }

    private void executeStatementIgnoreResult(String statement) {
        this.database.open();
        this.database.beginTransaction();
        SQLiteStatement object = this.database.compileStatement(statement);
        object.execute();
        this.database.setTransactionSuccessful();
        this.database.endTransaction();
        this.database.close();
    }

    private boolean checkStatement(String statement) {
        Boolean result = performStatement(statement, (cursor) -> (cursor.getInt(0) != 0));
        return result != null ? result : false;
    }

    private int intStatement(String statement) {
        Integer result = performStatement(statement, (cursor) -> (cursor.getInt(0)));
        return result != null ? result : 0;
    }

    @Nullable
    private <Result> Result performStatement(String statement, ProcessCursor<Result> cursorProcessor) {
        Result result = null;
        this.database.open();
        Cursor cursor = this.database.rawQuery(statement);
        if (cursor.moveToFirst()) {
            result = cursorProcessor.process(cursor);
        }
        cursor.close();
        this.database.close();
        return result;
    }

    @Nullable
    private <Result> List<Result> performLoopStatement(String statement, ProcessCursor<Result> cursorProcessor) {
        this.database.open();
        Cursor cursor = this.database.rawQuery(statement);
        List<Result> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Result result = cursorProcessor.process(cursor);
            list.add(result);
        }
        cursor.close();
        this.database.close();
        return list;
    }

    /* Mapping */
    // ------------------------------------------
    private static ArtistEntity createArtistFromCursor(Cursor cursor) {
        long id = cursor.getInt(cursor.getColumnIndex(ArtistDatabaseContract.ArtistsTable.COLUMN_NAME_ID));
        String name = cursor.getString(cursor.getColumnIndex(ArtistDatabaseContract.ArtistsTable.COLUMN_NAME_NAME));
        String genres = cursor.getString(cursor.getColumnIndex(ArtistDatabaseContract.ArtistsTable.COLUMN_NAME_GENRES));
        int tracksCount = cursor.getInt(cursor.getColumnIndex(ArtistDatabaseContract.ArtistsTable.COLUMN_NAME_TRACKS_COUNT));
        int albumsCount = cursor.getInt(cursor.getColumnIndex(ArtistDatabaseContract.ArtistsTable.COLUMN_NAME_ALBUMS_COUNT));
        String webLink = cursor.getString(cursor.getColumnIndex(ArtistDatabaseContract.ArtistsTable.COLUMN_NAME_LINK));
        String description = cursor.getString(cursor.getColumnIndex(ArtistDatabaseContract.ArtistsTable.COLUMN_NAME_DESCRIPTION));
        String cover_large = cursor.getString(cursor.getColumnIndex(ArtistDatabaseContract.ArtistsTable.COLUMN_NAME_COVER_LARGE));
        String cover_small = cursor.getString(cursor.getColumnIndex(ArtistDatabaseContract.ArtistsTable.COLUMN_NAME_COVER_SMALL));
        
        return new ArtistEntity.Builder(id, name)
                .setGenres(ArtistUtils.stringToGenres(genres))
                .setTracksCount(tracksCount)
                .setAlbumsCount(albumsCount)
                .setWebLink(webLink)
                .setDescription(description)
                .setCovers(cover_large, cover_small)
                .build();
    }

    private static SmallArtistEntity createSmallArtistFromCursor(Cursor cursor) {
        long id = cursor.getInt(cursor.getColumnIndex(ArtistDatabaseContract.ArtistsTable.COLUMN_NAME_ID));
        String name = cursor.getString(cursor.getColumnIndex(ArtistDatabaseContract.ArtistsTable.COLUMN_NAME_NAME));
        String cover_small = cursor.getString(cursor.getColumnIndex(ArtistDatabaseContract.ArtistsTable.COLUMN_NAME_COVER_SMALL));

        return new SmallArtistEntity.Builder(id, name)
                .setCover(cover_small)
                .build();
    }
}
