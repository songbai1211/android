package com.yanfan.easyIOC.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库工具
 * @author 闫帆
 */
public class SqliteDbHelper extends SQLiteOpenHelper {
	
	public SqliteDbHelper(Context context, String name) {
		super(context, name, null,1);
	}

	public void onCreate(SQLiteDatabase db) {
		
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
