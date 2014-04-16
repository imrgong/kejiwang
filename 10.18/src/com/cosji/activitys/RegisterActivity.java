package com.cosji.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class RegisterActivity extends BaseActivity {

	private WebView web;
	private String geturl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registerweb);
		intview();
	}
	@SuppressLint("SetJavaScriptEnabled")
	private void intview() {
		findViewById(R.id.header).setVisibility(View.VISIBLE);
		geturl ="http://rest.cosjii.com/user/register/";
		Intent exturs = getIntent();
		if(exturs.hasExtra("url")){
			((TextView)findViewById(R.id.mine_secend_title)).setText("忘记密码");
			Bundle url = exturs.getExtras();
			geturl = url.getString("url");
		}
		((TextView)findViewById(R.id.mine_secend_title)).setText("注册账号");
		web = (WebView)findViewById(R.id.webView1);
		web.getSettings().setJavaScriptEnabled(true);
		web.addJavascriptInterface(this, "javatojs");
		web.loadUrl(geturl);
		MyWebViewClient mywebviewclient = new MyWebViewClient();
		web.setWebViewClient(mywebviewclient);
		web.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	}
	public void jump(){
		Intent login = new Intent(RegisterActivity.this,LoginActivity.class);
		login.putExtra("flag", "register");
		startActivity(login);
		this.finish();
	}
	class MyWebViewClient extends WebViewClient{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			//view.loadUrl(url);
			return super.shouldOverrideUrlLoading(view, url);
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			findViewById(R.id.web_back).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			super.onPageFinished(view, url);
		}
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
		}
}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode && web.canGoBack()) {
			web.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	};
}
