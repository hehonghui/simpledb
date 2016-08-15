package com.simple.database.crud;

import com.simple.database.crud.base.BaseSQLBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 插入数据的Builder, 不能设置参数, 直接设置对象或者对象列表即可
 * Created by mrsimple on 6/8/16.
 */
public class InsertBuilder<T> extends BaseSQLBuilder<T> {

    public InsertBuilder(Class daoClass) {
        super(daoClass);
    }

    final List<T> mItems = new ArrayList<>();

    public InsertBuilder<T>  withItem(T item) {
        mItems.add(item);
        return this;
    }

    public InsertBuilder<T>  withItems(List<T> items) {
        mItems.addAll(items);
        return this;
    }

    @Override
    public void execute() {
        mAsyncDao.insert(mItems);
    }
}
