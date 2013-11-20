package com.yanfan.easyIOC.exception;

import android.util.Log;

/**
 * 容器异常
 * @author 闫帆
 */
public class IOCException extends Exception {
	private static final long serialVersionUID = 1L;
	private String strMsg = null;
	
	public IOCException() {}
	
	public IOCException(String strMsg) {
		this.strMsg = strMsg;
	}
	
	public IOCException(String strMsg,Exception e) {
		this.strMsg = strMsg;
		this.setStackTrace(e.getStackTrace());
	}
	
	public void printStackTrace() {
		Log.e("初始化IOC容器异常", strMsg);
		super.printStackTrace();
	}
}
