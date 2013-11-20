package com.metarnet.EasyApp.interfaces.impr;

import com.metarnet.EasyApp.interfaces.ITest;

public class Test1 implements ITest{

	@Override
	public String sayHello() {
		System.out.println("Test1");
		return getClass().getSimpleName();
	}

}
