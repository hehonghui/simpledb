package com.simple.database.cursor;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

/**
 * Created by mrsimple on 19/4/16.
 */
public class CloseCursorFactory implements SQLiteDatabase.CursorFactory {

    @Override
    public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
        /**
         * {@link SQLiteDatabase} and {@link android.database.sqlite.SQLiteDirectCursorDriver}
         */
        return new AutoCloseCursor(new SQLiteCursor(masterQuery, editTable, query));
    }
}
