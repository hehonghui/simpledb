package com.simple.database.utils;

import android.database.Cursor;

import java.io.Closeable;

/**
 * 将stream 转换 String的工具类
 */
public final class IOUtils {

    /**
     * 关闭Closeable对象
     * @param closeable
     */
	public static void closeSilently(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

    /**
     * 关闭cursor对象, 在api 16之前Cursor对象没有实现 Closeable 接口 ！！！！
     * @param cursor
     */
    public static void closeCursor(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
