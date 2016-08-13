/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.simple.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.simple.database.crud.CountBuilder;
import com.simple.database.crud.DeleteBuilder;
import com.simple.database.crud.InsertBuilder;
import com.simple.database.crud.QueryBuilder;
import com.simple.database.crud.UpdateBuilder;
import com.simple.database.cursor.CloseCursorFactory;
import com.simple.database.dao.AbsDAO;
import com.simple.database.listeners.DbListener;
import com.simple.database.upgrade.DbUpgradeHelper;
import com.simple.database.upgrade.SqlParser;
import com.simple.database.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 文章基本信息、文章内容的数据库操作类,包含Article、ArticleDetail的存储、删除操作。
 *
 * @author mrsimple
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private volatile SQLiteDatabase mWritableDb;
    private volatile SQLiteDatabase mReadableDb;
    private static DatabaseHelper sDatabaseHelper;
    private DbUpgradeHelper mUpgradeHelper;
    private Builder mBuilder;
    private volatile boolean isWALEnable = false;

    DatabaseHelper(Builder builder) {
        // 指定了 CursorFactory, 用于确保 Cursor对象被正确关闭
        super(builder.context, builder.dbName, new CloseCursorFactory(), builder.dbVersion);
        mBuilder = builder;
        sDatabaseHelper = this;
        mWritableDb = getWritableDatabase();
        mReadableDb = getReadableDatabase();
        isWALEnable = mWritableDb.enableWriteAheadLogging();
    }

    /**
     * 获取数据库操作对象
     *
     * @return
     */
    public static DatabaseHelper getInstance() {
        if (sDatabaseHelper == null) {
            throw new NullPointerException("sDatabaseHelper is null,please call init method first.");
        }
        return sDatabaseHelper;
    }

    public Builder getBuilder() {
        return mBuilder;
    }

    /**
     * database对象在异步进行初始化,该函数会导致调用线程block
     *
     * @return
     */
    @Override
    public SQLiteDatabase getWritableDatabase() {
        if (mWritableDb == null) {
            mWritableDb = super.getWritableDatabase();
        }
        return mWritableDb;
    }

    /**
     * database对象在异步进行初始化,该函数会导致调用线程block
     *
     * @return
     */
    @Override
    public SQLiteDatabase getReadableDatabase() {
        if (mReadableDb == null) {
            mReadableDb = super.getReadableDatabase();
        }
        return mReadableDb;
    }

    /**
     * 创建数据库
     *
     * @param db 数据库
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 执行创建数据库表的sql语句
        executeSqlScript(mBuilder.getContext(), db, mBuilder.getCreateSqlFile());
    }

    /**
     * 数据库升级
     *
     * @param db         数据库对象
     * @param oldVersion 旧的数据库版本
     * @param newVersion 新的版本
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (mUpgradeHelper == null) {
            mUpgradeHelper = new DbUpgradeHelper(mBuilder.getContext(), mBuilder.getUpgradePath());
        }
        mUpgradeHelper.upgrade(db, oldVersion, newVersion);
    }

    public boolean isWALEnable() {
        return isWALEnable;
    }

    public void shutdown() {
        close();
    }

    /**
     * 清空所有表数据
     */
    public void cleanAllTables() {
        executeSqlScript(mBuilder.getContext(), getWritableDatabase(), mBuilder.getCleanSqlFile());
    }


    /**
     * 执行sql文件中的升级SQL语句
     *
     * @param db   数据库对象
     * @param file sql脚本文件 [ 完成路径 ]
     */
    public static void executeSqlScript(Context context, SQLiteDatabase db, String file) {
        InputStream stream = null;
        try {
            stream = context.getAssets().open(file);
            executeDelimitedSqlScript(db, stream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeSilently(stream);
        }
    }

    /**
     * 解析SQL语句并且执行
     *
     * @param db
     * @param stream
     * @throws IOException
     */
    private static void executeDelimitedSqlScript(SQLiteDatabase db, InputStream stream) throws IOException {
        List<String> commands = SqlParser.parse(stream);
        for (String command : commands) {
            db.execSQL(command);
        }
    }


    // ==================== 下面的几个静态函数都是非类型安全的,使用时需要自行注意类型问题 ===================

    /**
     * select 语句
     *
     * @param <T>
     * @return
     */
    public static <T> QueryBuilder<T> selectFrom(Class<? extends AbsDAO<T>> daoClass) {
        return new QueryBuilder<T>(daoClass).listener(new DbListener<List<T>>() {
            @Override
            public void onComplete(List<T> result) {

            }
        });
    }

    /**
     * 插入数据
     *
     * @param daoClass
     * @param <T>
     * @return
     */
    public static <T> InsertBuilder<T> insertInto(Class<? extends AbsDAO<T>> daoClass) {
        return new InsertBuilder<T>(daoClass);
    }

    /**
     * update 语句
     *
     * @param <T>
     * @return
     */
    public static <T> UpdateBuilder<T> updateFrom(Class<? extends AbsDAO<T>> daoClass) {
        return new UpdateBuilder<>(daoClass);
    }

    /**
     * delete 语句
     *
     * @param <T>
     * @return
     */
    public static <T> DeleteBuilder<T> deleteFrom(Class<? extends AbsDAO<T>> daoClass) {
        return new DeleteBuilder<>(daoClass);
    }

    /**
     * 查询数据的数量
     *
     * @param daoClass
     * @return
     */
    public static CountBuilder countFrom(Class daoClass) {
        return new CountBuilder(daoClass);
    }
}

