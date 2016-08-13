package com.simple.database.cursor;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteCursor;
import android.util.Log;

/**
 * 确保 Cursor对象会被正确的关闭
 * Created by mrsimple on 19/4/16.
 */
public class AutoCloseCursor extends CursorWrapper {

    public AutoCloseCursor(Cursor cursor) {
        super(cursor);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (!getWrappedCursor().isClosed()) {
                showLeakInfo((SQLiteCursor) getWrappedCursor());
                close();
            }
        } finally {
            super.finalize();
        }
    }

    private void showLeakInfo(SQLiteCursor cursor) {
        Log.e("", "### ==========>  Cursor泄露了, Cursor中的字段为 : ");
        String[] columnNames = cursor.getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            Log.e("", "### column -> " + columnNames[i]);
        }
        Log.e("", "### ==========>Cursor 泄露 !!!!!! END ");
    }
}
