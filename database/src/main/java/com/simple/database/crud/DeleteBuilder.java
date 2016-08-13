package com.simple.database.crud;

import com.simple.database.crud.base.WhereBuilder;

/**
 * Created by mrsimple on 6/8/16.
 */
public class DeleteBuilder<T> extends WhereBuilder<T> {

    public DeleteBuilder(Class daoClass) {
        super(daoClass);
    }


    @Override
    public void execute() {
        mAsyncDao.delete(selection, selectionArgs);
    }
}
