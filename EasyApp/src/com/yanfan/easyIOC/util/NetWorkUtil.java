package com.yanfan.easyIOC.util;

import com.yanfan.easyIOC.system.AppManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * <uses-permission android:name="android.permission.INTERNET" />
 * 注册广播
 * 1：
 * <receiver
        android:name="com.test.NetworkBroadcast"
        android:label="NetworkConnection" >
        <intent-filter>
            <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
        </intent-filter>
    </receiver>
    
    2：
    	调用静态方法
 */
public class NetWorkUtil extends BroadcastReceiver{
	
	private static NetWorkUtil network=null;
	/**
	 * 注册广播
	 */
	public static boolean register()
	{
		if(network==null)
		{
			network = new NetWorkUtil();
			IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
			AppManager.currentActivity().registerReceiver(network, filter);
			return true;
		}else
		{
			return false;
		}
	}
	/**
	 * 反注册广播
	 */
	public static boolean unregister()
	{
		if(network!=null)
		{
			AppManager.currentActivity().unregisterReceiver(network);
			return true;
		}else
		{
			return false;
		}
	}
	/**
	 * 判断网络是否连接
	 */
	public static boolean isNetworkConnected() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager)AppManager.Context().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable();  
		}
		return false;  
	}

	/**
	 * 判断WIFI网络是否连接
	 */
	public static boolean isWifiConnected() {  
		ConnectivityManager mConnectivityManager = (ConnectivityManager)AppManager.Context().getSystemService(Context.CONNECTIVITY_SERVICE);  
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
		if (mWiFiNetworkInfo != null) {  
			return mWiFiNetworkInfo.isAvailable();  
		}
		return false;  
	}
	/**
	 * 移动网络是否可用
	 */
	public static boolean isMobileConnected() {  
		ConnectivityManager mConnectivityManager = (ConnectivityManager)AppManager.Context().getSystemService(Context.CONNECTIVITY_SERVICE);  
		NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
		if (mMobileNetworkInfo != null) {  
			return mMobileNetworkInfo.isAvailable();  
		}
		return false;  
	}
	/**
	 * 当前网络类型
	 * -1：没有网络  1：WIFI网络2：wap网络3：net网络
	 */
	public static int getConnectedType() {  
		ConnectivityManager mConnectivityManager = (ConnectivityManager)AppManager.Context().getSystemService(Context.CONNECTIVITY_SERVICE);  
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
		if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {  
			return mNetworkInfo.getType();  
		}
		return -1;
	}

	
	/**
		
		需要注册
		
		<receiver android:name="com.metarnet.easyIOC.NET.NetWorkUtil">  
            <intent-filter>
                <action android:name="android.net.conn.CONNECTIVITY_CHANGE"/>
            </intent-filter>
        </receiver>
		
		
	 **/
	@Override
	public void onReceive(Context context, Intent intent) {
		if(getConnectedType()==-1)
		{
			System.out.println("网络断开");
		}else
		{
			System.out.println("网络:"+getConnectedType());
		}
	}
}
