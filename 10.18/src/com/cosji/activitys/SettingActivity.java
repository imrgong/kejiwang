package com.cosji.activitys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.cosji.utils.HttpConnectionHepler;
import com.cosji.utils.SettingUtils;
import com.cosji.utils.ToastUtil;
import com.cosji.utils.Url;
/**
 * personal infomation setting function
 *com.cosji.activitys.SettingActivity
 * @author ty
 *create at 2013-8-29 下午3:29:44
 */
public class SettingActivity extends BaseTab {

	private TabWidget tabwidget;
	private List<TextView> titlelist = new ArrayList<TextView>();
	private LinearLayout back;
	private LinearLayout personalinfo ;
	private LinearLayout modify;
	private LinearLayout Finance;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personsetting_page);
		((TextView)findViewById(R.id.mine_secend_title)).setText("信息设置");
		TabHost tabhost = getTabHost();

		// 为TabHost添加标签
		// 新建一个newTabSpec(newTabSpec)
		// 设置其标签和图标（setIndicator）
		// 设置内容（setContent）
		tabhost.addTab(tabhost
				.newTabSpec("0")
				.setIndicator(composeLayout(R.string.PesonalInfo))
				.setContent(R.id.tab1));
		   
		tabhost.addTab(tabhost
				.newTabSpec("1")
				.setIndicator(composeLayout(R.string.modifypassword))
				.setContent(R.id.tab2));
		tabhost.addTab(tabhost
				.newTabSpec("2")
				.setIndicator(
						composeLayout(R.string.PersonalFinance))
				.setContent(R.id.tab3));
		// 设置TabHost的背景颜色
		tabhost.setBackgroundColor(getResources().getColor(R.color.rebate_tabhost));
		tabhost.setCurrentTab(0);
		tabwidget= (TabWidget) findViewById(android.R.id.tabs);
		
		onTabChanged("0");

		
		// 标签切换事件处理，setOnTabChangedListener
		tabhost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				int tabld = Integer.valueOf(tabId);
				for(int i =0;i<tabwidget.getChildCount();i++){
					if(i==tabld){
						tabwidget.getChildAt(i).setBackgroundResource(R.drawable.taobao_tabhost_selected_back);
						titlelist.get(i).setTextColor(Color.argb(225, 51, 51, 51));
					}else{
						tabwidget.getChildAt(i).setBackgroundColor(Color.argb(225, 186, 186, 186));
						titlelist.get(i).setTextColor(Color.argb(225, 113, 113, 113));
					}
				}
			}
		});
		initView();
		setonclicklistener();
	}
	public void initView() {
		back = (LinearLayout)findViewById(R.id.web_back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(back.getWindowToken(), 0);
				finish();
			}
		});
		LinearLayout lay1 = (LinearLayout) findViewById(R.id.tab1);
		LinearLayout lay2 = (LinearLayout) findViewById(R.id.tab2);
		LinearLayout lay3 = (LinearLayout) findViewById(R.id.tab3);
		LayoutInflater inflater = LayoutInflater.from(this);
		
		personalinfo = (LinearLayout) inflater.inflate(R.layout.personalinfo, null);
		modify = (LinearLayout) inflater.inflate(R.layout.modifypassword, null);
		Finance =(LinearLayout) inflater.inflate(R.layout.personalfinance, null);
		
		lay1.addView(personalinfo,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		lay2.addView(modify,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		lay3.addView(Finance,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
	}
	
	public void onTabChanged(String tabId) {
		
		int tabld = Integer.valueOf(tabId);
		for(int i =0;i<tabwidget.getChildCount();i++){
			if(i==tabld){
				tabwidget.getChildAt(i).setBackgroundResource(R.drawable.taobao_tabhost_selected_back);
				titlelist.get(i).setTextColor(Color.argb(225, 51, 51, 51));
			}else{
				tabwidget.getChildAt(i).setBackgroundColor(Color.argb(225, 186, 186, 186));
				titlelist.get(i).setTextColor(Color.argb(225, 113, 113, 113));
			}
		}
	}
	/*
	 * 设置监听
	 */
	private void setonclicklistener(){
		OnClickListener MyClick = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.submitPersonalinfo:
					String password = ((TextView)personalinfo.findViewById(R.id.personal_password)).getText().toString().trim();
					String qq = ((TextView)personalinfo.findViewById(R.id.personal_qq_number)).getText().toString().trim();
					String email = ((TextView)personalinfo.findViewById(R.id.personal_email)).getText().toString().trim();
					String mobilephone = ((TextView)personalinfo.findViewById(R.id.personal_phone_number)).getText().toString().trim();
					//提交个人信息
					if(password!=null&&password.length()>0){
					HashMap<String, Object> param = new HashMap<String, Object>();
					String url = Url.user()+"changeContact/";
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("qq", qq));
					params.add(new BasicNameValuePair("mobile", mobilephone));
					params.add(new BasicNameValuePair("email", email));
					param.put("flag", "changeContact");
					param.put("uri", url);
					param.put("params", params);
					MyAsyTask task = new MyAsyTask();
					task.execute(param);
					}else{
						ToastUtil.showShortToast(SettingActivity.this, "密码不能为空");
					}
					break;
				case R.id.submitPersonalmodify:
					String passold = ((TextView)modify.findViewById(R.id.personal_password_old)).getText().toString().trim();
					String passnew = ((TextView)modify.findViewById(R.id.personal_password_new)).getText().toString().trim();
					String passagin = ((TextView)modify.findViewById(R.id.personal_password_agin)).getText().toString().trim();
					if(passold.equals("")||passnew.equals("")||passagin.equals("")){
						ToastUtil.showShortToast(SettingActivity.this, "不能为空");
					}else if(passnew.equals(passagin)){
						//这里验证通过可以将请求提交到服务器
						HashMap<String, Object> param1 = new HashMap<String, Object>();
						MyAsyTask mytask = new MyAsyTask();
						String url1 = Url.user()+"changePwd";
						List<NameValuePair> params1 = new ArrayList<NameValuePair>();
						params1.add(new BasicNameValuePair("password", passold));
						params1.add(new BasicNameValuePair("newPwd", passnew));
						
						param1.put("uri", url1);
						param1.put("flag", "Modify");
						param1.put("passnew", passnew);
						param1.put("params", params1);
						mytask.execute(param1);
					}else{
						ToastUtil.showShortToast(SettingActivity.this, "两次输入不一致");
					}
					//修改密码
					break;
				case R.id.submitPersonalfinance:
					String password_finance = ((TextView)Finance.findViewById(R.id.personal_password_finance)).getText().toString().trim();
					String realname = ((TextView)Finance.findViewById(R.id.personal_realname)).getText().toString().trim();
					String zhifubao = ((TextView)Finance.findViewById(R.id.personal_zhifubao_count)).getText().toString().trim();
					//个人财务管理
					HashMap<String, Object> param2 = new HashMap<String, Object>();
					String url2 = Url.account()+"changeAlipay/";
					List<NameValuePair> params2 = new ArrayList<NameValuePair>();
					params2.add(new BasicNameValuePair("password", password_finance));
					params2.add(new BasicNameValuePair("realname", realname));
					params2.add(new BasicNameValuePair("alipay", zhifubao));
					param2.put("flag", "finance");
					param2.put("uri", url2);
					param2.put("params", params2);
					MyAsyTask task1 = new MyAsyTask();
					task1.execute(param2);
					break;
				}
			}
		};
		personalinfo.findViewById(R.id.submitPersonalinfo).setOnClickListener(MyClick);
		modify.findViewById(R.id.submitPersonalmodify).setOnClickListener(MyClick);
		Finance.findViewById(R.id.submitPersonalfinance).setOnClickListener(MyClick);
	}
	
	//自定义显示控件
	public View composeLayout(int s) {
		// 定义一个LinearLayout布局
		LinearLayout layout = new LinearLayout(this);
		// 设置布局垂直显示
		layout.setOrientation(LinearLayout.VERTICAL);
		TextView tv = new TextView(this);
		tv.setGravity(Gravity.CENTER);
		tv.setSingleLine(true);
		tv.setText(getResources().getString(s));
		tv.setTextColor(Color.argb(225, 113, 113, 113));
		titlelist.add(tv);
		layout.addView(tv, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		layout.setBackgroundColor(Color.argb(225, 180,180, 180));
		return layout;
	}
	class MyAsyTask extends AsyncTask<HashMap<String, Object>, Integer, HashMap<String, String>>{

		@Override
		protected HashMap<String, String> doInBackground(
				HashMap<String, Object>... params) {
			
			String flag = (String)params[0].get("flag");
			
			HttpConnectionHepler helper = new HttpConnectionHepler();
			String url = (String) params[0].get("uri");
			
			
			if(flag!=null&&flag.equals("Modify")){
				String passnew = (String)params[0].get("passnew");
				List<NameValuePair> param = (List<NameValuePair>)params[0].get("params");
				HashMap<String, String> result = helper.sendPostRequest(url, param);
				result.put("flag",flag);
				String msg = result.get("msg");
				if(msg.equals("success")){
					String username =SettingUtils.Stringget(getApplicationContext(),"username" , null);
					String relogin = Url.user()+"login/?account="+username+"&password="+passnew;
					helper.UserLogin(relogin);
				}
				return result;
			}else if(flag.equals("initInfo")){
				HashMap<String, String> initInfo = new HashMap<String, String>();
				HashMap<String, String> initFinance = new HashMap<String, String>();
				String autherUrl = Url.account()+"getAlipay/";
				initInfo = helper.sendPostRequest(url, null);
				initFinance = helper.sendPostRequest(autherUrl, null);
				initInfo.put("flag", flag);
				initInfo.putAll(initFinance);
				return initInfo;
			}else if(flag.equals("finance")){
				HashMap<String, String> finance = new HashMap<String, String>();
				List<NameValuePair> param = (List<NameValuePair>)params[0].get("params");
				finance = helper.sendPostRequest(url, param);
				finance.put("flag", flag);
				return finance;
			}else if(flag.equals("changeContact")){
				HashMap<String, String> changeinfo = new HashMap<String, String>();
				List<NameValuePair> param = (List<NameValuePair>)params[0].get("params");
				changeinfo = helper.sendPostRequest(url, param);
				changeinfo.put("flag", flag);
				return changeinfo;
			}
			return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			String flag = result.get("flag");
			if(flag.equals("Modify")){
				String msg = result.get("msg");
				if(msg.equals("success")){
					ToastUtil.showShortToast(SettingActivity.this, "修改成功");
				}else{
					ToastUtil.showShortToast(SettingActivity.this, msg);
				}
			}else if(flag.equals("initInfo")){
				//从网上取数据，完成页面初始化
				//个人信息
				 ((TextView)personalinfo.findViewById(R.id.personal_qq_number)).setText((String)result.get("qq"));
				((TextView)personalinfo.findViewById(R.id.personal_email)).setText((String)result.get("email"));
				((TextView)personalinfo.findViewById(R.id.personal_phone_number)).setText((String)result.get("mobile"));
				//财务信息
			    ((TextView)Finance.findViewById(R.id.personal_realname)).setText((String)result.get("realname"));
				((TextView)Finance.findViewById(R.id.personal_zhifubao_count)).setText((String)result.get("alipay"));
			}else if(flag.equals("finance")){
				String msg = result.get("msg");
				if(msg.equals("success")){
					ToastUtil.showShortToast(SettingActivity.this, "修改成功");
				}else{
					ToastUtil.showShortToast(SettingActivity.this, msg);
				}
			}else if (flag.equals("changeContact")){
				String msg = result.get("msg");
				if(msg.equals("success")){
					ToastUtil.showShortToast(SettingActivity.this, "修改成功");
				}else{
					ToastUtil.showShortToast(SettingActivity.this, msg);
				}
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onStart() {
		//先获取网上的数据
		String url = Url.user()+"getContact/";
	    HashMap<String , Object> param = new HashMap<String, Object>();
	    param.put("flag", "initInfo");
	    param.put("uri", url);
	    MyAsyTask task = new MyAsyTask();
	    task.execute(param);
		super.onStart();
	}
	
}
