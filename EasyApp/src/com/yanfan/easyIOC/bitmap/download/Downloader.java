package com.yanfan.easyIOC.bitmap.download;

import com.yanfan.easyIOC.exception.NetWorkException;


public interface Downloader  {
	
	/**
	 * 请求网络的inputStream填充outputStream
	 */
	public byte[] download(String urlString)throws NetWorkException;
}
