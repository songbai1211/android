package com.yanfan.easyIOC;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.net.ssl.SSLHandshakeException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import com.yanfan.easyIOC.exception.NetWorkException;
import com.yanfan.easyIOC.network.AjaxCallBack;
import com.yanfan.easyIOC.network.AjaxRequest;
import com.yanfan.easyIOC.network.AjaxStatus;
import com.yanfan.easyIOC.network.Json;
import com.yanfan.easyIOC.util.Convert;
import com.yanfan.easyIOC.util.JSONUtil;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
/**
 * android对网络的操作
 */
@SuppressLint("HandlerLeak")
public class EasyHttp {
	
	private static final String CHARSET_UTF8 = "UTF-8";
	public static int timeout = 30;
	public static String JSESSIONID = null;

	private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
		public boolean retryRequest(IOException exception, int executionCount,HttpContext context) {
			if (executionCount >= 3) {
				return false;
			}
			if (exception instanceof NoHttpResponseException) {
				return true;
			}
			if (exception instanceof SSLHandshakeException) {
				return false;
			}
			HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
			boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
			if (!idempotent) {
				// Retry if the request is considered idempotent
				return true;
			}
			return false;
		}
	};
	
	private static ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
		public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String charset = EntityUtils.getContentCharSet(entity) == null ? CHARSET_UTF8 : EntityUtils.getContentCharSet(entity);
				return new String(EntityUtils.toByteArray(entity), charset);
			} else {
				return null;
			}
		}
	};
	
	
	private static ResponseHandler<AjaxStatus> ajaxHandler = new ResponseHandler<AjaxStatus>() {
		public AjaxStatus handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			AjaxStatus ajaxResponse = new AjaxStatus();
			HttpEntity entity = response.getEntity();
			if(entity!=null)
				ajaxResponse.setContent(EntityUtils.toByteArray(entity));
			ajaxResponse.setStatus(response.getStatusLine().getStatusCode());
			return ajaxResponse;
		}
	};
	
	
	private static ExecutorService ajaxExecutor = Executors.newFixedThreadPool(5,new ThreadFactory() {
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			// 设置线程的优先级别，让线程先后顺序执行（级别越高，抢到cpu执行的时间越多）
			t.setPriority(Thread.NORM_PRIORITY - 1);
			return t;
		}
	});
	
	
	/**
	 * 请求服务器获取对象
	 */
	public static <T> T getObject(String url,Class<T> clazz) throws Exception
	{
		String json=post(url);
		System.out.println("接收到Json数据："+json);
		T model=JSONUtil.parseObject(json,clazz);
		return model;
	}
	
	/**
	 * 请求服务器获取对象
	 */
	public static <T> T getObject(String url,Map<String, String> params,Class<T> clazz) throws Exception
	{
		String json=post(url,params);
		System.out.println("接收到Json数据："+json);
		T model=JSONUtil.parseObject(json,clazz);
		return model;
	}
	
	/**
	 * 请求服务器获取对象集合
	 */
	public static <T> HashMap<String, Object> getMap(String url,Class<T> clazz) throws Exception
	{
		String json=post(url);
		System.out.println("接收到Json数据："+json);
		T model=JSONUtil.parseObject(json,clazz);
		return Convert.toMap(model);
	}
	
	/**
	 * 请求服务器获取对象集合
	 */
	public static <T> HashMap<String, Object> getMap(String url,Map<String, String> params,Class<T> clazz) throws Exception
	{
		String json=post(url,params);
		System.out.println("接收到Json数据："+json);
		T model=JSONUtil.parseObject(json,clazz);
		return Convert.toMap(model);
	}
	
	/**
	 * 获取Json对象
	 * @throws Exception 
	 */
	public static Json getJson(String url) throws Exception
	{
		return getJson(url, null);
	}
	/**
	 * 获取Json对象
	 * @throws Exception 
	 */
	public static Json getJson(String url,Map<String, String> params) throws Exception
	{
		return getObject(url, params,Json.class);
	}
	/**
	 * 上传文件（此方法只是用于Phone后台）
	 */
	public static boolean upload(String url, File tempFile) {
		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put("upload",tempFile);
		map.put("uploadPath","phone");
		map.put("uploadFileName",tempFile.getName());
		return upload(url,map);
	}
	/**
	 * 上传文件
	 */
	public static boolean upload(String url,Map<String,Object> params)
	{
		boolean flag=false;
		try {
			String str=uploadPost(url, params);
			Json json=JSONUtil.parseObject(str,Json.class);
			if(json.isSuccess())
			{
				flag=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 上传文件
	 */
	public static Json uploadForJson(String url,Map<String,Object> params)
	{
		Json json=null;
		try {
			String str=uploadPost(url, params);
			json=JSONUtil.parseObject(str,Json.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 请求服务器获取对象数组
	 */
	public static <T> T[] getList(String url,Class<T> clazz) throws Exception
	{
		String json=post(url);
		System.out.println("接收到Json数据："+json);
		T[] model=JSONUtil.parseArray(json,clazz);
		return model;
	}
	/**
	 * 获取服务器对象数组的Map（MapList）
	 * ArrayList<HashMap<String, Object>>
	 */
	public static <T> ArrayList<HashMap<String, Object>> getMapList(String url,Class<T> clazz) throws Exception
	{
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		String json=post(url);
		System.out.println("接收到Json数据："+json);
		T[] model=JSONUtil.parseArray(json,clazz);
		for (T t : model) {
			list.add(Convert.toMap(t));
		}
		return list;
	}
	
	/**
	 * 获取服务器对象数组的Map（MapList）
	 * ArrayList<HashMap<String, Object>>
	 */
	public static <T> ArrayList<HashMap<String, Object>> getMapList(String url,Map<String, String> params,Class<T> clazz) throws Exception
	{
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		String json=post(url,params);
		System.out.println("接收到Json数据："+json);
		T[] model=JSONUtil.parseArray(json,clazz);
		for (T t : model) {
			list.add(Convert.toMap(t));
		}
		return list;
	}
	
	
	/**
	 * 请求服务器获取对象数组
	 * @throws Exception 
	 */
	public static <T> T[] getList(String url,Map<String, String> params,Class<T> clazz) throws Exception
	{
		String json=post(url,params);
		System.out.println("接收到Json数据："+json);
		T[] model=JSONUtil.parseArray(json,clazz);
		return model;
	}

	public static String get(String url) throws Exception{
		return get(url, null, null);
	}

	public static String get(String url, Map<String, String> params) throws Exception{
		return get(url, params, null);
	}

	public static String get(String url, Map<String, String> params,String charset) throws Exception {
		if (url == null || url.trim().length()==0) {
			return null;
		}
		DefaultHttpClient httpclient = null;
		String json=null;
		HttpGet hg = null;
		try {
			url = patchUrl(url);
			List<NameValuePair> qparams = getParamsList(params);
			if (qparams != null && qparams.size() > 0) {
				charset = (charset == null ? CHARSET_UTF8 : charset);
				String formatParams = URLEncodedUtils.format(qparams, charset);
				url = (url.indexOf("?")) < 0 ? (url + "?" + formatParams) : (url.substring(0, url.indexOf("?") + 1) + formatParams);
			}
			httpclient= getDefaultHttpClient(charset);
			hg = new HttpGet(url);
			if(null != JSESSIONID){
				hg.setHeader("Cookie", "JSESSIONID=" + JSESSIONID);
	        }
			json=httpclient.execute(hg, responseHandler);
			CookieStore mCookieStore = httpclient.getCookieStore();
			List<Cookie> cookies = mCookieStore.getCookies();
			for (int i = 0; i < cookies.size(); i++) {
		        if ("JSESSIONID".equals(cookies.get(i).getName())) {
		        	JSESSIONID = cookies.get(i).getValue();
		        	break;
		        }
	        }
			return json;
		} catch (Exception e) {
			throw new Exception("请求服务器失败",e);
		}finally {
			abortConnection(hg, httpclient);
		}
	}
	
	
	public static String post(String url)  throws Exception{
		return post(url,new HashMap<String, String>(), null);
	}
	
	public static String post(String url, Map<String, String> params) throws Exception {
		return post(url, params, null);
	}

	public static String post(String url, Map<String, String> params,String charset) throws Exception {
		if (url == null || url.trim().length()==0) {
			return null;
		}
		HttpPost httpPost=null;
		DefaultHttpClient httpclient=null;
		try {
			httpPost = new HttpPost(url);
			httpclient = getDefaultHttpClient(charset);
			url = patchUrl(url);
			UrlEncodedFormEntity formEntity = null;
			if (charset == null || charset.trim().length()==0) {
				formEntity = new UrlEncodedFormEntity(getParamsList(params));
			} else {
				formEntity = new UrlEncodedFormEntity(getParamsList(params),
						charset);
			}
			httpPost.setEntity(formEntity);
			if(null != JSESSIONID){
	            httpPost.setHeader("Cookie", "JSESSIONID=" + JSESSIONID);
	        }
			String json=null;
			json=httpclient.execute(httpPost, responseHandler);
			CookieStore mCookieStore = httpclient.getCookieStore();
			List<Cookie> cookies = mCookieStore.getCookies();
			for (int i = 0; i < cookies.size(); i++) {
		        if ("JSESSIONID".equals(cookies.get(i).getName())) {
		        	JSESSIONID = cookies.get(i).getValue();
		        	break;
		        }
	        }
			return json;
		} catch (Exception e) {
			throw new Exception("请求服务器失败",e);
		}finally {
			abortConnection(httpPost, httpclient);
		}
	}
	
	/**
	 * 获取网络图片
	 */
	public static Bitmap getNetImage(String Url) throws NetWorkException {
		try {
			URL url=new URL(Url);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(timeout*1000);
			InputStream in = con.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(in);
			in.close();
			con.disconnect();
			return bitmap;
		} catch (Exception e) {
			throw new NetWorkException("获取网络图片出错",e);
		}
	}
	/**
	 * 获取网络文件
	 */
	public static byte[] getFile(String path)throws NetWorkException{
		try {
			URL url=new URL(path);
			byte[] b=null;
			HttpURLConnection con = (HttpURLConnection)url.openConnection();  //打开连接
			con.setRequestMethod("GET"); //设置请求方法
			con.setConnectTimeout(timeout*1000);
			InputStream in=con.getInputStream();  //取得字节输入流
		    b=readInputStream(in);
		    in.close();
		    con.disconnect();
			return b;
		} catch (Exception e) {
			throw new NetWorkException("下载文件出错",e);
		}
	}
	private static byte[] readInputStream(InputStream in) throws Exception{
		int len=0;
		byte buf[]=new byte[1024];
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		while((len=in.read(buf))!=-1){
			out.write(buf,0,len);
		}
		out.close();
		return out.toByteArray();
	}
	/**
	 * 上传文件
	 */
	public static String uploadPost(String url,Map<String,Object> params) throws Exception
	{
		return uploadPost(url, params,HTTP.UTF_8);
	}
	/**
	 * 上传文件
	 */
	public static String uploadPost(String url,Map<String,Object> params,String charset) throws Exception
	{
		DefaultHttpClient httpclient = null;
		HttpPost httppost = null;
		try {
			httpclient = getDefaultHttpClient(charset);
			httppost = new HttpPost(url);
			MultipartEntity mpEntity = new MultipartEntity(); //文件传输
			for (String key : params.keySet()) {
				if(params.get(key) instanceof File)
				{
					mpEntity.addPart(key,new FileBody((File) params.get(key)));
				}else
				{
					mpEntity.addPart(key,new StringBody((String) params.get(key)));
				}
			}
			if(null != JSESSIONID){
				httppost.setHeader("Cookie", "JSESSIONID=" + JSESSIONID);
	        }
		    httppost.setEntity(mpEntity);
		    String json=httpclient.execute(httppost, responseHandler);
		    CookieStore mCookieStore = httpclient.getCookieStore();
			List<Cookie> cookies = mCookieStore.getCookies();
			for (int i = 0; i < cookies.size(); i++) {
		        if ("JSESSIONID".equals(cookies.get(i).getName())) {
		        	JSESSIONID = cookies.get(i).getValue();
		        	break;
		        }
	        }
		    System.out.println("上传文件信息："+json);
			return json;
		}catch (Exception e) {
			throw new Exception("请求服务器失败",e);
		}finally {
			abortConnection(httppost,httpclient);
		}
	}
	
	public static void ajax(String url, AjaxCallBack callBack) {
		AjaxRequest request = new AjaxRequest(url);
		ajaxExecutor.submit(new AjaxTask(callBack, request));
	}
	
	private static AjaxStatus ajax(AjaxRequest request) {
		if (request == null ) {
			return null;
		}
		String url = patchUrl(request.getUrl());
		String charset = request.getCharset();
		DefaultHttpClient httpclient = getDefaultHttpClient(charset);
		UrlEncodedFormEntity formEntity = null;
		try {
			List<NameValuePair> paramList = getParamsList(request.getParams());
			if(paramList!=null && paramList.size()>0){
				if (charset == null || charset.trim().length()==0) {
					formEntity = new UrlEncodedFormEntity(getParamsList(request.getParams()));
				} else {
					formEntity = new UrlEncodedFormEntity(getParamsList(request.getParams()),charset);
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpPost hp = new HttpPost(url);
		hp.setEntity(formEntity);
		try {
			return httpclient.execute(hp,ajaxHandler).setUrl(request.getUrl());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			abortConnection(hp, httpclient);
		}
		return null;
	}
	
	public static String postSend(String url,String data,String charset) {
		if (url == null || url.trim().length()==0) {
			return null;
		}
		DefaultHttpClient httpclient = getDefaultHttpClient(charset);
		StringEntity formEntity = null;
		try {
			if (charset == null || charset.trim().length()==0) {
				formEntity = new StringEntity(data);
			} else {
				formEntity = new StringEntity(data,charset);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpPost hp = new HttpPost(url);
		hp.setEntity(formEntity);
		try {
			return httpclient.execute(hp, responseHandler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			abortConnection(hp, httpclient);
		}
		return null;
	}
	
	
	private static DefaultHttpClient getDefaultHttpClient(final String charset) {
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		httpclient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,charset == null ? CHARSET_UTF8 : charset);
		httpclient.setHttpRequestRetryHandler(requestRetryHandler);
		
		return httpclient;
	}

	private static void abortConnection(final HttpRequestBase hrb,final HttpClient httpclient) {
		if (hrb != null) {
			hrb.abort();
		}
		if (httpclient != null) {
			httpclient.getConnectionManager().shutdown();
		}
	}

	private static List<NameValuePair> getParamsList(Map<String, String> paramsMap) {
		if (paramsMap == null) {
			return null;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> map : paramsMap.entrySet()) {
			params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
		}
		return params;
	}
	
	
	private static String patchUrl(String url){
		return url.replaceAll(" ", "%20").replaceAll("\\|", "%7C");
	}
	
	
	
	static class AjaxTask implements Runnable{
		final private AjaxCallBack mCallBack;
		final private AjaxRequest request;
		
		final private Handler mHandler= new Handler(){
			public void handleMessage(Message msg) {
				mCallBack.callBack((AjaxStatus)msg.obj);
			}
		};
		
		public AjaxTask(AjaxCallBack callBack, AjaxRequest request) {
			this.mCallBack = callBack;
			this.request = request;
		}

		public void run() {
			Message msg = new Message();
			msg.obj = ajax(request);
			mHandler.sendMessage(msg);
		}
	}
}