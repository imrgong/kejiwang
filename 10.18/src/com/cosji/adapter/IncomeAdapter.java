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

public class IncomeAdapter extends BaseAdapter {
	Context context;
	List<HashMap<String, String>> data;
	LayoutInflater inflater;
	String flag;
	
	public IncomeAdapter(Context context,
			List<HashMap<String, String>> data,String flag){
		this.flag = flag;
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.data = data;
	
	}

	//数据发生改变时，更新UI
		public void refresh(List<HashMap<String, String>> list) {
			data = list;
			notifyDataSetChanged();
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
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.income_item,
					null);
		
			holder.item_time = (TextView) convertView
					.findViewById(R.id.income_item_time);
			holder.item_entity = (TextView) convertView
					.findViewById(R.id.income_item_entity);
			holder.item_detils = (TextView) convertView
					.findViewById(R.id.income_item_detlis);
		
			convertView.setTag(holder);
           
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(flag.equals("income")){
			// holder.tv.setText();
			holder.item_time.setText(data.get(position).get("time"));
			holder.item_entity.setText(data.get(position).get("content"));
			holder.item_detils.setText(data.get(position).get("event"));
			
			return convertView;
		}else if (flag.equals("withdraw")){
			holder.item_time.setText(data.get(position).get("time"));
			holder.item_entity.setText("提现:"+ data.get(position).get("amount"));
			holder.item_detils.setText(data.get(position).get("status"));
			
			return convertView;
		}else{
			holder.item_time.setText(data.get(position).get("time"));
			holder.item_entity.setText(data.get(position).get("content"));
			holder.item_detils.setText(data.get(position).get("event"));
			
			return convertView;
		}
	}

	class ViewHolder {
		TextView item_time;
		TextView item_entity;
		TextView item_detils;
		
	}
}
