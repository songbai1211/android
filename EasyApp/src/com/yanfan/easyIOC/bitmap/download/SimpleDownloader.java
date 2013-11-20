package com.yanfan.easyIOC.bitmap.download;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import com.yanfan.easyIOC.EasyHttp;
import com.yanfan.easyIOC.exception.NetWorkException;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * @title 根据 图片url地址下载图片 可以是本地和网络
 */
public class SimpleDownloader implements Downloader {

	private static final String TAG = SimpleDownloader.class.getSimpleName();
	@SuppressLint("DefaultLocale")
	public byte[] download (String urlString) throws NetWorkException
	{
		try {
			if (urlString == null)
				return null;

			if (urlString.trim().toLowerCase().startsWith("http")) {
			{
				return EasyHttp.getFile(urlString);
			}
			}else if(urlString.trim().toLowerCase().startsWith("file:")){
				try {
					File f = new File(new URI(urlString));
					if (f.exists() && f.canRead()) {
						return getFromFile(f);
					}
				} catch (URISyntaxException e) {
					Log.e(TAG, "Error in read from file - " + urlString + " : " + e);
				}
			}else{
				File f = new File(urlString);
				if (f.exists() && f.canRead()) {
					return getFromFile(f);
				}
			}
			
			return null;
		} catch (Exception e) {
			throw new NetWorkException("网络下载文件失败",e);
		}
	}

	private byte[] getFromFile(File file) {
		if(file == null) return null;
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = fis.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			Log.e(TAG, "Error in read from file - " + file + " : " + e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
					fis = null;
				} catch (IOException e) {
					// do nothing
				}
			}
		}

		return null;
	}

	
	public class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int by_te = read();
					if (by_te < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}
}
