package com.orcchg.data.source.local.artist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.Nullable;

import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;
import com.orcchg.data.entity.util.ArtistUtils;
import com.orcchg.data.source.local.FileManager;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;
import timber.log.Timber;

/**
 * Cache that stores {@link ArtistEntity} models locally.
 */
public class DatabaseSourceImpl extends SQLiteOpenHelper implements LocalSource {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ArtistsDatabase.db";

    private static final String SETTINGS_FILE_NAME = "ArtistsSettings";
    private static final String SETTINGS_KEY_LAST_CACHE_UPDATE = "last_cache_update";
    private static final long EXPIRATION_TIME = 60 * 10 * 1000;

    private final Context context;
    private final FileManager fileManager;

    public DatabaseSourceImpl(Context context, FileManager fileManager) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.fileManager = fileManager;
    }

    /* Lifecycle */
    // --------------------------------------------------------------------------------------------
    @DebugLog
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.CREATE_TABLE_STATEMENT);
        db.execSQL(DatabaseContract.CREATE_TABLE_SMALL_STATEMENT);
    }

    @DebugLog
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.CLEAR_TABLE_STATEMENT);
        db.execSQL(DatabaseContract.CLEAR_TABLE_SMALL_STATEMENT);
        onCreate(db);
    }

    @DebugLog
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        onUpgrade(db, oldVersion, newVersion);
    }

    /* Cache stuff */
    // ------------------------------------------
    @DebugLog
    @Override
    public boolean isEmpty() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DatabaseContract.READ_ALL_SMALL_STATEMENT, null);
        boolean result = true;
        if (cursor.moveToFirst()) {
            int total = cursor.getInt(0);
            Timber.v("Total in cache: %s", total);
            result = total == 0;
        }
        cursor.close();
        db.close();
        return result;
    }

    @DebugLog
    @Override
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        long lastUpdateTime = this.getLastCacheUpdateTimeMillis();
        boolean expired = ((currentTime - lastUpdateTime) > EXPIRATION_TIME);
        if (expired) {
            this.clear();
        }
        return expired;
    }

    @DebugLog
    @Override
    public void clear() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DatabaseContract.CLEAR_TABLE_STATEMENT);
        db.close();
    }

    /**
     * Set in millis, the last time the cache was accessed.
     */
    @DebugLog
    private void setLastCacheUpdateTimeMillis() {
        long currentMillis = System.currentTimeMillis();
        this.fileManager.writeToPreferences(this.context, SETTINGS_FILE_NAME,
                SETTINGS_KEY_LAST_CACHE_UPDATE, currentMillis);
    }

    /**
     * Get in millis, the last time the cache was accessed.
     */
    @DebugLog
    private long getLastCacheUpdateTimeMillis() {
        return this.fileManager.getFromPreferences(this.context, SETTINGS_FILE_NAME,
                SETTINGS_KEY_LAST_CACHE_UPDATE);
    }

    /* Repository */
    // --------------------------------------------------------------------------------------------
    /* Artists */
    // ------------------------------------------
    @DebugLog
    @Override
    public boolean hasArtist(long artistId) {
        String statement = String.format(DatabaseContract.CONTAINS_STATEMENT, artistId);
        return checkStatement(statement);
    }

    @DebugLog
    @Override
    public void addArtist(ArtistEntity artist) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        SQLiteStatement insert = db.compileStatement(DatabaseContract.INSERT_STATEMENT);

        insertArtist(insert, artist);

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    @DebugLog
    @Override
    public void addArtists(List<ArtistEntity> artists) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        SQLiteStatement insert = db.compileStatement(DatabaseContract.INSERT_STATEMENT);

        for (ArtistEntity artist : artists) {
            insertArtist(insert, artist);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        setLastCacheUpdateTimeMillis();
    }

    @DebugLog
    @Override
    public void updateArtists(List<ArtistEntity> artists) {
        addArtists(artists);  // using REPLACE clause
    }

    @DebugLog
    @Override
    public void removeArtists(ArtistsSpecification specification) {
        String statement = specification == null ? DatabaseContract.DELETE_ALL_STATEMENT :
                String.format(DatabaseContract.DELETE_STATEMENT, specification.getSelectionArgs());

        executeStatement(statement);
    }

    @DebugLog
    @Override
    public List<ArtistEntity> queryArtists(ArtistsSpecification specification) {
        final String statement = specification == null ? DatabaseContract.READ_ALL_STATEMENT :
                String.format(DatabaseContract.READ_STATEMENT, specification.getSelectionArgs());

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(statement, null);
        List<ArtistEntity> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            ArtistEntity artist = createArtistFromCursor(cursor);
            list.add(artist);
            Timber.v(artist.toString());
        }
        cursor.close();
        db.close();
        return list;
    }

    /* Small artists */
    // ------------------------------------------
    @DebugLog
    @Override
    public boolean hasSmallArtist(long artistId) {
        String statement = String.format(DatabaseContract.CONTAINS_SMALL_STATEMENT, artistId);
        return checkStatement(statement);
    }

    @DebugLog
    @Override
    public void addSmallArtist(SmallArtistEntity artist) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        SQLiteStatement insert = db.compileStatement(DatabaseContract.INSERT_SMALL_STATEMENT);

        insertSmallArtist(insert, artist);

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    @DebugLog
    @Override
    public void addSmallArtists(List<SmallArtistEntity> artists) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        SQLiteStatement insert = db.compileStatement(DatabaseContract.INSERT_SMALL_STATEMENT);

        for (SmallArtistEntity artist : artists) {
            insertSmallArtist(insert, artist);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        setLastCacheUpdateTimeMillis();
    }

    @DebugLog
    @Override
    public void updateSmallArtists(List<SmallArtistEntity> artists) {
        addSmallArtists(artists);  // using REPLACE clause
    }

    @DebugLog
    @Override
    public void removeSmallArtists(ArtistsSpecification specification) {
        String statement = specification == null ? DatabaseContract.DELETE_ALL_SMALL_STATEMENT :
                String.format(DatabaseContract.DELETE_SMALL_STATEMENT, specification.getSelectionArgs());

        executeStatement(statement);
    }

    @DebugLog
    @Override
    public List<SmallArtistEntity> querySmallArtists(ArtistsSpecification specification) {
        final String statement = specification == null ? DatabaseContract.READ_ALL_SMALL_STATEMENT :
                String.format(DatabaseContract.READ_SMALL_STATEMENT, specification.getSelectionArgs());

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(statement, null);
        List<SmallArtistEntity> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            SmallArtistEntity artist = createSmallArtistFromCursor(cursor);
            list.add(artist);
            Timber.v(artist.toString());
        }
        cursor.close();
        db.close();
        return list;
    }

    /* Data source implementation */
    // ------------------------------------------
    @DebugLog
    @Override
    public List<SmallArtistEntity> artists() {
        return querySmallArtists(null);
    }

    @DebugLog
    @Nullable
    @Override
    public ArtistEntity artist(long artistId) {
        List<ArtistEntity> list = queryArtists(new ByIdArtistsSpecification(artistId));
        if (!list.isEmpty()) return list.get(0);
        return null;
    }

    /* Internal */
    // --------------------------------------------------------------------------------------------
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

    private void executeStatement(String statement) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        SQLiteStatement object = db.compileStatement(statement);
        object.execute();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    private boolean checkStatement(String statement) {
        boolean result = false;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(statement, null);
        if (cursor.moveToFirst()) {
            result = cursor.getInt(0) != 0;
        }
        cursor.close();
        db.close();
        return result;
    }

    private ArtistEntity createArtistFromCursor(Cursor cursor) {
        long id = cursor.getInt(cursor.getColumnIndex(DatabaseContract.ArtistsTable.COLUMN_NAME_ID));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseContract.ArtistsTable.COLUMN_NAME_NAME));
        String genres = cursor.getString(cursor.getColumnIndex(DatabaseContract.ArtistsTable.COLUMN_NAME_GENRES));
        int tracksCount = cursor.getInt(cursor.getColumnIndex(DatabaseContract.ArtistsTable.COLUMN_NAME_TRACKS_COUNT));
        int albumsCount = cursor.getInt(cursor.getColumnIndex(DatabaseContract.ArtistsTable.COLUMN_NAME_ALBUMS_COUNT));
        String webLink = cursor.getString(cursor.getColumnIndex(DatabaseContract.ArtistsTable.COLUMN_NAME_LINK));
        String description = cursor.getString(cursor.getColumnIndex(DatabaseContract.ArtistsTable.COLUMN_NAME_DESCRIPTION));
        String cover_large = cursor.getString(cursor.getColumnIndex(DatabaseContract.ArtistsTable.COLUMN_NAME_COVER_LARGE));
        String cover_small = cursor.getString(cursor.getColumnIndex(DatabaseContract.ArtistsTable.COLUMN_NAME_COVER_SMALL));
        
        return new ArtistEntity.Builder(id, name)
                .setGenres(ArtistUtils.stringToGenres(genres))
                .setTracksCount(tracksCount)
                .setAlbumsCount(albumsCount)
                .setWebLink(webLink)
                .setDescription(description)
                .setCovers(cover_large, cover_small)
                .build();
    }

    private SmallArtistEntity createSmallArtistFromCursor(Cursor cursor) {
        long id = cursor.getInt(cursor.getColumnIndex(DatabaseContract.ArtistsTable.COLUMN_NAME_ID));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseContract.ArtistsTable.COLUMN_NAME_NAME));
        String cover_small = cursor.getString(cursor.getColumnIndex(DatabaseContract.ArtistsTable.COLUMN_NAME_COVER_SMALL));

        return new SmallArtistEntity.Builder(id, name)
                .setCover(cover_small)
                .build();
    }
}
