package com.cosji.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cosji.activitys.R;
import com.cosji.utils.Util;

public class ShangchengRebateAdapter extends BaseAdapter {
	Context context;
	List<HashMap<String, String>> data;
	LayoutInflater inflater;
	
	public ShangchengRebateAdapter(Context context,
			List<HashMap<String, String>> data) {
		// TODO Auto-generated constructor stub
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.data = data;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}
	public void refresh(List<HashMap<String, String>> shangchenglist) {
	data = shangchenglist;
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
			convertView = inflater.inflate(R.layout.shangcheng_rebate_item,
					null);
			holder.deal_time = (TextView) convertView
					.findViewById(R.id.deal_time);
			holder.introduce = (TextView) convertView
					.findViewById(R.id.shangcheng_goods_introduce);
			holder.price = (TextView) convertView
					.findViewById(R.id.shangcheng_goods_price);
			holder.reabate = (TextView) convertView
					.findViewById(R.id.rebate_money);
			holder.order = (TextView) convertView
					.findViewById(R.id.shangcheng_goods_ordernum);
			holder.goods_number = (TextView) convertView
					.findViewById(R.id.shangcheng_goods_number);
			
			convertView.setTag(holder);
           
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// holder.tv.setText();
		holder.deal_time.setText(":"+Util.getDate(data.get(position).get("chargeTime")));
		holder.introduce.setText("下单商城:"+data.get(position).get("mallName"));
		holder.price.setText("￥"+data.get(position).get("price"));
		holder.reabate.setText(data.get(position).get("profit")+"元");
		holder.order.setText("订单号:"+data.get(position).get("orderNo"));
		holder.goods_number.setText("成交数量:"+data.get(position).get("number"));
       
		return convertView;
	}

	class ViewHolder {
		TextView deal_time;
		TextView introduce;
		TextView price;
		TextView reabate;
		TextView order;
		TextView goods_number;
		
		

	}
}
