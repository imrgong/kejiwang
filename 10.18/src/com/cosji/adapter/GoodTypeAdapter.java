package com.cosji.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cosji.activitys.GoodsActivity;
import com.cosji.activitys.R;
public class GoodTypeAdapter extends BaseAdapter{
	LayoutInflater inflater=null;
	Context context;
	int[] good_type_logos = { R.id.goods_types_Image1,
			R.id.goods_types_Image2, R.id.goods_types_Image3,
			R.id.goods_types_Image4, R.id.goods_types_Image5,
			R.id.goods_types_Image6, R.id.goods_types_Image7,
			R.id.goods_types_Image8 };

	 int[] good_type_name = { R.id.goods_types_name1,
			R.id.goods_types_name2, R.id.goods_types_name3,
			R.id.goods_types_name4, R.id.goods_types_name5,
			R.id.goods_types_name6, R.id.goods_types_name7,
			R.id.goods_types_name8 };
	 int[] good_typesitem = { R.id.item_type1, R.id.item_type2,
			R.id.item_type3, R.id.item_type4, R.id.item_type5,
			R.id.item_type6, R.id.item_type7, R.id.item_type8 };
	final int count=8;
	int n;
	public GoodTypeAdapter(Context context,int n) {
		inflater = LayoutInflater.from(context);
		this.context=context;
		this.n=n;
		// TODO Auto-generated constructor stub
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return n;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int positon, View convertView,ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder  holder=null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.goods_types_item, null);
			 holder = new ViewHolder();
			for (int j = 0; j < this.count; j++) {	
					holder.goodt_types_logo[j] = (ImageView) convertView
							.findViewById(good_type_logos[j]);
					holder.goodt_types_name[j] = (TextView) convertView
							.findViewById(good_type_name[j]);
					holder.tao_good_types[j] = (RelativeLayout) convertView
							.findViewById(good_typesitem[j]);
					
				}	
			convertView.setTag(holder);
		
	}else {
		holder = (ViewHolder) convertView.getTag();
	}
		for (int i = 0; i <this.count; i++) {
			//holder.home_mall_item_name[i].setText(text);
			holder.tao_good_types[i]
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
						
							Intent intent = new Intent();
                            intent.putExtra("goods_types","衣服");
							intent.setClass(context,
									GoodsActivity.class);
			
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							context.startActivity(intent);
						}
					});
			 //holder.goodt_types_name.setText("牛仔"+position);
		//	DataInit.LoadingImage(context,holder.goodt_types_logo[i], MallMoreAdapter.URLS[i%7]);
		}
		
		return convertView;
		
	}

		

	class ViewHolder {
	
		ImageView [] goodt_types_logo=new ImageView[8];
		TextView []goodt_types_name=new TextView[8];
		RelativeLayout []tao_good_types=new RelativeLayout[8];

	}

}
