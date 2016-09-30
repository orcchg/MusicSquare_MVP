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

    @Inject
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

    private boolean openOrCreateDatabase() {
        boolean isNewDb = !databaseFile.exists();
        database = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
        oldVersion = database.getVersion();
        database.setVersion(DATABASE_VERSION);
        return isNewDb;
    }

    private void checkVersion() {
        int oldVersion = this.oldVersion;
        Timber.i("Database version: %s", oldVersion);
        this.oldVersion = DATABASE_VERSION;  // protect from recursion
        if (oldVersion <= 1) {
            Timber.i("Skip upgrade-downgrade event for the very first version");
            return;
        }
        if (oldVersion < DATABASE_VERSION) {
            if (lifeCycleCallback != null) lifeCycleCallback.onUpgrade();
        } else if (oldVersion > DATABASE_VERSION) {
            if (lifeCycleCallback != null) lifeCycleCallback.onDowngrade();
        }
    }

    private void checkCounter() {
        if (openCounter < 0) throw new RuntimeException("Open counter is negative !");
    }

    public void setLifeCycleCallback(LifeCycleCallback lifeCycleCallback) {
        this.lifeCycleCallback = lifeCycleCallback;
    }

    /* Interface */
    // --------------------------------------------------------------------------------------------
    @DebugLog
    public void open() {
        checkCounter();
        ++openCounter;
        Timber.i("Helper address: %s, open counter: %s", hashCode(), openCounter);
        if (openCounter > 1) {
            Timber.i("Database is already opened");
            return;
        }
        if (database == null || !database.isOpen()) {
            if (openOrCreateDatabase() && lifeCycleCallback != null) {
                lifeCycleCallback.onCreate();
            }
        }
        checkVersion();
    }

    @DebugLog
    public void close() {
        --openCounter;
        checkCounter();
        if (openCounter == 0) {
            Timber.i("Database to be closed");
            database.close();
        }
    }

    public boolean isOpened() {
        return openCounter > 0;
    }

    public void execSql(String sql) {
        database.execSQL(sql);
    }

    @DebugLog
    public Cursor rawQuery(String statement) {
        return database.rawQuery(statement, null);
    }

    /* Raw transaction */
    // ------------------------------------------
    public void beginTransaction() {
        database.beginTransaction();
    }

    public SQLiteStatement compileStatement(String statement) {
        return database.compileStatement(statement);
    }

    public void setTransactionSuccessful() {
        database.setTransactionSuccessful();
    }

    public void endTransaction() {
        database.endTransaction();
    }

    /* Expiration */
    // --------------------------------------------------------------------------------------------
    /**
     * Set in millis, the last time the cache was accessed.
     */
    public void setLastCacheUpdateTimeMillis(String key) {
        long currentMillis = System.currentTimeMillis();
        fileManager.writeToPreferences(context, SETTINGS_FILE_NAME, key, currentMillis);
    }

    /**
     * Get in millis, the last time the cache was accessed.
     */
    public long getLastCacheUpdateTimeMillis(String key) {
        return fileManager.getFromPreferences(context, SETTINGS_FILE_NAME, key);
    }
}
