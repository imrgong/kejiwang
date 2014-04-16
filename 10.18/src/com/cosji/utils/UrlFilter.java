package com.cosji.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cosji.activitys.GoodsDetialsActivitys;
import com.cosji.activitys.R;
import com.cosji.activitys.WebviewActivity;
import com.cosji.application.Base;
import com.cosji.bean.TbkItem;
import com.cosji.tbkapihelper.APITest;
import com.cosji.tbkapihelper.JsonUtil;
import com.cosji.tbkapihelper.Utils;
import com.cosji.view.CustomDialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;



/**
 * 对所有请求的url进行过滤
 * 返回所想要的访问的url
 * @author Administrator
 *
 */
public class UrlFilter {
	
	
	private Handler handler;
	/*
	 * 所有可能跳转到WebView的情况：
	 * 1。搜索     detils：商品名称，num_iid,url
	 * 2.折扣优惠    url
	 * 3.淘宝分类页面 url
	 * 4.注册、找回密码 url
	 * 
	 */
	
	public UrlFilter(){
		handler = new Handler();
	}
	
	public String filters(String url){
		
		if(url.contains("itemId=")||url.contains("id=")||url.contains("Id=")){
			return doFilter(url);
		}else if(url.contains("m.tmall.com")){
			return doFilter(url);
		}else{
			return url;
		}
	}
	/*
	 * 对每次请求的url进行过滤
	 */
	private String doFilter(String url){
		String num_iid = null;
		
		Pattern pattern = Pattern.compile("([0-9]d{11})"); 
		 Matcher matcher = pattern.matcher(url);   
		 while(matcher.find()) {
		  String findValue=matcher.group();
		  System.out.println("正则式:"+findValue); 
		  System.out.println(findValue.substring(0, findValue.length()-1)); 
		   }
		try {
			if(url.contains("m.tmall.com/")){
				num_iid = url.substring(url.indexOf("com/i")+5,url.indexOf(".htm"));
				return num_iid;
			}else if(url.contains("a.m.taobao.com")){
				num_iid = url.substring(url.indexOf("com/i")+5, url.indexOf(".htm"));
				return num_iid;
			}else if(url.contains("m.taobao.com/")){
				num_iid = url.substring(url.indexOf("id=")+3,url.indexOf("id=")+14);
				return num_iid;
			}else if (url.contains("item.taobao.com/")){
				num_iid = url.substring(url.indexOf("id=")+3,url.indexOf("id=")+14);
				return num_iid;
			}else{
				num_iid = url.substring(url.indexOf("itemId=")+7,url.indexOf("itemId=")+21);
				return num_iid;
			}
		} catch (Exception e) {
			Log.v("PushMessageReceiver", "这个url为什么不对:"+url);
			e.printStackTrace();
		}
		return url;
	}
	
	/*
	 * 分析request url 并跳转的逻辑处理方法
	 */
	public boolean dealRequest(String url,final WebView View,final Context context,String title){
		
		    handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what==500){
					TbkItem items = (TbkItem)msg.obj;
					IsIntent.goodsDetials(context, items);
					View.stopLoading();
					//((Activity) context).finish();
				}
				super.handleMessage(msg);
			}
		};
		
		final String endurl =filters(url);
		System.out.println("处理过的url"+endurl);
		
		if(endurl!=null&&endurl.length()==11&&isNumericAs(endurl)){
			new Thread(new Runnable() {
				@Override
				public void run() {
					TbkItem tbkItem = new TbkItem();
					TreeMap<String, String> apiparamsMap = new TreeMap<String, String>();
					apiparamsMap.put("num_iids",endurl);
					apiparamsMap.put("method", "taobao.tbk.items.detail.get");
					JsonUtil json2Object = new JsonUtil();
					String result = Utils.getItems(APITest.get(apiparamsMap));
					List<TbkItem> list = new ArrayList<TbkItem>();
					list = json2Object.jsonToObject(result);
					
					if(list.size()>0){
						tbkItem = list.get(0);
						String click_url = Utils.getconvert(tbkItem.getNum_iid(),Base.getUserId());
						if(click_url!=null&&click_url.length()>10){
							tbkItem.setClick_url(click_url);
						}
					}
					System.out.println("网页里面的数据Tbkitem:"+tbkItem.toString());
					if(tbkItem.getNum_iid()!=null&&tbkItem.getNum_iid().length()>0){
						Message message = new Message();
						message.what=500;
						message.obj = tbkItem;
						handler.sendMessage(message);
					}
				}
			}).start();
			return true;
		}else
		return false;
	}
	/*
	 * 判断 关键字是否是商品ID
	 */
	private static boolean isNumericAs(String str){
		   for(int i=str.length();--i>=0;){
		      int chr=str.charAt(i);
		      if(chr<48 || chr>57)
		         return false;
		   }
		   return true;
		}
}
