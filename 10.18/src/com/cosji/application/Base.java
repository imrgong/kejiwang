package com.cosji.application;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaApplication;
import com.baidu.frontia.api.FrontiaPush;
import com.cosji.activitys.MainTabActivity.MyHandler;
import com.cosji.utils.AsynDownloadManager;
import com.cosji.utils.Contact;
import com.cosji.utils.ImageLoader;
import com.cosji.utils.SettingUtils;

public class Base extends FrontiaApplication {
	 
      
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	public static final int REFERSH_PAGE = 125;//刷新我的可及页面
	
	public static AsynDownloadManager manager;
	public static ImageLoader imageLoader;
	private static String userId;
	public static boolean isTaobaoEnabled;
	private static int currentTotle;
	public static boolean isInit;
	private FrontiaPush mPush;
    public static CosjiDB cosjiDb;
    public static String VersionName;
    public static int VersionCode;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		manager = new AsynDownloadManager();
		imageLoader = new ImageLoader(getApplicationContext());
		cosjiDb = new CosjiDB(getApplicationContext());
		
		isInit = Frontia.init(getApplicationContext(), Contact.COSJI_BAIDU_APPKEY);
		
		isTaobaoEnabled = scanApp();
		
		VersionName = getVersionName();
		VersionCode = getVersionCode();
		
		File file = new File(Environment.getExternalStorageDirectory(),"Cosji");
		  try {
			  if (file.exists()) {
				  if (SettingUtils.getFolderSize(file)>50) {
					  SettingUtils.deleteFile(file);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * 三个全局变量
	 */
	private MyHandler handler = null;

	public MyHandler getHandler() {
		return handler;
	}
	public void setHandler(MyHandler handler) {
		this.handler = handler;
	}
	/**
	 * 检测网络是否可用
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	/*
	 * 判断是否安装淘宝客户端
	 */
	private boolean scanApp(){
		List<PackageInfo> appPackage = getPackageManager().getInstalledPackages(0);
		for(int i=0;i<appPackage.size();i++){
			PackageInfo packageinfo = appPackage.get(i);
			if(packageinfo.packageName.equals("com.taobao.taobao")){
				String versionName = packageinfo.versionName;
				versionName.indexOf(0);
			return true;
		  }
		}
		return false;
	}
/*
 * 判断wifi是否可用
 */
public static boolean isWifiConnected(Context context) { 
if (context != null) { 
ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
.getSystemService(Context.CONNECTIVITY_SERVICE); 
NetworkInfo mWiFiNetworkInfo = mConnectivityManager 
.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
if (mWiFiNetworkInfo != null) { 
return mWiFiNetworkInfo.isAvailable(); 
} 
} 
return false; 
} 
	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}
	public static String getUserId() {
		return userId;
	}
	public static void setUserId(String userId) {
		Base.userId = userId;
	}
	public static int getCurrentTotle() {
		return currentTotle;
	}
	public static void setCurrentTotle(int currentTotle) {
		Base.currentTotle = currentTotle;
	}
	public FrontiaPush getmPush() {
		return mPush;
	}
	public void setmPush(FrontiaPush mPush) {
		this.mPush = mPush;
	}
	
	private String getVersionName(){  
	    //getPackageName()是你当前类的包名，0代表是获取版本信息 
		PackageInfo packInfo =null;
		try {
			packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);  
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    return packInfo.versionName;   
	} 
	
	private int getVersionCode(){  
		//getPackageName()是你当前类的包名，0代表是获取版本信息 
		PackageInfo packInfo =null;
		try {
			packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return packInfo.versionCode;   
	} 
}
