package com.yanfan.easyIOC.system;

import java.util.Stack;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 */
public class AppManager extends Application
{
	private static AppManager instance;
	
	public static Context Context(){
        return instance;
    }
	
	public void onCreate() {
		Log.i("系统消息","系统启动");
		if(instance == null)
		{
			super.onCreate();
	        instance = this;
	        activityStack = new Stack<Activity>();
		}
    }
	
	private static Stack<Activity> activityStack;
	/**
	 * 添加Activity到堆栈
	 */
	public static void addActivity(Activity activity){
		activityStack.push(activity);
		System.out.println("添加栈："+activityStack.size());
	}
	/**
	 * 获取当前Activity
	 */
	public static Activity currentActivity(){
		if(activityStack.size()>0)
		{
			return activityStack.peek();
		}else
		{
			return null;
		}
	}
	/**
	 * 返回上一个页面
	 */
	public static Activity callBack()
	{
		finishActivity(currentActivity());
		return currentActivity();
	}
	/**
	 * 移除页面
	 */
	public static void finishActivity(Activity activity)
	{
		activityStack.remove(activity);
		activity.finish();
		System.out.println("删除栈："+activityStack.size());
	}
	/**
	 * 结束指定类名的Activity
	 */
	public static void finishActivity(Class<?> cls){
		for (Activity activity : activityStack) {
			if(activity.getClass().equals(cls) ){
				activityStack.remove(activity);
				activity.finish();
			}
		}
	}
	/**
	 * 结束所有Activity
	 */
	private static void finishAllActivity(){
		Activity activity = null;
		if(activityStack.size()>0)
		{
			while((activity=activityStack.get(0))!=null)
			{
				finishActivity(activity);
			}
		}
	}
	/**
	 * 退出应用程序
	 */
	public static void Exit() {
		Log.i("系统消息","系统结束");
		finishAllActivity();
		System.exit(0);
	}
}