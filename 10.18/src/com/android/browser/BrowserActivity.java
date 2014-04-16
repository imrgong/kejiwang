package com.android.browser;

import java.util.concurrent.ScheduledExecutorService;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cosji.activitys.R;

public class BrowserActivity extends Activity {

	private LinearLayout web_back;
	private ImageView refresh,refresh_big,refresh_small;
	private Animation rotate_big,rotate_small;
	private WebView webview;
    private TextView loading;
    private Handler handler;
	private int number = 1;
    
	private String url;
	protected static ScheduledExecutorService scheduledExecutorService;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simplewebactivity);
		web_back = (LinearLayout)findViewById(R.id.web_back);
		refresh = (ImageView)findViewById(R.id.web_refresh);
		//defulteBrowser();
		init();
		initWebSettings();
		new ScrollTask().run();
	}
	
	
	private class ScrollTask implements Runnable {
		public void run() {
			try {
				Thread.sleep(400);
				number = (number + 1) % 4;
				handler.sendEmptyMessage(123);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		}

	protected void initWebSettings()
	  {
		webview = (WebView)findViewById(R.id.SimpleWebView);
	    this.webview.setInitialScale(70);
	    WebSettings localWebSettings = this.webview.getSettings();
	    localWebSettings.setSupportZoom(false);
	    localWebSettings.setBuiltInZoomControls(true);
	    localWebSettings.setJavaScriptEnabled(true);
	    localWebSettings.setDomStorageEnabled(true);
	    localWebSettings.setBlockNetworkImage(false);
	    localWebSettings.setCacheMode(-1);
	    localWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
	    
	    MyWebViewClient client = new MyWebViewClient();
		webview.setWebChromeClient(client);
		webview.setWebViewClient(new MyClient());
		webview.loadUrl(url);
	  }
	
	private void init() {
		
		Intent intent=getIntent();
		url=intent.getStringExtra("url"); 
		System.out.println("simple_activity:"+url);
		web_back.setOnClickListener(myClickListener);
		refresh.setOnClickListener(myClickListener);
		refresh_big = (ImageView) findViewById(R.id.web_refresh_big);
		refresh_small = (ImageView) findViewById(R.id.web_refresh_small);
		rotate_big = AnimationUtils.loadAnimation(this, R.anim.progress_refresh);
		rotate_small = AnimationUtils.loadAnimation(this, R.anim.progress_refresh_small);
		LinearInterpolator polator = new LinearInterpolator();//匀速旋转
		rotate_big.setInterpolator(polator);
		rotate_small.setInterpolator(polator);
		loading = (TextView)findViewById(R.id.Simple_activity_toabao_rebate_loading);
		
		handler = new Handler(){
			 
			public void handleMessage(android.os.Message msg) {
				if(msg.what==123){
					StringBuffer sb = new StringBuffer();
					sb.append("正在跳转");
					for(int i = 1;i<=number;i++){
						sb.append(".");
					}
					loading.setText(sb.toString());
				}
			};
		};
	}
	
	OnClickListener myClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
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
private class MyWebViewClient extends WebChromeClient{
}

private class MyClient extends WebViewClient{
	
	
	private void notifyOtherSchema(String paramString)
	  {
	    try
	    {
			Intent localIntent = new Intent();
			localIntent.setData(Uri.parse(paramString));
			BrowserActivity.this.startActivity(localIntent);
	        return;
	      }
	      catch (ActivityNotFoundException localActivityNotFoundException)
	      {
	        localActivityNotFoundException.printStackTrace();
	      }
	}
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				if(url.startsWith("itaobao://"))
					notifyOtherSchema(url);
				return super.shouldOverrideUrlLoading(view, url);
		      }
		}
   @Override
protected void onStop() {
	this.finish();
	super.onStop();
}
}
