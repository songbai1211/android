package com.yanfan.easyIOC.util;

import java.lang.reflect.Field;
import java.util.HashMap;
public class Convert {
	/**
	 * 转换为Bean
	 * @param map
	 * @param obj
	 * @throws Exception
	 */
	public static void toBean(HashMap<String, ?> map, Object obj) throws Exception {  
		Class<? extends Object> class1 = obj.getClass();  
        Field[] fields = class1.getDeclaredFields();
        for (Field field : fields) {
        	field.setAccessible(true);
        	field.set(obj, map.get(field.getName()));
		}
    }
	/**
	 * 转为Map
	 * @param bean
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, Object> toMap(Object bean) throws Exception
	{
		HashMap<String,Object> map = new HashMap<String, Object>();
		Field[] fields = bean.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			map.put(field.getName(),field.get(bean));
		}
		return map;
	}
}
