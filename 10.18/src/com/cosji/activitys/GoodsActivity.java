package com.cosji.activitys;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cosji.adapter.GoodsListAdapter;
import com.cosji.application.Base;
import com.cosji.bean.TbkItem;
import com.cosji.tbkapihelper.APITest;
import com.cosji.tbkapihelper.JsonUtil;
import com.cosji.tbkapihelper.Utils;
import com.cosji.utils.SettingUtils;
import com.cosji.utils.ToastUtil;
import com.cosji.view.PullDownListView;
import com.umeng.analytics.MobclickAgent;

public class GoodsActivity extends BaseActivity implements
		PullDownListView.OnRefreshListioner {
	
	private static final int REFRESH_LIST = 0x10001;
	private PullDownListView mPullDownView;
	ListView listview;
	private Handler mHandler = new Handler();

	private GoodsListAdapter adapter;
    private String goodtypes;
    int page=1;
    int data_total=15;
    private List<TbkItem> data;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tao_goods_list);
		adapter = new GoodsListAdapter(GoodsActivity.this);
		Intent in=getIntent();
		data = new ArrayList<TbkItem>();
		goodtypes=in.getStringExtra("goods_types").toString().trim();
		 GetData(1);
		initView();
	}
	/*
	 * 淘宝商品列表页面初始化
	 */
	public void initView() {

		mPullDownView = (PullDownListView) findViewById(R.id.sreach_list);
		mPullDownView.setRefreshListioner(this);
		listview = mPullDownView.mListView;
		
		mPullDownView.setMore(true);// 这里设置true表示还有更多加载，设置为false底部将不显示更多
		listview.setAdapter(adapter);
		
		LinearLayout back = (LinearLayout) findViewById(R.id.tao_goods_back);
		back.setOnClickListener(lin);
		
		TextView tv = (TextView) findViewById(R.id.tao_goods_titlename);
		listview.setOnScrollListener(mScrollListener);
		tv.setText(goodtypes);
		
		//在这里取得数据
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
	OnClickListener lin = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.tao_goods_back:
            finish();
				break;
			
			default:
				break;
			}
		}
	};

	private void GetData(final int page) {
		
				this.page=page;
				if (Base.isNetworkConnected(GoodsActivity.this)) {
					SettingUtils.startdialog(GoodsActivity.this);
					new Thread(r).start();
				}
				else{
					ToastUtil.showShortToast(GoodsActivity.this, "网络出错,请检测网络...");
				}
		}
	Runnable r = new Runnable() {
		@Override
		public void run() {
			try {
				TreeMap<String, String> apiparamsMap = new TreeMap<String, String>();
				apiparamsMap.put("keyword",goodtypes);
				apiparamsMap.put("page_no", page+"");
				apiparamsMap.put("page_size","15");
//				Util util = new Util();
//				data = util.getResult( APITest.get(apiparamsMap));
				
				JsonUtil json2Object = new JsonUtil();
				String result = Utils.getItems(APITest.get(apiparamsMap));
				data_total = json2Object.getTotle(result);
				System.out.println("商品总条数:"+data_total);
				data = json2Object.jsonToObject(result);
			    adapter.setData(data);
			    sendMessage(REFRESH_LIST);
				} catch (Exception e) {
				// TODO: handle exception
			}
		  }
		};
			@Override
			protected void handleOtherMessage(int flag) {
				// TODO Auto-generated method stub
				switch (flag) {
				case REFRESH_LIST:
				if (page==1) {
					adapter.notifyDataSetChanged();
				}
		            SettingUtils.mProgressDialog.dismiss();  
					break;
				}
			}
	/**
	 * 刷新，先清空list中数据然后重新加载更新内容
	 */
	public void onRefresh() {

		mHandler.postDelayed(new Runnable() {

			public void run() {
				adapter.getData().clear();	
				if (adapter.getData().size() < data_total) {// 判断当前list中已添加的数据是否小于最大值maxAount，是那么久显示更多否则不显示
					GetData(1);// 每次加载五项新内容
					
				}
				
				mPullDownView.onRefreshComplete();// 这里表示刷新处理完成后把上面的加载刷新界面隐藏
				mPullDownView.setMore(true);// 这里设置true表示还有更多加载，设置为false底部将不显示更多
				adapter.notifyDataSetChanged();

			}
		}, 1500);

	}

	/**
	 * 加载更多，在原来基础上在添加新内容
	 */
	public void onLoadMore() {

		mHandler.postDelayed(new Runnable() {
			public void run() {
				if (adapter.getData().size() < data_total) {// 判断当前list中已添加的数据是否小于最大值maxAount，是那么久显示更多否则不显示
					GetData(++page);// 每次加载五项新内容
				
				}
				mPullDownView.onLoadMoreComplete();// 这里表示加载更多处理完成后把下面的加载更多界面（隐藏或者设置字样更多）
				if (adapter.getData().size() < data_total)// 判断当前list中已添加的数据是否小于最大值maxAount，是那么久显示更多否则不显示
					mPullDownView.setMore(true);// 这里设置true表示还有更多加载，设置为false底部将不显示更多
				else
					mPullDownView.setMore(false);
				adapter.notifyDataSetChanged();

			}
		}, 1500);
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(GoodsActivity.this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(GoodsActivity.this);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode ) {
		SettingUtils.mProgressDialog.dismiss();
			return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	};
}


