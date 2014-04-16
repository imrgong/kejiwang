package com.cosji.activitys;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
public abstract class BaseActivity extends Activity  {

	private MyHandler handler = new MyHandler();
	private static final String ACTION_FILTER="com.cosji.recevice.message";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	protected void handleOtherMessage(int flag){
		
	}
	
	public void sendMessage(int flag) {
		handler.sendEmptyMessage(flag);
	}
	
	public void sendMessageDely(int flag,long delayMillis){
		handler.sendEmptyMessageDelayed(flag, delayMillis);
	}
	
	public void showToast(String toast_message){
		handler.toast_message = toast_message;
		sendMessage(MyHandler.SHOW_STR_TOAST);
	}

	public void showToast(int res){
		handler.toast_res = res;
		sendMessage(MyHandler.SHOW_RES_TOAST);
	}
	
	private class MyHandler extends Handler {
		public static final int SHOW_STR_TOAST = 0;
		public static final int SHOW_RES_TOAST = 1;
		
		private String toast_message=null;
		private int toast_res;

		@Override
		public void handleMessage(Message msg) {
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
					case SHOW_STR_TOAST:
						Toast.makeText(getBaseContext(), toast_message, 1).show();
						break;
					case SHOW_RES_TOAST:
						Toast.makeText(getBaseContext(), toast_res, 1).show();
						break;
					default:
						handleOtherMessage(msg.what);
				}
			}
		}
		
		
	}
//	public static void showMessage(Context context,String message,
//			String customContentString){
//		CustomDialog.Builder builder = new CustomDialog.Builder(context);  
//        builder.setMessage(message);  
//        builder.setTitle("提示");  
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
//            public void onClick(DialogInterface dialog, int which) {  
//                dialog.dismiss();  
//                //设置你的操作事项  
//            }  
//        });  
//  
//        builder.setNegativeButton("取消",  
//                new android.content.DialogInterface.OnClickListener() {  
//                    public void onClick(DialogInterface dialog, int which) {  
//                        dialog.dismiss();  
//                    }  
//                });  
//  
//        builder.create().show();  
//	}
}
