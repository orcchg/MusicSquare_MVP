package com.orcchg.musicsquare.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SampleDb extends SQLiteOpenHelper {

    public SampleDb(Context context) {
        super(context, "MSQSample.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void check() {
        getWritableDatabase();
    }
}
