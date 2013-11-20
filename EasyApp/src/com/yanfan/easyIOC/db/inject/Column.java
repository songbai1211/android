package com.yanfan.easyIOC.db.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yanfan.easyIOC.db.ColumeType;

/**
 * 声明此字段可以作为数据库字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME) 
public @interface Column {
	/**
	 * 数据库字段类型
	 * @return
	 */
	public ColumeType type() default ColumeType.String;
}
