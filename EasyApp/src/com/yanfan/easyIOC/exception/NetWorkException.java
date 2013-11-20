package com.yanfan.easyIOC.exception;

import android.util.Log;

/**
 * 网络通信异常
 * @author 闫帆
 */
public class NetWorkException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	private String strMsg = null;
	
	public NetWorkException() {}
	
	public NetWorkException(String strMsg) {
		this.strMsg = strMsg;
	}
	
	public NetWorkException(String strMsg,Exception e) {
		this.strMsg = strMsg;
		this.setStackTrace(e.getStackTrace());
	}
	
	public void printStackTrace() {
		Log.e("网络异常", strMsg);
		super.printStackTrace();
	}
}
