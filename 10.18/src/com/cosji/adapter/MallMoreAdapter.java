package com.cosji.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cosji.activitys.MallMoreActivity;
import com.cosji.activitys.R;
import com.cosji.application.Base;
import com.cosji.utils.AsynDownloadManager;
import com.cosji.utils.IsIntent;

public class MallMoreAdapter extends BaseAdapter {
	List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

	Context context;
	boolean isrefresh;
	private LayoutInflater inflater;
	public List<ViewHolder> hod = new ArrayList<MallMoreAdapter.ViewHolder>();
	AsynDownloadManager manager;
	Handler handler = new Handler();

	private boolean mBusy = false;

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	public List<HashMap<String, String>> getData() {
		return data;
	}

	public void setData(List<HashMap<String, String>> data1) {
		int n = data.size() + data1.size();
		int j = 0;
		for (int i = data.size(); i < n; i++) {
			data.add(data1.get(j));
			j++;
		}

	}

	public MallMoreAdapter(Context context, boolean isrefresh) {
		// TODO Auto-generated constructor stub
		this.isrefresh = isrefresh;
		manager = Base.manager;
		manager.setHandler(handler);
		this.context = context;
		inflater = LayoutInflater.from(context);

	}

	public int getCount() {
		// TODO Auto-generated method stub

		if (isrefresh && data.size() > 0) {
			return data.size() + 1;
		} else {
			return data.size();
		}

	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.home_malls, null);
			holder = new ViewHolder();
			hod.add(holder);

			holder.home_mall_item_logo = (ImageView) convertView
					.findViewById(R.id.home_mall_item_Image1);
			holder.home_mall_item_profit = (TextView) convertView
					.findViewById(R.id.home_mall_item_name1);
			holder.home_mall_item_type = (RelativeLayout) convertView
					.findViewById(R.id.home_mall_item1);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		refreshView(holder, position);

		return convertView;

	}

	public void refreshView(ViewHolder holder, final int position) {
		if (position < data.size()) {

			holder.home_mall_item_type
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							IsIntent.prompt(context,
									data.get(position).get("name"),
									data.get(position).get("yiqifaurl"));
							System.out.println("一起发:"
									+ data.get(position).get("yiqifaurl"));

						}
					});
			String profit = data.get(position).get("profit").toString().trim();
			if (profit != null) {
				holder.home_mall_item_profit.setText("返利" + profit);
			} else {
				holder.home_mall_item_profit.setText("返利" + profit);
			}

			String url = data.get(position).get("logo");

			holder.home_mall_item_logo.setTag(url);
			if (!mBusy) {
				manager.loadThembBitmap(context, url,
						holder.home_mall_item_logo, true);
			} else {
				Bitmap bitmap = manager.getBitmapFromCache(url);
				if (bitmap != null) {
					holder.home_mall_item_logo.setImageBitmap(bitmap);
				} else {
					holder.home_mall_item_logo
							.setImageResource(R.drawable.default_goods);
				}
			}
		} else {

			holder.home_mall_item_profit.setVisibility(View.GONE);
			holder.home_mall_item_logo
					.setBackgroundResource(R.drawable.home_more);

			holder.home_mall_item_type
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent in = new Intent(context,
									MallMoreActivity.class);
							context.startActivity(in);
						}
					});
		}
	}

	public class ViewHolder {

		ImageView home_mall_item_logo;
		TextView home_mall_item_profit;
		RelativeLayout home_mall_item_type;

	}
}
