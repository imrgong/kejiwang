package com.cosji.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cosji.activitys.R;

public class ListViewAdapter extends BaseAdapter {
     private Context context;
     private LayoutInflater inflater;
     private List<HashMap<String, String>> data;
     private int curentposition =-1;
     
     public ListViewAdapter(Context context,List<HashMap<String, String>> data){
    	 this.context = context;
    	 this.data = data;
    	 inflater = LayoutInflater.from(context);
     }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}
	public void refersh(int position) {
		curentposition = position;
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		String value = null;
		if(convertView ==null){
			convertView = inflater.inflate(R.layout.item_comm_note, null);
			holder = new ViewHolder();
			holder.titile = (TextView)convertView.findViewById(R.id.item_name);
			holder.linearlayout = (LinearLayout)convertView.findViewById(R.id.layout_other);
			holder.content = (TextView)convertView.findViewById(R.id.content_for_question);
			holder.arrow = (ImageView)convertView.findViewById(R.id.help_item_arrow);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.titile.setText((position+1)+"."+data.get(position).get("title"));
		value = data.get(position).get("content");
		value = value.replace("\r\n\r\n\r\n", "");
		value = value.replace("\t", "");
		holder.content.setText(value);
		
		if(curentposition == position&&data.get(position).get("flag").equals("true")){
				holder.linearlayout.setVisibility(View.VISIBLE);
				holder.arrow.setBackgroundResource(R.drawable.help_item_arro_down);
		}else{
			holder.linearlayout.setVisibility(View.GONE);
			holder.arrow.setBackgroundResource(R.drawable.help_item_arro);
			data.get(position).put("flag", "false");
		}
		return convertView;
	}
	
	class ViewHolder {
		public TextView titile;
		public LinearLayout linearlayout;
		public TextView content;
		public ImageView arrow;
	}
}
