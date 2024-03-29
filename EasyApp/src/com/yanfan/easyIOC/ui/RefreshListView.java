package com.yanfan.easyIOC.ui;

import java.util.Date;
import com.metarnet.EasyApp.R;
import com.yanfan.easyIOC.util.DateUtil;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;


public class RefreshListView extends ListView implements OnScrollListener,OnClickListener{

	/**
	 * 下拉刷新接口
	 */
	public interface IOnRefreshListener{
		public boolean List_OnRefresh(RefreshListView view);
	}
	/**
	 * 点击加载更多接口
	 */
	public interface IOnLoadMoreListener{
		public boolean List_OnLoadMore(RefreshListView view);
	}
	/**
	 * 初始化
	 */
	public RefreshListView(Context context) {
		super(context);
		init(context);
	}
	/**
	 * 初始化
	 */
	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	/**
	 * 初始化
	 */
	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	/**
	 * 记录更新状态
	 */
	public interface IListViewState
	{
		int LVS_NORMAL = 0;					//  普通状态
		int LVS_PULL_REFRESH = 1;			//  下拉刷新状态
		int LVS_RELEASE_REFRESH = 2;		//  松开刷新状态
		int LVS_LOADING = 3;				//  加载状态
		
		int LMVS_NORMAL= 0;					//  普通状态
		int LMVS_LOADING = 1;				//  加载状态
		int LMVS_OVER = 2;					//  结束状态
	}
	private IOnRefreshListener mOnRefreshListener;// 头部刷新监听器
	private IOnLoadMoreListener mLoadMoreListener;// 加载更多监听器
	private Activity activity;
	
	private View mHeadView;				
	private TextView mRefreshTextview;
	private TextView mLastUpdateTextView;
	private ImageView mArrowImageView;
	private ProgressBar mHeadProgressBar;
	private int mHeadContentHeight;
	
	private View mFootView;								
	private View mLoadMoreView;
	private TextView mLoadMoreTextView;
	private View mLoadingView;
	
	private int mLoadMoreState = IListViewState.LVS_NORMAL;
	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean mIsRecord = false;
	// 标记的Y坐标值
	private int mStartY = 0;
	// 当前视图能看到的第一个项的索引
	private int mFirstItemIndex = -1;
	// MOVE时保存的Y坐标值
	private int mMoveY = 0;
	// LISTVIEW状态
	private int mViewState = IListViewState.LVS_NORMAL;

	private final static int RATIO = 2;
	
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	private boolean mBack = false;
	
	/**
	 * 设置下拉刷新监听
	 */
	public void setOnRefreshListener(Activity activity)
	{
		this.activity = activity;
		mOnRefreshListener = (IOnRefreshListener) activity;
	}
	/**
	 * 设置加载更多监听
	 */
	public void setOnLoadMoreListener(Activity activity)
	{
		mLoadMoreListener = (IOnLoadMoreListener) activity;
		this.activity = activity;
	}
	
	private void init(Context context)
	{
		initHeadView(context);
		initLoadMoreView(context);
		setOnScrollListener(this);
	}
	
	
	// 初始化headview试图
	private void initHeadView(Context context)
	{
		mHeadView = LayoutInflater.from(context).inflate(R.layout.listview_head, null);
		mArrowImageView = (ImageView) mHeadView.findViewById(R.id.head_arrowImageView);
		mArrowImageView.setMinimumWidth(60);
		mHeadProgressBar= (ProgressBar) mHeadView.findViewById(R.id.head_progressBar);
		mRefreshTextview = (TextView) mHeadView.findViewById(R.id.head_tipsTextView);
		mLastUpdateTextView = (TextView) mHeadView.findViewById(R.id.head_lastUpdatedTextView);
		measureView(mHeadView);
		mHeadContentHeight = mHeadView.getMeasuredHeight();
		mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
		mHeadView.invalidate();
		addHeaderView(mHeadView, null, false);
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);
		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);
	}
	
	// 初始化footview试图
	private void initLoadMoreView(Context context)
	{
		mFootView= LayoutInflater.from(context).inflate(R.layout.listview_more, null);
		mLoadMoreView = mFootView.findViewById(R.id.load_more_view);
		mLoadMoreTextView = (TextView) mFootView.findViewById(R.id.load_more_tv);
		mLoadingView = mFootView.findViewById(R.id.loading_layout);
		mLoadMoreView.setOnClickListener(this);
		addFooterView(mFootView);
	}
	
	// 此方法直接照搬自网络上的一个下拉刷新的demo，计算headView的width以及height
	@SuppressWarnings("deprecation")
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}


	public void onScroll(AbsListView arg0, int firstVisiableItem, int visibleItemCount, int totalItemCount){mFirstItemIndex = firstVisiableItem;}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {}

	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mOnRefreshListener != null)
		{
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:		
				doActionDown(ev);
				break;
			case MotionEvent.ACTION_MOVE:
				doActionMove(ev);
				break;
			case MotionEvent.ACTION_UP:
				doActionUp(ev);
				break;
			default:
				break;
			}
		}
		return super.onTouchEvent(ev);
	}
	
	/**
	 * 点击
	 */
	private void doActionDown(MotionEvent ev)
	{
		if(mIsRecord == false && mFirstItemIndex == 0)
		{
			mStartY = (int) ev.getY();
			mIsRecord = true;
		}
	}
	/**
	 * 移动
	 */
	private void doActionMove(MotionEvent ev)
	{
		mMoveY = (int) ev.getY();
		if(mIsRecord == false && mFirstItemIndex == 0)
		{
			mStartY = (int) ev.getY();
			mIsRecord = true;
		}
		if (mIsRecord == false || mViewState == IListViewState.LVS_LOADING)
		{
			return ;
		}	
		int offset = (mMoveY - mStartY) / RATIO;	
		switch(mViewState)
		{
			case IListViewState.LVS_NORMAL:
			{
				if (offset > 0)
				{		
					mHeadView.setPadding(0, offset - mHeadContentHeight, 0, 0);
					switchViewState(IListViewState.LVS_PULL_REFRESH);
				}
				break;
			}
			case IListViewState.LVS_PULL_REFRESH:
			{
				setSelection(0);
				mHeadView.setPadding(0, offset - mHeadContentHeight, 0, 0);
				if (offset < 0)
				{
					switchViewState(IListViewState.LVS_NORMAL);
				}else if (offset > mHeadContentHeight)
				{
					switchViewState(IListViewState.LVS_RELEASE_REFRESH);
				}
				break;
			}
			case IListViewState.LVS_RELEASE_REFRESH:
			{
				setSelection(0);
				mHeadView.setPadding(0, offset - mHeadContentHeight, 0, 0);
				if (offset >= 0 && offset <= mHeadContentHeight)
				{
					mBack = true;
					switchViewState(IListViewState.LVS_PULL_REFRESH);
				}else if (offset < 0)
				{
					switchViewState(IListViewState.LVS_NORMAL);
				}else{
				
				}
				break;
			}
			default:
				return;
		};
	}
	/**
	 * 手指离开
	 */
	private void doActionUp(MotionEvent ev)
	{
		mIsRecord = false;
		mBack = false;
		if (mViewState == IListViewState.LVS_LOADING)
		{
			return ;
		}
		switch(mViewState)
		{
			case IListViewState.LVS_NORMAL:
			
				break;
			case IListViewState.LVS_PULL_REFRESH:
			{
				mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
				switchViewState(IListViewState.LVS_NORMAL);
				break;
			}
			case IListViewState.LVS_RELEASE_REFRESH:
			{
				final RefreshListView view = this;
				mHeadView.setPadding(0, 0, 0, 0);
				switchViewState(IListViewState.LVS_LOADING);
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						mOnRefreshListener.List_OnRefresh(view);
					}
				});
				thread.start();
				try {thread.join();} catch (InterruptedException e) {e.printStackTrace();}
				mHeadView.setPadding(0,  -1 * mHeadContentHeight, 0, 0);
				mLastUpdateTextView.setText("最近更新:" + DateUtil.format(new Date()));
				switchViewState(IListViewState.LVS_NORMAL);
				break;
			}
		}
	}
	

	// 切换headview视图
	private void switchViewState(int state)
	{
		switch(state)
		{
			case IListViewState.LVS_NORMAL:
			{
				mArrowImageView.clearAnimation();
				mArrowImageView.setImageResource(R.drawable.refresh_arrow_up);
				break;
			}
			case IListViewState.LVS_PULL_REFRESH:
			{
				mHeadProgressBar.setVisibility(View.GONE);
				mArrowImageView.setVisibility(View.VISIBLE);
				mRefreshTextview.setText("下拉可以刷新");
				mArrowImageView.clearAnimation();
				if (mBack)
				{
					mBack = false;
					mArrowImageView.clearAnimation();
					mArrowImageView.startAnimation(reverseAnimation);
				}
				break;
			}
			case IListViewState.LVS_RELEASE_REFRESH:
			{
				mHeadProgressBar.setVisibility(View.GONE);
				mArrowImageView.setVisibility(View.VISIBLE);
				mRefreshTextview.setText("松开获取更多");
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(animation);
				break;
			}
			case IListViewState.LVS_LOADING:
			{
				mHeadProgressBar.setVisibility(View.VISIBLE);
				mArrowImageView.clearAnimation();
				mArrowImageView.setVisibility(View.GONE);
				mRefreshTextview.setText("载入中...");
				break;
			}
			default:
				return;
		}
		mViewState = state;
	}
	
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.load_more_view:
			{
				if (mLoadMoreListener != null && mLoadMoreState == IListViewState.LVS_NORMAL)
				{
					final RefreshListView view = this;
					updateLoadMoreViewState(IListViewState.LMVS_LOADING);
					new Thread(new Runnable() {
						public void run() {
							final boolean flag = mLoadMoreListener.List_OnLoadMore(view);
							view.activity.runOnUiThread(new Runnable() {
								public void run() {
									if(flag)
									{
										updateLoadMoreViewState(IListViewState.LMVS_NORMAL);
									}else
									{
										updateLoadMoreViewState(IListViewState.LMVS_OVER);
									}
								}
							});
						}
					}).start();
				}
			}
			break;
		}
	}
	/**
	 * 更新footview视图
	 */
	private void updateLoadMoreViewState(int state)
	{
		switch(state)
		{
			case IListViewState.LMVS_NORMAL:
				mLoadingView.setVisibility(View.GONE);
				mLoadMoreTextView.setVisibility(View.VISIBLE);
				mLoadMoreTextView.setText("查看更多");
				break;
			case IListViewState.LMVS_LOADING:
				mLoadingView.setVisibility(View.VISIBLE);
				mLoadMoreTextView.setVisibility(View.GONE);
				break;
			case IListViewState.LMVS_OVER:
				mLoadingView.setVisibility(View.GONE);
				mLoadMoreTextView.setVisibility(View.VISIBLE);
				mLoadMoreTextView.setText("数据都加载完了！");
				break;
				default:
					break;
		}
		mLoadMoreState = state;
	}
	public void removeFootView()
	{
		removeFooterView(mFootView);
	}
}
