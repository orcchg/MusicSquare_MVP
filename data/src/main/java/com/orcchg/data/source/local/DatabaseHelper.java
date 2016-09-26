package com.orcchg.data.source.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import hugo.weaving.DebugLog;
import timber.log.Timber;

@Singleton
public class DatabaseHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MusicSquareDatabase";

    private static final String SETTINGS_FILE_NAME = "MusicSquareSettings";

    private final Context context;
    private final FileManager fileManager;
    private SQLiteDatabase database;
    private File databaseFile;
    private LifeCycleCallback lifeCycleCallback;
    private int oldVersion;
    private int openCounter;

    @DebugLog @Inject
    DatabaseHelper(Context context, FileManager fileManager) {
        this.context = context;
        this.fileManager = fileManager;
        this.databaseFile = context.getDatabasePath(DATABASE_NAME);
    }

    /* Lifecycle */
    // ------------------------------------------
    public interface LifeCycleCallback {
        void onCreate();
        void onUpgrade();
        void onDowngrade();
    }

    @DebugLog
    private boolean openOrCreateDatabase() {
        boolean isNewDb = !this.databaseFile.exists();
        this.database = SQLiteDatabase.openOrCreateDatabase(this.databaseFile, null);
        this.oldVersion = this.database.getVersion();
        this.database.setVersion(DATABASE_VERSION);
        return isNewDb;
    }

    @DebugLog
    private void checkVersion() {
        int oldVersion = this.oldVersion;
        Timber.i("Database version: %s", oldVersion);
        this.oldVersion = DATABASE_VERSION;  // protect from recursion
        if (oldVersion <= 1) {
            Timber.v("Skip upgrade-downgrade event for the very first version");
            return;
        }
        if (oldVersion < DATABASE_VERSION) {
            if (this.lifeCycleCallback != null) this.lifeCycleCallback.onUpgrade();
        } else if (oldVersion > DATABASE_VERSION) {
            if (this.lifeCycleCallback != null) this.lifeCycleCallback.onDowngrade();
        }
    }

    private void checkCounter() {
        if (this.openCounter < 0) throw new RuntimeException("Open counter is negative !");
    }

    public void setLifeCycleCallback(LifeCycleCallback lifeCycleCallback) {
        this.lifeCycleCallback = lifeCycleCallback;
    }

    /* Interface */
    // --------------------------------------------------------------------------------------------
    @DebugLog
    public void open() {
        checkCounter();
        ++this.openCounter;
        Timber.v("Helper address: %s, open counter: %s", hashCode(), this.openCounter);
        if (this.openCounter > 1) {
            Timber.i("Database is already opened");
            return;
        }
        if (this.database == null || !this.database.isOpen()) {
            if (openOrCreateDatabase() && this.lifeCycleCallback != null) {
                this.lifeCycleCallback.onCreate();
            }
        }
        checkVersion();
    }

    @DebugLog
    public void close() {
        --this.openCounter;
        checkCounter();
        if (this.openCounter == 0) {
            Timber.i("Database to be closed");
            this.database.close();
        }
    }

    @DebugLog
    public boolean isOpened() {
        return this.openCounter > 0;
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
