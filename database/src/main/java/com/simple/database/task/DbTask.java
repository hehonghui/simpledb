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

package com.simple.database.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import com.simple.database.DatabaseHelper;
import com.simple.database.listeners.DbListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 数据库操作抽象类,该对象提交给线程池进行异步执行
 *
 * @param <T> 返回的数据类型
 */
public abstract class DbTask<T> {

    /**
     * 单线程的数据库线程池
     */
    private static final ExecutorService SERIAL_EXECUTOR = Executors.newSingleThreadExecutor();
    /**
     * 并发线程池的数据库线程池,在启动了WAL模式之后使用
     */
    private static final ExecutorService CONCURRENT_EXECUTOR = Executors.newFixedThreadPool(2);
    /**
     * 关联UI线程消息队列的Handler
     */
    private final static Handler mUIHandler = new Handler(Looper.getMainLooper());
    /**
     * 数据回调
     */
    public DbListener<T> dbListener;

    public DbTask() {
    }

    /**
     * 含有数据回调的数据库命令
     *
     * @param listener
     */
    public DbTask(DbListener<T> listener) {
        dbListener = listener;
    }

    /**
     * 提交到单线程的线程池
     */
    public void execute() {
        SERIAL_EXECUTOR.submit(mDbRunnable);
    }

    /**
     * 提交到指定的引擎中, 在WAL模式下并发执行
     */
    public void executeConcurrent() {
        // 判断WAL是否启动成功
        if (DatabaseHelper.getInstance().isWALEnable()) {
            // 提交到含有2个线程的线程池
            CONCURRENT_EXECUTOR.submit(mDbRunnable);
        } else {
            // 提交请求到单线程池
            SERIAL_EXECUTOR.submit(mDbRunnable);
        }
    }

    /**
     * 数据库异步runnable
     */
    private Runnable mDbRunnable = new Runnable() {
        @Override
        public void run() {
            // 执行数据库操作
            final T result = doSqlAction();
            if (dbListener != null) {
                // 执行数据库操作请求,将结果投递到UI线程
                postResultToUiThread(result, dbListener);
            }
        }
    };

    /**
     * 执行数据库请求的模板方法,在该函数中开启事务
     *
     * @return
     */
    private final T doSqlAction() {
        SQLiteDatabase database = DatabaseHelper.getInstance().getWritableDatabase();
        T result = null;
        try {
            if (DatabaseHelper.getInstance().isWALEnable()) {
                database.beginTransactionNonExclusive();
            } else {
                database.beginTransaction();
            }
            result = doInBackground();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
        return result;
    }

    /**
     * 在后台执行数据库操作,并且返回结果,如果dataListener不会空,则将返回的结果通过回调执行在UI线程
     *
     * @return
     */
    protected abstract T doInBackground();

    /**
     * 将结果投递到UI线程
     *
     * @param result   数据库返回的结果
     * @param listener 结果监听器
     */
    public <T> void postResultToUiThread(final T result, final DbListener<T> listener) {
        if (listener == null) {
            return;
        }
        mUIHandler.post(new Runnable() {

            @Override
            public void run() {
                listener.onComplete(result);
            }
        });
    }

}
