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

package com.simple.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.simple.database.DatabaseHelper;
import com.simple.database.utils.IOUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数据库操作接口, 所有操作均为同步操作. 异步的数据库操作类需要使用 {@link com.simple.database.dao.async.AsyncDAO}.
 * <p>
 * 子类需要覆写 {@link #convert(Object)} 和 {@link #parseResult(Cursor)} 函数实现对象的存、取.
 * <p>
 * 注意 : 操作查询相关的功能要确保Cursor对象正确的关闭!!! 避免Cursor泄露
 *
 * @param <T>
 * @author mrsimple
 */
public abstract class AbsDAO<T> {
    /**
     * 表名
     */
    protected String mTableName;
    /**
     * 数据库对象
     */
    protected SQLiteDatabase mWritableDatabase;
    /**
     * 数据库对象
     */
    protected SQLiteDatabase mReadableDatabase;
    /**
     * 插入冲突时的处理策略
     */
    protected int mConflictAlgorithm = SQLiteDatabase.CONFLICT_REPLACE;
    /**
     * 是否需要去重
     */
    private boolean removeRedundantItems = false;

    public AbsDAO(String table) {
        mTableName = table;
        mWritableDatabase = DatabaseHelper.getInstance().getWritableDatabase();
        mReadableDatabase = DatabaseHelper.getInstance().getReadableDatabase();
    }

    /**
     * 保存数据到数据库 [ 注意这里 没有直接调用saveItem是为了实现多个插入只开启一次事物 ]
     *
     * @param datas 要存储的数据列表
     */
    public void insert(final List<T> datas) {
        if (datas == null) {
            return;
        }
        for (T item : datas) {
            insert(item);
        }
    }

    /**
     * 保存数据
     *
     * @param item 要插入的对象
     */
    public void insert(final T item) {
        if (isInvalid(item)) {
            return;
        }
        mWritableDatabase.insertWithOnConflict(mTableName, null, convert(item), mConflictAlgorithm);
    }

    /**
     * 是否是无效 item
     *
     * @param item 要处理的对象
     * @return 该对象是否是有效值
     */
    protected boolean isInvalid(T item) {
        return item == null;
    }

    /**
     * 将实体类转为ContentValues
     *
     * @param item 要存储的item
     * @return
     */
    protected abstract ContentValues convert(T item);

    /**
     * 从Cursor中解析数据, 这一步相当于 {@link #convert}的逆向过程
     *
     * @param cursor Cursor对象
     * @return
     */
    protected List<T> parseResult(Cursor cursor) {
        List<T> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            T item = parseOneItem(cursor);
            if (item != null) {
                // 去重,防止在list中含有相同的item
                if (removeRedundantItems && !result.contains(item)) {
                    result.add(item);
                } else {
                    result.add(item);
                }
            }
        }
        return result;
    }

    /**
     * 从Cursor 解析单条数据
     *
     * @param cursor Cursor对象
     * @return 返回单个结果
     */
    protected abstract T parseOneItem(Cursor cursor);

    /**
     * 获取表中的记录数量
     *
     * @return 数据表中的数据
     */
    public int count() {
        return count("", null);
    }

    /**
     * 获取符合条件的表中记录数量
     *
     * @param where where 语句
     * @param args  where 参数
     * @return 数据表中的数据
     */
    public int count(String where, String[] args) {
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = mReadableDatabase.rawQuery("select * from " + mTableName + " " + where, args);
            count = cursor.getCount();
        } finally {
            closeCursor(cursor);
        }
        return count;
    }

    /**
     * 查询单个数据
     *
     * @param where where 语句
     * @param args  where 参数
     * @return 返回查询到的单个数据
     */
    public T queryOne(String where, String[] args) {
        List<T> result = query(where, args);
        return result.size() > 0 ? result.get(0) : null;
    }

    /**
     * 加载所有数据
     *
     * @return
     */
    public List<T> queryAll() {
        return query(null, null);
    }

    /**
     * 带参数的查询功能
     *
     * @param where where 语句
     * @param args  where 参数
     * @return
     */
    public List<T> query(String where, String[] args) {
        return query(where, args, null, null);
    }

    /**
     * 带参数的查询功能
     *
     * @param where   where 语句
     * @param args    where 参数
     * @param orderBy order by 参数
     * @param limit   limit 参数
     * @return
     */
    public List<T> query(String where, String[] args, String orderBy, String limit) {
        Cursor cursor = null;
        List<T> result = Collections.EMPTY_LIST;
        try {
            cursor = mReadableDatabase.query(mTableName, null, where, args, null, null, orderBy, limit);
            result = parseResult(cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return result;
    }


    /**
     * 更新实体
     *
     * @param item  要更新的对象
     * @param where where 语句
     * @param args  where 参数
     */
    public void update(T item, String where, String[] args) {
        mWritableDatabase.update(mTableName, convert(item), where, args);
    }

    /**
     * 删除符合特定条件的数据
     *
     * @param whereClause where 语句
     * @param whereArgs   where 参数
     */
    public void delete(String whereClause, String[] whereArgs) {
        mWritableDatabase.delete(mTableName, whereClause, whereArgs);
    }

    /**
     * 清空表
     */
    public void deleteAll() {
        delete(null, null);
    }

    /**
     * 设置插入时发生冲突的解决方法
     *
     * @param algorithm 冲突时的处理策略 .  参数为 {@link SQLiteDatabase#CONFLICT_REPLACE} 、{@link SQLiteDatabase#CONFLICT_IGNORE} 等
     */
    public void setConflictAlgorithm(int algorithm) {
        this.mConflictAlgorithm = algorithm;
    }

    /**
     * 设置是否移除重复的Item
     *
     * @param remove 是否移除重复的数据
     */
    public void setRemoveRedundantItems(boolean remove) {
        this.removeRedundantItems = remove;
    }

    protected void closeCursor(Cursor cursor) {
        IOUtils.closeCursor(cursor);
    }
}
