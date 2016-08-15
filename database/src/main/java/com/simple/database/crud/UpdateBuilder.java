package com.simple.database.crud;

import com.simple.database.crud.base.WhereBuilder;

/**
 * 更新数据的Builder, 只能设置 where 参数.
 * Created by mrsimple on 6/8/16.
 */
public class UpdateBuilder<T> extends WhereBuilder<T> {
    /**
     * 要更新的对象
     */
    private T mItem;

    public UpdateBuilder(Class daoClass) {
        super(daoClass);
    }

    public UpdateBuilder<T> withItem(T mItem) {
        this.mItem = mItem;
        return this;
    }

    @Override
    public UpdateBuilder<T> where(String selection, String[] selectionArgs) {
        super.where(selection, selectionArgs);
        return this;
    }

    public void execute() {
        mAsyncDao.update(mItem, selection, selectionArgs);
    }
}
