package com.cosji.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cosji.activitys.R;

public class AdviceAdapter extends BaseAdapter {
	Context context;
	ArrayList<HashMap<String, Object>> data;
	LayoutInflater inflater;

	public AdviceAdapter(Context context,
			ArrayList<HashMap<String, Object>> data) {
		// TODO Auto-generated constructor stub
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.data = data;
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
			convertView = inflater.inflate(
					R.layout.complaintcontents_list_item, null);
			holder.advice_item_content = (TextView) convertView
					.findViewById(R.id.advice_item_content);
			holder.advice_item_date = (TextView) convertView
					.findViewById(R.id.advice_item_date);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.advice_item_content.setText(data.get(position).get("contents")
				.toString());
		holder.advice_item_date.setText(data.get(position).get("time")
				.toString());
		return convertView;
	}

	class ViewHolder {
		TextView advice_item_date;
		TextView advice_item_content;

	}
}