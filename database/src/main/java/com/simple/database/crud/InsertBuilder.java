package com.simple.database.crud;

import com.simple.database.crud.base.BaseSQLBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrsimple on 6/8/16.
 */
public class InsertBuilder<T> extends BaseSQLBuilder<T> {

    public InsertBuilder(Class daoClass) {
        super(daoClass);
    }

    final List<T> mItems = new ArrayList<>();

    public InsertBuilder withItem(T item) {
        mItems.add(item);
        return this;
    }

    public InsertBuilder withItems(List<T> items) {
        mItems.addAll(items);
        return this;
    }

    @Override
    public void execute() {
        mAsyncDao.insert(mItems);
    }
}
