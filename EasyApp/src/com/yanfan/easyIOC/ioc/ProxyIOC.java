package com.yanfan.easyIOC.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.text.TextUtils;

import com.yanfan.easyIOC.exception.IOCException;
import com.yanfan.easyIOC.ioc.inject.Aop;
import com.yanfan.easyIOC.ioc.inject.Autowired;

/**
 * IOC核心  AOP切面的代理对象
 * @author Administrator
 * @param <T>
 * @param <T>
 *
 */
public class ProxyIOC implements InvocationHandler{
	
	private Object t;
	/**
	 *  动态生成方法被处理过后的对象 (写法固定)
	 *  target(需要关注的类方法)
	 */
	public Object bind(Object obj) throws InstantiationException, IllegalAccessException
	{
		this.t = obj;
		return Proxy.newProxyInstance(t.getClass().getClassLoader(),t.getClass().getInterfaces(),this);
	}
	
	/**
	 * 设置AOP切面执行
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result;
		Aop aop = method.getAnnotation(Aop.class);
		if(aop == null)
		{
			result = method.invoke(this.t, args);
		}else
		{
			if(!TextUtils.isEmpty(aop.before()) || !TextUtils.isEmpty(aop.after()))
			{
				try {
					Class<?> clazz = aop.proxy();
					Object intercept = clazz.newInstance();
					setAutowired(intercept);
					if(!TextUtils.isEmpty(aop.before()))
					{
						Method before = clazz.getDeclaredMethod(aop.before());
						before.invoke(intercept,args);
					}
					result = method.invoke(this.t, args);
					if(!TextUtils.isEmpty(aop.after()))
					{
						Method after = clazz.getDeclaredMethod(aop.after());
						after.invoke(intercept,args);
					}
				} catch (Exception e) {
					throw new IOCException("IOC代理对象执行任务失败",e);
				}
			}else
			{
				result = method.invoke(this.t, args);
			}
		}
		return result;
	}
    
    /**
	 * 自动创建Bean，注入数据
	 * @param field
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void setAutowired(Object t) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Field[] fields = t.getClass().getDeclaredFields();
		if(fields!=null && fields.length>0)
		{
			for (Field field : fields) {
				Autowired autowired = field.getAnnotation(Autowired.class);
				if(autowired!=null)
				{
					Class<?> clazz = autowired.classtype();
					Object newObj = clazz.newInstance();
					setAutowired(newObj);
					field.setAccessible(true);
					field.set(t,new ProxyIOC().bind(newObj));
					field.setAccessible(false);
				}
			}
		}
	}
}