package com.simple.database.crud;

import com.simple.database.crud.base.WhereBuilder;
import com.simple.database.listeners.DbListener;

import java.util.List;

/**
 * @param <T> 要返回的数据类型
 */
public class QueryBuilder<T> extends WhereBuilder {
    private String orderBy = null;
    private String limit = null;
    private DbListener<List<T>> mDbListListener;

    public QueryBuilder(Class daoClass) {
        super(daoClass);
    }

    /**
     * where 查询条件
     *
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public QueryBuilder<T> where(String selection, String[] selectionArgs) {
        super.where(selection, selectionArgs);
        return this;
    }

    public QueryBuilder<T> orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public QueryBuilder<T> limit(String limit) {
        this.limit = limit;
        return this;
    }

    /**
     * 返回的数据是 列表类型的Listener
     *
     * @param listener
     * @return
     */
    public QueryBuilder<T> listener(DbListener<List<T>> listener) {
        this.mDbListListener = listener;
        return this;
    }

    /**
     * 只需要单个数据
     */
    public void queryOne(final DbListener<T> listener) {
        mAsyncDao.query(selection, selectionArgs, orderBy, limit, new DbListener<List<T>>() {
            @Override
            public void onComplete(List<T> result) {
                if (result != null && result.size() > 0 && listener != null) {
                    listener.onComplete(result.get(0));
                }
            }
        });
    }

    /**
     * 查询列表数据
     */
    @Override
    public void execute() {
        mAsyncDao.query(selection, selectionArgs, orderBy, limit, mDbListListener);
    }
}
