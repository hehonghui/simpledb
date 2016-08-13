package com.simple.simpledatabase.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.simple.database.dao.AbsDAO;
import com.simple.simpledatabase.domain.Book;

/**
 * books 表的DAO对象。
 *
 * Created by mrsimple on 13/8/16.
 */
public class BookDao extends AbsDAO<Book> {

    public BookDao() {
        super("books");
    }

    @Override
    protected ContentValues convert(Book item) {
        ContentValues newValues = new ContentValues();
        newValues.put("id", item.id);
        newValues.put("name", item.name);
        return newValues;
    }


    @Override
    protected Book parseOneItem(Cursor cursor) {
        Book item = new Book();
        item.id = cursor.getString(0);
        item.name = cursor.getString(1) ;
        return item;
    }

}
