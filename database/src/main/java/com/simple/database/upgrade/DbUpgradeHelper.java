package com.simple.database.upgrade;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.simple.database.DatabaseHelper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 数据库升级工具类,当数据库升级时会获取assets/migrations目录下的所有升级数据库的sql文件,sql文件以版本号命名,例如 3.sql。
 * 数据库更新时执行旧版本的数据库版本 到 新版本数据库之间的sql文件,使得不同版本之间的升级都可以顺利的进行.
 * 例如最新的数据库版本号为4,当用户从数据库版本号为2的应用升级时,那么3.sql、4.sql将被执行.
 * 注意: sql文件的规范为标准的sql语句,语句之间通过";"分隔.
 * Created by mrsimple on 26/10/15.
 */
public final class DbUpgradeHelper {

    private final static String TAG = DbUpgradeHelper.class.getSimpleName();
    private Context mContext;
    private String mUpgradePath;

    public DbUpgradeHelper(Context context, String upgradePath) {
        mContext = context;
        mUpgradePath = upgradePath;
    }

    /**
     * 获取所有升级的sql脚本文件名
     *
     * @return
     * @throws IOException
     */
    private List<String> getUpgradleSqlFiles() throws IOException {
        return Arrays.asList(mContext.getAssets().list(mUpgradePath));
    }

    /**
     * 升级数据库
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            List<String> allSQLFiles = getUpgradleSqlFiles();
            // 对升级文件进行排序
            Collections.sort(allSQLFiles, new NaturalOrderComparator());
            db.beginTransaction();
            try {
                // 执行所有升级的sql文件
                executeAllSqlScripts(allSQLFiles, db, oldVersion, newVersion);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("", "#### 打开sql更新目录失败 : " + e);
            e.printStackTrace();
        }
    }

    /**
     * 执行所有sql升级文件
     *
     * @param allSQLFiles
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    private void executeAllSqlScripts(List<String> allSQLFiles, SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String sqlfile : allSQLFiles) {
            try {
                final int version = Integer.valueOf(sqlfile.replace(".sql", ""));
                // 判断版本号
                if (version > oldVersion && version <= newVersion) {
                    DatabaseHelper.executeSqlScript(mContext, db, mUpgradePath + File.separator + sqlfile);
                    Log.d("", "#### db 升级 : " + sqlfile
                            + ", old version = " + oldVersion + ", newVersion = " + newVersion);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Skipping invalidly named file: " + sqlfile);
            }
        }
    }
}
