package com.cosji.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cosji.activitys.R;
import com.cosji.application.Base;
import com.cosji.utils.AsynDownloadManager;
import com.cosji.utils.Util;


public class PaipaiRebateAdapter extends BaseAdapter {
	Context context;
	List<HashMap<String, String>> data;
	LayoutInflater inflater;
	Handler handler = new Handler();
	AsynDownloadManager manager;
	public PaipaiRebateAdapter(Context context,
			List<HashMap<String, String>> data) {
		// TODO Auto-generated constructor stub
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.data = data;
		manager = Base.manager;
		manager.setHandler(handler);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}
	public void refresh(List<HashMap<String, String>> paipailist) {
		data = paipailist;
		notifyDataSetChanged();
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
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.paipai_rebate_item,
					null);
//			holder.pic = (ImageView) convertView
//					.findViewById(R.id.paipai_rebate_goods_pic);
			holder.deal_time = (TextView) convertView
					.findViewById(R.id.deal_time);
			holder.introduce = (TextView) convertView
					.findViewById(R.id.paipai_goods_introduce);
			holder.price = (TextView) convertView
					.findViewById(R.id.paipai_goods_price);
			holder.reabate = (TextView) convertView
					.findViewById(R.id.rebate_money);
//			holder.reward = (TextView) convertView
//					.findViewById(R.id.reward_jifen);
			holder.order = (TextView) convertView
					.findViewById(R.id.paipai_goods_ordernumber);
			holder.number = (TextView) convertView
					.findViewById(R.id.paipai_goods_number);
			
			convertView.setTag(holder);
           
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// holder.tv.setText();
		holder.deal_time.setText(":"+Util.getDate(data.get(position).get("chargeTime")));
		holder.introduce.setText(data.get(position).get("name"));
		holder.price.setText("￥"+data.get(position).get("amount"));
		holder.reabate.setText(data.get(position).get("profit")+"元");
		//holder.reward.setText("分享可获得");
		holder.order.setText("订单号:"+data.get(position).get("orderNo"));
		holder.number.setText("数量:"+data.get(position).get("number"));
//   	    manager.loadThembBitmap(context,data.get(position).get("imgUrl").toString()
//			.trim(), holder.pic,true);
		return convertView;
	}

	class ViewHolder {
		TextView order;
		TextView deal_time;
		TextView introduce;
		TextView price;
		TextView reabate;
		TextView number;
		//ImageView pic;
		

	}
}
