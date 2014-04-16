package com.cosji.activitys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.cosji.adapter.PaipaiRebateAdapter;
import com.cosji.adapter.ShangchengRebateAdapter;
import com.cosji.adapter.TaobaoRebateAdapter;
import com.cosji.utils.HttpConnectionHepler;
import com.cosji.utils.ToastUtil;
import com.cosji.utils.Url;
import com.umeng.analytics.MobclickAgent;

@SuppressWarnings("deprecation")
public class RebateActivity extends BaseTab {

	private ListView taobao_rebate_list, paipai_rebate_list, shangcheng_rebate_list;
	private TabWidget tabwidget;
	private List<HashMap<String, String>> taobaolist,paipailist,shangchenglist;
	private LinearLayout back;
	private TextView shuoming=null;
	private List<TextView> titlelist = new ArrayList<TextView>();
	private TaobaoRebateAdapter adapter1;
	private PaipaiRebateAdapter adapter2;
	private ShangchengRebateAdapter adapter3;
	private Button button1,button2,button3;
	private ProgressBar pro1,pro2,pro3;
	private int taobaototal =1,paipaitotal = 1,shangchengtotal =1;//各个页面总数
	private int taobaopage = 1,paipaipage = 1,shangchengpage = 1;//页面数
	private int num = 8; //设置每次加载的条数,初始化设置为8条
    private TabHost tabhost;
    private ImageView refresh_big,refresh_small,refresh;//刷新动画控件资源
    private Animation rotate_big,rotate_small;
    private View moreView1,moreView2,moreView3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rebate_page);
		
		tabhost = getTabHost();

		// 为TabHost添加标签
		// 新建一个newTabSpec(newTabSpec)
		// 设置其标签和图标（setIndicator）
		// 设置内容（setContent）
		tabhost.addTab(tabhost
				.newTabSpec("0")
				.setIndicator(composeLayout(R.string.taobao_rebate))
				.setContent(R.id.tab1));
		tabhost.addTab(tabhost
				.newTabSpec("1")
				.setIndicator(composeLayout(R.string.paipai_rebate))
				.setContent(R.id.tab2));
		tabhost.addTab(tabhost
				.newTabSpec("2")
				.setIndicator(
						composeLayout(R.string.shangcheng_rebate))
				.setContent(R.id.tab3));
		// 设置TabHost的背景颜色
		tabhost.setBackgroundColor(getResources().getColor(R.color.rebate_tabhost));
		tabhost.setCurrentTab(0);
		tabwidget= (TabWidget) findViewById(android.R.id.tabs);
		onTabChanged("0");
		// 标签切换事件处理，setOnTabChangedListener
		tabhost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				int ld = Integer.valueOf(tabId);
				for(int i =0;i<tabwidget.getChildCount();i++){
					if(i==ld){
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
		
	}
	
	public void initView() {
		
		taobaolist = new ArrayList<HashMap<String,String>>();
		paipailist = new ArrayList<HashMap<String,String>>();
		shangchenglist = new ArrayList<HashMap<String,String>>();
		
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
					taobaopage = 1;
					taobaolist.clear();
					adapter1.refresh(taobaolist);
					showdate(hostid);
					break;
				case 1:
					paipaipage = 1;
					paipailist.clear();
					adapter2.refresh(paipailist);
					showdate(hostid);
					break;
				case 2:
					shangchengpage = 1;
					shangchenglist.clear();
					adapter3.refresh(shangchenglist);
					showdate(hostid);
					break;
				}
				anymore(hostid);
			}
		});
		
		back = (LinearLayout)findViewById(R.id.web_back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		LinearLayout lay1 = (LinearLayout) findViewById(R.id.tab1);
		LinearLayout lay2 = (LinearLayout) findViewById(R.id.tab2);
		LinearLayout lay3 = (LinearLayout) findViewById(R.id.tab3);
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout taobao_rebate = (LinearLayout) inflater.inflate(R.layout.taobao_rebate_page, null);
		LinearLayout paipai_rebate = (LinearLayout) inflater.inflate(R.layout.paipai_rebate_page, null);
		LinearLayout shangcheng_rebate = (LinearLayout) inflater.inflate(R.layout.shangcheng_rebate_page, null);
		
		lay1.addView(taobao_rebate,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		lay2.addView(paipai_rebate,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		lay3.addView(shangcheng_rebate,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		moreView1 = getLayoutInflater().inflate(R.layout.moredata, null);
		moreView2 = getLayoutInflater().inflate(R.layout.moredata, null);
		moreView3 = getLayoutInflater().inflate(R.layout.moredata, null);
		
		taobao_rebate_list = (ListView)taobao_rebate.findViewById(R.id.rebate_list);
		taobao_rebate_list.addFooterView(moreView1);
		 paipai_rebate_list = (ListView)paipai_rebate.findViewById(R.id.rebate_list);
		 paipai_rebate_list.addFooterView(moreView2);
		 shangcheng_rebate_list = (ListView)shangcheng_rebate.findViewById(R.id.rebate_list);
		 shangcheng_rebate_list.addFooterView(moreView3);
		 
		//设置加载更多的监听
			button1 = (Button)moreView1.findViewById(R.id.bt_load);
			button2 = (Button)moreView2.findViewById(R.id.bt_load);
			button3 = (Button)moreView3.findViewById(R.id.bt_load);
			pro1 = (ProgressBar)moreView1.findViewById(R.id.Load_pro);
			pro2 = (ProgressBar)moreView2.findViewById(R.id.Load_pro);
			pro3 = (ProgressBar)moreView3.findViewById(R.id.Load_pro);
			button1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					button1.setVisibility(View.GONE);
					pro1.setVisibility(View.VISIBLE);
					moredate(++taobaopage, 0, num);
				}
			});
			button2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					button2.setVisibility(View.GONE);
					pro2.setVisibility(View.VISIBLE);
					moredate(++paipaipage,1, num);
				}
			});
			button3.setOnClickListener(new OnClickListener() {
				int tabid = tabhost.getCurrentTab();
				@Override
				public void onClick(View v) {
					button3.setVisibility(View.GONE);
					pro3.setVisibility(View.VISIBLE);			
					moredate(++shangchengpage,2, num);
				}
			});
		//暂时留着，没什么用
		shuoming = (TextView)taobao_rebate.findViewById(R.id.taobao_rebate_shuoming);
	}
	
	public void onTabChanged(String tabId) {
		
		int tabld = Integer.valueOf(tabId);
		for(int i =0;i<tabwidget.getChildCount();i++){
			if(i==tabld){
				showdate(0);
				showdate(1);//初始化加载数据
				showdate(2);
				tabwidget.getChildAt(i).setBackgroundResource(R.drawable.taobao_tabhost_selected_back);
				titlelist.get(i).setTextColor(Color.argb(225, 51, 51, 51));
			}else{
				tabwidget.getChildAt(i).setBackgroundColor(Color.argb(225, 186, 186, 186));
				titlelist.get(i).setTextColor(Color.argb(225, 113, 113, 113));
			}
		}
		
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
	class RebateAsytask extends AsyncTask<HashMap<String, Object>, Integer, List<HashMap<String, String>>>{

		@Override
		protected List<HashMap<String, String>> doInBackground(
				HashMap<String, Object>... params) {
			HttpConnectionHepler helper = new HttpConnectionHepler();
			List<HashMap<String, String>> result = new ArrayList<HashMap<String,String>>();
			HashMap<String, String> flagmap = new HashMap<String, String>();
			
			String url = (String)params[0].get("url");
			String flag = (String)params[0].get("flag");
			List<NameValuePair> arg = (List<NameValuePair>)params[0].get("arg");
			flagmap.put("flag", flag);
			result = helper.sendPostRequestArray(url, arg);
			result.add(flagmap);
			return result;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {
			System.out.println("数据未删改之前："+result.toString());
			String flag = result.get(result.size()-1).get("flag");
			int total =0;
			if(result.size()>=2){
			total = Integer.valueOf( result.get(result.size()-2).get("total"));
			result.remove(result.size()-1);
			}
			
			result.remove(result.size()-1);
			if(flag.equals("tolistfirst")){
				if(total%num==0){
					taobaototal = total/num;
				}else{
					taobaototal = total/num+1;
				}
				anymore(0);
				if(taobaolist.size()==0){
					taobaolist=result;
				}
				adapter1 = new TaobaoRebateAdapter(getApplicationContext(),taobaolist);
				taobao_rebate_list.setAdapter(adapter1);
			}else if(flag.equals("polistfirst")){
				if(total%num==0){
					paipaitotal = total/num;
				}else{
					paipaitotal = total/num+1;
				}
				anymore(1);
				if(paipailist.size()==0){
					paipailist=result;
				}
				adapter2 = new PaipaiRebateAdapter(RebateActivity.this, paipailist);
				paipai_rebate_list.setAdapter(adapter2);
			}else if(flag.equals("molistfirst")){
				if(total%num==0){
					shangchengtotal = total/num;
				}else{
					shangchengtotal = total/num+1;
				}
				anymore(2);
			    if(shangchenglist.size()==0){
			    	shangchenglist=result;
			    }
			    adapter3 = new ShangchengRebateAdapter(RebateActivity.this,shangchenglist);
			    shangcheng_rebate_list.setAdapter(adapter3);
			}else if(flag.equals("tolist")){
				taobaolist.addAll(result);
				adapter1.refresh(taobaolist);
				if(taobaopage<taobaototal){
				button1.setVisibility(View.VISIBLE);
				pro1.setVisibility(View.GONE);
				}else{
					ToastUtil.showShortToast(RebateActivity.this, "数据全部加载完成");
					pro1.setVisibility(View.GONE);
				}
			}else if(flag.equals("polist")){
				paipailist.addAll(result);
				adapter2.refresh(paipailist);
				if(paipaipage<paipaitotal){
				button2.setVisibility(View.VISIBLE);
				pro2.setVisibility(View.GONE);
				}else{
					ToastUtil.showShortToast(RebateActivity.this, "数据全部加载完成");
					pro2.setVisibility(View.GONE);
				}
			}else{
				shangchenglist.addAll(result);
				adapter3.refresh(shangchenglist);
				if(shangchengpage<shangchengtotal){
				button3.setVisibility(View.VISIBLE);
				pro3.setVisibility(View.GONE);
				}
				else{
					ToastUtil.showShortToast(RebateActivity.this, "数据全部加载完成");
					pro3.setVisibility(View.GONE);
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
protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(RebateActivity.this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(RebateActivity.this);
	}
	//加载更多数据
		private void moredate(int page,int tabid,int num){
			//url1暂时还没完成
			String url1 = Url.order("tolist");
			String flag1 = "tolist";
			String url2 = Url.order("polist");
			String flag2 = "polist";
			String url3 = Url.order("molist");
			String flag3 = "molist";
			switch(tabid){
			case 0:
				LoadData(url1, page, num, flag1);
				break;
			case 1:
				LoadData(url2, page, num, flag2);
				break;
			case 2:
				LoadData(url3, page, num, flag3);
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
			RebateAsytask task = new RebateAsytask();
			task.execute(params);
		}
		/*
		 * 判断是否该显示
		 * 加载更多的按钮
		 */
		private void anymore(int tabid){
			switch (tabid) {
			case 0:
				if(taobaopage>=taobaototal){
					button1.setVisibility(View.GONE);
					pro1.setVisibility(View.GONE);
				}else{
					button1.setVisibility(View.VISIBLE);
				}
				break;
				case 1:
					if(paipaipage>=paipaitotal){
						button2.setVisibility(View.GONE);
						pro2.setVisibility(View.GONE);
						}else{
						button2.setVisibility(View.VISIBLE);
						}
					break;
				case 2:
					if(shangchengpage>=shangchengtotal){
						button3.setVisibility(View.GONE);
						pro3.setVisibility(View.GONE);
						}else{
							button3.setVisibility(View.VISIBLE);
						}
					break;
			}
		}
		//打开页面时，加载数据，切换时不会重复加载
		private void showdate(int tabid){
			int page1 =1;
			String url1 = Url.order("tolist");
			String flag1 = "tolistfirst";
			String url2 = Url.order("polist");
			String flag2 = "polistfirst";
			String url3 = Url.order("molist");
			String flag3 = "molistfirst";
			switch(tabid){
				case 0:
					 LoadData(url1, page1, num, flag1);
					 break;
				case 1:
					LoadData(url2, page1, num, flag2);
					break;
				case 2:
					LoadData(url3, page1, num, flag3);
					break;
			}
		}
}
