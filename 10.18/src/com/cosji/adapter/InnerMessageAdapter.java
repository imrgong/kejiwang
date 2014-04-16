package com.cosji.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cosji.activitys.R;

public class InnerMessageAdapter extends BaseAdapter {
	Context context;
	List<HashMap<String, String>> data;
	LayoutInflater inflater;
	
	public InnerMessageAdapter(Context context,
			List<HashMap<String, String>> data) {
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
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.message_item,
					null);
			holder.time = (TextView) convertView
					.findViewById(R.id.emailtime);
			holder.entity = (TextView) convertView
					.findViewById(R.id.myemailcontents);
			holder.ck = (CheckBox) convertView
					.findViewById(R.id.isdeleteinfo);

			convertView.setTag(holder);
           
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// holder.tv.setText();
        holder.time.setText(data.get(position).get("time"));
        holder.entity.setText(data.get(position).get("content"));
        holder.ck.setChecked(data.get(position).get("flag").equals("true"));
		return convertView;
	}

	final public class ViewHolder {
		TextView time;
		TextView entity;
		public CheckBox ck;
		
	}
}
