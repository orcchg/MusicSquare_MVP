package com.orcchg.data.source.local.artist;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.Nullable;

import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;
import com.orcchg.data.entity.TotalValueEntity;
import com.orcchg.data.entity.util.ArtistUtils;
import com.orcchg.data.source.local.DatabaseHelper;
import com.orcchg.data.source.local.base.BaseLocalSourceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import hugo.weaving.DebugLog;
import timber.log.Timber;

/**
 * Cache that stores {@link ArtistEntity} models locally.
 */
@Singleton
public class ArtistLocalSourceImpl extends BaseLocalSourceImpl implements ArtistLocalSource {

    private static final String SETTINGS_KEY_LAST_CACHE_UPDATE = "last_cache_update";
    private static final long EXPIRATION_TIME = 60 * 10 * 1000;

    private boolean isEmpty;

    public ArtistLocalSourceImpl(DatabaseHelper database) {
        super(database);
        this.isEmpty = true;
    }

    /* Lifecycle */
    // --------------------------------------------------------------------------------------------
    @Override
    public String getId() {
        return "sc_artists";
    }

    @DebugLog @Override
    public void createSchema() {
        database.open();
        database.execSql(ArtistDatabaseContract.CREATE_TABLE_STATEMENT);
        database.execSql(ArtistDatabaseContract.CREATE_TABLE_SMALL_STATEMENT);
        database.close();
    }

    @DebugLog @Override
    public void deleteSchema() {
        database.open();
        database.execSql(ArtistDatabaseContract.DELETE_TABLE_STATEMENT);
        database.execSql(ArtistDatabaseContract.DELETE_TABLE_SMALL_STATEMENT);
        database.close();
    }

    /* Cache stuff */
    // ------------------------------------------
    @DebugLog @Override
    public boolean isEmpty() {
        if (!isEmpty) return false;

        database.open();
        Cursor cursor = database.rawQuery(ArtistDatabaseContract.COUNT_ALL_SMALL_STATEMENT);
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
//        long currentTime = System.currentTimeMillis();
//        long lastUpdateTime = database.getLastCacheUpdateTimeMillis(SETTINGS_KEY_LAST_CACHE_UPDATE);
//        boolean expired = ((currentTime - lastUpdateTime) > EXPIRATION_TIME);
//        if (expired) {
//            clear();
//        }
        return false;  // TODO: fix:   expired;
    }

    @DebugLog @Override
    public void clear() {
        database.open();
        database.execSql(ArtistDatabaseContract.CLEAR_TABLE_STATEMENT);
        database.execSql(ArtistDatabaseContract.CLEAR_TABLE_SMALL_STATEMENT);
        database.close();
        isEmpty = true;
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
        database.open();
        database.beginTransaction();
        SQLiteStatement insert = database.compileStatement(ArtistDatabaseContract.INSERT_STATEMENT);

        insertArtist(insert, artist);

        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }

    @DebugLog @Override
    public void addArtists(List<ArtistEntity> artists) {
        database.open();
        database.beginTransaction();
        SQLiteStatement insert = database.compileStatement(ArtistDatabaseContract.INSERT_STATEMENT);

        for (ArtistEntity artist : artists) {
            insertArtist(insert, artist);
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }

    @DebugLog @Override
    public void updateArtists(List<ArtistEntity> artists) {
        addArtists(artists);  // using REPLACE clause
    }

    @DebugLog @Override
    public void removeArtists(ArtistSpecification specification) {
        String statement = specification == null ? ArtistDatabaseContract.DELETE_ALL_STATEMENT :
                String.format(ArtistDatabaseContract.DELETE_STATEMENT, specification.getSelectionArgs());

        executeStatementIgnoreResult(statement);
    }

    @DebugLog @Override
    public List<ArtistEntity> queryArtists(ArtistSpecification specification) {
        final String statement = specification == null ? ArtistDatabaseContract.READ_ALL_STATEMENT :
                String.format(ArtistDatabaseContract.READ_STATEMENT, specification.getSelectionArgs());

        database.open();
        Cursor cursor = database.rawQuery(statement);
        List<ArtistEntity> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            ArtistEntity artist = createArtistFromCursor(cursor);
            list.add(artist);
        }
        cursor.close();
        database.close();
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
        database.open();
        database.beginTransaction();
        SQLiteStatement insert = database.compileStatement(ArtistDatabaseContract.INSERT_SMALL_STATEMENT);

        insertSmallArtist(insert, artist);

        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }

    @DebugLog @Override
    public void addSmallArtists(List<SmallArtistEntity> artists) {
        database.open();
        database.beginTransaction();
        SQLiteStatement insert = database.compileStatement(ArtistDatabaseContract.INSERT_SMALL_STATEMENT);

        for (SmallArtistEntity artist : artists) {
            insertSmallArtist(insert, artist);
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }

    @DebugLog @Override
    public void updateSmallArtists(List<SmallArtistEntity> artists) {
        addSmallArtists(artists);  // using REPLACE clause
    }

    @DebugLog @Override
    public void removeSmallArtists(ArtistSpecification specification) {
        String statement = specification == null ? ArtistDatabaseContract.DELETE_ALL_SMALL_STATEMENT :
                String.format(ArtistDatabaseContract.DELETE_SMALL_STATEMENT, specification.getSelectionArgs());

        executeStatementIgnoreResult(statement);
    }

    @DebugLog @Override
    public List<SmallArtistEntity> querySmallArtists(ArtistSpecification specification) {
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
            ArtistSpecification specification = new ByGenresArtistSpecification(genres);
            String statement = String.format(ArtistDatabaseContract.READ_SMALL_STATEMENT, specification.getSelectionArgs());
            return executeSelectionBySpecifiedQuery(statement);
        }
        return artists();
    }

    @DebugLog @Override
    public List<SmallArtistEntity> artists(int limit, int offset, @Nullable List<String> genres) {
        if (genres != null) {
            ArtistSpecification specification = new ByGenresArtistSpecification(genres);
            String statement = String.format(ArtistDatabaseContract.READ_SMALL_STATEMENT_LIMIT, specification.getSelectionArgs(), limit, offset);
            return executeSelectionBySpecifiedQuery(statement);
        }
        return artists(limit, offset);
    }

    @DebugLog @Nullable @Override
    public ArtistEntity artist(long artistId) {
        List<ArtistEntity> list = queryArtists(new ByIdArtistSpecification(artistId));
        if (!list.isEmpty()) return list.get(0);
        return null;
    }

    @DebugLog @Override
    public TotalValueEntity total() {
        return total(null);
    }

    @DebugLog @Override
    public TotalValueEntity total(@Nullable List<String> genres) {
        String statement = ArtistDatabaseContract.COUNT_ALL_SMALL_STATEMENT;
        if (genres != null) {
            ArtistSpecification specification = new ByGenresArtistSpecification(genres);
            statement = String.format(ArtistDatabaseContract.COUNT_ALL_SMALL_STATEMENT_WHERE, specification.getSelectionArgs());
        }
        return new TotalValueEntity.Builder(intStatement(statement)).build();
    }

    /* Internal */
    // --------------------------------------------------------------------------------------------
    private List<SmallArtistEntity> executeSelectionBySpecifiedQuery(String statement) {
        return performLoopStatement(statement, ArtistLocalSourceImpl::createSmallArtistFromCursor);
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
