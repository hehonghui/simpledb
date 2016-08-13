package com.simple.database.crud.base;

import com.simple.database.dao.async.AsyncDAO;

import java.util.HashMap;
import java.util.Map;

/**
 * 构建查询条件的基础类.
 * Created by mrsimple on 6/8/16.
 */
public abstract class BaseSQLBuilder<T> implements Executable {
    protected AsyncDAO<T> mAsyncDao;
    // 缓存异步的DAO类,避免重复创建与销毁
    private static final Map<String, AsyncDAO> DAO_CACHE = new HashMap<>();

    /**
     * @param daoClass 同步的dao类,用于构建异步的dao类
     */
    public BaseSQLBuilder(Class daoClass) {
        String syncDaoClzName = daoClass.getName();
        // 先检测缓存
        mAsyncDao = DAO_CACHE.get(syncDaoClzName);
        if (mAsyncDao == null) {
            mAsyncDao = AsyncDAO.createDAO(daoClass);
        }
        if (mAsyncDao == null) {
            throw new NullPointerException("Async DAO is null !!!");
        } else {
            // 缓存dao类型
            DAO_CACHE.put(syncDaoClzName, mAsyncDao);
        }
    }
}
