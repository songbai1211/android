package com.yanfan.easyIOC;

import java.io.File;
import java.lang.reflect.Field;
import com.yanfan.easyIOC.config.RequestCode;
import com.yanfan.easyIOC.exception.IOCException;
import com.yanfan.easyIOC.ioc.ProxyIOC;
import com.yanfan.easyIOC.system.AppManager;
import com.yanfan.easyIOC.ui.EventListener;
import com.yanfan.easyIOC.ui.RefreshListView;
import com.yanfan.easyIOC.ui.inject.Desktop;
import com.yanfan.easyIOC.ui.inject.InjectView;
import com.yanfan.easyIOC.util.FileUtils;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
/**
 * 应用程序Activity的基类
 * @author 代码狂
 */
public abstract class EasyActivity extends FragmentActivity implements RefreshListView.IOnLoadMoreListener,RefreshListView.IOnRefreshListener,OnGestureListener{
	private boolean savePage=false;
	
	private static final int FLEEP_DISTANCE = 120;
	private GestureDetector gestureDetector;
	
	/**
	 * 启动视图时绑定数据
	 */
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		AppManager.addActivity(this);
		System.out.println("启动"+this.getLocalClassName());
		initGestureDetector();
		new Thread(new Runnable() {
			public void run() {
				try {
					initView();
					init();
				} catch (IOCException e) {
					Log.e("IOC", "初始化失败");
					e.printStackTrace();
					AppManager.Exit();
				}catch (Exception e) {
					Log.e("init", "初始化失败");
					e.printStackTrace();
					AppManager.Exit();
				}
			}
		}).start();
	}
	/**
	 * 设置页面的响应
	 */
	public void initGestureDetector()
	{
		gestureDetector = new GestureDetector(getApplicationContext(),this);
		new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};
	}
	
	@Override
	protected void onDestroy() {
		System.out.println("关闭"+this.getLocalClassName());
		AppManager.finishActivity(this);
		super.onDestroy();
	}
	/**
	 * 初始化注入所有的控件
	 * @throws IOCException 
	 */
	private void initView() throws IOCException
	{
		System.out.println("依赖注入开始");
		Desktop desktop = getClass().getAnnotation(Desktop.class);
		if(desktop!=null){SavePage();}
		try {
			/**
			 * 自动创建Bean，注入数据
			 */
			ProxyIOC.setAutowired(this);
			Field[] fields = getClass().getDeclaredFields();
			for(Field field : fields){
				InjectView Injectview = field.getAnnotation(InjectView.class);
				/**
				 * 如果存在需要注入的<控件>则进行注入
				 */
				if(Injectview!=null){
					int viewId = Injectview.id();
					field.setAccessible(true);
					field.set(this,this.findViewById(viewId));
					/**
					 * 绑定监听
					 */
					String clickMethod = Injectview.click();
					if(!TextUtils.isEmpty(clickMethod))
					{
						setViewClickListener(field,clickMethod);
						setViewLongClickListener(field,clickMethod);
					}
					/**
					 * 设置菜单的监听事件
					 */
					String itemClickMethod = Injectview.itemClick();
					if(!TextUtils.isEmpty(itemClickMethod))
					{
						setItemClickListener(field,itemClickMethod);
						setItemLongClickListener(field,itemClickMethod);
					}
					/**
					 * 下拉框的监听
					 */
					if(!TextUtils.isEmpty(Injectview.selected()) || !TextUtils.isEmpty(Injectview.noSelected()))
					{
						setViewSelectListener(field,Injectview.selected(),Injectview.noSelected());
					}
					/**
					 * 设置ListView
					 */
					boolean listview = Injectview.Listview();
					if(listview)
					{
						setListViewListener(field);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOCException("初始化视图失败",e);
		}
		System.out.println("依赖注入完成");
	}
	/**
	 * 依赖注入，设置ListView的下拉刷新监听
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void setListViewListener(Field field) throws IllegalArgumentException, IllegalAccessException {
		Object obj = field.get(this);
		if(obj instanceof View){
			((RefreshListView)obj).setOnRefreshListener(this);
			((RefreshListView)obj).setOnLoadMoreListener(this);
		}
	}

	/**
	 * 依赖注入，绑定监听其他控件
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void setViewClickListener(Field field,String clickMethod) throws IllegalArgumentException, IllegalAccessException{
		Object obj = field.get(this);
		if(obj instanceof View){
			((View)obj).setOnClickListener(new EventListener(this).click(clickMethod));
		}
	}
	/**
	 * 依赖注入，绑定监听其他控件(长按)
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void setViewLongClickListener(Field field,String clickMethod) throws IllegalArgumentException, IllegalAccessException{
		Object obj = field.get(this);
		if(obj instanceof View){
			((View)obj).setOnLongClickListener(new EventListener(this).longClick(clickMethod));
		}
	}
	/**
	 * 依赖注入，绑定监听(ListView)
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void setItemClickListener(Field field,String itemClickMethod) throws IllegalArgumentException, IllegalAccessException{
		Object obj = field.get(this);
		if(obj instanceof AbsListView){
			((AbsListView)obj).setOnItemClickListener(new EventListener(this).itemClick(itemClickMethod));
		}
	}
	/**
	 * 依赖注入，绑定监听(菜单)(长按)
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void setItemLongClickListener(Field field,String itemClickMethod) throws IllegalArgumentException, IllegalAccessException{
		Object obj = field.get(this);
		if(obj instanceof AbsListView){
			((AbsListView)obj).setOnItemLongClickListener(new EventListener(this).itemLongClick(itemClickMethod));
		}
	}
	/**
	 * 依赖注入，绑定监听(下拉框)
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void setViewSelectListener(Field field,String select,String noSelect) throws IllegalArgumentException, IllegalAccessException{
		Object obj = field.get(this);
		if(obj instanceof View){
			EventListener listener = new EventListener(this);
			if(!TextUtils.isEmpty(select))
			{
				listener.select(select);
			}
			if(!TextUtils.isEmpty(noSelect))
			{
				listener.noSelect(noSelect);
			}
			((AbsListView)obj).setOnItemSelectedListener(listener);
		}
	}
	public void SavePage()
	{
		savePage=true;
	}
	/**
	 * 返回键事件处理
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果是返回键,直接返回到桌面 
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(savePage)
			{
				Intent i= new Intent(Intent.ACTION_MAIN); 
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); 
				i.addCategory(Intent.CATEGORY_HOME); 
				startActivity(i); 
				return false;
			}else
			{
				Activity activity = AppManager.callBack();
				if(activity!=null)
				{
					startActivity(activity.getIntent());
				}else
				{
					AppManager.Exit();
				}
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (gestureDetector!=null && gestureDetector.onTouchEvent(event)) {
			event.setAction(MotionEvent.ACTION_CANCEL);
		}
		return super.dispatchTouchEvent(event);
	}
	public boolean onDown(MotionEvent e) {
		// TODO 自动生成的方法存根
		return false;
	}
	public void onShowPress(MotionEvent e) {
		// TODO 自动生成的方法存根
		
	}
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO 自动生成的方法存根
		return false;
	}
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO 自动生成的方法存根
		return false;
	}
	public void onLongPress(MotionEvent e) {
		// TODO 自动生成的方法存根
		
	}
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() <= (-FLEEP_DISTANCE)) {//从左向右滑动
			LeftToRight();
		} else if (e1.getX() - e2.getX() >= FLEEP_DISTANCE) {//从右向左滑动
			RightToLeft();
		}
		return false;
	}
	/**
	 * 从左向右滑动
	 */
	public abstract void LeftToRight();
	/**
	 * 从右向左滑动
	 */
	public abstract void RightToLeft();
	
	/**
	 * 点击事件后的回调函数
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode == RESULT_OK)
		{
			switch (requestCode) {
			case RequestCode.CAMERA_REQUESTCODE:
			{
				Bitmap bitmap = (Bitmap) data.getParcelableExtra("data");
				openCameraCallBack(bitmap);
				break;
			}
			case RequestCode.IMAGE_REQUESTCODE:
			{
				Bitmap bitmap = (Bitmap) data.getParcelableExtra("data");
				openPhotosCallBack(bitmap);
				break;
			}
			case RequestCode.CRONPHOTO_REQUESTCODE:
			{
				Bitmap bitmap = (Bitmap) data.getParcelableExtra("data");
				openCropPhotoCallBack(bitmap);
				break;
			}
			case RequestCode.FILE_REQUESTCODE:
			{
				Uri uri = data.getData();
				File file = new File(FileUtils.getAbsoluteImagePath(uri));
				openFileCallBack(file);
				break;
			}
			case RequestCode.SOUND_REQUESTCODE:
			{
				Uri uri = data.getData();
				File file = new File(FileUtils.getAbsoluteImagePath(uri));
				openSoundCallBack(file);
				break;
			}
			case RequestCode.VIDEO_REQUESTCODE:
			{
				Uri uri = data.getData();
				File file = new File(FileUtils.getAbsoluteImagePath(uri));
				openVideoCallBack(file);
				break;
			}
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 初始化页面
	 */
	protected abstract void init()throws Exception;
	/**
	 * 调用摄像头后返回的数据
	 */
	protected void openCameraCallBack(Bitmap bitmap){}
	/**
	 * 调用系统相册返回数据
	 */
	protected void openPhotosCallBack(Bitmap bitmap){}
	/**
	 * 调用系统剪裁图片后返回
	 */
	protected void openCropPhotoCallBack(Bitmap bitmap){}
	/**
	 * 调用文件管理器返回文件
	 */
	protected void openFileCallBack(File file){}
	/**
	 * 调用系统录音功能
	 */
	protected void openSoundCallBack(File file){}
	/**
	 * 调用系统录像功能
	 */
	protected void openVideoCallBack(File file){}
	/**
	 * ListView
	 * 刷新（在顶部往下拉）
	 */
	public boolean List_OnRefresh(RefreshListView view){return true;};
	/**
	 * ListView
	 * 加载更多（在底部点击More）
	 */
	public boolean List_OnLoadMore(RefreshListView view){return true;};
}
