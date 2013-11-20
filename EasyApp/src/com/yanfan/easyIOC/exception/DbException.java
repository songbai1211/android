package com.yanfan.easyIOC.exception;

import android.util.Log;

/**
 * 数据库操作异常
 * @author 闫帆
 */
public class DbException extends Exception {
	private static final long serialVersionUID = 1L;
	private String strMsg = null;
	
	public DbException() {}
	
	public DbException(String strMsg) {
		this.strMsg = strMsg;
	}
	
	public DbException(String strMsg,Exception e) {
		this.strMsg = strMsg;
		this.setStackTrace(e.getStackTrace());
	}
	
	public void printStackTrace() {
		Log.e("数据库异常", strMsg);
		super.printStackTrace();
	}
}
