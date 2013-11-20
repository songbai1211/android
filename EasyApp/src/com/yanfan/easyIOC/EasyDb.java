package com.yanfan.easyIOC;

import java.util.List;

import com.yanfan.easyIOC.db.SqlBuilder;
import com.yanfan.easyIOC.db.SqliteDbHelper;
import com.yanfan.easyIOC.exception.DbException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/**
 * Android对数据库操作
 */
public class EasyDb {
	/**
	 * 数据库名称
	 */
	public static EasyDb easydb = null;
	public static boolean DEBUG = true;
	public String db_name = "databases.db";
	private SQLiteDatabase db = null;
	
	private EasyDb()
	{
		
	}
	/**
	 * 创建FinalDb连接
	 */
	public static EasyDb open(Context context){
		if(easydb == null)
		{
			easydb = new EasyDb();
			easydb.db = new SqliteDbHelper(context,easydb.db_name).getWritableDatabase();;
		}
		return easydb;
	}
	/**
	 * 判断是否存在表
	 */
	 public <T> boolean tabIsExist(Class<T> clazz){
         boolean result = false;
         Cursor cursor = null;
         String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+clazz.getSimpleName()+"' ";
         cursor = this.db.rawQuery(sql,null);
         if(cursor.moveToNext()){
             int count = cursor.getInt(0);
             if(count>0){
                 result = true;
             }
         }
         return result;
	 }
	/**
	 * 根据POJO对象创建数据库表
	 */
	public <T> void create(Class<T> clazz) throws DbException
	{
		String sql = SqlBuilder.getCreate(clazz);
		if(EasyDb.DEBUG)
		{
			Log.i("创建表："+clazz.getName(), sql);
		}
		this.db.execSQL(sql);
	}
	/**
	 * 根据POJO删除表
	 */
	public <T> void DropTable(Class<T> clazz) throws DbException
	{
		String sql = "DROP TABLE "+clazz.getSimpleName();
		if(EasyDb.DEBUG)
		{
			Log.i("删除表："+clazz.getName(), sql);
		}
		this.db.execSQL(sql);
	}
	/**
	 * 根据传递的POJO对象保存数据
	 */
	public <T> void save(T t) throws DbException
	{
		String sql = SqlBuilder.getInsert(t);
		if(EasyDb.DEBUG)
		{
			Log.i("保存数据", sql);
		}
		this.db.execSQL(sql);
	}
	/**
	 * 根据传递的POJO对象删除数据
	 */
	public <T> void delete(T t) throws DbException
	{
		String sql = SqlBuilder.getDelete(t);
		if(EasyDb.DEBUG)
		{
			Log.i("删除数据", sql);
		}
		this.db.execSQL(sql);
	}
	/**
	 * 将符合条件的数据修改
	 */
	public <T> void update(T oldData,T newData) throws DbException
	{
		String sql = SqlBuilder.getUpdate(oldData,newData);
		if(EasyDb.DEBUG)
		{
			Log.i("修改数据", sql);
		}
		this.db.execSQL(sql);
	}
	/**
	 * 查询复合条件的所有数据
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> select(T t) throws DbException
	{
		String sql = SqlBuilder.getSelect(t);
		if(EasyDb.DEBUG)
		{
			Log.i("查询数据", sql);
		}
		Cursor cursor = null;
		cursor = this.db.rawQuery(sql,null);
		List<T> list = (List<T>) SqlBuilder.getEntitys(cursor,t.getClass());
		cursor.close();
		return list;
	}
	
	/**
	 * 查询复合条件的所有数据
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> selectLike(T t) throws DbException
	{
		String sql = SqlBuilder.getSelectLike(t);
		if(EasyDb.DEBUG)
		{
			Log.i("查询数据", sql);
		}
		Cursor cursor = null;
		cursor = this.db.rawQuery(sql,null);
		List<T> list = (List<T>) SqlBuilder.getEntitys(cursor,t.getClass());
		cursor.close();
		return list;
	}
	/**
	 * 根据主键查询数据
	 */
	public <T> T selectByID(Class<T> clazz,String id) throws DbException
	{
		String sql = SqlBuilder.getSelectByID(clazz,id);
		if(EasyDb.DEBUG)
		{
			Log.i("查询数据", sql);
		}
		Cursor cursor = null;
		cursor = this.db.rawQuery(sql,null);
		T t = SqlBuilder.getEntity(cursor,clazz);
		cursor.close();
		return t;
	}
}
