package com.orcchg.data.source.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.IntDef;
import android.util.LongSparseArray;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

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
    private LongSparseArray<WeakReference<LifeCycleCallback>> lifeCycleCallbackRefs;
    private int oldVersion;
    private int openCounter;

    @Inject
    DatabaseHelper(Context context, FileManager fileManager) {
        this.context = context;
        this.fileManager = fileManager;
        this.databaseFile = context.getDatabasePath(DATABASE_NAME);
        this.lifeCycleCallbackRefs = new LongSparseArray<>();
    }

    /* Lifecycle */
    // ------------------------------------------
    private static final int LCC_CREATE = 0;
    private static final int LCC_UPGRADE = 1;
    private static final int LCC_DOWNGRADE = 2;
    private static final int LCC_DESTROY = 3;
    @IntDef({LCC_CREATE, LCC_UPGRADE, LCC_DOWNGRADE, LCC_DESTROY})
    @Retention(RetentionPolicy.SOURCE)
    private @interface LccEvent {}

    public interface LifeCycleCallback {
        void onCreate();
        void onUpgrade();
        void onDowngrade();
        void onDestroy();
    }

    @DebugLog
    private boolean openOrCreateDatabase() {
        boolean isNewDb = !databaseFile.exists();
        database = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
        oldVersion = database.getVersion();
        database.setVersion(DATABASE_VERSION);
        return isNewDb;
    }

    @DebugLog
    private void checkVersion() {
        int oldVersion = this.oldVersion;
        Timber.i("Database version: %s", oldVersion);
        this.oldVersion = DATABASE_VERSION;  // protect from recursion
        if (oldVersion <= 1) {
            Timber.i("Skip upgrade-downgrade event for the very first version");
            return;
        }
        if (oldVersion < DATABASE_VERSION) {
            notifyUpgrade();
        } else if (oldVersion > DATABASE_VERSION) {
            notifyDowngrade();
        }
    }

    private void checkCounter() {
        if (openCounter < 0) throw new RuntimeException("Open counter is negative !");
    }

    @DebugLog
    public void addLifeCycleCallback(LifeCycleCallback lifeCycleCallback) {
        lifeCycleCallbackRefs.put(lifeCycleCallback.hashCode(), new WeakReference<>(lifeCycleCallback));
    }

    @DebugLog
    public void removeLifeCycleCallback(LifeCycleCallback lifeCycleCallback) {
        lifeCycleCallbackRefs.delete(lifeCycleCallback.hashCode());
    }

    @DebugLog
    private void notifyCreate() {
        notifyLifecycle(LCC_CREATE);
    }

    @DebugLog
    private void notifyUpgrade() {
        notifyLifecycle(LCC_UPGRADE);
    }

    @DebugLog
    private void notifyDowngrade() {
        notifyLifecycle(LCC_DOWNGRADE);
    }

    @DebugLog
    private void notifyDestroy() {
        notifyLifecycle(LCC_DESTROY);
    }

    @DebugLog
    private void notifyLifecycle(@LccEvent int event) {
        for (int i = 0; i < lifeCycleCallbackRefs.size(); ++i) {
            long key = lifeCycleCallbackRefs.keyAt(i);
            WeakReference<LifeCycleCallback> lccRef = lifeCycleCallbackRefs.get(key);
            LifeCycleCallback lcc = lccRef.get();
            if (lcc != null) {
                switch (event) {
                    case LCC_CREATE:     lcc.onCreate();     break;
                    case LCC_UPGRADE:    lcc.onUpgrade();    break;
                    case LCC_DOWNGRADE:  lcc.onDowngrade();  break;
                    case LCC_DESTROY:    lcc.onDestroy();    break;
                    default:             // no-op
                }
            } else {
                Timber.w("Life-cycle callback has already been GC'ed");
            }
        }
    }

    /* Interface */
    // --------------------------------------------------------------------------------------------
    @DebugLog
    synchronized public void open() {
        checkCounter();
        ++openCounter;
        Timber.i("Helper address: %s, open counter: %s", hashCode(), openCounter);
        if (openCounter > 1) {
            Timber.i("Database is already opened");
            return;
        }
        if (database == null || !database.isOpen()) {
            if (openOrCreateDatabase()) {
                notifyCreate();
            }
        }
        checkVersion();
    }

    @DebugLog
    synchronized public void close() {
        --openCounter;
        checkCounter();
        if (openCounter == 0) {
            Timber.i("Database to be closed");
            notifyDestroy();
            database.close();
        }
    }

    @DebugLog
    public boolean isOpened() {
        return openCounter > 0;
    }

    @DebugLog
    synchronized public void execSql(String sql) {
        database.execSQL(sql);
    }

    @DebugLog
    synchronized public Cursor rawQuery(String statement) {
        return database.rawQuery(statement, null);
    }

    /* Raw transaction */
    // ------------------------------------------
    @DebugLog
    synchronized public void beginTransaction() {
        database.beginTransaction();
    }

    @DebugLog
    synchronized public SQLiteStatement compileStatement(String statement) {
        return database.compileStatement(statement);
    }

    @DebugLog
    synchronized public void setTransactionSuccessful() {
        database.setTransactionSuccessful();
    }

    @DebugLog
    synchronized public void endTransaction() {
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
