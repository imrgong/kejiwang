package com.cosji.activitys;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.cloudsdk.social.core.MediaType;
import com.baidu.frontia.Frontia;
import com.baidu.frontia.api.FrontiaSocialShare;
import com.baidu.frontia.api.FrontiaSocialShare.FrontiaTheme;
import com.baidu.frontia.api.FrontiaSocialShareContent;
import com.baidu.frontia.api.FrontiaSocialShareListener;
import com.cosji.application.Base;
import com.cosji.utils.Dialog;
import com.cosji.utils.HttpConnectionHepler;
import com.cosji.utils.SettingUtils;
import com.cosji.utils.ToggleListener;
import com.cosji.utils.Url;
import com.cosji.view.CustomDialog;

@SuppressWarnings("deprecation")
public class MainTabActivity extends BaseTab implements OnTabChangeListener{
	private boolean hasMeasured = false;// 是否Measured.
	private TabHost mTabHost;
	// 存放Tab页中ImageView信息
	public List<ImageView> imageList = new ArrayList<ImageView>();
	// 存放Tab页中textview信息
	public List<TextView> textList = new ArrayList<TextView>();
	/** 每次自动展开/收缩的范围 */
	private int MAX_WIDTH = 0;
	/** 每次自动展开/收缩的速度 */
	private final static int SPEED = 30;
	private final static int sleep_time = 5;

	private GestureDetector mGestureDetector;// 手势
	private boolean isScrolling = false;
	private float mScrollX; // 滑块滑动距离
	private int window_width;// 屏幕的宽度
	private ToggleButton toggle_Wifi;
	private ImageButton toggleButton_Wifi;

	private TabWidget mTabWidget;
	private LinearLayout layout_left;// 左边布局
	private LinearLayout layout_right;// 右边布局
	String[] str = { "首页", "淘宝返利", "折买团购", "我的可及" };
	int[] tab_icon = { R.drawable.tab_home_icon, R.drawable.tab_tao_icon,
			R.drawable.tab_zhe, R.drawable.tab_mine_icon };
	int[] tab_icon1 = { R.drawable.tab_home_icon1, R.drawable.tab_tao_icon1,
			R.drawable.tab_zhe1, R.drawable.tab_mine_icon1 };
	int[] tab_color = { R.color.tab_text, R.color.white };
	
     private Toast mToast;//toast成员变量
     private String ss = null;
     private int page=0;
     private MyHandler handler;
 	 private FrontiaSocialShareContent mShareContent;
 	 private int h;
 	 
     
 	//接收广播接收的过滤器
 		public static String RECEIVER_MESSAGE_ACTION = "RECEIVER_MESSAGE_ACTION"; 
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
		setContentView(R.layout.activity_main);
	
		handler = new MyHandler();
		Base app =(Base)getApplication();
		app.setHandler(handler);
        
		// 取得TabHost对象
	    mTabHost = getTabHost();
		mTabWidget = (TabWidget) findViewById(android.R.id.tabs);
		h = getWindowManager().getDefaultDisplay().getHeight();
		
		init();
		InitView();
		setListeners();
		// 设置TabHost的背景颜色
		//mTabHost.setBackground(getResources().getDrawable(R.drawable.tab_bg));
		// 设置当前选中的Tab页
		mTabHost.setCurrentTab(page);
		// TabHost添加事件

		mTabHost.setOnTabChangedListener(this);
		if(ss==null){
		onTabChanged("0");// 人为调用回调方法，初始化选项卡tabs的颜色
		}else{
			onTabChanged(ss);
		}

		mShareContent = new FrontiaSocialShareContent();
		mShareContent.setImageUri(Uri.parse(Url.baidushare_image));
		mShareContent.setTitle("我的可及");
		mShareContent.setContent("超便宜啦有木有，我现在正在用可及网购物哟，你也来试一下吧？");
		mShareContent.setLinkUrl("http://www.cosji.com");
	}
	
	/**
	 * Tab页改变
	 */
	public void onTabChanged(String tabId) {
		// 设置所有选项卡的图片为未选中图
		int tabID = Integer.valueOf(tabId);
		for (int i = 0; i < mTabWidget.getChildCount(); i++) {
			if (i == tabID) {
				mTabWidget.getChildAt(i).setBackgroundResource(
						R.drawable.tab_selected);
			} else {
				mTabWidget.getChildAt(i).setBackgroundResource(
						R.drawable.tab_unselected);
			}
		}

		imageList.get(0).setImageDrawable(
				getResources().getDrawable(tab_icon[0]));
		textList.get(0).setTextColor(getResources().getColor(R.color.tab_text));
		imageList.get(1).setImageDrawable(
				getResources().getDrawable(tab_icon[1]));
		textList.get(1).setTextColor(getResources().getColor(R.color.tab_text));
		imageList.get(2).setImageDrawable(
				getResources().getDrawable(tab_icon[2]));
		textList.get(2).setTextColor(getResources().getColor(R.color.tab_text));
		imageList.get(3).setImageDrawable(
				getResources().getDrawable(tab_icon[3]));
		textList.get(3).setTextColor(getResources().getColor(R.color.tab_text));
		if (tabId.equalsIgnoreCase("0")) {
			imageList.get(0).setImageDrawable(
					getResources().getDrawable(tab_icon1[0]));
			// 移动底部背景图片
			textList.get(0).setTextColor(
					getResources().getColor(R.color.white));

		} else if (tabId.equalsIgnoreCase("1")) {
			imageList.get(1).setImageDrawable(
					getResources().getDrawable(tab_icon1[1]));
			// 移动底部背景图片
			textList.get(1).setTextColor(
					getResources().getColor(R.color.white));

		} else if (tabId.equalsIgnoreCase("2")) {
			imageList.get(2).setImageDrawable(
					getResources().getDrawable(tab_icon1[2]));
			textList.get(2).setTextColor(
					getResources().getColor(R.color.white));
			// 移动底部背景图片

		} else if (tabId.equalsIgnoreCase("3")) {
			textList.get(3).setTextColor(
					getResources().getColor(R.color.white));
			imageList.get(3).setImageDrawable(
					getResources().getDrawable(tab_icon1[3]));
			// 移动底部背景图片
		}
	}
	/**
	 * 这个设置Tab标签本身的布局，需要TextView和ImageView不能重合 s:是文本显示的内容 i:是ImageView的图片位置
	 */

	public View composeLayout(String s, int i) {
		// 定义一个LinearLayout布局
		LinearLayout layout = new LinearLayout(this);
		// 设置布局垂直显示
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER_HORIZONTAL);
		ImageView iv = new ImageView(this);
		imageList.add(iv);
		iv.setImageResource(i);
		iv.setScaleType(ScaleType.FIT_CENTER);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
			    h/32);
		lp.setMargins(0, 10, 0, 0);
		layout.addView(iv, lp);
		// 定义TextView
		TextView tv = new TextView(this);
		textList.add(tv);
		tv.setGravity(Gravity.CENTER);
		tv.setSingleLine(true);
		tv.setText(s);
		tv.setTextColor(Color.WHITE);
		tv.setTextSize(10);
		layout.addView(tv, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		return layout;
	}

	private void init() {
		layout_left = (LinearLayout) findViewById(R.id.layout_left);
		layout_right = (LinearLayout) findViewById(R.id.layout_right);
		layout_left.setFocusable(true);
		layout_left.setClickable(true);
		/* 为TabHost添加标签 */
		mTabHost.addTab(mTabHost.newTabSpec("0")
				.setIndicator(composeLayout(str[0], tab_icon[0]))
				.setContent(new Intent(this, HomeActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec("1")
				.setIndicator(composeLayout(str[1], tab_icon[1]))
				.setContent(new Intent(this, TaoBaoActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec("2")
				.setIndicator(composeLayout(str[2],tab_icon[2]))
				.setContent(new Intent(this, JiuYuanActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec("3")
				.setIndicator(composeLayout(str[3], tab_icon[3]))
				.setContent(new Intent(this, MineActivity.class)));
	
	}
	/**
	 * handler接收消息用于更新主界面
	 * 
	 */
	@SuppressLint("HandlerLeak")
	public class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			if(msg.what==500){
				RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) layout_left
					.getLayoutParams();
				if (layoutParams.leftMargin < 0) {
					new AsynMove().execute(SPEED);
				} else {
					new AsynMove().execute(-SPEED);
				}
			}else if(msg.what==300){
				mTabHost.setCurrentTab(0);
				onTabChanged("0");
			}
			super.handleMessage(msg);
		}
		
	}
	
	/***
	 * 初始化view
	 */
	void InitView() {
		layout_left = (LinearLayout) findViewById(R.id.layout_left);
		layout_right = (LinearLayout) findViewById(R.id.layout_right);
		toggle_Wifi = (ToggleButton)findViewById(R.id.toggle_Wifi);
		toggleButton_Wifi = (ImageButton)findViewById(R.id.toggleButton_Wifi);
		boolean isWiFiOpen = SettingUtils.get(this, SettingUtils.WIFI_SWITCH,
				false);
		toggle_Wifi.setChecked(isWiFiOpen);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toggleButton_Wifi
				.getLayoutParams();
		if (isWiFiOpen) { // 如果wifi打开
			// 调整位置
			params.addRule(RelativeLayout.ALIGN_RIGHT, -1);
			params.addRule(RelativeLayout.ALIGN_LEFT,
					R.id.toggleButton_Wifi);
			toggleButton_Wifi.setLayoutParams(params);
			toggleButton_Wifi
					.setImageResource(R.drawable.progress_thumb_selector);
			toggle_Wifi.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		} else {
			// 调整位置
			params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.toggle_Wifi);
			params.addRule(RelativeLayout.ALIGN_LEFT, -1);
			toggleButton_Wifi.setLayoutParams(params);
			toggleButton_Wifi
					.setImageResource(R.drawable.progress_thumb_off_selector);
			toggle_Wifi.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		}
OnClickListener MyClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.UserGuid:
					//新手指南
				//	ToastUtil.showShortToast(MainTabActivity.this, "可及网--手机端");
					Intent guid = new Intent(MainTabActivity.this,GuideActivity.class);
					startActivity(guid);
					break;
					
				case R.id.Userrebate:
					//返利问题
					Intent fanli = new Intent(MainTabActivity.this,rebateQuestionActivity.class);
					fanli.putExtra("cateId", "30");
					startActivity(fanli);
					//ToastUtil.showShortToast(MainTabActivity.this, "模块已经移除");
					break;
					
				case R.id.UserTiXian:
					//提现问题
					FrontiaSocialShare  mFrontShare = Frontia.getSocialShare(); 
					mFrontShare.setClientId(MediaType.QQWEIBO.toString(), "1101079877");
					mFrontShare.setClientId(MediaType.QQFRIEND.toString(), "1101079877");
					mFrontShare.setClientId(MediaType.WEIXIN.toString(), "wxd566dac57b6b1f5f");
					mFrontShare.setClientId(MediaType.SINAWEIBO.toString(), "419942267");
					mFrontShare.setClientId(MediaType.RENREN.toString(), "1101079877");
					mFrontShare.setParentView(MainTabActivity.this.getWindow().getDecorView());
					mFrontShare.setContext(MainTabActivity.this);
					
					mFrontShare.show(MainTabActivity.this.getWindow().getDecorView(),
							mShareContent , FrontiaTheme.DARK,
					    new FrontiaSocialShareListener(){
					        @Override
					        public void onSuccess() {}
					        @Override
					        public void onFailure(int errCode, String errMsg){}
					        @Override
					        public void onCancel() {}
					}); 
					break;
					
				case R.id.commProblem:
					Intent comm = new Intent(MainTabActivity.this,rebateQuestionActivity.class);
					comm.putExtra("cateId", "29");
					startActivity(comm);
					//ToastUtil.showShortToast(MainTabActivity.this, "模块已经移除");
					//常见问题
					break;
//				case R.id.KeFuOnLine:
//					//客服在线
//					Uri uri = Uri.parse("tel:4000347678");
//					Intent it = new Intent(Intent.ACTION_DIAL, uri);
//					startActivity(it);
//					break;
				case R.id.suggsionRvi:
					//意见反馈
					if(Base.getUserId()!=null&&Base.getUserId().length()>0){
						Intent setting = new Intent(MainTabActivity.this,AdviceActivity.class);
						startActivity(setting);
					}else{
						showToast("您还没有登录呢");
					}
					break;
				case R.id.cleancache:
					//清除缓存
					if(clearcache()){
						 SettingUtils.Stringset(getApplicationContext(), "0", "");
						Base.imageLoader.clearCache();
						showToast("清除成功");
					}

					

					break;
				case R.id.checkversion:
					//检测更新
					//打开软件时获得的数据，直接放在这里
				  Float oldversion = Float.parseFloat(Base.VersionName);
				//  String url = SettingUtils.Stringget(getApplicationContext(), SettingUtils.DOWNLOAD, "");
				  Float newversion = Float.parseFloat(SettingUtils.Stringget(getApplicationContext(), SettingUtils.VERSION_ID, "1.0"));
				  System.out.println("检查更新:"+newversion+"网址:"+SettingUtils.Stringget(getApplicationContext(), SettingUtils.DOWNLOAD, ""));
				  if(newversion>oldversion){
					  //有新版本提示更新
					  Dialog log = new Dialog();
					  String url = SettingUtils.Stringget(getApplicationContext(), SettingUtils.DOWNLOAD, "");
					  if(!url.equals("")){
						  log.dialogshow("是否下载新版本", MainTabActivity.this, url);
					  }
				  }else{
				  showToast("已经是最新版本");
				  }
					break;
				case R.id.login_out:
					//退出登录
					if(Base.getUserId()!=null&&Base.getUserId().length()>0){
						String url = Url.user()+"logout";
						HashMap<String, Object> param = new HashMap<String, Object>();
						param.put("uri", url);
						myAsytask task = new myAsytask();
						task.execute(param);
					}else{
						showToast( "用户没有登录");
					}
					break;
				default:
					break;
				}			}

			class myAsytask extends AsyncTask<HashMap<String, Object>, Integer, HashMap<String, String>>{

				@Override
				protected HashMap<String, String> doInBackground(
						HashMap<String, Object>... params) {
					HashMap<String, Object> param = params[0];
					String url = (String) param.get("uri");
					HttpConnectionHepler helper = new HttpConnectionHepler();
					return helper.sendPostRequest(url, null);
				}
				@Override
				protected void onPreExecute() {
					
					super.onPreExecute();
				}
				//后台线程结束执行
				@Override
				protected void onPostExecute(HashMap<String, String> result) {
					String msg = result.get("msg");
					if(msg!=null&&msg.equals("success")){
						Base.setUserId("");
						showToast("已退出登录");
						SettingUtils.set(getApplicationContext(), SettingUtils.USER_RE, false);
						SettingUtils.Stringset(getApplicationContext(), "password", null);
						MyHandler handler = new MyHandler();
						handler.sendEmptyMessage(300);
						new AsynMove().execute(SPEED);
					}
					super.onPostExecute(result);
				}
				@Override
				protected void onProgressUpdate(Integer... values) {
					super.onProgressUpdate(values);
				}
			}
		};
		
		this.findViewById(R.id.UserGuid).setOnClickListener(MyClickListener);
		this.findViewById(R.id.Userrebate).setOnClickListener(MyClickListener);
		//提现按钮，作分享软件
		this.findViewById(R.id.UserTiXian).setOnClickListener(MyClickListener);
		this.findViewById(R.id.commProblem).setOnClickListener(MyClickListener);
//		this.findViewById(R.id.KeFuOnLine).setOnClickListener(MyClickListener);
		this.findViewById(R.id.suggsionRvi).setOnClickListener(MyClickListener);
		this.findViewById(R.id.cleancache).setOnClickListener(MyClickListener);
		this.findViewById(R.id.checkversion).setOnClickListener(MyClickListener);
		this.findViewById(R.id.login_out).setOnClickListener(MyClickListener);
		
		mGestureDetector = new GestureDetector(new TabHostTouch());
		// 禁用长按监听
		mGestureDetector.setIsLongpressEnabled(false);
		getMAX_WIDTH();
	}
	//togglebutton 的监听事件
	private void setListeners() {
		toggle_Wifi.setOnCheckedChangeListener(new ToggleListener(this,
				"Wifi开关", toggle_Wifi, toggleButton_Wifi));
	
		// UI事件，按钮点击事件
		OnClickListener clickToToggleListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggle_Wifi.toggle();
				
				boolean flag = SettingUtils.get(getApplicationContext(), SettingUtils.WIFI_SWITCH,false);
				if(flag){
					showToast( "节省流量模式关闭，使用在线图片");
				}else{
					showToast("节省流量模式打开，使用缓存图片");
				}
			}
		};
		findViewById(R.id.saveGPRS).setOnClickListener(clickToToggleListener);
		findViewById(R.id.toggleButton_Wifi).setOnClickListener(clickToToggleListener);
		toggle_Wifi.setOnClickListener(clickToToggleListener);

	}
	/***
	 * listview 正在滑动时执行.
	 */
	void doScrolling(float distanceX) {
		isScrolling = true;
		mScrollX += distanceX;// distanceX:向左为正，右为负
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_left
				.getLayoutParams();
		layoutParams.leftMargin -= mScrollX;
		if (layoutParams.leftMargin >= 0) {
			isScrolling = false;// 拖过头了不需要再执行AsynMove了
			layoutParams.leftMargin = 0;

		} else if (layoutParams.leftMargin <= -MAX_WIDTH) {
			// 拖过头了不需要再执行AsynMove了
			isScrolling = false;
			layoutParams.leftMargin = -MAX_WIDTH;
		}
		layout_left.setLayoutParams(layoutParams);
	}
	/***
	 * 获取移动距离 移动的距离其实就是layout_left的宽度
	 */
	void getMAX_WIDTH() {
		ViewTreeObserver viewTreeObserver = layout_left.getViewTreeObserver();
		// 获取控件宽度
		viewTreeObserver.addOnPreDrawListener(new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				if (!hasMeasured) {
					window_width = getWindowManager().getDefaultDisplay()
							.getWidth();
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_left
							.getLayoutParams();
					layoutParams.width = window_width;
					layout_left.setLayoutParams(layoutParams);
					MAX_WIDTH = layout_right.getWidth();
					hasMeasured = true;
				}
				return true;
			}
		});
	}
	
	public void showMessage(String message,
			String customContentString){
		CustomDialog.Builder builder = new CustomDialog.Builder(this);  
        builder.setMessage("这个就是自定义的提示框");  
        builder.setTitle("提示");  
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
                //设置你的操作事项  
            }  
        });  
  
        builder.setNegativeButton("取消",  
                new android.content.DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int which) {  
                        dialog.dismiss();  
                    }  
                });  
  
        builder.create().show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_left
					.getLayoutParams();
			if (layoutParams.leftMargin < 0) {
				new AsynMove().execute(SPEED);
			}
			return false;
		}else{
		return super.onKeyDown(keyCode, event);
		}
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(mTabHost.getCurrentTab()!=3){
			return super.dispatchTouchEvent(ev);
		}
		if(mGestureDetector.onTouchEvent(ev)){
			ev.setAction(MotionEvent.ACTION_CANCEL);
		}
		if(ev.getAction()==MotionEvent.ACTION_MOVE){
			//ev.setAction(MotionEvent.ACTION_CANCEL);
			return super.dispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}

	private class TabHostTouch extends SimpleOnGestureListener{
		
		private static final int MIN_DISTANCE = 60;        //最小距离
		private static final int MIN_VELOCITY = 100;      //最小滑动速率
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			 if (Math.abs(velocityX) > MIN_VELOCITY&&Math.abs(e1.getY()-e2.getY())<MIN_DISTANCE) {
		         if ((e2.getX() - e1.getX()) > MIN_DISTANCE) {  //向右滑动
		             new AsynMove().execute(SPEED);
		         } else if ((e1.getX() - e2.getX()) > MIN_DISTANCE) {  //向左滑动
		            new AsynMove().execute(-SPEED);
		         }
		     }
		     return false;
		}
	}
	
public void showToast(String text) {  
    if(mToast == null) {  
        mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);  
    } else {  
        mToast.setText(text);    
        mToast.setDuration(Toast.LENGTH_SHORT);  
    }  
    mToast.show();  
   }


private boolean clearcache(){
	  File file = new File(Environment.getExternalStorageDirectory(),"Cosji");
	  try {
			  SettingUtils.deleteFile(file);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  boolean flaga = SettingUtils.Stringset(getApplicationContext(),"fanli","");
	  boolean flagb =  SettingUtils.Stringset(getApplicationContext(),"comm","");
	if(flaga&&flagb){
		return true;
	}
	  return false;
   }
public class AsynMove extends AsyncTask<Integer, Integer, Void> {
	@Override
	protected Void doInBackground(Integer... params) {
		int times = 0;
		if (MAX_WIDTH % Math.abs(params[0]) == 0)// 整除
			times = MAX_WIDTH / Math.abs(params[0]);
		else
			times = MAX_WIDTH / Math.abs(params[0]) + 1;// 有余数
		for (int i = 0; i < times; i++) {
			publishProgress(params[0]);
			try {
				Thread.sleep(sleep_time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * update UI
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
		//退出登录按钮
		if(Base.getUserId()!=null&&Base.getUserId().length()>0){
			findViewById(R.id.login_out).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.login_out).setVisibility(View.GONE);
		}
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_left
				.getLayoutParams();
		// 右移动
		if (values[0] > 0) {
			layoutParams.leftMargin = Math.min(layoutParams.leftMargin
					+ values[0], 0);
		} else {
			// 左移动
			layoutParams.leftMargin = Math.max(layoutParams.leftMargin
					+ values[0], -MAX_WIDTH);
		}
		layout_left.setLayoutParams(layoutParams);
		
	}
}

	@Override
	protected void onDestroy() {
		try {
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.onDestroy();
	}
}
