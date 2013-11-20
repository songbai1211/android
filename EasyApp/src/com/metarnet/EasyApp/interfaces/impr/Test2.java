package com.metarnet.EasyApp.interfaces.impr;

import com.metarnet.EasyApp.interfaces.IT_T;
import com.metarnet.EasyApp.interfaces.ITest;
import com.yanfan.easyIOC.ioc.inject.Autowired;

public class Test2 implements ITest{
	
	@Autowired(classtype=T_T1.class)
	public IT_T t_t;

	public String sayHello() {
		System.out.println("Test2");
		return t_t.hello();
	}

}
