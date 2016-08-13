package com.simple.simpledatabase.model;

import com.simple.database.listeners.DbListener;
import com.simple.simpledatabase.dao.BookDao;
import com.simple.simpledatabase.domain.Book;

import java.util.List;

import static com.simple.database.DatabaseHelper.deleteFrom;
import static com.simple.database.DatabaseHelper.insertInto;
import static com.simple.database.DatabaseHelper.selectFrom;
import static com.simple.database.DatabaseHelper.updateFrom;

/**
 * 接口使用
 * Created by mrsimple on 13/8/16.
 */
public class BookModel {
    /**
     * 插入数据
     *
     * @param aBook
     */
    public void insertBook(Book aBook) {
        insertInto(BookDao.class).withItem(aBook).execute();
    }

    /**
     * 删除图书
     *
     * @param aBook
     */
    public void deleteBook(Book aBook) {
        // 根据Id 删除图书
        deleteFrom(BookDao.class).where("id=?", new String[]{aBook.id}).execute();
    }


    public void updateBook(Book aBook) {
        // 更新数据
        updateFrom(BookDao.class).withItem(aBook).where("id=?", new String[]{aBook.id});
    }

    /**
     * 查询所有书籍
     *
     * @param listener
     */
    public void queryAllBook(DbListener<List<Book>> listener) {
        selectFrom(BookDao.class).listener(listener).execute();
    }

    /**
     * 按条件查询
     *
     * @param selection
     * @param args
     * @param listener
     */
    public void queryBook(String selection, String[] args, DbListener<List<Book>> listener) {
        selectFrom(BookDao.class).where(selection, args).listener(listener).execute();
    }
}
