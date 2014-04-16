package com.cosji.activitys;

import java.util.HashMap;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cosji.application.Base;
import com.cosji.utils.HttpConnectionHepler;
import com.cosji.utils.SettingUtils;
import com.cosji.utils.ToastUtil;
import com.cosji.utils.Url;

public class LoginActivity extends BaseActivity {

	private ImageView image;
	private boolean remenber;
	private EditText username,password;
	private RelativeLayout buttom;
	private Button login_button;
	private LinearLayout laytop,remenberlayout;
	private HttpConnectionHepler helper;
	private TextView regisger,forget;
	private String name;
	private String pas;
	private String flag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);
		initview();
		initData();
	}
	private void initData() {
		Intent intent = getIntent();
		if(intent.hasExtra("flag")){
			Bundle page = intent.getExtras();
			flag  = page.getString("flag");
			if(flag.equals("register")){
			SettingUtils.Stringset(getApplicationContext(), "username", "");
			SettingUtils.Stringset(getApplicationContext(), "password", "");
			}
		}
	}
	private void initview() {
		login_button = (Button)findViewById(R.id.login_to_cosji_button);
		login_button.setEnabled(false);
		
		buttom = (RelativeLayout)findViewById(R.id.login_page_buttom);
		laytop = (LinearLayout)findViewById(R.id.login_page_inLoginlayout);
		remenberlayout = (LinearLayout)findViewById(R.id.remenberUserLayout);
		image = (ImageView)findViewById(R.id.remenberUser);
		username = (EditText)findViewById(R.id.username);
		password = (EditText)findViewById(R.id.password);
		password.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				name = username.getText().toString().trim();
				pas = password.getText().toString();
				if(name!=null&&pas.length()>4){
					login_button.setTextColor(Color.argb(225, 0, 0, 0));
					login_button.setEnabled(true);
				}else{
					login_button.setTextColor(Color.argb(225, 130, 130, 130));
				}
			}
		});
		
		login_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String[] params = new String[]{name,pas};
				myTashk task = new myTashk();
				task.execute(params);
			}
		});
		
		regisger = (TextView)findViewById(R.id.login_page_register);
		forget = (TextView)findViewById(R.id.forgetmima);
		helper = new HttpConnectionHepler();
		password.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				name = username.getText().toString().trim();
				pas = password.getText().toString().trim();
				if(KeyEvent.KEYCODE_ENTER==keyCode){
					if (Base.isNetworkConnected(LoginActivity.this)){
						if(!(name.equals("")||pas.equals(""))){
						String[] params = new String[]{name,pas};
						myTashk task = new myTashk();
						task.execute(params);
						}else{
							ToastUtil.showShortToast(LoginActivity.this, "用户名/密码不能为空");
						}
					}else{
						ToastUtil.showShortToast(LoginActivity.this, "网络出错,请检测网络...");
					}
					
				}
				return false;
			}
		});
		username.setOnFocusChangeListener(myfocus);
		password.setOnFocusChangeListener(myfocus);
		
		SettingUtils.set(getApplicationContext(), SettingUtils.USER_RE, true);
		remenber = SettingUtils.get(getApplicationContext(), SettingUtils.USER_RE, false);
		ReadUser(remenber);
		if(remenber){
			image.setImageDrawable(getResources().getDrawable(R.drawable.remenbered));
		}else{
			image.setImageDrawable(getResources().getDrawable(R.drawable.remenber));
		}
		
		
		OnClickListener MyListner =new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch(v.getId()){
				case R.id.remenberUserLayout:
					//remenber 为true 表示记住用户 false 表示不记住用户
					if(remenber){
						remenber = false;
						image.setImageDrawable(getResources().getDrawable(R.drawable.remenber));
						SettingUtils.set(getApplicationContext(), SettingUtils.USER_RE, remenber);
					}else{
						remenber = true;
						image.setImageDrawable(getResources().getDrawable(R.drawable.remenbered));
						SettingUtils.set(getApplicationContext(), SettingUtils.USER_RE, remenber);
					}
					break;
				case R.id.login_page_register:
					Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
					startActivity(intent);
					break;
				case R.id.forgetmima:
					//跳转密码找回页面
					Intent fog = new Intent(LoginActivity.this,RegisterActivity.class);
					fog.putExtra("url", "http://www.cosji.com/index.php?mod=user&act=getpassword");
					startActivity(fog);
					finish();
					break;
				}
				
			}
			
		};		
		findViewById(R.id.remenberUserLayout).setOnClickListener(MyListner);
		findViewById(R.id.login_page_register).setOnClickListener(MyListner);
		findViewById(R.id.forgetmima).setOnClickListener(MyListner);
	}
	
	//异步
	class myTashk extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... params) {
			
			HashMap<String, String> cookie = new HashMap<String, String>();
			String name = params[0];
			String pas = params[1];
			String url = Url.user()+"login/?account="+name+"&password="+pas;
			cookie = helper.UserLogin(url);
			String msg = cookie.get("msg");
			System.out.println("错误信息:"+msg);
			if(msg!=null&&msg.equals("success")){
				Base.setUserId(cookie.get("userId"));
				//SettingUtils.Stringset(getApplicationContext(), SettingUtils.USER_ID, cookie.get("userId"));
				return "success";
			}
			return "error";
		}
		@Override
		protected void onPostExecute(String result) {
			if(result.equals("success")){
				//
				SaveUser(name, pas, remenber);
				LoginActivity.this.finish();
			}else{
				laytop.setVisibility(View.GONE);
				password.setFocusable(true);
				password.setFocusableInTouchMode(true);
				username.setFocusableInTouchMode(true);
				username.setFocusable(true);
				regisger.setClickable(true);
				regisger.setFocusable(true);
				remenberlayout.setClickable(true);
				remenberlayout.setFocusable(true);
				forget.setClickable(true);
				forget.setFocusable(true);
				login_button.setEnabled(true);
				login_button.setClickable(true);
				login_button.setFocusable(true);
				
				username.requestFocus();
				SaveUser(name, pas, false);
				Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
				ReadUser(remenber);
			}
		}
		//异步线程开启之前所做操作
		@Override
		protected void onPreExecute() {
			loding();
		}

		//异步线程运行之中所做
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
		
	}
   OnFocusChangeListener myfocus = new OnFocusChangeListener() {
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if(hasFocus){
			((EditText)v).setHintTextColor(Color.argb(225, 178, 173, 173));
		}else{
			((EditText)v).setHintTextColor(Color.argb(225, 130, 130, 130));
		}
	}
};
private void loding(){
	TextView loadtext = (TextView)findViewById(R.id.Loading_text);
	ImageView loadimg =(ImageView)findViewById(R.id.Loading_img);
	Animation loading = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.loading_progressbar);
	LinearInterpolator polator = new LinearInterpolator();//匀速旋转
	loading.setInterpolator(polator);
	loadimg.setAnimation(loading);
	loadtext.setText("登录中...");
	laytop.setVisibility(View.VISIBLE);
	loadimg.requestFocus();
	loadimg.requestFocusFromTouch();
	password.setFocusable(false);
	password.clearFocus();
	username.setFocusable(false);
	username.clearFocus();
	remenberlayout.setClickable(false);
	remenberlayout.clearFocus();
	regisger.setClickable(false);
	regisger.clearFocus();
	forget.setClickable(false);
	forget.clearFocus();
	login_button.setClickable(false);
	login_button.clearFocus();
	login_button.setEnabled(false);
	final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
}
//存取用户信息
private void SaveUser(String username,String password,boolean remenber){
	if(remenber){
		SettingUtils.Stringset(getApplicationContext(), "username", username);
		SettingUtils.Stringset(getApplicationContext(), "password", password);
	}else{
		SettingUtils.Stringset(getApplicationContext(), "username", username);
		SettingUtils.Stringset(getApplicationContext(), "password", null);
	}
}
private void ReadUser(boolean remenber){
	if(remenber){
	    username.setText(SettingUtils.Stringget(getApplicationContext(),"username" , null));
	    password.setText(SettingUtils.Stringget(getApplicationContext(),"password" , null));
         }else{
        	 username.setText(SettingUtils.Stringget(getApplicationContext(),"username" , null));
         }
    }
}
