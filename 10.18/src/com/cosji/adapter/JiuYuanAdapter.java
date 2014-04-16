package com.cosji.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cosji.activitys.R;
import com.cosji.application.Base;
import com.cosji.bean.TbkItem;
import com.cosji.tbkapihelper.Utils;
import com.cosji.utils.ImageLoader;
import com.cosji.utils.IsIntent;
import com.cosji.utils.Url;

/**
 * 一、对于缩略图： 1、在Activity中实例化AsynDownloadManager: Handler handler=new Handler();
 * 
 * 
 * 2、对于ListView或GridView中的缩略图，一般可在自定义Adapter的的构造中： AsynDownloadManager
 * manager=AsynDownloadManager.getInterface(); manager.setHandler(handler);
 * 
 * getView()方法中作如下调用： manager.loadThembBitmap(urlList.get(position), imageView);
 * 
 * 3、OK
 * 
 * 二、对于大图：(压缩)
 * 
 * 前两步同上
 * 
 * 3、 直接调用 AsynDownloadManager.getInterface().loadBitmap(url, ImageView, true);
 * 当然最后那个参数也可以用false,即不压缩的模式
 * 
 * 
 * @author Administrator
 * 
 */
public class JiuYuanAdapter extends BaseAdapter {
	private boolean mBusy = false;
	private ImageLoader imageLoader;
	
	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}
	Context context;

	public static boolean isloading = false;

	private LayoutInflater inflater;
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what==500){
				TbkItem items =(TbkItem) msg.obj;
				IsIntent.jiuyuanGoodsDetials(context, items);
			}
			super.handleMessage(msg);
		}
		
	};
	public List<ViewHolder> hod = new ArrayList<ViewHolder>();
	List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

	public JiuYuanAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		 imageLoader = Base.imageLoader;
	}

	public List<HashMap<String, String>> getData() {
		return data;
	}

	public void setData(List<HashMap<String, String>> data1) {

		for (int i = 0; i < data1.size(); i++) {
			data.add(data1.get(i));
		}
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
	if (convertView == null) {
			holder = new ViewHolder();
			hod.add(holder);
			convertView = inflater.inflate(R.layout.jiuyuan_item, null);
			holder.picture = (ImageView) convertView
					.findViewById(R.id.jiu_goods_picture);
			holder.name = (TextView) convertView
					.findViewById(R.id.jiu_goods_name);

			holder.price = (TextView) convertView
					.findViewById(R.id.jiu_item_price);
			holder.bt = (LinearLayout) convertView
					.findViewById(R.id.jiu_item_bt);
			
holder.ishas=(Button)convertView.findViewById(R.id.ispost);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		refreshView(holder, position);
		isloading = true;
		return convertView;
	}

	public void refreshView(ViewHolder holder, final int position) {
		holder.name.setText(Html.fromHtml(data.get(position).get("name")
				.toString().trim()));
		holder.price.setText("￥"
				+ data.get(position).get("promotion").toString().trim());
	//	if (data.get(position).get("freight").toString().trim().equals("1")) {
  	          holder.ishas.setVisibility(View.VISIBLE);
	//	}
	//	else{
	//		  holder.ishas.setVisibility(View.GONE);
	//	}
		holder.bt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						TbkItem tbkItem = new TbkItem();
						tbkItem.setPrice(data.get(position).get("promotion").toString().trim());
						tbkItem.setNum_iid(data.get(position).get("iid").toString().trim());
						
						if(!data.get(position).get("imgUrl").startsWith("http:"))
						tbkItem.setPic_url(Url.ZHEMAI+data.get(position).get("imgUrl").toString().trim());
						else
						tbkItem.setPic_url(data.get(position).get("imgUrl").toString().trim());
						
						tbkItem.setTitle((data.get(position).get("name").toString().trim()));
						tbkItem.setVolume(data.get(position).get("rate").toString().trim());
						
						String click_url = Utils.getconvert(tbkItem.getNum_iid(),Base.getUserId());
						if(click_url!=null&&click_url.length()>10){
							tbkItem.setClick_url(click_url);
						}
						Message msg = new Message();
						msg.what = 500;
						msg.obj = tbkItem;
						handler.sendMessage(msg);
					}
				}).start();
			}
		});
	     String  url=data.get(position).get("imgUrl");
	     if(!url.startsWith("http:"))
	    	 url=Url.ZHEMAI+url;
	     holder.picture.setTag (url) ;
	     if(!mBusy){
	    	 imageLoader.DisplayImage(url, holder.picture, false);
	     }else{
	    	// imageLoader.DisplayImage(url, holder.picture, true);
	     }
	}
	public class ViewHolder {
		ImageView picture;
		TextView name;
		TextView price;
		TextView ishas;// 包邮
		LinearLayout bt;

	}
}
