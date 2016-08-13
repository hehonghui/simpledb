package com.simple.database.crud;

import com.simple.database.crud.base.WhereBuilder;
import com.simple.database.listeners.DbListener;

/**
 * 查询数量的Builder, 可以设置 where 参数
 * Created by mrsimple on 6/8/16.
 */
public class CountBuilder extends WhereBuilder<Integer> {

    private DbListener<Integer> mCountListener ;

    public CountBuilder(Class daoClass) {
        super(daoClass);
    }

    /**
     * 查询单个数据的listener
     *
     * @param listener
     * @return
     */
    public CountBuilder listener(DbListener<Integer> listener) {
        this.mCountListener = listener;
        return this;
    }


    @Override
    public void execute() {
        mAsyncDao.count(selection, selectionArgs, mCountListener);
    }
}
