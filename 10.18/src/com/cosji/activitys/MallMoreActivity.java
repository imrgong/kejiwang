package com.cosji.activitys;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cosji.adapter.MallMoreAdapter;
import com.cosji.application.Base;
import com.cosji.utils.HttpConnectionHepler;
import com.cosji.utils.SettingUtils;
import com.cosji.utils.ToastUtil;
import com.cosji.utils.Url;
import com.cosji.view.PullToRefreshView;
import com.cosji.view.PullToRefreshView.OnFooterRefreshListener;
import com.cosji.view.PullToRefreshView.OnHeaderRefreshListener;
import com.umeng.analytics.MobclickAgent;

public class MallMoreActivity extends BaseActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {
	GridView list;
	private static final int REFRESH_LIST = 0x10011;
	PullToRefreshView mPullToRefreshView;
MallMoreAdapter adapter;
	String lastupdatetime = null;
	boolean isloading=false;
	int num,page=1;
	int data_total=30;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new MallMoreAdapter(MallMoreActivity.this,false);
		setContentView(R.layout.mall_more);

		initView();  
		 setData(24,1);
		lastupdatetime = udpatetime();
	}
	
	private void initView() {
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.mall_more_pull_refresh_view);
		LinearLayout back = (LinearLayout) findViewById(R.id.web_back);
		TextView textview = (TextView) findViewById(R.id.mine_secend_title);
		textview.setText("更多商城");
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		list = (GridView) findViewById(R.id.mall_more_list);
		
		list.setAdapter(adapter);// 设置菜单Adapter
		list.setOnScrollListener(mScrollListener);
		 
	}
	OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_FLING:
				adapter.setFlagBusy(true);
				break;
			case OnScrollListener.SCROLL_STATE_IDLE:
				adapter.setFlagBusy(false);
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				adapter.setFlagBusy(false);
				break;
			default:
				break;
			}
			adapter.notifyDataSetChanged();
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}
	};
public void setData(int n,int page){
	int size=adapter.getData().size();
	if (size<data_total) {
		int a=data_total-size;;
		if (n>=a) {
			this.num=a;
		}
		else{
			this.num=n;
		}
		this.page=page;
		//DataInit
		if (Base.isNetworkConnected(MallMoreActivity.this)) {
		 new Thread(mallrunnable).start();
		 SettingUtils.startdialog(MallMoreActivity.this);
		}
		else{
			ToastUtil.showShortToast(MallMoreActivity.this, "网络出错,请检测网络...");
		}
	}
	else{
		Toast.makeText(MallMoreActivity.this, "商城加载完毕,进入你想进入的商城购物吧...",2000).show();
	}
	
}
	Runnable mallrunnable = new Runnable() {
		@Override
		public void run() {
		try {
			
			HttpConnectionHepler th=new HttpConnectionHepler();
	
			List<NameValuePair> a=new ArrayList<NameValuePair>();
			  a.add(new BasicNameValuePair("num", num+""));
				  a.add(new BasicNameValuePair("page", page+""));
	
	          adapter.setData(th.sendPostReArray(MallMoreActivity.this,Url.mallmoreapi, a,"0"));
	          isloading=true;
	          data_total=th.getData_total();
	         sendMessage(REFRESH_LIST);
					 
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
		};
		protected void handleOtherMessage(int flag) {
			// TODO Auto-generated method stub
			switch (flag) {
			case REFRESH_LIST:
				SettingUtils.mProgressDialog.dismiss();
				adapter.notifyDataSetChanged();		
				
				break;
			default:
				break;
			}
		}
		//异步
			class MyTashkRefresh extends AsyncTask<String, Integer, String>{

				@Override
				protected String doInBackground(String... params) {
					return "error";
				}
				@Override
				protected void onPostExecute(String result) {
					if(result.equals("success")){
			
					}
				}
				//异步线程开启之前所做操作
				@Override
				protected void onPreExecute() {
					
				}

				//异步线程运行之中所做
				@Override
				protected void onProgressUpdate(Integer... values) {
					// TODO Auto-generated method stub
					super.onProgressUpdate(values);
				}
			}
	/**
	 * 刷新，先清空list中数据然后重新加载更新内容
	 */
	public void onRefresh() {
		adapter.getData().clear();
		setData(24,1);
	}

	/**
	 * 加载更多，在原来基础上在添加新内容
	 */
	public void onLoadMore() {
			setData(24,++page);
			  

	}

	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			public void run() {
				onLoadMore();
				mPullToRefreshView.onFooterRefreshComplete();
				lastupdatetime = udpatetime();
			}
		}, 1000);
	}

	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			public void run() {
				// 璁剧疆鏇存柊鏃堕棿
				// mPullToRefreshView.onHeaderRefreshComplete("鏈�繎鏇存柊:01-23 12:01");
				onRefresh();
				mPullToRefreshView.onHeaderRefreshComplete(lastupdatetime);
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);
	}

	public String udpatetime() {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy.MM.dd   HH:mm:ss");

		// String data[] = { "23.5", "167.7", "98", "26", "45" };
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(curDate);

	}
protected void onResume() {
		
		super.onResume();
		MobclickAgent.onResume(MallMoreActivity.this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(MallMoreActivity.this);
	}
}
