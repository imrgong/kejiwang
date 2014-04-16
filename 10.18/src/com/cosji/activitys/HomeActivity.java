package com.cosji.activitys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cosji.adapter.MallMoreAdapter;
import com.cosji.adapter.ProPagerViewAdapter;
import com.cosji.application.Base;
import com.cosji.utils.Dialog;
import com.cosji.utils.Exit;
import com.cosji.utils.HttpConnectionHepler;
import com.cosji.utils.IsIntent;
import com.cosji.utils.SettingUtils;
import com.cosji.utils.ToastUtil;
import com.cosji.utils.Url;
import com.cosji.view.CustomDialog;
import com.cosji.view.MyGridView;
import com.cosji.view.MyScrollView;
import com.umeng.analytics.MobclickAgent;

public class HomeActivity extends BaseActivity {

	MyGridView grid;
	int num = 14;
	ImageView goodsP1, goodsP2;
	TextView goodsN1, goodsN2;
	LinearLayout progoods1, progoods2;
	List<HashMap<String, String>> data1 = new ArrayList<HashMap<String, String>>();
	boolean isloading = false;
	MallMoreAdapter adapter;
	private static final int REFRESH_LIST = 0x10001;
	/*
	 * 促销幻灯片设计
	 */

	private ViewPager viewPager; // android-support-v4中的滑动组件
	private List<ImageView> imageViews; // 滑动的图片集合
	MyScrollView scrollView;

	private int[] imageResId; // 图片ID
	private List<View> dots; // 图片标题正文的那些点
	AlertDialog dlg = null;
	private int currentItem = 0; // 当前图片的索引号
	// private TextView check;// 签到
	// An ExecutorService that can schedule commands to run after a given delay,
	// or to execute periodically.
	protected static ScheduledExecutorService scheduledExecutorService;
	SettingUtils stu = new SettingUtils();
	// 切换当前显示的图片
	private Handler handler;
	private List<HashMap<String, String>> slidemap;
	private TextView cliear_button;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_page);
		adapter = new MallMoreAdapter(HomeActivity.this, true);
		init();
		slidemap = new ArrayList<HashMap<String, String>>();
		initSomePro();
		refreshGoods();
		initData();
		initProPageView();

		// num = 15;
		// 检测版本
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 125) {
					initProPageView();
				} else if (msg.what == 700) {
					viewPager.setCurrentItem(currentItem);
				} else if (msg.what == 129) {
					String URL = null;
					// 提醒用户，有新版本是否更新
					HashMap<String, String> result = new HashMap<String, String>();
					result = (HashMap<String, String>) msg.obj;
					float oldVersion = Float.parseFloat(Base.VersionName);
					String nVersion = result.get("version");
					float newVersion = oldVersion;
					try {
						if (!nVersion.equals("") && nVersion != null) {
							newVersion = Float
									.parseFloat(result.get("version"));
						}
					} catch (Exception e) {
					}
					if (oldVersion >= newVersion) {
						// 版本未发生变化
					} else {
						// 版本发生变化，提醒更新
						URL = result.get("download");
						URL.replaceAll("\"", " ");
						Dialog log = new Dialog();
						String url = SettingUtils.Stringget(
								getApplicationContext(), SettingUtils.DOWNLOAD,
								"");
						if (!url.equals("")) {
							log.dialogshow("是否下载更新", HomeActivity.this, url);
						}
					}
					initProPageView();
				}
				super.handleMessage(msg);
			}
		};

	}

	private void initData() {
		if (Base.isNetworkConnected(HomeActivity.this)) {
			// SettingUtils.startdialog(HomeActivity.this);
			new Thread(mallrunnable).start();
			new Thread(new Runnable() {

				@Override
				public void run() {
					HttpConnectionHepler helper = new HttpConnectionHepler();
					HashMap<String, String> result = new HashMap<String, String>();

					String url = Url.version();
					String slideshow = Url.Slideshow;

					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("type", "1"));

					result = helper.sendPostRequest(url, params);
					slidemap = helper.sendPostRequestArray(slideshow, null);

					// 把取得的数据存入
					SettingUtils.Stringset(getApplicationContext(),
							SettingUtils.VERSION_ID, result.get("version"));
					SettingUtils.Stringset(getApplicationContext(),
							SettingUtils.DOWNLOAD, result.get("download"));
					Message msg = new Message();
					msg.what = 129;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}).start();
		} else {
			ToastUtil.showShortToast(HomeActivity.this, "网络出错,数据无法加载请检测网络...");
		}

	}

	private void init() {

		ImageView titlename = (ImageView) findViewById(R.id.title_name);
		titlename.setImageResource(R.drawable.title_name1);

		ImageView menu = (ImageView) findViewById(R.id.menu_view);
		menu.setImageResource(R.drawable.home_search);
		menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showSearchView();
			}
		});
		grid = (MyGridView) findViewById(R.id.home_hot_mall);
		grid.setAdapter(adapter);// 设置菜单Adapter
	}

	private void showSearchView() {
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout view = (LinearLayout) inflater.inflate(
				R.layout.pupwindow_search, null);
		cliear_button = (TextView) view
				.findViewById(R.id.clear_button_text_home);
		dlg = new AlertDialog.Builder(this).create();

		dlg.show();
		Window window = dlg.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		window.setLayout(android.view.WindowManager.LayoutParams.FILL_PARENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT);
		window.setGravity(Gravity.TOP);
		window.setWindowAnimations(R.style.AnimationFade);
		window.setContentView(view);
		// 为确认按钮添加事件,执行退出应用操作

		// 关闭alert对话框架
		LinearLayout back = (LinearLayout) view
				.findViewById(R.id.home_search_title);
		final AutoCompleteTextView searchtext = (AutoCompleteTextView) view
				.findViewById(R.id.home_search);
		stu.AutoCompleteText(HomeActivity.this, searchtext);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlg.cancel();
				dlg = null;
				final InputMethodManager imm = (InputMethodManager) HomeActivity.this
						.getSystemService(HomeActivity.this.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchtext.getWindowToken(), 0);
			}
		});
		cliear_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchtext.setText("");
			}
		});

		searchtext.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (searchtext.getText().toString().length() > 0) {
					cliear_button.setVisibility(View.VISIBLE);
				} else {
					cliear_button.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void initProPageView() {

		if (slidemap.size() < 1) {
			return;
		}
		int w = getWindowManager().getDefaultDisplay().getWidth();
		imageViews = new ArrayList<ImageView>();
		dots = new ArrayList<View>();
		LinearLayout dotMathor = (LinearLayout) findViewById(R.id.dot_mathor);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w / 43,
				w / 43);
		// 初始化图片资源
		for (int i = 0; i < slidemap.size() - 1; i++) {
			ImageView imageView = new ImageView(this);
			Base.imageLoader.DisplayImage(
					Url.COSJII + slidemap.get(i).get("imgUrl"), imageView,
					false);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageViews.add(imageView);
			View dot = getLayoutInflater().inflate(R.layout.prodefulte, null)
					.findViewById(R.id.v_dot1);
			dotMathor.addView(dot, lp);
			dots.add(dot);
		}
		dots.get(0).setBackgroundResource(R.drawable.dot_focused);

		viewPager = (ViewPager) findViewById(R.id.vp);
		viewPager
				.setAdapter(new ProPagerViewAdapter(imageViews, this, slidemap));// 设置填充ViewPager页面的适配器
		// 设置一个监听器，当ViewPager中的页面改变时调用
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
	}

	/**
	 * 换行切换任务
	 * 
	 * @author Administrator
	 */
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				currentItem = (currentItem + 1) % imageViews.size();
				handler.sendEmptyMessage(700); // 通过Handler切换图片
			}
		}
	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			currentItem = position;
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/*
	 * 商品推荐、签到和教程
	 */
	public void initSomePro() {
		// check = (TextView) findViewById(R.id.home_checkinState);
		// RelativeLayout checkin = (RelativeLayout)
		// findViewById(R.id.home_checkedin);
		// RelativeLayout jiaocheng = (RelativeLayout)
		// findViewById(R.id.home_jiaocheng);
		LinearLayout jiaocheng = (LinearLayout) findViewById(R.id.home_pup);
		progoods1 = (LinearLayout) findViewById(R.id.home_pup1);
		progoods2 = (LinearLayout) findViewById(R.id.home_pup2);
		// checkin.setOnClickListener(hot_mall_listener);
		jiaocheng.setOnClickListener(hot_mall_listener);
		// goodsP1 = (ImageView) findViewById(R.id.home_pup_Image1);
		// goodsP2 = (ImageView) findViewById(R.id.home_pup_Image2);
	}

	public void refreshGoods() {
		// goodsP1.setBackgroundResource(R.drawable.jhs);
		// goodsP2.setBackgroundResource(R.drawable.tmao);
		progoods1.setOnClickListener(hot_mall_listener);
		progoods2.setOnClickListener(hot_mall_listener);
	};

	OnClickListener hot_mall_listener = new OnClickListener() {
		public void onClick(View v) {

			switch (v.getId()) {
			/*
			 * 签到赚现金
			 */
			// case R.id.home_checkedin:
			// boolean flag = SettingUtils.get(getApplicationContext(),
			// SettingUtils.SIGN_STATE, false);
			// if (!flag&&SettingUtils.get(getApplicationContext(),
			// SettingUtils.USER_STATE, false)) {
			// Animation in = AnimationUtils.loadAnimation(
			// HomeActivity.this, R.anim.hyperspace_in);
			// in.setAnimationListener(myAnimation);
			// check.startAnimation(in);
			// Sign_out();
			// SettingUtils.set(getApplicationContext(),
			// SettingUtils.SIGN_STATE, true);
			// }else if(flag){
			// Animation shack = AnimationUtils.loadAnimation(
			// HomeActivity.this, R.anim.shake);
			// check.startAnimation(shack);
			// }
			// if(!SettingUtils.get(getApplicationContext(),
			// SettingUtils.USER_STATE, false)){
			// ToastUtil.showShortToast(HomeActivity.this, "您还没有登录");
			// Intent gotologin = new
			// Intent(HomeActivity.this,LoginActivity.class);
			// startActivity(gotologin);
			// }
			// break;
			/*
			 * 返利教程
			 */
			case R.id.home_pup:
				IntentActivity(GuideActivity.class);
				break;
			/*
			 * 促销商品1
			 */
			case R.id.home_pup1:
				CustomDialog.Builder buidler = new CustomDialog.Builder(
						HomeActivity.this);
				buidler.setMessage(R.string.diglog_zhemai_home_message);
				buidler.setTitle(R.string.diglog_zhemai_title);
				buidler.setNegativeButton(R.string.diglog_zhemai_cancal,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 取消
								dialog.dismiss();
							}
						});
				buidler.setPositiveButton(R.string.diglog_zhemai_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 去下载淘宝客户端
								dialog.dismiss();
								Uri uri = Uri.parse("http://www.zhemai.com");
								Intent zhemai = new Intent(Intent.ACTION_VIEW,
										uri);
								startActivity(zhemai);
							}
						});
				buidler.create().show();
				break;
			/*
			 * 促销商品2
			 */
			case R.id.home_pup2:
				String url = "http://s.click.taobao.com/t?e=zGU34CA7K%2BPkqB05%2Bm7rfGKas1PIKp0U37pZuBotzOg7OjeU9mIWS5%2B5UROBz6sq4fiHePnGhf6UrWuOtfD2G6QGcAHIXpjhEtESpuqsRo0a&pid=mm_26039255_0_0&unid="
						+ Base.getUserId();
				IsIntent.prompt1(HomeActivity.this, "聚划算", url);
				break;
			default:
				break;
			}
		}
	};

	public void IntentActivity(Class a) {
		Intent intent4 = new Intent();
		intent4.setClass(HomeActivity.this, a);
		intent4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent4);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}

	Runnable mallrunnable = new Runnable() {
		@Override
		public void run() {
			try {

				HttpConnectionHepler th = new HttpConnectionHepler();
				List<NameValuePair> a = new ArrayList<NameValuePair>();
				a.add(new BasicNameValuePair("num", num + ""));

				adapter.setData(th.sendPostMallReArray(getApplicationContext(),
						Url.hotmallapi, a, "0"));
				isloading = true;

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
			if (adapter.getData().size() > 0) {
				adapter.notifyDataSetChanged();
			}
			break;
		default:
			break;
		}
	}

	protected void onStart() {
		startshes();
		super.onStart();
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		scheduledExecutorService.shutdown();
		super.onStop();
	}

	protected void startshes() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 2, 3,
				TimeUnit.SECONDS);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Exit et = new Exit();
			et.exit(HomeActivity.this);
		}
		return false;
	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);

	}
}
