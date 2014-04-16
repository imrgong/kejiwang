package com.cosji.activitys;

import java.util.HashMap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.api.FrontiaPush;
import com.cosji.application.Base;
import com.cosji.utils.HttpConnectionHepler;
import com.cosji.utils.SettingUtils;
import com.cosji.utils.ToastUtil;
import com.cosji.utils.Url;

public class WelcomeActivity extends BaseActivity{
	  SharedPreferences preferences;
	  private boolean remenber;
	  private HttpConnectionHepler helper;
	  private  FrontiaPush mPush;

protected void onCreate(Bundle savedInstanceState) { 
	super.onCreate(savedInstanceState);
	setContentView(R.layout.welcome);
	remenber = SettingUtils.get(getApplicationContext(), SettingUtils.USER_RE, false);
	isFirstEnter();
	helper = new HttpConnectionHepler();
	initPush();
}

private void initPush(){
	
	mPush = Frontia.getPush();
	Base base = new Base();
	base.setmPush(mPush);
	mPush.start();
}
private void isFirstEnter(){
	  //读取SharedPreferences中需要的数据
    preferences = getSharedPreferences("count",MODE_WORLD_READABLE);
  
    new Thread(){
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		super.run();
    		String name = SettingUtils.Stringget(getApplicationContext(),"username" , null);
		    String pas = SettingUtils.Stringget(getApplicationContext(),"password" , null);
		    boolean once = (name!=null&&name.length()>0);
	    	boolean twice = (pas!=null&&pas.length()>0);
    		if(remenber){
    		    if (Base.isNetworkConnected(WelcomeActivity.this)){
    				if(once&&twice){
    					//这里做登陆操作
    					String url = Url.user()+"login/?account="+name+"&password="+pas;
    					HashMap<String, String> cookie = helper.UserLogin(url);
    					String msg = cookie.get("msg");
    					if(msg!=null&&msg.equals("success")){
    						SettingUtils.Stringset(getApplicationContext(), SettingUtils.USER_ID, cookie.get("userId"));
    						System.out.println("welcomeActivity"+cookie.toString());
    						Base.setUserId(cookie.get("userId"));
    						ToastUtil.showShortToast(getApplicationContext(),R.string.first_page_welcome_cosji+"name");
    					}
    				}else{
    					ToastUtil.showShortToast(WelcomeActivity.this, "用户名/密码不能为空");
    				}
    			}else{
    				ToastUtil.showShortToast(WelcomeActivity.this, "网络出错,请检测网络...");
    			}
    		}
    		
    		try {
    			sleep(1000);
    			  //判断程序与第几次运行，如果是第一次运行则跳转到引导页面
    			  int count = preferences.getInt("count", 0);
    			  //为了掩盖事实真相，不让它进入guide页面。
    			//  count = 0;
    		    if (count==0) {

    		        Intent intent = new Intent();
    		        intent.setClass(getApplicationContext(),WelcomeGuid.class);
    		        startActivity(intent);
    		        finish();
    		    }
    		    else{
    		    	  Intent intent = new Intent();
    		          intent.setClass(getApplicationContext(),MainTabActivity.class);

    		          startActivity(intent);
    		          finish();
    		    }
    		    Editor editor = preferences.edit();
    		    //存入数据
    		    editor.putInt("count", ++count);
    		    //提交修改
    		    editor.commit();
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    }.start();
}
/**
 * 获取App安装包信息
 * 
 * @return
 */
 	public PackageInfo getPackageInfo() {
 		PackageInfo info = null;
 		try {
 			info = getPackageManager().getPackageInfo(getPackageName(), 0);
 		} catch (NameNotFoundException e) {
 			e.printStackTrace(System.err);
 		}
 		if (info == null)
 			info = new PackageInfo();
 		return info;
 	}
 	/* 
 	 * 获取当前程序的版本号  
 	 */  
 	public String getVersionName(){  
 	    //getPackageName()是你当前类的包名，0代表是获取版本信息 
 		PackageInfo packInfo =null;
 		try {
 			packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);  
 		} catch (Exception e) {
 		    e.printStackTrace();
 		}
 	    return packInfo.versionName;   
 	} 
}
