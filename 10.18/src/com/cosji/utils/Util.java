package com.cosji.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cosji.application.Base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.telephony.TelephonyManager;

public class Util {
	/**
	 * 工具类
	 */
	public static String getDate(String unixDate) {
		  
		   SimpleDateFormat fm1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		   SimpleDateFormat fm2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		   long unixLong = 0;
		   String date = "";
		   try {
		   unixLong = Long.parseLong(unixDate) * 1000;
		   } catch(Exception ex) {
		   System.out.println("String转换Long错误，请确认数据可以转换！");
		   }

		   try {
		   date = fm1.format(unixLong);
		   date = fm2.format(new Date(date));
		   } catch(Exception ex) {
		   System.out.println("String转换Date错误，请确认数据可以转换！");
		   }
		   return date;
		   }
	/*
	 * 获取手机IMEI
	 * 作为SID返回给URL拼接
	 */
	private String getPhoneIMEI(Context ctx){
		 TelephonyManager tm=(TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
		 return "t"+tm.getDeviceId();
	}
	/*
	 * 生成ttid
	 */
	private String createTTid(){
		
		StringBuffer sb = new StringBuffer();
		sb.append("400000_").append(Contact.APPKEY);
		sb.append("@").append("cosji_");
		sb.append("Android_");
		sb.append(Base.VersionName);
		return sb.toString();
	}
	
	public String paramRules(String url,Context ctx){
		StringBuffer sb = new StringBuffer();
		sb.append(url);
		sb.append("&sid=");
		sb.append(getPhoneIMEI(ctx));
		sb.append("&ttid=");
		sb.append(createTTid());
		System.out.println("拼接过后的url:"+sb.toString());
		return sb.toString();
	}
	
}
