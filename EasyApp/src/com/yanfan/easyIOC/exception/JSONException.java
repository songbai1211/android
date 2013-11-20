package com.yanfan.easyIOC.exception;

import android.util.Log;

/**
 * JSON解析字符串异常
 * @author 闫帆
 */
public class JSONException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3622751063871340517L;

	public JSONException(String message)
	{
		super(message);
	}
	
	public void printStackTrace() {
		Log.e("JSON对象异常","JSON对象异常");
		super.printStackTrace();
	}
}
