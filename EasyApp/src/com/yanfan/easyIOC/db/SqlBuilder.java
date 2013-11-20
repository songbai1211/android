package com.yanfan.easyIOC.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;

import com.yanfan.easyIOC.db.inject.Column;
import com.yanfan.easyIOC.db.inject.ID;
import com.yanfan.easyIOC.db.inject.Table;
import com.yanfan.easyIOC.exception.DbException;
import com.yanfan.easyIOC.util.DateUtil;

/**
 * 用于生成SQL语句
 * @author 闫帆
 */
public final class SqlBuilder {
	
	/**
	 * 创建表
	 * @throws DbException 
	 */
	public static <T> String getCreate(Class<T> clazz) throws DbException
	{
		try {
			if(clazz.getAnnotation(Table.class)!=null)//如果此表为数据库表
			{
				StringBuffer sqlbuffer = new StringBuffer("CREATE TABLE ").append(clazz.getSimpleName()).append("(");
				Field[] fields = clazz.getDeclaredFields();
				for(Field field : fields)
				{
					Column colume = field.getAnnotation(Column.class);
					ID id = field.getAnnotation(ID.class);
					if(colume!=null)
					{
						sqlbuffer.append(field.getName()).append(",");
					}else if(id!=null)
					{
						sqlbuffer.append(field.getName()+" PRIMARY KEY").append(",");
					}
				}
				return sqlbuffer.toString().substring(0, sqlbuffer.toString().length()-1)+")";
			}
			return null;
		} catch (Exception e) {
			throw new DbException("创建表出错",e);
		}
	}
	
	/**
	 * 获取保存数据库的SQL
	 * @throws DbException 
	 */
	public static <T> String getInsert(T t) throws DbException
	{
		try {
			Class<? extends Object> clazz = t.getClass();
			String sql = "INSERT INTO "+clazz.getSimpleName()+"(";
			StringBuffer colums = new StringBuffer();
			StringBuffer values = new StringBuffer();
			for(Field field : clazz.getDeclaredFields())
			{
				Column colume = field.getAnnotation(Column.class);
				ID id = field.getAnnotation(ID.class);
				if(colume!=null || id!=null)
				{
					
					field.setAccessible(true);
					Object obj = field.get(t);
					if(obj!=null)
					{
						String value;
						if(obj.getClass() == Date.class || obj.getClass() == java.sql.Date.class)
						{
							value = DateUtil.format(obj);
						}else
						{
							value = String.valueOf(obj);
						}
						values.append("'").append(value).append("',");
						colums.append(field.getName()).append(",");
					}
					field.setAccessible(false);
				}
			}
			sql += colums.substring(0,colums.length()-1)+")"+" VALUES("+values.substring(0,values.length()-1)+")";
			return sql;
		} catch (Exception e) {
			throw new DbException("组装Insert语句出错",e);
		}
	}
	/**
	 * 获取删除对象的SQL语句
	 */
	public static <T> String getDelete(T t) throws DbException
	{
		try {
			Class<? extends Object> clazz = t.getClass();
			String sql = "DELETE FROM "+clazz.getSimpleName()+" WHERE 1=1 ";
			for(Field field : clazz.getDeclaredFields())
			{
				Column colume = field.getAnnotation(Column.class);
				ID id = field.getAnnotation(ID.class);
				if(colume!=null || id!=null)
				{
					field.setAccessible(true);
					Object obj = field.get(t);
					if(obj!=null)
					{
						String value;
						if(obj.getClass() == Date.class || obj.getClass() == java.sql.Date.class)
						{
							value = DateUtil.format(obj);
						}else
						{
							value = String.valueOf(obj);
						}
						sql += "AND "+field.getName()+" = " + "'"+value+"' ";
					}
					field.setAccessible(false);
				}
			}
			return sql;
		} catch (Exception e) {
			throw new DbException("组装Delete语句出错",e);
		}
	}
	/**
	 * 获取修改对象的SQL语句
	 */
	public static <T> String getUpdate(T oldT,T newT) throws DbException
	{
		try {
			Class<? extends Object> clazz = oldT.getClass();
			String sql = "UPDATE "+clazz.getSimpleName()+" SET ";
			String set = "";
			String where = " WHERE 1=1 ";
			for(Field field : clazz.getDeclaredFields())
			{
				Column colume = field.getAnnotation(Column.class);
				ID id = field.getAnnotation(ID.class);
				if(colume!=null || id!=null)
				{
					field.setAccessible(true);
					Object newData = field.get(newT);
					if(newData!=null)
					{
						String value;
						if(newData.getClass() == Date.class || newData.getClass() == java.sql.Date.class)
						{
							value = DateUtil.format(newData);
						}else
						{
							value = String.valueOf(newData);
						}
						set += field.getName()+" = " + "'"+value+"',";
					}
					Object oldData = field.get(oldT);
					if(oldData!=null)
					{
						String value;
						if(oldData.getClass() == Date.class || oldData.getClass() == java.sql.Date.class)
						{
							value = DateUtil.format(oldData);
						}else
						{
							value = String.valueOf(oldData);
						}
						where += "AND "+field.getName()+" = " + "'"+value+"' ";
					}
					field.setAccessible(false);
				}
			}
			sql += set.substring(0, set.length()-1) + where;
			return sql;
		} catch (Exception e) {
			throw new DbException("组装UPDATE语句出错",e);
		}
	}
	/**
	 * 获取查询数据的SQL
	 */
	public static <T> String getSelect(T t) throws DbException
	{
		try {
			Class<? extends Object> clazz = t.getClass();
			String where = " WHERE 1=1 ";
			String colums = "";
			for(Field field : clazz.getDeclaredFields())
			{
				Column colume = field.getAnnotation(Column.class);
				ID id = field.getAnnotation(ID.class);
				if(colume!=null || id!=null)
				{
					colums += field.getName()+",";
					field.setAccessible(true);
					Object obj = field.get(t);
					if(obj!=null)
					{
						String value;
						if(obj.getClass() == Date.class || obj.getClass() == java.sql.Date.class)
						{
							value = DateUtil.format(obj);
						}else
						{
							value = String.valueOf(obj);
						}
						where += "AND "+field.getName()+" = " + "'"+value+"' ";
					}
					field.setAccessible(false);
				}
			}
			return "SELECT "+colums.substring(0,colums.length()-1)+" FROM "+clazz.getSimpleName()+where;
		} catch (Exception e) {
			throw new DbException("组装Delete语句出错",e);
		}
	}
	
	/**
	 * 获取查询数据的SQL
	 */
	public static <T> String getSelectLike(T t) throws DbException
	{
		try {
			Class<? extends Object> clazz = t.getClass();
			String where = " WHERE 1=1 ";
			String colums = "";
			for(Field field : clazz.getDeclaredFields())
			{
				Column colume = field.getAnnotation(Column.class);
				ID id = field.getAnnotation(ID.class);
				if(colume!=null || id!=null)
				{
					colums += field.getName()+",";
					field.setAccessible(true);
					Object obj = field.get(t);
					if(obj!=null)
					{
						String value;
						if(obj.getClass() == Date.class || obj.getClass() == java.sql.Date.class)
						{
							value = DateUtil.format(obj);
						}else
						{
							value = String.valueOf(obj);
						}
						where += "AND "+field.getName()+" LIKE " + "'%"+value+"%' ";
					}
					field.setAccessible(false);
				}
			}
			return "SELECT "+colums.substring(0,colums.length()-1)+" FROM "+clazz.getSimpleName()+where;
		} catch (Exception e) {
			throw new DbException("组装Delete语句出错",e);
		}
	}
	public static <T> String getSelectByID(Class<T> clazz,String id) throws DbException
	{
		try {
			String where = " WHERE 1=1 ";
			String colums = "";
			for(Field field : clazz.getDeclaredFields())
			{
				Column colume = field.getAnnotation(Column.class);
				ID idField = field.getAnnotation(ID.class);
				if(colume!=null)
				{
					colums += field.getName()+",";
				}else if(idField!=null)
				{
					colums += field.getName()+",";
					where += " and "+field.getName() + "= '"+id+"'";
				}
			}
			return "SELECT "+colums.substring(0,colums.length()-1)+" FROM "+clazz.getSimpleName()+where;
		} catch (Exception e) {
			throw new DbException("组装Delete语句出错",e);
		}
	}
	/**
	 * 将cursor对象转换成POJO对象
	 */
	public static <T> T cursorToEntity(Cursor cursor,Class<T> clazz) throws DbException
	{
		try {
			T t = clazz.newInstance();
			int i =0;
			for (String colume : cursor.getColumnNames()) {
				Field field= clazz.getDeclaredField(colume);
				field.setAccessible(true);
				if(field.getType() == Date.class || field.getType() == java.sql.Date.class)
				{
					String date = cursor.getString(i);
					if(date!=null)
					{
						field.set(t,DateUtil.parse(date));
					}
				}else if(field.getType() == int.class || field.getType() == long.class)
				{
					field.set(t,cursor.getLong(i));
				}else
				{
					field.set(t,cursor.getString(i));
				}
				field.setAccessible(false);
				i++;
			}
			return t;
		} catch (Exception e) {
			throw new DbException("将数据转换成POJO对象出错",e);
		}
	}
	/**
	 * 将数据转换成List对象
	 */
	public static <T> List<T> getEntitys(Cursor cursor,Class<T> clazz) throws DbException
	{
		try {
			ArrayList<T> list = new ArrayList<T>();
			while(cursor.moveToNext()){
				T t = (T) cursorToEntity(cursor, clazz);
				list.add(t);
	        }
			return list;
		} catch (Exception e) {
			throw new DbException("将数据转换成List对象出错",e);
		}
	}
	/**
	 * 将一行数据转换成POJO对象
	 */
	public static <T> T getEntity(Cursor cursor,Class<T> clazz) throws DbException
	{
		try {
			if(cursor.moveToNext()){
				T t = (T) cursorToEntity(cursor, clazz);
				return t;
	        }
			return null;
		} catch (Exception e) {
			throw new DbException("将数据转换成List对象出错",e);
		}
	}
}
