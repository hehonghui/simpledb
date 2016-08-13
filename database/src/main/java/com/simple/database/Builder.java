package com.simple.database;

import android.content.Context;
import android.text.TextUtils;

/**
 * 创建数据库
 */
public class Builder {
    /**
     * 存放创建数据表的sql文件, 需要存储在assets目录
     */
    private final static String CREATE_SQL_FILE = "db/create.sql";
    /**
     * 存放清空数据表的sql文件, 需要存储在assets目录
     */
    private final static String CLEAN_SQL_FILE = "db/clean.sql";
    /**
     * 存放sql升级文件的目录 ( 里面会有多个版本的升级文件,文件名以数据库版本号命名 ), 需要存储在assets目录
     */
    private final static String SQL_PATH = "db/migrations";
    /**
     * Context , 用于访问assets目录下的文件
     */
    Context context;
    /**
     * 数据库名字
     */
    String dbName = "simple_database.db";
    /**
     * 数据库版本
     */
    int dbVersion = 1;
    /**
     * 创建数据库表的文件位置( 一般存在assets目录中 ),具体的文件名, 默认为 db/create.sql
     */
    String createSqlFile = CREATE_SQL_FILE;
    /**
     * 清空数据库表的文件位置( 一般存在assets目录中 ),具体的文件名, 默认为 db/clean.sql
     */
    String emptySqlFile = CLEAN_SQL_FILE;
    /**
     * 升级数据库的sql文件存储目录,默认为 db/migrations
     */
    String upgradePath = SQL_PATH;

    public Builder(Context context) {
        this.context = context;
    }

    public Builder setDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public Builder setDbVersion(int dbVersion) {
        this.dbVersion = dbVersion;
        return this;
    }

    /**
     * 设置创建数据库表的sql文件
     *
     * @param createSqlFile
     * @return
     */
    public Builder setCreateSqlFile(String createSqlFile) {
        this.createSqlFile = createSqlFile;
        return this;
    }

    /**
     * 设置清空数据库的sql文件
     *
     * @param createSqlFile
     * @return
     */
    public Builder setCleanSqlFile(String createSqlFile) {
        this.emptySqlFile = createSqlFile;
        return this;
    }

    /**
     * 设置更新路径
     *
     * @param upgradePath
     * @return
     */
    public Builder setUpgradePath(String upgradePath) {
        this.upgradePath = upgradePath;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public String getDbName() {
        return dbName;
    }

    public int getDbVersion() {
        return dbVersion;
    }

    public String getCreateSqlFile() {
        return createSqlFile;
    }

    public String getCleanSqlFile() {
        return emptySqlFile;
    }

    public String getUpgradePath() {
        return upgradePath;
    }

    private void checkConfig() {
        if (TextUtils.isEmpty(dbName)) {
            throw new IllegalArgumentException("你没有设置数据库名称 !!! ");
        }
        if (TextUtils.isEmpty(createSqlFile)) {
            throw new IllegalArgumentException("你没有设置创建数据库表的文件位置");
        }
        if (TextUtils.isEmpty(upgradePath)) {
            throw new IllegalArgumentException("你没有设置创建数据库升级文件的目录");
        }
    }

    public DatabaseHelper create() {
        checkConfig();
        return new DatabaseHelper(this);
    }
}
