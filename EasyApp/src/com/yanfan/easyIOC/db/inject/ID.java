package com.yanfan.easyIOC.db.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yanfan.easyIOC.db.ColumeType;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME) 
public @interface ID {
	/**
	 * 数据库字段类型
	 */
	public ColumeType type() default ColumeType.String;
}
