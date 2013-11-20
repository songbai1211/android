package com.yanfan.easyIOC.bitmap.display;

import com.yanfan.easyIOC.bitmap.core.BitmapDisplayConfig;
import android.graphics.Bitmap;
import android.view.View;

public interface Displayer {

	/**
	 * 图片加载完成 回调的函数
	 * @param imageView
	 * @param bitmap
	 * @param config
	 */
	public void loadCompletedisplay(View imageView,Bitmap bitmap,BitmapDisplayConfig config);
	
	/**
	 * 图片加载失败回调的函数
	 * @param imageView
	 * @param bitmap
	 */
	public void loadFailDisplay(View imageView,Bitmap bitmap);
	
}
