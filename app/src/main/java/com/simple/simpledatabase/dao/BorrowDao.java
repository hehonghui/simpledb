package com.simple.simpledatabase.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.simple.database.dao.AbsDAO;
import com.simple.simpledatabase.domain.BorrowRecord;

/**
 * borrow 表的DAO对象。
 * Created by mrsimple on 13/8/16.
 */
public class BorrowDao extends AbsDAO<BorrowRecord> {

    public BorrowDao() {
        super("borrow");
    }

    @Override
    protected ContentValues convert(BorrowRecord item) {
        ContentValues newValues = new ContentValues();
        newValues.put("user_id", item.userId);
        newValues.put("book_id", item.bookId);
        return newValues;
    }

    @Override
    protected BorrowRecord parseOneItem(Cursor cursor) {
        BorrowRecord item = new BorrowRecord();
        item.userId = cursor.getString(0);
        item.bookId = cursor.getString(1);
        return item;
    }

}
