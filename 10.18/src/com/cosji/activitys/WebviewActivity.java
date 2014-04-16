package com.cosji.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cosji.application.Base;
import com.cosji.utils.ToastUtil;
import com.cosji.utils.UrlFilter;
import com.cosji.view.ProgressWebView;

public class WebviewActivity extends BaseActivity {

	private ProgressWebView webview;
	private String url,title;
	private boolean isrebate;
	private ImageView refresh_big,refresh_small,refresh;
	private LinearLayout back;
	private Animation rotate_big,rotate_small;
    private TextView title_name,title_state;
    private UrlFilter filter;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webactivity);
		filter = new UrlFilter();
		// ~~~ 获取参数
	try {
		Intent intent=getIntent();
		title=intent.getStringExtra("title");
		isrebate=intent.getBooleanExtra("isrebate", true);
		url=intent.getStringExtra("url"); 
		//淘宝给的跳转测试参数
		initView();
        if(intent.hasExtra("flag")&&intent.getStringExtra("flag").equals("search")){
        	
    		if(filter.dealRequest(url, webview, WebviewActivity.this, title)){
    			
    		}else{
    			ToastUtil.showShortToast(WebviewActivity.this,R.string.webView_no_result);
    			this.finish();
    		}
		}
        Web_link(url);
		
	  } catch (Exception e) {
	 	e.printStackTrace();
	  }
	}

	public void initView() {
		refresh = (ImageView) findViewById(R.id.web_refresh);
		refresh_big = (ImageView) findViewById(R.id.web_refresh_big);
		refresh_small = (ImageView) findViewById(R.id.web_refresh_small);
        title_name=(TextView)findViewById(R.id.web_title_name);
        
        if(title.equals("商品详情"))
        	title_name.setText("淘宝网");
        else
        title_name.setText(title);
      
        title_state=(TextView)findViewById(R.id.web_state);
		back = (LinearLayout) findViewById(R.id.web_back);
		//初始化动画资源
		 rotate_big = AnimationUtils.loadAnimation(this, R.anim.progress_refresh);
		 rotate_small = AnimationUtils.loadAnimation(this, R.anim.progress_refresh_small);
		LinearInterpolator polator = new LinearInterpolator();//匀速旋转
		rotate_big.setInterpolator(polator);
		rotate_small.setInterpolator(polator);
		refresh.setOnClickListener(lin);

		back.setOnClickListener(lin);
	}

	OnClickListener lin = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.web_back:
             finish();
				break;
			case R.id.web_refresh:
				refresh.setVisibility(View.GONE);
				refresh_big.setVisibility(View.VISIBLE);
				refresh_small.setVisibility(View.VISIBLE);
				if(rotate_big!=null&&rotate_small!=null&&refresh.getVisibility()==View.GONE){
					refresh_big.startAnimation(rotate_big);
					refresh_small.startAnimation(rotate_small);
				}

				webview.reload();
				webview.setWebViewClient(new WebViewClient() {
					public void onPageFinished(android.webkit.WebView view,
							String url) {
						refresh.setVisibility(View.VISIBLE);
						refresh_big.clearAnimation();
						refresh_small.clearAnimation();
						refresh_big.setVisibility(View.GONE);
						refresh_small.setVisibility(View.GONE);

					}
				});

				break;

			default:
				break;
			}
		}
	};

	private void Web_link(String url) {
		// ~~~ 绑定控件
		webview = (ProgressWebView) findViewById(R.id.webView1);
	
		webview.setDownloadListener(new DownloadListener() {

			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
			if (url != null && url.startsWith("http://"))
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			}
		});
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		// 设置支持javascript脚本
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
//		// 设置可以访问的文件
//		webSettings.setAllowFileAccess(true);
		// 设置支持缩放
		webview.loadUrl(url);
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDomStorageEnabled(true);
		// 设置webviewclient
		MyWebViewClient myWebViewClient=new MyWebViewClient();
		webview.setWebViewClient(myWebViewClient);
	}
class MyWebViewClient extends WebViewClient{
	
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return super.shouldOverrideUrlLoading(view, url);

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			if (isrebate) {
			if (Base.getUserId()!=null&&Base.getUserId().length()>0) {
				
				title_state.setText("可及网最高返50%");
				
			}
			else{
				title_state.setText("最高返50%(未登录,不能返利)");
			}
			}
			else{
				if(title.equals("新消息"))
					title_state.setText("自由可及  必出精品");
				else
				title_state.setText("请搜索商品网址进行返利跟单");
			}
			
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			if(filter.dealRequest(url, view, WebviewActivity.this, title)){
				WebviewActivity.this.webview.stopLoading();
				WebviewActivity.this.webview.goBack();
			}else{
			super.onPageStarted(view, url, favicon);
			}
		}

	
}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode && webview.canGoBack()) {
			webview.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	};
	
}