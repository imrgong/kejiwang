package com.cosji.activitys;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cosji.adapter.JiuYuanAdapter;
import com.cosji.application.Base;
import com.cosji.utils.Exit;
import com.cosji.utils.HttpConnectionHepler;
import com.cosji.utils.SettingUtils;
import com.cosji.utils.ToastUtil;
import com.cosji.utils.Url;
import com.cosji.view.CustomDialog;
import com.cosji.view.PullToRefreshView;
import com.cosji.view.PullToRefreshView.OnFooterRefreshListener;
import com.cosji.view.PullToRefreshView.OnHeaderRefreshListener;
import com.umeng.analytics.MobclickAgent;

public class JiuYuanActivity extends BaseActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {
	GridView grid;
	PullToRefreshView mPullToRefreshView;
    boolean isfirstloading=false;
	//ArrayList<JiuYuantiem> data = new ArrayList<JiuYuantiem>();
	int n,page=1,more_num=12;
	private static final int REFRESH_LIST = 0x10111;
	JiuYuanAdapter adapter;
	String lastupdatetime = null;
	int data_total=18;
	private RelativeLayout zhemai_title;
	/**时*/
	private int hour = 0;
	/**分*/
	private int minute = 0;
	/**秒*/
	private int second = 0;
	
	private SimpleDateFormat sdf;

	/**定时器*/
	Timer timer = new Timer();
	
	private TextView textHour,textMinute,textSecond;
	private ImageView naoz;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jiuyuan_page);
		adapter = new JiuYuanAdapter(JiuYuanActivity.this);
		GetData(more_num,1);
		initView();
		CountDown();
	}

	private void initView() {
		textHour = (TextView) findViewById(R.id.DownCount_hour);
		textMinute = (TextView) findViewById(R.id.DownCount_minetes);
		textSecond = (TextView) findViewById(R.id.DownCount_secends);
		naoz = (ImageView) findViewById(R.id.jiuyuan_naoz);
		ImageView titlename=(ImageView)findViewById(R.id.title_name);
		titlename.setImageResource(R.drawable.jiutitle);
		
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		grid = (GridView) findViewById(R.id.jiuyuangou_grid);
		grid.setAdapter(adapter);// 璁剧疆鑿滃崟Adapter
	    grid.setOnScrollListener(mScrollListener);
	    
	    naoz.setOnClickListener(myClickListener);
	}
	
	OnClickListener myClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			CustomDialog.Builder buidler = new CustomDialog.Builder(JiuYuanActivity.this);
		    buidler.setMessage(R.string.diglog_zhemai_message);
		    buidler.setTitle(R.string.diglog_zhemai_title);
		    buidler.setNegativeButton(R.string.diglog_zhemai_cancal, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//取消
					dialog.dismiss();
				}
			});
		    buidler.setPositiveButton(R.string.diglog_zhemai_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//去下载淘宝客户端
					dialog.dismiss();
					Uri uri = Uri.parse("http://www.zhemai.com/index.php?m=index&a=preview");
					Intent zhemai = new Intent(Intent.ACTION_VIEW,uri);
					startActivity(zhemai);
					
				}
			});
		    buidler.create().show();
		}
	};
	
	OnScrollListener mScrollListener = new OnScrollListener() {

		int state =0;
		boolean flag = true;
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			state = scrollState;
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
			if(state>0&&firstVisibleItem>0&&flag)
			{
				//隐藏
				 flag = false;
				 Animation anim_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.outputdown);
				 anim_out.setAnimationListener(myAnmation);
				 zhemai_title = (RelativeLayout) findViewById(R.id.zhemai_title_hidden);
				 zhemai_title.setAnimation(anim_out);
				 zhemai_title.startAnimation(anim_out);
			}
			else if(state==0&&firstVisibleItem==0&&!flag){
				 zhemai_title.setVisibility(View.VISIBLE);
				 Animation anim_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.outputup);
				 anim_out.setAnimationListener(myoutputup);
				 zhemai_title = (RelativeLayout) findViewById(R.id.zhemai_title_hidden);
				 zhemai_title.setAnimation(anim_out);
				 zhemai_title.startAnimation(anim_out);
				flag = true;
			}
		}
	};
	AnimationListener myAnmation = new AnimationListener() {
		
		@Override
		public void onAnimationStart(Animation animation) {
			
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			 
			 zhemai_title.setVisibility(View.GONE);
			 zhemai_title.clearAnimation();
		}
	};
	AnimationListener myoutputup = new AnimationListener() {
		
		@Override
		public void onAnimationStart(Animation animation) {
			
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}
		 
		@Override
		public void onAnimationEnd(Animation animation) {
			zhemai_title.setVisibility(View.VISIBLE);
			zhemai_title.clearAnimation();
		}
	};
	
	private void GetData(final int n,final int page) {
	int size=adapter.getData().size();
		if (size<data_total) {
			int a=data_total-size;;
			if (n>=a) {
				this.n=a;
			}
			else{
				this.n=n;
			}
			this.page=page;
			
			if (Base.isNetworkConnected(JiuYuanActivity.this)) {
				SettingUtils.startdialog(JiuYuanActivity.this);
			new Thread(r).start();
			}
			else{
				ToastUtil.showShortToast(JiuYuanActivity.this, "数据加载错误，请检查网络..");
			}
		}
		else{
			ToastUtil.showShortToast(JiuYuanActivity.this, "数据加载完成");
		}
			}
	Runnable r = new Runnable() {
		@Override
		public void run() {
			try {
				
				HttpConnectionHepler th=new HttpConnectionHepler();
				List<NameValuePair> a=new ArrayList<NameValuePair>();
				  a.add(new BasicNameValuePair("num", n+""));
			     a.add(new BasicNameValuePair("page", page+""));
			       adapter.setData(th.sendPostReArray(JiuYuanActivity.this,Url.jiuapi, a,"0"));
			       System.out.println("九元购的数据："+adapter.getData().toString());
			       data_total=th.getData_total();
			       isfirstloading=true;
			       sendMessage(REFRESH_LIST);
				} catch (Exception e) {

			}
		}
		};
		protected void handleOtherMessage(int flag) {
			// TODO Auto-generated method stub
			switch (flag) {
			case REFRESH_LIST:
				adapter.notifyDataSetChanged();
				SettingUtils.mProgressDialog.dismiss();
				
				break;
			case 900:
				if(hour<10){
					textHour.setText("0"+hour);
				}else{
				    textHour.setText(hour+"");
				}
				if(minute<10){
					textMinute.setText("0"+minute);
				}else{
				    textMinute.setText(minute+"");
				}
				if(second<10){
					textSecond.setText("0"+second+"");
				}else{
				    textSecond.setText(second+"");
				}
				break;
			}
		}
		
	/**
	 * 鍒锋柊锛屽厛娓呯┖list涓暟鎹劧鍚庨噸鏂板姞杞芥洿鏂板唴瀹�
	 */
	public void onRefresh() {
  adapter.getData().clear();
		GetData(more_num,1);
	}

	/**
	 * 鍔犺浇鏇村锛屽湪鍘熸潵鍩虹涓婂湪娣诲姞鏂板唴瀹�
	 */
	public void onLoadMore() {
		if (adapter.getData().size() < data_total) {// 鍒ゆ柇褰撳墠list涓凡娣诲姞鐨勬暟鎹槸鍚﹀皬浜庢渶澶у�maxAount锛屾槸閭ｄ箞涔呮樉绀烘洿澶氬惁鍒欎笉鏄剧ず
			GetData(more_num,++page);// 姣忔鍔犺浇浜旈」鏂板唴瀹�
		}
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
				// 鐠佸墽鐤嗛弴瀛樻煀閺冨爼妫�
				// mPullToRefreshView.onHeaderRefreshComplete("閺堬拷绻庨弴瀛樻煀:01-23 12:01");
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
		Date curDate = new Date(System.currentTimeMillis());// 鑾峰彇褰撳墠鏃堕棿
		return formatter.format(curDate);
	}
	
	//倒计时的实现
		private void CountDown(){

			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				long beging = betwenTime();
				this.hour = (int) (beging/1000/60/60);
				this.minute = (int) (beging%3600000/1000/60);
				this.second = (int) (beging/1000%60);
				countdown_hms();
		}
	
		private void countdown_hms(){
			
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (hour > 0) {
						if (minute>0) {
							if (second <= 0) {
								minute -= 1;
								second = 59;
							}else {
								second -= 1;
							}
						}else {
							if (second > 0) {
								second -= 1;
							}else {
								hour -= 1;
								minute = 59;
								second = 59;
							}
						}
					}else {
						if (minute>0) {
							if (second <= 0) {
								minute -= 1;
								second = 59;
							}else {
								second -= 1;
							}
						}else {
							if (second <= 0) {
								timer.cancel();
							}else {
								second -= 1;
							}
						}
					}
					sendMessage(900);
				}
			}, 0, 1000);
		}
		
		private long betwenTime(){
			SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd ");
			
			String now = sdf.format(new java.util.Date());
			String startTime = data.format(new java.util.Date())+"10:00:00";
			
			try {
				Date backTime = sdf.parse(data.format(new java.util.Date())+"00:00:00");//如果已经过了十点了 就要算成第二天
				
				Date start = sdf.parse(now);//当前的系统时间
				
				Date end = sdf.parse(startTime);//每天更新的标准时间
				
				if(start.getTime()-end.getTime()>=0){
					return 86400000-(start.getTime()-end.getTime());
				}else{
					return end.getTime()-start.getTime();
				}
			} catch (ParseException e) {
				System.out.println("获取倒计时初始时间出错");
			}
			return (long)0;
		} 
		
	protected void onResume() {
		
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}
	
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	 if(keyCode == KeyEvent.KEYCODE_BACK){ 
		 
		Exit et=new Exit();
		et.exit(JiuYuanActivity.this);
			 }
		return false;
		}
}
