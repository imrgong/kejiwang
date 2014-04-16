package com.cosji.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.sax.StartElementListener;
import android.util.Log;

import com.android.browser.BrowserActivity;
import com.cosji.activitys.GoodsActivity;
import com.cosji.activitys.GoodsDetialsActivitys;
import com.cosji.activitys.LoginActivity;
import com.cosji.activitys.OtherGoodsDetilsActivity;
import com.cosji.activitys.R;
import com.cosji.activitys.WebviewActivity;
import com.cosji.application.Base;
import com.cosji.bean.TbkItem;
import com.cosji.tbkapihelper.Utils;
public class IsIntent {
	/*
	 * 提示判断跳转购物之前是否需要登陆；
	 */
	public static void prompt(final Context context,final String name,final String url) {
		String userId = Base.getUserId();
		if (userId!=null&&userId.length()>0) {
			Login(context, name,url,Base.getUserId());
		
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("提示").setMessage("只有登录后才可以获取返利哦？");
			builder.setIcon(android.R.drawable.btn_star);
			builder.setPositiveButton("登录",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Login(context, name,url, null);
						}
					});
					builder.setNegativeButton("跳过",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									
									if (url!=null&&url.contains("http")) {
										 ToWeb(context,url,name,true);
									}
									
							dialog.dismiss();
								}
							});
					builder.show();
		}
	}
	public static void prompt1(final Context context,final String name,final String id) {
		if(id==null&&id.length()==0){
			return;
		}
		if (Base.getUserId()!=null&&Base.getUserId().length()>0) {
			ToWeb(context,id,name,true);
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("提示").setMessage("只有登录后才可以获取返利哦？");
			builder.setIcon(android.R.drawable.btn_star);
			builder.setPositiveButton("登录",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							ToLogin(context);
						}
					})
					.setNegativeButton("跳过",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									ToWeb(context,id,name,true);
							dialog.dismiss();
								}
							}).show();
		}
	}
	public static void goodsDetials(final Context context, final TbkItem tbkItem) {
		//	ToWeb(context,id,name,true);
			context.startActivity(new Intent(context,OtherGoodsDetilsActivity.class).putExtra("tbkitem", tbkItem));
	}
	public static void jiuyuanGoodsDetials(final Context context, final TbkItem tbkItem) {
		//	ToWeb(context,id,name,true);
		
		if (Base.getUserId()!=null&&Base.getUserId().length()>0) {
			String userid=Base.getUserId();
			context.startActivity(new Intent(context,GoodsDetialsActivitys.class).putExtra("tbkitem", tbkItem));
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("提示").setMessage("只有登录后才可以获取返利哦？");
			builder.setIcon(android.R.drawable.btn_star);
			builder.setPositiveButton("登录",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							ToLogin(context);
						}
					})
					.setNegativeButton("跳过",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									context.startActivity(new Intent(context,GoodsDetialsActivitys.class).putExtra("tbkitem", tbkItem).putExtra("height", "short"));
							dialog.dismiss();
								}
							}).show();
		}
	}
/*
 * 跳转，islogin:1:已登录，跳转；1未登录，需要登陆跳转；2未登录，不需要登陆
 */
	private static void Login(final Context context,final String name,final String url, String userId) {
		if (userId!=null&&userId.length()>0) {
			String url1="";
				if (url.contains("&e=")) {
					String[] urls=url.split("&e=");
					url1=urls[0]+"&e="+userId+urls[1];
				}
				else{
					url1=url+""+userId;
				}
		Log.e("urlss",url1);			
		ToWeb(context, url1, name,true);
		}else {
			ToLogin(context);
		}
	}
	public static void ToLogin(Context context){
		Intent intent = new Intent();
		intent.setClass(context, LoginActivity.class);
		context.startActivity(intent);
		((Activity) context).overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_right_out);
	}
	public static void ToWeb(Context context,String url,String name,boolean isrebate){
		Intent intent = new Intent(context, WebviewActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("isrebate", isrebate);
		intent.putExtra("title", name);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
		((Activity) context).overridePendingTransition(R.anim.slide_left_in,
				R.anim.slide_right_out);
	}
	/*
	 * 从商品详情里面跳转
	 */
	public static void ToWeb1(Context context,String url,String name,boolean isrebate){
		Util util = new Util();
		Intent intent = new Intent(context, BrowserActivity.class);
		intent.putExtra("url", util.paramRules(url, context));
		intent.putExtra("isrebate", isrebate);
		intent.putExtra("title", name);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
//		Uri address = Uri.parse(util.paramRules(url, context));
//		Intent intent = new Intent(Intent.ACTION_VIEW, address);
//		context.startActivity(intent);
		((Activity) context).overridePendingTransition(R.anim.slide_left_in,
				R.anim.slide_right_out);
	}
	public static void ToWeb2(Context context,String url,String name){
		Util util = new Util();
		Intent intent = new Intent(context, WebviewActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("isrebate", false);
		intent.putExtra("title", name);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		((Activity) context).overridePendingTransition(R.anim.slide_left_in,
				R.anim.slide_right_out);
	}
		/*
		 * 商品搜索跳转
		 */
		public static void SearchGoods(Context context, String goods_types) {
			Intent intent = new Intent();
			if (goods_types.startsWith("http://")||IsIntent.isNumericAs(goods_types)) {
				intent.putExtra("url", goods_types);
				intent.putExtra("isrebate", true);
				intent.putExtra("title", "商品详情");
				intent.putExtra("flag", "search");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(context, WebviewActivity.class);
			}
			else{
			intent.putExtra("goods_types", goods_types);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(context, GoodsActivity.class);
			}
			context.startActivity(intent);
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
