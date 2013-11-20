package com.yanfan.easyIOC;

import java.io.File;
import com.yanfan.easyIOC.config.RequestCode;
import com.yanfan.easyIOC.system.AppManager;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/** 
 * 调用系统功能
 */
public class EasyCall{
	
	/**
	 * 打开相机拍照并将照片保存到相册
	 */
	public static void openCamera()
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		AppManager.currentActivity().startActivityForResult(intent,RequestCode.CAMERA_REQUESTCODE);
	}
	/**
	 * 调用系统录音功能
	 */
	public static void openSound()
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/amr");
		intent.setClassName("com.android.soundrecorder","com.android.soundrecorder.SoundRecorder");
		AppManager.currentActivity().startActivityForResult(intent,RequestCode.SOUND_REQUESTCODE);
	}
	/**
	 * 调用系统录像功能
	 */
	public static void openVideo()
	{
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
		AppManager.currentActivity().startActivityForResult(intent,RequestCode.VIDEO_REQUESTCODE);
	}
	/**
	 * 调用系统相册功能
	 */
	public static void openPhotos(boolean isCron)
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		if(isCron)
		{
			intent.putExtra("crop", "true");  
	        intent.putExtra("aspectX", 1);
	        intent.putExtra("aspectY", 1);
	        intent.putExtra("outputX", 80);
	        intent.putExtra("outputY", 80);
		}
		intent.putExtra("return-data", true);
	    intent.addCategory(Intent.CATEGORY_OPENABLE);
		AppManager.currentActivity().startActivityForResult(intent, RequestCode.IMAGE_REQUESTCODE);
	}
	/**
	 * 调用系统文件管理器
	 */
	public static void openFile()
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.putExtra("return-data", true);
	    intent.addCategory(Intent.CATEGORY_OPENABLE);
		AppManager.currentActivity().startActivityForResult(intent, RequestCode.FILE_REQUESTCODE);
	}
	/**
	 * 调用系统剪裁图片
	 */
	public static void openCropPhoto(String imagePath)
	{
		Intent intent = new Intent("com.android.camera.action.CROP");
		Uri uri = Uri.fromFile(new File(imagePath));
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 80);
        intent.putExtra("outputY", 80);
        intent.putExtra("return-data", true);
		AppManager.currentActivity().startActivityForResult(intent, RequestCode.CRONPHOTO_REQUESTCODE);
	}
}