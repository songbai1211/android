package com.metarnet.EasyApp;

import com.metarnet.EasyApp.R;
import com.metarnet.EasyApp.interfaces.ITest;
import com.metarnet.EasyApp.interfaces.impr.Test2;
import com.yanfan.easyIOC.EasyActivity;
import com.yanfan.easyIOC.ioc.inject.Autowired;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends EasyActivity {
	
	@Autowired(classtype=Test2.class)
	public ITest test;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void LeftToRight() {
	}

	@Override
	public void RightToLeft() {
	}
	
	public void onClick(View view) {
		
	}
	
	@Override
	protected void init() throws Exception {
		System.out.println("最终获取到的是："+test.sayHello());
	}
}
