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

import com.cosji.adapter.IncomeAdapter;
import com.cosji.utils.HttpConnectionHepler;
import com.cosji.utils.ToastUtil;
import com.cosji.utils.Url;
import com.umeng.analytics.MobclickAgent;
/**
 * 账户明细
 *com.cosji.activitys.AcountDetils
 * @author ty
 *create at 2013-8-28 下午2:49:53
 */
@SuppressWarnings("deprecation")
public class AcountDetils extends BaseTab {

	private TabWidget tabwidget;
	private List<TextView> titlelist = new ArrayList<TextView>();
	private LinearLayout back;
	private ListView income_page_list,my_tixian_page_list;
	private List<HashMap<String, String>> incomelist,tixianlist;
	private IncomeAdapter adapter1,adapter2;
	private TabHost tabhost ;
	private Button button1,button2;
	private ProgressBar pro1,pro2;
	private int incometotal =1,withdrawtotal = 1;//各个页面总数
	private int incomepage = 1,withdrawpage = 1;//页面数
	private int num = 8; //设置每次加载的条数,初始化设置为8条
    private ImageView refresh_big,refresh_small,refresh;//刷新动画控件资源
	private Animation rotate_big,rotate_small;
	private View moreView1,moreView2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rebate_page);
		((TextView)findViewById(R.id.mine_secend_title)).setText("账户明细");
		tabhost = getTabHost();

		// 为TabHost添加标签
		// 新建一个newTabSpec(newTabSpec)
		// 设置其标签和图标（setIndicator）
		// 设置内容（setContent）
		tabhost.addTab(tabhost
				.newTabSpec("0")
				.setIndicator(composeLayout(R.string.acount_income))
				.setContent(R.id.tab1));
		   
		tabhost.addTab(tabhost
				.newTabSpec("1")
				.setIndicator(composeLayout(R.string.acount_tixian))
				.setContent(R.id.tab2));
		// 设置TabHost的背景颜色
		tabhost.setBackgroundColor(getResources().getColor(R.color.rebate_tabhost));
		tabhost.setCurrentTab(0);
		tabwidget= (TabWidget) findViewById(android.R.id.tabs);
		
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
		onTabChanged("0");
	}
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
					incomepage = 1;
					incomelist.clear();
					adapter1.refresh(incomelist);
					showdate(hostid);
					break;
				case 1:
					withdrawpage = 1;
					tixianlist.clear();
					adapter2.refresh(tixianlist);
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
		incomelist = new ArrayList<HashMap<String,String>>();
		tixianlist = new ArrayList<HashMap<String,String>>();
		
		LinearLayout lay1 = (LinearLayout) findViewById(R.id.tab1);
		LinearLayout lay2 = (LinearLayout) findViewById(R.id.tab2);
		LayoutInflater inflater = LayoutInflater.from(this);
		
		LinearLayout income_page = (LinearLayout) inflater.inflate(R.layout.income_page, null);
		LinearLayout tixian_page = (LinearLayout) inflater.inflate(R.layout.income_page, null);
		
		lay1.addView(income_page,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		lay2.addView(tixian_page,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		 moreView1 = getLayoutInflater().inflate(R.layout.moredata, null);
		 moreView2 = getLayoutInflater().inflate(R.layout.moredata, null);
		
		income_page_list = (ListView)income_page.findViewById(R.id.income_list);
		income_page_list.addFooterView(moreView1);
		my_tixian_page_list = (ListView)tixian_page.findViewById(R.id.income_list);
		my_tixian_page_list.addFooterView(moreView2);
	
		//设置加载更多的监听
		button1 = (Button)moreView1.findViewById(R.id.bt_load);
		button2 = (Button)moreView2.findViewById(R.id.bt_load);
		pro1 = (ProgressBar)moreView1.findViewById(R.id.Load_pro);
		pro2 = (ProgressBar)moreView2.findViewById(R.id.Load_pro);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				button1.setVisibility(View.GONE);
				pro1.setVisibility(View.VISIBLE);
				moredate(++incomepage, 0, num);
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				button2.setVisibility(View.GONE);
				pro2.setVisibility(View.VISIBLE);
				moredate(++withdrawpage,1, num);
			}
		});
	}
	
	
	public void onTabChanged(String tabId) {
		
		int tabld = Integer.valueOf(tabId);
		for(int i =0;i<tabwidget.getChildCount();i++){
			if(i==tabld){
				showdate(0);
				showdate(1);
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
	//一下3个list全是模拟数据，从网上得出数据开线程获取
	class mytask extends AsyncTask<HashMap<String, Object>, Integer, List<HashMap<String, String>>>{

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
			String flag = result.get(result.size()-1).get("flag");
			int total = Integer.valueOf( result.get(result.size()-2).get("total"));
			
			result.remove(result.size()-1);
			result.remove(result.size()-1);
			if(flag.equals("incomefirst")){
				if(total%num==0){
					incometotal = total/num;
				}else{
					incometotal = total/num+1;
				}
				anymore(0);
				if(incomelist.size()==0){
					incomelist=result;
				}
				adapter1 =  new IncomeAdapter(getApplicationContext(), incomelist,"income");
				income_page_list.setAdapter(adapter1);
			}else if(flag.equals("withdrawfirst")){
				if(total%num==0){
					withdrawtotal = total/num;
				}else{
					withdrawtotal = total/num+1;
				}
				anymore(1);
				if(tixianlist.size()==0){
					tixianlist=result;
				}
				adapter2 = new IncomeAdapter(getApplicationContext(), tixianlist,"withdraw");
				my_tixian_page_list.setAdapter(adapter2);
			}else if(flag.equals("income")){
				incomelist.addAll(result);
				adapter1.refresh(incomelist);
				if(incomepage<incometotal){
				button1.setVisibility(View.VISIBLE);
				pro1.setVisibility(View.GONE);
				}else{
					ToastUtil.showShortToast(AcountDetils.this, "数据全部加载完成");
					pro1.setVisibility(View.GONE);
				}
			}else if(flag.equals("withdraw")){
				tixianlist.addAll(result);
				adapter2.refresh(tixianlist);
				if(withdrawpage<withdrawtotal){
				button2.setVisibility(View.VISIBLE);
				pro2.setVisibility(View.GONE);
				}else{
					ToastUtil.showShortToast(AcountDetils.this, "数据全部加载完成");
					pro2.setVisibility(View.GONE);
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
	@Override
	protected void onResume() {
		MobclickAgent.onResume(AcountDetils.this);
		super.onResume();
	}
	@Override
	protected void onPause() {
		MobclickAgent.onPause(AcountDetils.this);
		super.onPause();
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
		mytask task = new mytask();
		task.execute(params);
	}
	//选择性显示
	private void showdate(int tabid){
		String url1 = Url.account()+"income/";
		int page1 =1;
		String flag1 = "incomefirst";
		String url2 = Url.account()+"withdraw/";
		String flag2 = "withdrawfirst";
		switch(tabid){
			case 0:
				 LoadData(url1, page1, num, flag1);
				 break;
			case 1:
				LoadData(url2, page1, num, flag2);
				break;
			case 2:
				//LoadData(url3, page1, num, flag3);
				break;
		}
	}
	//加载更多数据
	private void moredate(int page,int tabid,int num){
		String url1 = Url.account()+"income/";
		String flag1 = "income";
		String url2 = Url.account()+"withdraw/";
		String flag2 = "withdraw";
		switch(tabid){
		case 0:
			LoadData(url1, page, num, flag1);
			break;
		case 1:
			LoadData(url2, page, num, flag2);
			break;
		case 2:
			//LoadData(url3, page, num, flag3);
			break;
		}
	}
	/*
	 * 判断是否该显示
	 * 加载更多的按钮
	 */
	private void anymore(int tabid){
		switch (tabid) {
		case 0:
			if(incomepage>=incometotal){
				button1.setVisibility(View.GONE);
				pro1.setVisibility(View.GONE);
			}else{
				button1.setVisibility(View.VISIBLE);
			}
			break;
			case 1:
				if(withdrawpage>=withdrawtotal){
					button2.setVisibility(View.GONE);
					pro2.setVisibility(View.GONE);
					}else{
					button2.setVisibility(View.VISIBLE);
					}
				break;
		}
	}
}
