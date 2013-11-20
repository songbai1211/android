package com.yanfan.easyIOC.ioc.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aop {
	public Class<?> proxy();
	/**
	 * 在此方法执行之前执行
	 */
	public String before();
	/**
	 * 在此方法执行之后执行某个方法
	 */
	public String after();
}
