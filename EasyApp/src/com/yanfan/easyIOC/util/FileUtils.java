package com.yanfan.easyIOC.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.yanfan.easyIOC.system.AppManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;

/** 
 * 文件操作工具包
 */
public class FileUtils 
{
	/**
	 * 写文本文件
	 * 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
	 */
	public static void write(String fileName,String text) throws IOException 
	{
		if(text == null )text = "";
		FileOutputStream fos = AppManager.Context().openFileOutput(fileName,Context.MODE_PRIVATE);
		fos.write(text.getBytes()); 
		fos.close();
	}
	
	/**
	 * 读取文本文件
	 * @throws IOException 
	 */
	public static String read(String filePath) throws IOException 
	{
		FileInputStream in = AppManager.Context().openFileInput(filePath);
		return readInStream(in);
	} 
	
	private static String readInStream(FileInputStream inStream) throws IOException
	{
	    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	    byte[] buffer = new byte[512];
	    int length = -1;
	    while((length = inStream.read(buffer)) != -1 )
	    {
		   outStream.write(buffer, 0, length);
	    }
	    outStream.close();
	    inStream.close();
	    return outStream.toString();
	}
	/**
	 * 创建新文件
	 * folderPath 文件夹
	 * fileName 文件名称
	 */
	public static File createFile(String folderPath,String fileName)
	{
		File destDir = new File(folderPath);
		if (!destDir.exists()) 
		{
			destDir.mkdirs();
		}
		return new File(folderPath,fileName);
	}
	
	/**
	 * 将图片保存到文件夹
	 */
	public static void writeFile(byte[] buffer,String filename) throws IOException
	{
		FileOutputStream out = new FileOutputStream(ImgPath() +filename);
		out.write(buffer);
		out.flush();
		out.close();
	}
	/**
	 * 将图片保存到文件夹
	 */
	public static void writeFile(InputStream in,String filename)throws IOException
	{
		FileOutputStream out = new FileOutputStream(ImgPath() +filename);
		byte buffer[]=new byte[4*1024];
		int len = 0;
		while((len = in.read(buffer)) != -1)
		{
			out.write(buffer,0,len);
		}
		out.flush();
		out.close();
		in.close();
	}
	/**
	 * 获取SD卡根目录
	 */
	public static String sdCardPath()
	{
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if(sdCardExist)
		{
			return Environment.getExternalStorageDirectory() + File.separator;
		}else
		{
			return null;
		}
	}
	/**
	 * 获取相册路径
	 */
	public static String ImgPath()
	{
		return sdCardPath() +"DCIM"+File.separator+"Camera"+ File.separator;
	}
	/**
	 * 根据文件绝对路径获取文件名
	 */
	public static String getFileName( String filePath )
	{
		if(StringUtil.isEmpty(filePath) )	return "";
		return filePath.substring( filePath.lastIndexOf( File.separator )+1 );
	}
	/**
	 * 根据文件的绝对路径获取文件名但不包含扩展名
	 * @param filePath
	 * @return
	 */
	public static String getFileNameNoFormat( String filePath){
		if(StringUtil.isEmpty(filePath)){
			return "";
		}
		int point = filePath.lastIndexOf('.');
		return filePath.substring(filePath.lastIndexOf(File.separator)+1,point);
	}
	
	/**
	 * 获取文件扩展名
	 * @param fileName
	 * @return
	 */
	public static String getFileFormat( String fileName )
	{
		if( StringUtil.isEmpty(fileName) )	return "";
		
		int point = fileName.lastIndexOf( '.' );
		return fileName.substring( point+1 );
	}
	
	/**
	 * 获取文件大小
	 * @param filePath
	 * @return
	 */
	public static long getFileSize( String filePath )
	{
		long size = 0;
		
		File file = new File( filePath );
		if(file!=null && file.exists())
		{
			size = file.length();
		} 
		return size;
	}
	
	/**
	 * 获取文件大小
	 * @param size 字节
	 * @return
	 */
	public static String getFileSize(long size) 
	{
		if (size <= 0)	return "0";
		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		float temp = (float)size / 1024;
		if (temp >= 1024) 
		{
			return df.format(temp / 1024) + "M";
		}
		else 
		{
			return df.format(temp) + "K";
		}
	}
	/**
	 * 根据URI获取文件真实路径
	 */
	@SuppressWarnings("deprecation")
	public static String getAbsoluteImagePath(Uri uri)  
    {
		if(uri.toString().indexOf("content://")!=-1)
		{
	        String [] proj={MediaStore.Images.Media.DATA};
	        Cursor cursor = AppManager.currentActivity().managedQuery(uri,proj,null,null,null);
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
	        cursor.moveToFirst();
	        return cursor.getString(column_index);
		}else
		{
			return uri.getPath();
		}
    }
	/**
	 * 转换文件大小
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize(long fileS) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

	/**
	 * 获取目录文件大小
	 * @param dir
	 * @return
	 */
	public static long getDirSize(File dir) {
		if (dir == null) {
			return 0;
		}
	    if (!dir.isDirectory()) {
	    	return 0;
	    }
	    long dirSize = 0;
	    File[] files = dir.listFiles();
	    for (File file : files) {
	    	if (file.isFile()) {
	    		dirSize += file.length();
	    	} else if (file.isDirectory()) {
	    		dirSize += file.length();
	    		dirSize += getDirSize(file); //递归调用继续统计
	    	}
	    }
	    return dirSize;
	}
	
	/**
	 * 获取目录文件个数
	 * @param f
	 * @return
	 */
	public long getFileList(File dir){
        long count = 0;
        File[] files = dir.listFiles();
        count = files.length;
        for (File file : files) {
            if (file.isDirectory()) {
            	count = count + getFileList(file);//递归
            	count--;
            }
        }
        return count;  
    }
	
	public static byte[] toBytes(InputStream in) throws IOException 
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
	    int ch;
	    while ((ch = in.read()) != -1)
	    {
	    	out.write(ch);
	    }
	    byte buffer[]=out.toByteArray();
	    out.close();
	    return buffer;
	}
	
	/**
	 * 检查文件是否存在
	 * @param name
	 * @return
	 */
	public static boolean checkFileExists(String name) {
		boolean status;
		if (!name.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + name);
			status = newPath.exists();
		} else {
			status = false;
		}
		return status;

	}
	
	/**
	 * 计算SD卡的剩余空间
	 * @return 返回-1，说明没有安装sd卡
	 */
	public static long getFreeDiskSpace() {
		String status = Environment.getExternalStorageState();
		long freeSpace = 0;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				freeSpace = availableBlocks * blockSize / 1024;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return -1;
		}
		return (freeSpace);
	}

	/**
	 * 新建目录
	 * @param directoryName
	 * @return
	 */
	public static boolean createDirectory(String directoryName) {
		boolean status;
		if (!directoryName.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + directoryName);
			status = newPath.mkdir();
			status = true;
		} else
			status = false;
		return status;
	}

	/**
	 * 检查是否安装SD卡
	 * @return
	 */
	public static boolean checkSaveLocationExists() {
		String sDCardStatus = Environment.getExternalStorageState();
		boolean status;
		if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) {
			status = true;
		} else
			status = false;
		return status;
	}

	/**
	 * 删除目录(包括：目录里的所有文件)
	 * @param fileName
	 * @return
	 */
	public static boolean deleteDirectory(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isDirectory()) {
				String[] listfile = newPath.list();
				// delete all files within the specified directory and then
				// delete the directory
				try {
					for (int i = 0; i < listfile.length; i++) {
						File deletedFile = new File(newPath.toString() + "/"
								+ listfile[i].toString());
						deletedFile.delete();
					}
					newPath.delete();
					Log.i("DirectoryManager deleteDirectory", fileName);
					status = true;
				} catch (Exception e) {
					e.printStackTrace();
					status = false;
				}

			} else
				status = false;
		} else
			status = false;
		return status;
	}

	/**
	 * 删除文件
	 * @param fileName
	 * @return
	 */
	public static boolean deleteFile(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isFile()) {
				try {
					Log.i("DirectoryManager deleteFile", fileName);
					newPath.delete();
					status = true;
				} catch (SecurityException se) {
					se.printStackTrace();
					status = false;
				}
			} else
				status = false;
		} else
			status = false;
		return status;
	}
}