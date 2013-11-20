package com.yanfan.easyIOC.network;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AjaxStatus {

	private int status;
	private String url ; 
	private byte[] content;
    private Map<String, String> headers;
	private Map<String, String> cookies;
    
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getUrl() {
		return url;
	}
	public AjaxStatus setUrl(String url) {
		this.url = url;
		return this;
	}
	public byte[] getContent() {
		return content;
	}
	
	public String getContentAsString() {
		return content==null?null:new String(content);
	}
	
	public AjaxStatus setContent(byte[] content) {
		this.content = content;
		return this;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	public AjaxStatus setHeaders(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}
	public Map<String, String> getCookies() {
		return cookies;
	}
	public AjaxStatus setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
		return this;
	}
	
	
	public AjaxStatus cookie(String name, String value){
		if(cookies == null){
			cookies = new HashMap<String, String>();
		}
		cookies.put(name, value);
		return this;
	}
	
	
	public String makeCookie(){
		if(cookies == null || cookies.size() == 0) return null;
		Iterator<String> iter = cookies.keySet().iterator();
		
		StringBuilder sb = new StringBuilder();
		
		while(iter.hasNext()){
			String key = iter.next();
			String value = cookies.get(key);
			sb.append(key);
			sb.append("=");
			sb.append(value);
			if(iter.hasNext()){
				sb.append("; ");
			}
		}
		return sb.toString();
	}
	
}
