package com.orcchg.data.source.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.IntDef;
import android.support.v4.util.ArrayMap;
import android.util.LongSparseArray;

import com.orcchg.data.source.local.base.ISchema;

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
    private ArrayMap<String, WeakReference<ISchema>> schemaRefs;
    private LongSparseArray<WeakReference<LifeCycleCallback>> lifeCycleCallbackRefs;
    private int oldVersion;
    private int openCounter;
    private boolean alreadyCreatedAllScheme;

    @Inject
    DatabaseHelper(Context context, FileManager fileManager) {
        this.context = context;
        this.fileManager = fileManager;
        this.databaseFile = context.getDatabasePath(DATABASE_NAME);
        this.schemaRefs = new ArrayMap<>();
        this.lifeCycleCallbackRefs = new LongSparseArray<>();
    }

    /* Schema */
    // ------------------------------------------
    @DebugLog
    public void addSchema(ISchema schema) {
        WeakReference<ISchema> oldValue = schemaRefs.put(schema.getId(), new WeakReference<>(schema));
        if (oldValue == null && alreadyCreatedAllScheme) {
            Timber.i("Creating a new schema in already configured database...");
            schema.createSchema();
        }
    }

    @DebugLog
    public void removeSchema(ISchema schema) {
        schemaRefs.remove(schema.getId());
    }

    /* Lifecycle */
    // --------------------------------------------------------------------------------------------
    private static final int LCC_CREATE = 0;
    private static final int LCC_UPGRADE = 1;
    private static final int LCC_DOWNGRADE = 2;
    private static final int LCC_OPEN = 3;
    private static final int LCC_CLOSE = 4;
    @IntDef({LCC_CREATE, LCC_UPGRADE, LCC_DOWNGRADE, LCC_OPEN, LCC_CLOSE})
    @Retention(RetentionPolicy.SOURCE)
    private @interface LccEvent {}

    public interface LifeCycleCallback {
        void onCreate();
        void onUpgrade();
        void onDowngrade();
        void onOpen();
        void onClose();
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
    private void notifyOpen() {
        notifyLifecycle(LCC_OPEN);
    }

    @DebugLog
    private void notifyClose() {
        notifyLifecycle(LCC_CLOSE);
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
                    case LCC_OPEN:       lcc.onOpen();       break;
                    case LCC_CLOSE:      lcc.onClose();      break;
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
            if (openOrCreateDatabase()) {  // fresh free database
                performCreateDatabaseSchema();  // create all tables according to schemes
                notifyCreate();
            } else {
                alreadyCreatedAllScheme = true;
                notifyOpen();
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
            notifyClose();
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

    /* Internal */
    // --------------------------------------------------------------------------------------------
    @DebugLog
    private boolean openOrCreateDatabase() {
        boolean isNewDb = !databaseFile.exists();
        database = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
        oldVersion = database.getVersion();
        database.setVersion(DATABASE_VERSION);
        return isNewDb;
    }

    @DebugLog
    private void performCreateDatabaseSchema() {
        for (int i = 0; i < schemaRefs.size(); ++i) {
            String key = schemaRefs.keyAt(i);
            WeakReference<ISchema> schemaRef = schemaRefs.get(key);
            ISchema schema = schemaRef.get();
            if (schema != null) {
                schema.createSchema();
            } else {
                Timber.w("Schema has already been GC'ed");
            }
        }
        alreadyCreatedAllScheme = true;
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
}
