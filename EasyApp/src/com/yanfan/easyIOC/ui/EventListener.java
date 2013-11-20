package com.yanfan.easyIOC.ui;

import java.lang.reflect.Method;
import com.yanfan.easyIOC.exception.IOCException;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
/**
 * 动态绑定监听
 */
public class EventListener implements OnClickListener, OnLongClickListener, OnItemClickListener, OnItemSelectedListener,OnItemLongClickListener {

	private Object handler;
	private String clickMethod;
	private String longClickMethod;
	private String itemClickMethod;
	private String itemSelectMethod;
	private String nothingSelectedMethod;
	private String itemLongClickMehtod;
	
	public EventListener(Object handler) {
		this.handler = handler;
	}
	
	public EventListener click(String method){
		this.clickMethod = method;
		return this;
	}
	
	public EventListener longClick(String method){
		this.longClickMethod = method;
		return this;
	}
	
	public EventListener itemLongClick(String method){
		this.itemLongClickMehtod = method;
		return this;
	}
	
	public EventListener itemClick(String method){
		this.itemClickMethod = method;
		return this;
	}
	
	public EventListener select(String method){
		this.itemSelectMethod = method;
		return this;
	}
	
	public EventListener noSelect(String method){
		this.nothingSelectedMethod = method;
		return this;
	}
	
	@Override
	public boolean onLongClick(View v) {
		try {
			return invokeLongClickMethod(handler,longClickMethod,v);
		} catch (IOCException e) {
			Log.e("IOC", "容器不能处理用户事务");
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
		try {
			return invokeItemLongClickMethod(handler,itemLongClickMehtod,arg0,arg1,arg2,arg3);
		} catch (IOCException e) {
			Log.e("IOC", "容器不能处理用户事务");
			e.printStackTrace();
			return false;
		}
	}
	
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
		try {
			invokeItemSelectMethod(handler,itemSelectMethod,arg0,arg1,arg2,arg3);
		} catch (IOCException e) {
			Log.e("IOC", "容器不能处理用户事务");
			e.printStackTrace();
		}
	}
	
	public void onNothingSelected(AdapterView<?> arg0) {
		try {
			invokeNoSelectMethod(handler,nothingSelectedMethod,arg0);
		} catch (IOCException e) {
			Log.e("IOC", "容器不能处理用户事务");
			e.printStackTrace();
		}
	}
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		try {
			invokeItemClickMethod(handler,itemClickMethod,arg0,arg1,arg2,arg3);
		} catch (IOCException e) {
			Log.e("IOC", "容器不能处理用户事务");
			e.printStackTrace();
		}
	}
	
	public void onClick(View v) {
		try {
			invokeClickMethod(handler, clickMethod, v);
		} catch (IOCException e) {
			Log.e("IOC", "容器不能处理用户事务");
			e.printStackTrace();
		}
	}
	
	
	private static Object invokeClickMethod(Object handler, String methodName,  Object... params)throws IOCException{
		try {
			if(handler == null) throw new IOCException("invokeItemLongClickMethod: handler is null :");
			Method method = handler.getClass().getDeclaredMethod(methodName,View.class);
			if(method!=null)
				return method.invoke(handler, params);	
			else
				throw new IOCException("no such method:"+methodName);
		} catch (Exception e) {
			throw new IOCException("invokeClickMethod事件添加失败", e);
		}
	}
	
	private static boolean invokeLongClickMethod(Object handler, String methodName,  Object... params)throws IOCException{
		try {
			if(handler == null) throw new IOCException("invokeItemLongClickMethod: handler is null :");
			Method method = handler.getClass().getDeclaredMethod(methodName,View.class);
			if(method!=null){
				Object obj = method.invoke(handler, params);
				return obj==null?false:Boolean.valueOf(obj.toString());	
			}
			else
			{
				throw new IOCException("no such method:"+methodName);
			}
		} catch (Exception e) {
			throw new IOCException("invokeLongClickMethod事件添加失败", e);
		}
	}
	
	
	
	private static Object invokeItemClickMethod(Object handler, String methodName,  Object... params)throws IOCException{
		try {
			if(handler == null) throw new IOCException("invokeItemLongClickMethod: handler is null :");
			Method method = handler.getClass().getDeclaredMethod(methodName,AdapterView.class,View.class,int.class,long.class);
			if(method!=null)
			{
				return method.invoke(handler, params);
			}else
			{
				throw new IOCException("no such method:"+methodName);
			}
		} catch (Exception e) {
			throw new IOCException("invokeItemClickMethod事件添加失败", e);
		}
	}
	
	
	private static boolean invokeItemLongClickMethod(Object handler, String methodName,  Object... params) throws IOCException{
		try {
			if(handler == null) throw new IOCException("invokeItemLongClickMethod: handler is null :");
			Method method = handler.getClass().getDeclaredMethod(methodName,AdapterView.class,View.class,int.class,long.class);
			if(method!=null){
				Object obj = method.invoke(handler, params);
				return Boolean.valueOf(obj==null?false:Boolean.valueOf(obj.toString()));	
			}
			else
			{
				throw new IOCException("no such method:"+methodName);
			}
		} catch (Exception e) {
			throw new IOCException("invokeItemLongClickMethod事件添加失败", e);
		}
	}
	
	
	private static Object invokeItemSelectMethod(Object handler, String methodName,  Object... params) throws IOCException{
		try {
			if(handler == null) throw new IOCException("invokeItemSelectMethod: handler is null :");
			Method method = handler.getClass().getDeclaredMethod(methodName,AdapterView.class,View.class,int.class,long.class);
			if(method!=null)
			{
				return method.invoke(handler, params);	
			}
			else
			{
				throw new IOCException("no such method:"+methodName);
			}
		} catch (Exception e) {
			throw new IOCException("invokeItemSelectMethod事件添加失败", e);
		}
	}
	
	private static Object invokeNoSelectMethod(Object handler, String methodName,  Object... params) throws IOCException{
		try {
			if(handler == null) throw new IOCException("invokeNoSelectMethod: handler is null :");
			Method method = handler.getClass().getDeclaredMethod(methodName,AdapterView.class);
			if(method!=null)
			{
				return method.invoke(handler, params);
			}else
			{
				throw new IOCException("no such method:"+methodName);
			}
		} catch (Exception e) {
			throw new IOCException("invokeNoSelectMethod事件添加失败", e);
		}
	}
}
