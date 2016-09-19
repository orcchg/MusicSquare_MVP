package com.orcchg.data.source.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.io.File;

import javax.inject.Inject;

public class DatabaseHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MusicSquareDatabase.db";

    private static final String SETTINGS_FILE_NAME = "MusicSquareSettings";

    private final Context context;
    private final FileManager fileManager;
    private SQLiteDatabase database;
    private File databaseFile;
    private LifeCycleCallback lifeCycleCallback;
    private int oldVersion;

    @Inject
    DatabaseHelper(Context context, FileManager fileManager) {
        this.context = context;
        this.fileManager = fileManager;
        this.databaseFile = context.getDatabasePath(DATABASE_NAME);
    }

    /* Lifecycle */
    // ------------------------------------------
    public interface LifeCycleCallback {
        void onUpgrade();
        void onDowngrade();
    }

    private void openOrCreateDatabase() {
        this.database = SQLiteDatabase.openOrCreateDatabase(this.databaseFile, null);
        this.oldVersion = this.database.getVersion();
        this.database.setVersion(DATABASE_VERSION);
    }

    private void checkVersion() {
        if (this.oldVersion < DATABASE_VERSION) {
            if (this.lifeCycleCallback != null) this.lifeCycleCallback.onUpgrade();
        } else if (this.oldVersion > DATABASE_VERSION) {
            if (this.lifeCycleCallback != null) this.lifeCycleCallback.onDowngrade();
        }
    }

    public void setLifeCycleCallback(LifeCycleCallback lifeCycleCallback) {
        this.lifeCycleCallback = lifeCycleCallback;
    }

    /* Interface */
    // --------------------------------------------------------------------------------------------
    public void open() {
        if (this.database == null || !this.database.isOpen()) {
            openOrCreateDatabase();
        }
        checkVersion();
    }

    public void close() {
        this.database.close();
    }

    public void execSql(String sql) {
        this.database.execSQL(sql);
    }

    public Cursor rawQuery(String statement) {
        return this.database.rawQuery(statement, null);
    }

    /* Raw transaction */
    // ------------------------------------------
    public void beginTransaction() {
        this.database.beginTransaction();
    }

    public SQLiteStatement compileStatement(String statement) {
        return this.database.compileStatement(statement);
    }

    public void setTransactionSuccessful() {
        this.database.setTransactionSuccessful();
    }

    public void endTransaction() {
        this.database.endTransaction();
    }

    /* Expiration */
    // --------------------------------------------------------------------------------------------
    /**
     * Set in millis, the last time the cache was accessed.
     */
    public void setLastCacheUpdateTimeMillis(String key) {
        long currentMillis = System.currentTimeMillis();
        this.fileManager.writeToPreferences(this.context, SETTINGS_FILE_NAME, key, currentMillis);
    }

    /**
     * Get in millis, the last time the cache was accessed.
     */
    public long getLastCacheUpdateTimeMillis(String key) {
        return this.fileManager.getFromPreferences(this.context, SETTINGS_FILE_NAME, key);
    }
}
