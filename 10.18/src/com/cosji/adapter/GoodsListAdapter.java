package com.cosji.adapter;

import java.util.ArrayList;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cosji.activitys.R;
import com.cosji.application.Base;
import com.cosji.bean.TbkItem;
import com.cosji.tbkapihelper.Utils;
import com.cosji.utils.ImageLoader;
import com.cosji.utils.IsIntent;
public class GoodsListAdapter extends BaseAdapter {
	Context context;
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what==500){
				TbkItem items = (TbkItem)msg.obj;
				IsIntent.jiuyuanGoodsDetials(context, items);
			}
			super.handleMessage(msg);
		}
		
	};
	public static boolean isloading = false;
	private ImageLoader imageLoader;
	public List<ViewHolder>hod=new ArrayList<ViewHolder>();
	List<TbkItem> data=new ArrayList<TbkItem>();
	LayoutInflater inflater;

	private boolean mBusy = false;

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}
	
	public GoodsListAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
    	imageLoader = Base.imageLoader;
	}

public List<TbkItem> getData(){
		return data;
	}
	public void setData(List<TbkItem> data1) {
		data.addAll(data1);
	}
	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			hod.add(holder);
			convertView = inflater.inflate(R.layout.tao_goods_item,
					null);
			holder.good_item=(RelativeLayout)convertView.findViewById(R.id.goods_item);
			
			holder.goods_icon = (ImageView) convertView
					.findViewById(R.id.product_icon);
			holder.goods_name = (TextView) convertView
					.findViewById(R.id.product_name);
			holder.goods_money_save = (TextView) convertView
					.findViewById(R.id.product_money_save);
			
			holder.goods_price = (TextView) convertView
					.findViewById(R.id.product_price);
		
			holder.goods_selt = (TextView) convertView
					.findViewById(R.id.product_selt);

			convertView.setTag(holder);
           
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		 refreshView(holder,position);
		 isloading=true;
       holder.good_item.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// IsIntent.prompt1(context, "淘宝网",Util.getconvert(data.get(position).getNum_iid(),Base.getUserId()));
			new Thread(new Runnable() {
				@Override
				public void run() {
					TbkItem items = data.get(position);
					String click_url = Utils.getconvert(items.getNum_iid(),Base.getUserId());
					if(click_url!=null&&click_url.length()>10){
						items.setClick_url(click_url);
					}
					Message message = new Message();
					message.what=500;
					message.obj=items;
					handler.sendMessage(message);
				}
			}).start();
		}
	});
		
		return convertView;
	}
	private void refreshView(ViewHolder holder,int position) {
		if(data.get(position).getPrice()!=null&&!data.get(position).getPrice().equals("0")){
			 holder.goods_money_save.setText("有返利");
		}else{
			 holder.goods_money_save.setText("   ");
		}
        holder.goods_name.setText(Html.fromHtml(data.get(position).getTitle()));
	     
	        holder.goods_price.setText(data.get(position).getPrice());
	      
	        holder.goods_selt.setText("最近售出"+data.get(position).getVolume()+"件");
	   
	      String  url=data.get(position).getPic_url();
	      holder.goods_icon.setTag (url) ;
	      if(!mBusy){
	    	  imageLoader.DisplayImage(url, holder.goods_icon, false);
	      }else{
	    	  imageLoader.DisplayImage(url, holder.goods_icon, true);
	      }
	}
	public class ViewHolder {
		TextView goods_name;
		TextView goods_price;
		TextView goods_money_save;
		TextView goods_selt;
	  public	ImageView goods_icon;
RelativeLayout good_item;
	}
}
