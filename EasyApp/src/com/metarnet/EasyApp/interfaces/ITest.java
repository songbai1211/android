package com.metarnet.EasyApp.interfaces;

import com.metarnet.EasyApp.proxy.ProxyTest;
import com.yanfan.easyIOC.ioc.inject.Aop;

public interface ITest {
	
	@Aop(proxy=ProxyTest.class,before="before",after="after")
	public String sayHello();
}
