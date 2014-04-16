package com.cosji.activitys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cosji.adapter.ListViewAdapter;
import com.cosji.utils.HttpConnectionHepler;
import com.cosji.utils.Url;

public class rebateQuestionActivity extends BaseActivity {

	private ListViewAdapter adapter;
	private Handler handler;
	private ListView listview;
    private List<HashMap<String, String>> data;
    private LinearLayout helpcorner_back;
    private String ss = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_list);
		initView();
		initdata();
	}
	
	private void initView() {
		helpcorner_back = (LinearLayout)findViewById(R.id.helpcorner_back);
		data= new ArrayList<HashMap<String,String>>();
		listview = (ListView)findViewById(R.id.notebook_list);
		listview.setOnItemClickListener(myclicklistener);
		helpcorner_back.setOnClickListener(Myclicklintener);
		}
	OnClickListener Myclicklintener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			rebateQuestionActivity.this.finish();
		}
	};

	OnItemClickListener myclicklistener = new OnItemClickListener() {
		@SuppressLint("NewApi")
		@Override
		public void onItemClick(AdapterView<?> listview, View arg1, int position,
				long arg3) {
//			int y = 48+position*px2dip(rebateQuestionActivity.this,arg1.getHeight());
			if(data.get(position).get("flag").equals("false")){
				data.get(position).put("flag", "true");
			}else{
				data.get(position).put("flag", "false");
			}
			adapter.refersh(position);
//			listview.scrollBy(0, y);
		}
	};
	private int px2dip(Context context, float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(pxValue / scale +0.5f);
		}

	private void initdata() {
		Intent ea = getIntent();
			Bundle page = ea.getExtras();
			ss = page.getString("cateId");
			if(ss.equals("30")){
				((TextView)this.findViewById(R.id.mine_secend_title)).setText("账户问题");
			}else{
				((TextView)this.findViewById(R.id.mine_secend_title)).setText("常见问题");
			}
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what==2){
					data = (List<HashMap<String,String>>)msg.obj;
					for (int i = 0; i < data.size(); i++) {
						data.get(i).put("flag", "false");
					}
				}
				adapter = new ListViewAdapter(rebateQuestionActivity.this, data);
				listview.setAdapter(adapter);
				super.handleMessage(msg);
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = Url.help();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("catId", ss));
				HttpConnectionHepler helper = new HttpConnectionHepler();
				List<HashMap<String, String>> result = new ArrayList<HashMap<String,String>>();
				if(ss.equals("30")){
				result = helper.sendPostMallReArray(rebateQuestionActivity.this, url, params, "fanli");
				}else{
				result = helper.sendPostMallReArray(rebateQuestionActivity.this, url, params, "comm");
				}
				Message msg = new Message();
				msg.what = 2;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}
}
