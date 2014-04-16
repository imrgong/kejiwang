package com.cosji.activitys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.cosji.adapter.InnerMessageAdapter;
import com.cosji.adapter.InnerMessageAdapter.ViewHolder;
import com.cosji.utils.HttpConnectionHepler;
import com.cosji.utils.ToastUtil;
import com.cosji.utils.Url;
import com.umeng.analytics.MobclickAgent;

public class InnerMessageActivity extends BaseTab {
	ListView listview1, listview2;
	private TabWidget tabwidget;
	CheckBox ck1, ck2;
	Button bt1, bt2;
	private List<TextView> titlelist = new ArrayList<TextView>();
	InnerMessageAdapter adapter1, adapter2;
	List<HashMap<String, String>> datain;
	List<HashMap<String, String>> dataout;
	private int checkNum1; // 记录选中的条目数量
	private int checkNum2; // 记录选中的条目数量
	private RelativeLayout buttom1;
	private RelativeLayout buttom2;
	private int num = 8;
	private int inboxtotal =1,outboxtotal =1;//总页数
	private int inboxpage = 1,outboxpage = 1;//当前页数
	private Button button1,button2;
	private ProgressBar pro1,pro2;
	private TabHost tabhost;
	private ImageView refresh_big,refresh_small,refresh;//刷新动画控件资源
    private Animation rotate_big,rotate_small;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rebate_page);
		tabhost = getTabHost();
		datain = new ArrayList<HashMap<String,String>>();
		dataout = new ArrayList<HashMap<String,String>>();
		// 为TabHost添加标签
		// 新建一个newTabSpec(newTabSpec)
		// 设置其标签和图标（setIndicator）
		// 设置内容（setContent）
		tabhost.addTab(tabhost
				.newTabSpec("0")
				.setIndicator(composeLayout(R.string.ReciveInof))
				.setContent(R.id.tab1));
		tabhost.addTab(tabhost
				.newTabSpec("1")
				.setIndicator(composeLayout(R.string.SendInfo))
				.setContent(R.id.tab2));

		// 设置TabHost的背景颜色
				tabhost.setBackgroundColor(getResources().getColor(R.color.rebate_tabhost));
				tabhost.setCurrentTab(0);
				tabwidget= (TabWidget) findViewById(android.R.id.tabs);

		// 标签切换事件处理，setOnTabChangedListener
		tabhost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				
				int tabid = Integer.valueOf(tabId);
				for(int i =0;i<tabwidget.getChildCount();i++){
					if(i==tabid){
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
		onTabChanged("0");
	};

	public void initView() {
		
		refresh = (ImageView) findViewById(R.id.web_refresh);
		refresh_big = (ImageView) findViewById(R.id.web_refresh_big);
		refresh_small = (ImageView) findViewById(R.id.web_refresh_small);
		//初始化动画资源
		  rotate_big = AnimationUtils.loadAnimation(this, R.anim.progress_refresh);
		 rotate_small = AnimationUtils.loadAnimation(this, R.anim.progress_refresh_small);
		LinearInterpolator polator = new LinearInterpolator();//匀速旋转
		rotate_big.setInterpolator(polator);
		rotate_small.setInterpolator(polator);
		refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refresh.setVisibility(View.GONE);
				refresh_big.setVisibility(View.VISIBLE);
				refresh_small.setVisibility(View.VISIBLE);
				if(rotate_big!=null&&rotate_small!=null&&refresh.getVisibility()==View.GONE){
					refresh_big.startAnimation(rotate_big);
					refresh_small.startAnimation(rotate_small);
				}
				//数据加载
				int hostid = tabhost.getCurrentTab();
				switch (hostid) {
				case 0:
					inboxpage = 1;
					datain.clear();
					adapter1.refresh(datain);
					showdate(hostid);
					break;
				case 1:
					outboxpage = 1;
					dataout.clear();
					adapter2.refresh(dataout);
					showdate(hostid);
					break;
				}
				anymore(hostid);
			}
		});
		
		LinearLayout back = (LinearLayout) findViewById(R.id.web_back);
		back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});
		
		TextView tv = (TextView) findViewById(R.id.mine_secend_title);
		tv.setText(R.string.innerMessage);
		LinearLayout lay1 = (LinearLayout) findViewById(R.id.tab1);
		LinearLayout lay2 = (LinearLayout) findViewById(R.id.tab2);

		LayoutInflater inflater = LayoutInflater.from(this);
		RelativeLayout layout1 = (RelativeLayout) inflater.inflate(R.layout.inner_message_page, null);
		RelativeLayout layout2 = (RelativeLayout) inflater.inflate(	R.layout.inner_message_page, null);

		lay1.addView(layout1, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		lay2.addView(layout2, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);

		View moreView1 = getLayoutInflater().inflate(R.layout.moredata, null);
		View moreView2 = getLayoutInflater().inflate(R.layout.moredata, null);
		
		listview1 = (ListView) layout1.findViewById(R.id.income_list);
		listview1.addFooterView(moreView1);
		listview2 = (ListView) layout2.findViewById(R.id.income_list);
		listview2.addFooterView(moreView2);
		
		//加载更多的监听
		button1 = (Button)moreView1.findViewById(R.id.bt_load);
		button2 = (Button)moreView2.findViewById(R.id.bt_load);
		pro1 = (ProgressBar)moreView1.findViewById(R.id.Load_pro);
		pro2 = (ProgressBar)moreView2.findViewById(R.id.Load_pro);
		
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				button1.setVisibility(View.GONE);
				pro1.setVisibility(View.VISIBLE);
				moredate(++inboxpage, 0, num);
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				button2.setVisibility(View.GONE);
				pro2.setVisibility(View.VISIBLE);
				moredate(++inboxpage,1, num);
			}
		});
		
		ck1 = (CheckBox) layout1.findViewById(R.id.isquanxuan);
		bt1 = (Button) layout1.findViewById(R.id.zhanneiinfo_delete_bt);
		ck2 = (CheckBox) layout2.findViewById(R.id.isquanxuan);
		bt2 = (Button) layout2.findViewById(R.id.zhanneiinfo_delete_bt);
		//两个隐藏的控件
		buttom1 = (RelativeLayout)layout1.findViewById(R.id.inner_message_page_hidden);
		buttom2 = (RelativeLayout)layout2.findViewById(R.id.inner_message_page_hidden);

		listview1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				ViewHolder holder = (ViewHolder)arg1.getTag();
				holder.ck.toggle();
				if(holder.ck.isChecked()){
					datain.get(arg2).put("flag", "true");
					checkNum1++;
				}else{
					datain.get(arg2).put("flag", "false");
					checkNum1--;
				}
				if(checkNum1!=0&&buttom1.getVisibility()==View.GONE){
				    //动画
					Animation anim_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
					buttom1.startAnimation(anim_in);
					buttom1.setVisibility(View.VISIBLE);
				}else if(checkNum1==0&&buttom1.getVisibility()==View.VISIBLE){
					Animation anim_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_dowm);
					buttom1.startAnimation(anim_out);
					buttom1.setVisibility(View.GONE);
				}
			}
		});
		
		ck1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					// 遍历list的长度，将MyAdapter中的map值全部设为true
					for (int i = 0; i < datain.size(); i++) {
						datain.get(i).put("flag", "true");
					}
					// 数量设为list的长度
					checkNum1 = datain.size();
					// 刷新listview和TextView的显示

				} else {
					for (int i = 0; i < datain.size(); i++) {
						if (datain.get(i).get("flag").equals("true")) {
							datain.get(i).put("flag", "false");
							checkNum1--;// 数量减1
						}
					}
					Animation anim_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_dowm);
					buttom1.startAnimation(anim_out);
					buttom1.setVisibility(View.GONE);
				}
				adapter1.notifyDataSetChanged();

			}
		});
		bt1.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						InnerMessageActivity.this);
				builder.setTitle("提示").setMessage("你确定删除所选项么？");
				builder.setIcon(android.R.drawable.btn_star);
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

				StringBuffer sb = new StringBuffer();
				Iterator<HashMap<String, String>> iterator = datain.iterator();
				while (iterator.hasNext()) {
					HashMap<String, String> temp = iterator.next();
					if (temp.get("flag").equals("true")) {
						sb.append(temp.get("id"));
						sb.append(",");
						iterator.remove();
					}
				}
				String param = sb.toString();
				param = param.substring(0, param.length()-1);
				DeleteData(param);
				checkNum1 = 0;
				adapter1.notifyDataSetChanged();
							}
				})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								dialog.cancel();
							}
						}).show();
			}
		});
		ck2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					// 遍历list的长度，将MyAdapter中的map值全部设为true
					for (int i = 0; i < dataout.size(); i++) {
						dataout.get(i).put("flag", "true");
					}
					// 数量设为list的长度
					checkNum2 = dataout.size();
					// 刷新listview和TextView的显示

				} else {
					for (int i = 0; i < dataout.size(); i++) {
						if (dataout.get(i).get("flag").equals("true")) {
							dataout.get(i).put("flag", "false");
							checkNum2--;// 数量减1
						}
					}
					Animation anim_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_dowm);
					buttom2.startAnimation(anim_out);
					buttom2.setVisibility(View.GONE);
				}
				adapter2.notifyDataSetChanged();
			}

		});
		bt2.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(
						InnerMessageActivity.this);
				builder.setTitle("提示").setMessage("你确定删除所选项么？");
				builder.setIcon(android.R.drawable.btn_star);
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								StringBuffer sb = new StringBuffer();
								Iterator<HashMap<String, String>> iterator = dataout.iterator();
								while (iterator.hasNext()) {
									HashMap<String, String> temp = iterator.next();
									if (temp.get("flag").equals("true")) {
										sb.append(temp.get("id"));
										sb.append(",");
										iterator.remove();
									}
								}
								String param = sb.toString();
								param = param.substring(0, param.length()-1);
								DeleteData(param);
								checkNum2 = 0; 
								adapter2.notifyDataSetChanged();
							}
						})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

										dialog.cancel();
									}
								}).show();

			}
		});
		// 绑定listView的监听器
				listview2.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {

						// 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
						ViewHolder holder = (ViewHolder) arg1.getTag();
						// 改变CheckBox的状态
						holder.ck.toggle();
						// 将CheckBox的选中状况记录下来
						// 调整选定条目
						if (holder.ck.isChecked() == true) {
							dataout.get(arg2).put("flag", "true");
							checkNum2++;
						} else {
							dataout.get(arg2).put("flag", "false");
							checkNum2--;
						}if(checkNum2!=0&&buttom2.getVisibility()==View.GONE){
							Animation anim_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
							buttom2.startAnimation(anim_in);
							buttom2.setVisibility(View.VISIBLE);
						}else if(checkNum2==0&&buttom2.getVisibility()==View.VISIBLE){
							Animation anim_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_dowm);
							buttom2.startAnimation(anim_out);
							buttom2.setVisibility(View.GONE);
						}
					}
				});

	
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
		//初始化按钮
		public void onTabChanged(String tabId) {
			
			int tabid = Integer.valueOf(tabId);
			for(int i =0;i<tabwidget.getChildCount();i++){
				if(i==tabid){
					showdate(0);
					showdate(1);
					tabwidget.getChildAt(i).setBackgroundResource(R.drawable.taobao_tabhost_selected_back);
					titlelist.get(i).setTextColor(Color.argb(225, 51, 51, 51));
				}else{
					tabwidget.getChildAt(i).setBackgroundColor(Color.argb(225, 186, 186, 186));
					titlelist.get(i).setTextColor(Color.argb(225, 113, 113, 113));
				}
			}
		}
		protected void onResume() {
			super.onResume();
			MobclickAgent.onResume(InnerMessageActivity.this);
		}
		@Override
		protected void onPause() {
			super.onPause();
			MobclickAgent.onPause(InnerMessageActivity.this);
		}

		class MyAsytask extends AsyncTask<HashMap<String, Object>, Integer, List<HashMap<String, String>>>{

			@Override
			protected List<HashMap<String, String>> doInBackground(
					HashMap<String, Object>... params) {
				HttpConnectionHepler helper = new HttpConnectionHepler();
				List<HashMap<String, String>> result = new ArrayList<HashMap<String,String>>();
				HashMap<String, String> flagmap = new HashMap<String, String>();
				
				String url = (String)params[0].get("url");
				String flag = (String)params[0].get("flag");
				List<NameValuePair> arg = (List<NameValuePair>)params[0].get("arg");
				if(flag!=null&&flag.equals("delete")){
					HashMap<String, String> temp1 = new HashMap<String, String>();
					HashMap<String, String> temp2 = new HashMap<String, String>();
					flagmap.put("flag", flag);
					temp1 = helper.sendPostRequest(url, arg);
					temp2.put("total", "0");
					result.add(temp1);
					result.add(temp2);
					result.add(flagmap);
					return result;
				}else{
				flagmap.put("flag", flag);
				result = helper.sendPostRequestArray(url, arg);
				for (int i = 0; i < result.size()-1; i++) {
					result.get(i).put("flag", "false");
				}
				result.add(flagmap);
				return result;
				}
			}

			@Override
			protected void onPostExecute(List<HashMap<String, String>> result) {
				String flag = result.get(result.size()-1).get("flag");
				int total = Integer.valueOf( result.get(result.size()-2).get("total"));
				result.remove(result.size()-1);
				result.remove(result.size()-1);
				if(flag!=null&&flag.equals("inboxfirst")){
					if(total%num==0){
						inboxtotal = total/num;
					}else{
						inboxtotal = total/num+1;
					}
					anymore(0);
					if(datain.size()==0){
						datain=result;
					}
					adapter1 = new InnerMessageAdapter(InnerMessageActivity.this, datain);
					listview1.setAdapter(adapter1);
				}else if(flag!=null&&flag.equals("outboxfirst")){
					if(total%num==0){
						outboxtotal = total/num;
					}else{
						outboxtotal = total/num+1;
					}
					anymore(1);
					if(dataout.size()==0){
						dataout=result;
					}
					adapter2 = new InnerMessageAdapter(InnerMessageActivity.this, dataout);
					listview2.setAdapter(adapter2);
				}else if(flag!=null&&flag.equals("inbox")){
					datain.addAll(result);
					adapter1.refresh(datain);
					if(inboxpage<inboxtotal){
					button1.setVisibility(View.VISIBLE);
					pro1.setVisibility(View.GONE);
					}else{
						ToastUtil.showShortToast(InnerMessageActivity.this, "数据全部加载完成");
						pro1.setVisibility(View.GONE);
					}
				}else if(flag!=null&&flag.equals("outbox")){
					dataout.addAll(result);
					adapter2.refresh(dataout);
					if(outboxpage<outboxtotal){
					button2.setVisibility(View.VISIBLE);
					pro2.setVisibility(View.GONE);
					}else{
						ToastUtil.showShortToast(InnerMessageActivity.this, "数据全部加载完成");
						pro2.setVisibility(View.GONE);
					}
				}else{
					String msg = result.get(0).get("msg");
					if(msg!=null&&msg.equals("success")){
						ToastUtil.showShortToast(InnerMessageActivity.this, "删除成功");
					}else{
						ToastUtil.showShortToast(InnerMessageActivity.this, "删除成功");
					}
				}
				refresh.setVisibility(View.VISIBLE);
				refresh_big.clearAnimation();
				refresh_small.clearAnimation();
				refresh_big.setVisibility(View.GONE);
				refresh_small.setVisibility(View.GONE);
				super.onPostExecute(result);
			}
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
		}
		
		//选择性显示
		private void showdate(int tabid){
			String url1 = Url.message()+"inbox/";
			int page1 =1;
			int num = 8;
			String flag1 = "inboxfirst";
			String url2 = Url.message()+"outbox/";
			String flag2 = "outboxfirst";
			switch(tabid){
				case 0:
					 LoadData(url1, page1, num, flag1);
					 break;
				case 1:
					LoadData(url2, page1, num, flag2);
					break;
			}
		}
		//加载更多数据
		private void moredate(int page,int tabid,int num){
			String url1 = Url.message()+"inbox/";
			String flag1 = "inbox";
			String url2 = Url.message()+"outbox/";
			String flag2 = "outbox";
			switch(tabid){
			case 0:
				LoadData(url1, page, num, flag1);
				break;
			case 1:
				LoadData(url2, page, num, flag2);
				break;
			}
		}
		/*
		 * 异步加载实现
		 * @param url
		 * @param page
		 * @param num
		 */
		private void LoadData(String url,int page,int num,String flag){
			HashMap<String, Object> params = new HashMap<String, Object>();
			List<NameValuePair> arg =new ArrayList<NameValuePair>();
			arg.add(new BasicNameValuePair("page", page+""));
			arg.add(new BasicNameValuePair("num", num+""));//定义打开页面的时候只显示前面10条
			params.put("flag", flag);
			params.put("url", url);
			params.put("arg", arg);
			MyAsytask task = new MyAsytask();
			task.execute(params);
		}
		/*
		 * 异步删除消息
		 */
		private void DeleteData(String id){
			HashMap<String, Object> params = new HashMap<String, Object>();
			List<NameValuePair> arg =new ArrayList<NameValuePair>();
			arg.add(new BasicNameValuePair("id", id));//定义打开页面的时候只显示前面10条
			params.put("flag", "delete");
			params.put("url", Url.message()+"/remove/");
			params.put("arg", arg);
			MyAsytask task = new MyAsytask();
			task.execute(params);
		}
		/*
		 * 判断是否该显示
		 * 加载更多的按钮
		 */
		private void anymore(int tabid){
			switch (tabid) {
			case 0:
				if(inboxpage>=inboxtotal){
					button1.setVisibility(View.GONE);
					pro1.setVisibility(View.GONE);
			}else{
				button1.setVisibility(View.VISIBLE);
			}
				break;
			case 1:
				if(outboxpage>=outboxtotal){
					button2.setVisibility(View.GONE);
					pro2.setVisibility(View.GONE);
					}else{
					button2.setVisibility(View.VISIBLE);
					}
				break;
			}
		}
}
