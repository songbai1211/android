package com.yanfan.easyIOC.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AjaxRequest {

	private String charset = "utf-8";
	private String url;
	private String contentType;
	private byte[] content;
	private Map<String, String> headers;
	private Map<String, String> params;
	private Map<String, String> cookies;
	
	public AjaxRequest(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	public AjaxRequest setUrl(String url) {
		this.url = url;
		return this;
	}
	public String getContentType() {
		return contentType;
	}
	public AjaxRequest setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}
	public byte[] getContent() {
		return content;
	}
	public AjaxRequest setContent(byte[] content) {
		this.content = content;
		return this;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public AjaxRequest setHeaders(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public AjaxRequest setParams(Map<String, String> params) {
		this.params = params;
		return this;
	}

	public String getCharset() {
		return charset;
	}

	public AjaxRequest setCharset(String charset) {
		this.charset = charset;
		return this;
	}
	
	public AjaxRequest cookie(String name, String value){
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
