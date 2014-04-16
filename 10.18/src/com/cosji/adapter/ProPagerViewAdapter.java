package com.cosji.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.cosji.activitys.WebviewActivity;
import com.cosji.utils.IsIntent;

public class ProPagerViewAdapter extends PagerAdapter {
	private List<ImageView> imageViews;
	private List<View> gimageViews;
	private Context context;
	private List<HashMap<String, String>> slidemap;

	public ProPagerViewAdapter(List<ImageView> imageViews, Context context) {
		// TODO Auto-generated constructor stub
		this.imageViews = imageViews;
		this.context = context;
	}

	public ProPagerViewAdapter(List<View> imageViews, Context context, int i) {
		// TODO Auto-generated constructor stub
		this.gimageViews = imageViews;
		this.context = context;
	}
	
	public ProPagerViewAdapter(List<ImageView> imageViews, Context context,List<HashMap<String, String>> slidemap) {
		// TODO Auto-generated constructor stub
		this.imageViews = imageViews;
		this.context = context;
		this.slidemap = slidemap;
	}

	@Override
	public int getCount() {
		if (gimageViews != null) {
			return gimageViews.size();
		}
		return imageViews.size();
	}

	@Override
	public Object instantiateItem(View arg0, final int arg1) {
		if (gimageViews != null) {
			((ViewPager) arg0).addView(gimageViews.get(arg1));

			return gimageViews.get(arg1);
		}

		((ViewPager) arg0).addView(imageViews.get(arg1));
		/*
		 * 设置当点击幻灯片图片的触发事件
		 */
		imageViews.get(arg1).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (arg1) {
				case 0:
					String url = slidemap.get(0).get("url").replace("\\", "");
					IsIntent.prompt1(context, slidemap.get(0).get("title"), url);
					break;
				case 1:
					String url1 = slidemap.get(1).get("url").replace("\\", "");
					IsIntent.prompt1(context, slidemap.get(1).get("title"), url1);
					break;
				case 2:
					String url2 = slidemap.get(2).get("url").replace("\\", "");
					IsIntent.prompt1(context, slidemap.get(2).get("title"), url2);
					break;
				}
			}
		});
		return imageViews.get(arg1);
	}
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);
	}
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}
	@Override
	public Parcelable saveState() {
		return null;
	}
	@Override
	public void startUpdate(View arg0) {
	}
	@Override
	public void finishUpdate(View arg0) {
	}
}