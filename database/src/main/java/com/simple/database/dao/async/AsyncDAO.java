package com.simple.database.dao.async;

import com.simple.database.dao.AbsDAO;
import com.simple.database.listeners.DbListener;
import com.simple.database.task.DbTask;
import com.simple.database.task.NoReturnTask;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * 异步数据库操作基类, 读数据库可以使用并发,写时只能是单线程写. 使用方式为:
 *
 * @param <T> 要操作的实体类型
 */
public class AsyncDAO<T> {
    /**
     * 同步的DAO对象
     */
    protected AbsDAO<T> mDao;

    /**
     * 创建异步的DAO类
     *
     * @param daoClz 同步的DAO类型
     * @param <T>    数据类型T
     * @return 返回异步的DAO操作类型
     */
    public static <T> AsyncDAO<T> createDAO(Class daoClz) {
        try {
            AbsDAO<T> syncDao = (AbsDAO<T>) daoClz.newInstance();
            AsyncDAO<T> instance = new AsyncDAO<>();
            instance.mDao = syncDao;
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建异步的DAO类
     *
     * @param daoClz    同步的dao类型
     * @param tableName 表名
     * @param <T>       数据类型T
     * @return 返回异步的DAO操作类型
     */
    public static <T> AsyncDAO<T> createDAO(Class daoClz, String tableName) {
        try {
            Constructor constructor = daoClz.getConstructor(String.class);
            // 创建同步的dao对象
            AbsDAO<T> syncDao = (AbsDAO<T>) constructor.newInstance(tableName);
            // 创建异步的dao对象
            AsyncDAO<T> instance = new AsyncDAO<>();
            instance.mDao = syncDao;
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取具体的同步DAO对象
     *
     * @return 具体的DAO对象
     */
    public AbsDAO<T> getSyncDao() {
        return mDao;
    }

    /**
     * 插入单项数据
     *
     * @param item 要插入的数据
     */
    public void insert(final T item) {
        new NoReturnTask() {
            protected Void doInBackground() {
                mDao.insert(item);
                return null;
            }
        }.execute();
    }

    /**
     * 批量插入
     *
     * @param items 要插入的数据列表
     */
    public void insert(final List<T> items) {
        new NoReturnTask() {
            protected Void doInBackground() {
                mDao.insert(items);
                return null;
            }
        }.execute();
    }

    /**
     * 更新实体
     *
     * @param item
     */
    public void update(final T item, final String where, final String[] args) {
        new NoReturnTask() {
            protected Void doInBackground() {
                mDao.update(item, where, args);
                return null;
            }
        }.execute();
    }

    /**
     * 更新实体
     *
     * @param item
     */
    public void updateWithId(final T item, final String id) {
        new NoReturnTask() {
            protected Void doInBackground() {
                mDao.update(item, "id=?", new String[]{id});
                return null;
            }
        }.execute();
    }

    /**
     * 加载某个数据库中的所有数据
     *
     * @param listener
     */
    public void queryAll(final DbListener<List<T>> listener) {
        // 构建命令
        new DbTask<List<T>>(listener) {
            protected List<T> doInBackground() {
                return mDao.queryAll();
            }
        }.executeConcurrent();
    }

    /**
     * 加载某条数据
     *
     * @param listener
     */
    public void queryOne(final String where, final String[] args, final DbListener<T> listener) {
        // 构建命令
        new DbTask<T>(listener) {
            protected T doInBackground() {
                return mDao.queryOne(where, args);
            }
        }.executeConcurrent();
    }

    /**
     * 加载某个数据库中的所有数据
     *
     * @param listener
     */
    public void query(final String where, final String[] args, final DbListener<List<T>> listener) {
        query(where, args, null, null, listener);
    }

    /**
     * @param where
     * @param args
     * @param orderBy
     * @param limit
     * @param listener
     */
    public void query(final String where, final String[] args, final String orderBy, final String limit, final DbListener<List<T>> listener) {
        // 构建命令
        new DbTask<List<T>>(listener) {
            protected List<T> doInBackground() {
                return mDao.query(where, args, orderBy, limit);
            }
        }.executeConcurrent();
    }

    /**
     * 按照条件删除
     *
     * @param whereArgs
     */
    public void delete(final String where, final String[] whereArgs) {
        new NoReturnTask() {

            @Override
            protected Void doInBackground() {
                mDao.delete(where, whereArgs);
                return null;
            }
        }.execute();
    }

    /**
     * 删除所有数据
     */
    public void deleteAll() {
        new NoReturnTask() {
            protected Void doInBackground() {
                mDao.deleteAll();
                return null;
            }
        }.execute();
    }

    /**
     * 查询数量
     *
     * @param where
     * @param whereArgs
     * @param listener
     */
    public void count(final String where, final String[] whereArgs, final DbListener<Integer> listener) {
        new DbTask<Integer>(listener) {
            protected Integer doInBackground() {
                return mDao.count(where, whereArgs);
            }
        }.execute();
    }
}
