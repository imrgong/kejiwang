package com.cosji.application;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.cosji.activitys.WebviewActivity;
import com.cosji.utils.IsIntent;
import com.cosji.utils.ToastUtil;

public class PushMessageReceiver extends FrontiaPushMessageReceiver {

	
	private final static String ACTION_FILTER = "com.cosji.getPushMessageReceiver";
	private final static String TAG = "PushMessageReceiver";

	@Override
	public void onBind(Context context, int errorCode, String appid,
			String userId, String channelId, String requestId) {
	}

	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
	}

	@Override
	public void onSetTags(Context context, int errorCode,
			List<String> successTags, List<String> failTags,
			String requestId) {
	}

	@Override
	public void onDelTags(Context context, int errorCode,
			List<String> successTags, List<String> failTags,
			String requestId) {
	}

	@Override
	public void onListTags(Context context, int errorCode,
			List<String> tags, String requestId) {
	}

	@Override
	public void onMessage(Context context, String message,
			String customContentString) {
	}

	@Override
	public void onNotificationClicked(Context context, String title,
			String description, String customContentString) {
		if(customContentString!=null&&customContentString.length()>0){
			Log.d(TAG, "自定义字段:"+customContentString);
			try {
				JSONObject json = new JSONObject(customContentString);
				String body = (String) json.get("html");
				body.replace("\\", "");
				Intent intent = new Intent(context,WebviewActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("url", body);
				intent.putExtra("title", "新消息");
				intent.putExtra("isrebate",false);
				context.startActivity(intent);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		StringBuffer sb = new StringBuffer();
		sb.append("通知被点击\n");
		sb.append("title:"+title+"\n");
		sb.append("description:"+description);
		sb.append("customContentString:"+customContentString+"\n");
		Log.d(TAG,sb.toString());
	}

	

}