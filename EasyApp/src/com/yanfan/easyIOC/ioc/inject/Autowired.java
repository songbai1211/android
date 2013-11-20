package com.yanfan.easyIOC.ioc.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * IOC容器自动注入
 * @author 闫帆
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
	/**
	 * 自动注入需要注入的类型
	 */
	public Class<?> classtype();
}
