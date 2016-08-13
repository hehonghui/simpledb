package com.simple.database.crud.base;

/**
 * 构建查询条件的基础类.
 * Created by mrsimple on 6/8/16.
 */
public abstract class WhereBuilder<T> extends BaseSQLBuilder<T> {
    protected String selection = null;
    protected String[] selectionArgs = null;

    /**
     * @param daoClass 同步的dao类,用于构建异步的dao类
     */
    public WhereBuilder(Class daoClass) {
        super(daoClass);
    }

    /**
     * where 查询条件
     *
     * @param selection
     * @param selectionArgs
     * @return
     */
    public WhereBuilder where(String selection, String[] selectionArgs) {
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        return this;
    }
}
