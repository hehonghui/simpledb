package com.simple.simpledatabase.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.simple.database.dao.AbsDAO;
import com.simple.simpledatabase.domain.Book;
import com.simple.simpledatabase.domain.BorrowRecord;
import com.simple.simpledatabase.domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * users表的DAO对象。
 *
 * Created by mrsimple on 13/8/16.
 */
public class UserDao extends AbsDAO<User> {

    private BorrowDao mBorrowDao = new BorrowDao();

    public UserDao() {
        super("users");
    }

    @Override
    public void insert(User item) {
        super.insert(item);
        // 插入借阅表
        insertBorrowedBooks(item);
    }

    private void insertBorrowedBooks(User item) {
        if (item.borrowedBooks != null) {
            BorrowRecord record = new BorrowRecord();
            record.userId = item.id;
            for (Book aBook : item.borrowedBooks) {
                record.bookId = aBook.id;
                mBorrowDao.insert(record);
            }
        }
    }

    @Override
    protected ContentValues convert(User item) {
        ContentValues newValues = new ContentValues();
        newValues.put("id", item.id);
        newValues.put("name", item.name);
        newValues.put("gender", item.gender);
        return newValues;
    }

    @Override
    protected User parseOneItem(Cursor cursor) {
        User user = new User();
        user.id = cursor.getString(0);
        user.name = cursor.getString(1);
        user.gender = cursor.getInt(2);
        // 查询该用户借阅的书
        queryBorrowedBooks(user);
        return user;
    }

    private void queryBorrowedBooks(User user) {
        List<BorrowRecord> records = mBorrowDao.query("user_id=?", new String[]{user.id});
        if (records.size() == 0) {
            return;
        }
        // 查询到该用户借阅的书籍
        user.borrowedBooks = new ArrayList<>();
        final BookDao bookDao = new BookDao();
        for (BorrowRecord aRecord : records) {
            Book aBook = bookDao.queryOne("id=?", new String[]{aRecord.bookId});
            user.borrowedBooks.add(aBook);
        }
    }
}
