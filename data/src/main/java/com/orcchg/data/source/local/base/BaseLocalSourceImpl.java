package com.orcchg.data.source.local.base;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.Nullable;

import com.orcchg.data.source.local.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseLocalSourceImpl implements DatabaseHelper.LifeCycleCallback {

    protected interface ProcessCursor<Result> {
        Result process(Cursor cursor);
    }

    protected final DatabaseHelper database;

    protected BaseLocalSourceImpl(DatabaseHelper database) {
        this.database = database;
        this.database.setLifeCycleCallback(this);
    }

    /* Execution */
    // ------------------------------------------
    protected void executeStatementIgnoreResult(String statement) {
        database.open();
        database.beginTransaction();
        SQLiteStatement object = database.compileStatement(statement);
        object.execute();
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }

    protected boolean checkStatement(String statement) {
        Boolean result = performStatement(statement, (cursor) -> (cursor.getInt(0) != 0));
        return result != null ? result : false;
    }

    protected int intStatement(String statement) {
        Integer result = performStatement(statement, (cursor) -> (cursor.getInt(0)));
        return result != null ? result : 0;
    }

    @Nullable
    protected <Result> Result performStatement(String statement, ProcessCursor<Result> cursorProcessor) {
        Result result = null;
        database.open();
        Cursor cursor = database.rawQuery(statement);
        if (cursor.moveToFirst()) {
            result = cursorProcessor.process(cursor);
        }
        cursor.close();
        database.close();
        return result;
    }

    @Nullable
    protected <Result> List<Result> performLoopStatement(String statement, ProcessCursor<Result> cursorProcessor) {
        database.open();
        Cursor cursor = database.rawQuery(statement);
        List<Result> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Result result = cursorProcessor.process(cursor);
            list.add(result);
        }
        cursor.close();
        database.close();
        return list;
    }
}
