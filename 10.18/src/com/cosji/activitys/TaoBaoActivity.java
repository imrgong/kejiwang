package com.cosji.activitys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.cosji.application.Base;
import com.cosji.utils.AsynDownloadManager;
import com.cosji.utils.Exit;
import com.cosji.utils.HttpConnectionHepler;
import com.cosji.utils.IsIntent;
import com.cosji.utils.SettingUtils;
import com.cosji.utils.ToastUtil;
import com.cosji.utils.Url;
import com.umeng.analytics.MobclickAgent;

public class TaoBaoActivity extends BaseActivity {
	private LayoutInflater inflater;
	private static final int REFRESH_LIST = 0x11111;
	int[] good_type_logos = { R.id.goods_types_Image1, R.id.goods_types_Image2,
							R.id.goods_types_Image3, R.id.goods_types_Image4,
							R.id.goods_types_Image5, R.id.goods_types_Image6,
							R.id.goods_types_Image7, R.id.goods_types_Image8 };

	int[] good_type_name = { R.id.goods_types_name1, R.id.goods_types_name2,
							 R.id.goods_types_name3, R.id.goods_types_name4,
							 R.id.goods_types_name5, R.id.goods_types_name6,
							 R.id.goods_types_name7, R.id.goods_types_name8 };
	
	int[] good_typesitem = { R.id.item_type1, R.id.item_type2, R.id.item_type3,
			                 R.id.item_type4, R.id.item_type5, R.id.item_type6,
			                 R.id.item_type7, R.id.item_type8 };
	final int count = 8;
	int n, page, more_num = 8;
	List<ViewHolder> hod = new ArrayList<ViewHolder>();
	Handler handler = new Handler();
	public static boolean isloading = false;
	public Dialog mProgressDialog;
	AsynDownloadManager manager;
	private EditText act;

	List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	HttpConnectionHepler th;

	SettingUtils stu = new SettingUtils();

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taobao_page);
		manager = Base.manager;
		manager.setHandler(handler);
		init();

		inflater = LayoutInflater.from(TaoBaoActivity.this);
		initGTsView();
		if (Base.isNetworkConnected(TaoBaoActivity.this)) {
			new Thread(r).start();
		} else {
			ToastUtil.showShortToast(TaoBaoActivity.this, "网络出错,请检测网络...");
		}
		act = (EditText) findViewById(R.id.taobao_search);
		final TextView cliear_button = (TextView) findViewById(R.id.clear_button_text);
		
        cliear_button.setOnClickListener(hot_mall_listener);		
		act.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (KeyEvent.KEYCODE_ENTER == keyCode
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					IsIntent.SearchGoods(TaoBaoActivity.this, act.getText().toString()
							);
//					final InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
//					imm.hideSoftInputFromWindow(searchtext.getWindowToken(), 0);
				}
				return false;
			}
		});
		act.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(act.getText().toString().length()>0)
				cliear_button.setVisibility(View.VISIBLE);
				else
					cliear_button.setVisibility(View.GONE);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		initHot_MallEnter();

	}

	private void init() {
		int count = 0;
		// TODO Auto-generated method stub
		/*
		 * 标题优化
		 */

		ImageView titlename = (ImageView) findViewById(R.id.title_name);
		titlename.setImageResource(R.drawable.taotitle);
		ImageView menu = (ImageView) findViewById(R.id.menu_view);
		menu.setImageResource(R.drawable.taobao_type);
		menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String url = "http://s.click.taobao.com/t?e=zGU34CA7K%2BPkqB05%2Bm7rfGGjlY60oHcc7bkKOQYnJ4SvJ3eG4UMiW6r9RUBj7CyUTKvBk9yE2Re6ZPJbLLhSxt5F2lqxt%2F134AasJk2jYw1zpo97OIb3triKn5%2FnblogfMBGTmWCEZ4BEOZFSLhzuUl8kGVjpdpbomm3dUlR9tt5xuzOW0QuPg58uzVcWe1wiwMRDp1u%2FXCX98l4JQM4urYgDuMzjUjrKO9z&unid=";
				IsIntent.prompt1(TaoBaoActivity.this, "更多商品分类", url);

			}
		});

		/*
		 * 数据获取
		 */
		// DataInit.initGoodsTypesData(count, data);

	}

	private void initGTsView() {

		LinearLayout goodstype = (LinearLayout) findViewById(R.id.tao_goodstype);

		for (int i = 0; i < 7; i++) {

			goodstype.addView(getView(i, null, null), i);
		}

	}

	Runnable r = new Runnable() {
		@Override
		public void run() {
			try {

				th = new HttpConnectionHepler();
				isloading = true;
				List<NameValuePair> a = new ArrayList<NameValuePair>();
				setData(th.sendPostTaoGdsTpReArray(TaoBaoActivity.this,
						Url.taogdsapi, a));

				sendMessage(REFRESH_LIST);

			} catch (Exception e) {

			}
		}
	};

	protected void handleOtherMessage(int flag) {
		// TODO Auto-generated method stub
		switch (flag) {
		case REFRESH_LIST:
			manager = Base.manager;
			manager.setHandler(handler);
			for (int i = 0; i < 7; i++) {
				refreshView(hod.get(i), i);

			}

			// mProgressDialog.dismiss();
			break;
		default:
			break;
		}
	}

	public void startdialog() {

		mProgressDialog = new Dialog(TaoBaoActivity.this,
				R.style.theme_dialog_alert);
		mProgressDialog.setContentView(R.layout.window_layout);
		// mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}

	public List<HashMap<String, String>> getData() {
		return data;
	}

	public void setData(List<HashMap<String, String>> data1) {
		data = data1;
	}

	public View getView(final int postion, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		if (convertView == null) {

			convertView = inflater.inflate(R.layout.goods_types_item, null);
			holder = new ViewHolder();
			hod.add(holder);
			holder.type_name = (TextView) convertView
					.findViewById(R.id.good_types_name);

			for (int j = 0; j < this.count; j++) {
				holder.goodt_types_logo[j] = (ImageView) convertView
						.findViewById(good_type_logos[j]);
				holder.goodt_types_name[j] = (TextView) convertView
						.findViewById(good_type_name[j]);
				holder.tao_good_types[j] = (RelativeLayout) convertView
						.findViewById(good_typesitem[j]);

			}
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		return convertView;

	}

	public void refreshView(ViewHolder holder, int position) {

		if (getData().size() > 0) {
			String type_name=null;
			try {
				type_name=th.getTaoGdTitleHash()
						.get("tyn" + position).toString().trim();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
if (type_name==null) {
	holder.type_name.setText("");	
}
else{
	holder.type_name.setText(type_name);	
}
	
			for (int i = 0; i < this.count; i++) {

				final String name = getData().get(position * count + i)
						.get("name").toString().trim();
				// holder.home_mall_item_name[i].setText(text);
				holder.goodt_types_name[i].setText(name);
				String url="http://"+getData().get(position * count + i).get("imgUrl");
				holder.goodt_types_logo[i].setTag(url);
				manager.loadThembBitmap(TaoBaoActivity.this,
						url, holder.goodt_types_logo[i],
						true);
				holder.tao_good_types[i]
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								toGoodsListActivity(name);

							}
						});

			}
		} else {
			ToastUtil.showShortToast(TaoBaoActivity.this, "服务器出错，数据获取失败...");
		}

	}

	class ViewHolder {

		ImageView[] goodt_types_logo = new ImageView[8];
		TextView[] goodt_types_name = new TextView[8];
		RelativeLayout[] tao_good_types = new RelativeLayout[8];
		TextView type_name;

	}

	/**
	 * 初始化AutoCompleteTextView，最多显示5项提示，使 AutoCompleteTextView在一开始获得焦点时自动提示
	 * 
	 * @param field
	 *            保存在sharedPreference中的字段名(key)
	 * @param auto
	 *            要操作的AutoCompleteTextView
	 */

	/*
	 * 首页中 淘宝、聚划算、天猫点击跳转到返利模式中。
	 */

	public void initHot_MallEnter() {
		RelativeLayout taobao = (RelativeLayout) findViewById(R.id.tao_goods_item1);
		RelativeLayout tianmao = (RelativeLayout) findViewById(R.id.tao_goods_item2);
		RelativeLayout jiuhuasuan = (RelativeLayout) findViewById(R.id.tao_goods_item3);

		taobao.setOnClickListener(hot_mall_listener);
		tianmao.setOnClickListener(hot_mall_listener);
		jiuhuasuan.setOnClickListener(hot_mall_listener);

	}

	OnClickListener hot_mall_listener = new OnClickListener() {
		public void onClick(View v) {

			switch (v.getId()) {
			/*
			 * 淘宝登陆
			 */
			case R.id.tao_goods_item1:
				// http://h5.m.taobao.com/my/index.htm 我的淘宝
					String url1 = "http://r.m.taobao.com/s?p=mm_45144085_4234703_14432173&q=";
					IsIntent.prompt1(TaoBaoActivity.this, "淘宝", url1);
				break;
			/*
			 * 天猫登陆
			 */
			case R.id.tao_goods_item2:
				String url2 ="http://s.click.taobao.com/t?e=zGU34CA7K%2BPkqB05%2Bm7rfGGjlY60oHcc7bkKOQYnJ4SpyQI%2FBmNLpN1iCFRqRKWmo%2F%2FT9s0ojNLlfHXjkA8q4kglcvpj5xDOjSnjKzxXGQn8RMA%3D&unid="; 
				IsIntent.prompt1(TaoBaoActivity.this, "天猫", url2);
				break;
			/*
			 * 聚划算
			 */
			case R.id.tao_goods_item3:
				String url3 = "http://s.click.taobao.com/t?e=zGU34CA7K%2BPkqB05%2Bm7rfGKas1PIKp0U37pZuBotzOg7OjeU9mIWS5%2B5UROBz6sq4fiHePnGhf6UrWuOtfD2G6QGcAHIXpjhEtESpuqsRo0a&pid=mm_26039255_0_0&unid=";
				IsIntent.prompt1(TaoBaoActivity.this, "聚划算", url3);
				break;
			case R.id.clear_button_text:
				act.setText("");
			default:
				break;
			}

		}
	};



	private void toGoodsListActivity(String str) {
		Intent intent = new Intent();
		intent.setClass(TaoBaoActivity.this, GoodsActivity.class);
		intent.putExtra("goods_types", str);
		startActivity(intent);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// DataInit.fb.onPause();
		MobclickAgent.onPause(TaoBaoActivity.this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!isloading
				&& Base.isNetworkConnected(TaoBaoActivity.this)) {
			new Thread(r).start(); 
		}
		// DataInit.fb.onResume();
		MobclickAgent.onResume(TaoBaoActivity.this);
	}
	
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	 if(keyCode == KeyEvent.KEYCODE_BACK){ 
		 
Exit et=new Exit();
et.exit(TaoBaoActivity.this);
	 }
return false;
}
}
