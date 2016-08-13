package com.simple.simpledatabase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.simple.database.Builder;
import com.simple.database.listeners.DbListener;
import com.simple.simpledatabase.dao.BookDao;
import com.simple.simpledatabase.dao.BorrowDao;
import com.simple.simpledatabase.dao.UserDao;
import com.simple.simpledatabase.domain.Book;
import com.simple.simpledatabase.domain.BorrowRecord;
import com.simple.simpledatabase.domain.User;

import java.util.ArrayList;
import java.util.List;

import static com.simple.database.DatabaseHelper.insertInto;
import static com.simple.database.DatabaseHelper.selectFrom;

/**
 * 演示 数据的增删改查. 请查看 {@link com.simple.simpledatabase.model.BookModel} 类
 */
public class MainActivity extends AppCompatActivity {
    private List<User> mAllUsers;
    private List<Book> mAllBooks;
    private int mBorrowedIndex = 0;
    private TextView mLogTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 初始化数据库
        initDatabase();
        initWidgets();
        mAllUsers = createUser();
        mAllBooks = createBooks();

        // 插入数据, 用户和数据
        insertInto(UserDao.class).withItems(mAllUsers).execute();
        insertInto(BookDao.class).withItems(mAllBooks).execute();
    }

    /**
     * 初始化数据库
     */
    private void initDatabase() {
        new Builder(getApplicationContext())
                .setDbName("demo.db")                       // 数据库名为 demo.db
                .setDbVersion(1)                            // 数据库版本号为1
                .setCreateSqlFile("db/create.sql")          // 创建数据库表的sql文件在 asserts/db/create.sql文件中
                .setUpgradePath("db/migrations")            // 数据库升级文件在 asserts/db/migrations 目录中
                .create();
    }

    private void initWidgets() {
        // 借书
        findViewById(R.id.borrow_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertBorrowRecord();
            }
        });

        // 查询用户0的借阅记录
        findViewById(R.id.borrow_record_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 从 BorrowDao 中查询所有借阅记录
                selectFrom(BorrowDao.class).listener(new DbListener<List<BorrowRecord>>() {
                    @Override
                    public void onComplete(List<BorrowRecord> result) {
                        showBorrowRecord(result);
                    }
                }).execute();
            }
        });

        // 查询用户0的借阅记录
        findViewById(R.id.user0_record_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 单项查询, 这里直接从 UserDao 中查询到的用户信息, UserDao 中又会从 borrow 表中查询到该用户借阅的书籍
                selectFrom(UserDao.class).where("id=?", new String[]{"user-0"}).queryOne(new DbListener<User>() {
                    @Override
                    public void onComplete(User result) {
                        showBorrowBooks(result.name, result.borrowedBooks);
                    }
                });
            }
        });

        mLogTv = (TextView) findViewById(R.id.log_tv);
    }

    /**
     * 为用户0增加借阅书籍记录, 每次借阅不同的书籍, 直到借满
     */
    private void insertBorrowRecord() {
        if (mBorrowedIndex >= mAllBooks.size() - 1) {
            Toast.makeText(MainActivity.this, "借满啦", Toast.LENGTH_SHORT).show();
            return;
        }
        BorrowRecord record = new BorrowRecord();
        // 用户0借阅书籍
        record.userId = mAllUsers.get(0).id;
        record.bookId = mAllBooks.get(mBorrowedIndex).id;
        // 插入数据
        insertInto(BorrowDao.class).withItem(record).execute();
        // 借了一本书之后索引加1
        mBorrowedIndex++;
    }

    /**
     * 显示借阅记录
     *
     * @param result
     */
    private void showBorrowRecord(List<BorrowRecord> result) {
        mLogTv.setText("");
        for (BorrowRecord record : result) {
            mLogTv.append(record.userId);
            mLogTv.append("  -->  ");
            mLogTv.append(record.bookId);
            mLogTv.append("\n");
        }
    }

    /**
     * 显示用户0借阅的书籍信息
     *
     * @param result
     */
    private void showBorrowBooks(String userName, List<Book> result) {
        mLogTv.setText(userName + "借阅书籍: \n\n");
        for (Book record : result) {
            mLogTv.append(record.id);
            mLogTv.append(" --> ");
            mLogTv.append(record.name);
            mLogTv.append("\n");
        }
    }

    private List<User> createUser() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User aUser = new User();
            aUser.id = "user-" + i;
            aUser.name = "username-" + i;
            aUser.gender = i % 2;
            users.add(aUser);
        }
        return users;
    }

    private List<Book> createBooks() {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Book aBook = new Book();
            aBook.id = "book-" + i;
            aBook.name = "bookname-" + i;
            books.add(aBook);
        }
        return books;
    }

}
