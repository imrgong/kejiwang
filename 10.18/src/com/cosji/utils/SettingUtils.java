package com.cosji.utils;

import java.io.File;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.cosji.activitys.R;

public class SettingUtils {
	public static final String WIFI_SWITCH= "wifi_switch";	//wifi是否打开
	public static final String USER_RE= "user_remenber";	//是否记住用户
	public static final String USER_STATE= "user_state";	//用户是否登录
	public static final String SIGN_STATE = "status";//是否签到
	public static final String USER_ID = "user_id";//是否签到
	public static final String VERSION_ID = "version_id";//版本号
	public static final String DOWNLOAD = "download";//更新地址
	public static Dialog mProgressDialog;//数据刷新进度条
	/*
	 * 搜索框自动提示内容
	 */

	private static final String PREF = "MySearChHistory";
	public SharedPreferences sp;
	/**
	 * 判断给定字符串是否空白串。
	 * 空白串是指由空格、制表符、回车符、换行符组成的字符串
	 * 若输入字符串为null或空字符串，返回true
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty( String input ) 
	{
		if ( input == null || "".equals( input ) )
			return true;
		
		for ( int i = 0; i < input.length(); i++ ) 
		{
			char c = input.charAt( i );
			if ( c != ' ' && c != '\t' && c != '\r' && c != '\n' )
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * 读取用户wifi状态
	 * @param context
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static boolean get(Context context, String name, boolean defaultValue) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean value = prefs.getBoolean(name, defaultValue);
		return value;
	}
	
	/**
	 * 保存用户wifi状态
	 * @param context
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean set(Context context, String name, boolean value) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putBoolean(name, value);
		return editor.commit();	//提交
	}
	/**
	 * 保存用户的状态
	 * @param context
	 * @param name
	 * @param value
	 * @return
	 */
	public static boolean Stringset(Context context, String name, String value) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString(name, value);
		return editor.commit();	//提交
	}
	/**
	 * 获取用户的状态
	 * @param context
	 * @param name
	 * @param defultvalue
	 * @return
	 */
	public static String Stringget(Context context, String name, String defultvalue) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(name, defultvalue);
	}

	/**
	 * 获取文件夹大小
	 * @param file File实例
	 * @return long 单位为M
	 * @throws Exception
	 */
	public static long getFolderSize(java.io.File file)throws Exception{
		long size = 0;
	    java.io.File[] fileList = file.listFiles();
	    for (int i = 0; i < fileList.length; i++)
	    {
	        if (fileList[i].isDirectory())
	        {
	            size = size + getFolderSize(fileList[i]);
	        } else
	        {
	            size = size + fileList[i].length();
	        }
	    }
	    return size/1048576;//1024*1024=1048576
	}
	/*
	 * 删除文件
	 * 
	 */
	
	public static void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
		if (file.isFile()) { // 判断是否是文件
		file.delete(); // delete()方法 你应该知道 是删除的意思;
		} else if (file.isDirectory()) { // 否则如果它是一个目录
		File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
		for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
	deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
		}
		}
		file.delete();
		} else {
	
		}
		}
	
	public static void startdialog(Context context) {

		mProgressDialog = new Dialog(context, R.style.theme_dialog_alert);
		mProgressDialog.setContentView(R.layout.window_layout);
	//	mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}
	
	

	/*
	 * 搜索框自动提示方法
	 * 
	 * @see android.app.Activity#onStart()
	 */

	public void AutoCompleteText(final Context context,final AutoCompleteTextView searchtext) {

		
	//	initAutoComplete(context,"history", searchtext);
		searchtext.setDropDownBackgroundDrawable(context.getResources().getDrawable(
				R.color.white));
		
		searchtext.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (searchtext.getText().toString().equals("清除历史记录")) {
					sp.edit().clear().commit();
					searchtext.setText(null);
				}
				else{
				IsIntent.SearchGoods(context, searchtext.getText()
						.toString().trim());
				}
			}
		});
		searchtext.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (KeyEvent.KEYCODE_ENTER == keyCode
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
//					saveHistory(context,"history", searchtext);
					IsIntent.SearchGoods(context, searchtext
							.getText().toString().trim());
					final InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchtext.getWindowToken(), 0);
				}
				return false;
			}
		});
	}
	private void initAutoComplete(final Context context,String field, AutoCompleteTextView auto) {
		sp = context.getSharedPreferences(PREF, 0);
		String longhistory = sp.getString(field, "清除历史记录");
		auto.setDrawingCacheBackgroundColor(context.getResources().getColor(R.color.autocompletview_drop));
		auto.setThreshold(1);
		String[] hisArrays = longhistory.split(",");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.textview_item, R.id.autocompletetextview_id, hisArrays);
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
//				android.R.layout.simple_dropdown_item_1line, hisArrays);		
		// 只保留最近的50条的记录
		if (hisArrays.length > 50) {
			String[] newArrays = new String[50];
			System.arraycopy(hisArrays, 0, newArrays, 0, 50);
//			adapter = new ArrayAdapter<String>(context,
//					android.R.layout.simple_dropdown_item_1line, newArrays);
			adapter = new ArrayAdapter<String>(context, R.layout.textview_item, R.id.autocompletetextview_id, newArrays);
		}
		auto.setAdapter(adapter);
//		if (hisArrays.length<5) {
//			auto.setDropDownHeight(70*hisArrays.length);
//		}
//		else{
//			auto.setDropDownHeight(350);	
//		}
		
//	auto.setCompletionHint("");
		auto.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				AutoCompleteTextView view = (AutoCompleteTextView) v;
				if (hasFocus) {
					view.showDropDown();
				}
				SharedPreferences sp =context.getSharedPreferences(PREF, 0);
				String longhistory = sp.getString("history", "清除历史记录");

				String[] hisArrays = longhistory.split(",");
				ArrayAdapter<String>	adapter = new ArrayAdapter<String>(context, R.layout.textview_item, R.id.autocompletetextview_id, hisArrays);
//				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//						context,
//						android.R.layout.simple_dropdown_item_1line, hisArrays);
			}
		});
	}

	/**
	 * 保存数据时选择一个固定值做　“ｋｅｙ“ 这样再读取时才知道通过什么ｋｅｙ来取值。
	 * 
	 * @param field
	 * @param auto
	 */
	public void saveHistory(Context context,String field, AutoCompleteTextView auto) {
		String addText = auto.getText().toString();
		SharedPreferences sp=context.getSharedPreferences(PREF, 0);
		SharedPreferences.Editor edit = sp.edit();
		String longhistory = sp.getString(field, "清除历史记录");

		if (!longhistory.contains(addText + ",")) {
			StringBuilder sb = new StringBuilder(longhistory);
			sb.insert(0, addText + ",");
			edit.putString("history", sb.toString());
			edit.commit();
		}
	}
	 
}
