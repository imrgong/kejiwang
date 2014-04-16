package com.cosji.activitys;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.cosji.adapter.ProPagerViewAdapter;
import com.umeng.analytics.MobclickAgent;

public class WelcomeGuid extends BaseActivity{
	/*
	 * 促销幻灯片设计
	 */
	private ViewPager viewPager; // android-support-v4中的滑动组件
	private List<View> imageViews; // 滑动的图片集合
    int count=0;
	private String[] titles; // 图片标题
	private int[] imageResId; // 图片ID
	private List<View> dots; // 图片标题正文的那些点

	private TextView tv_title;
	private int currentItem = 0; // 当前图片的索引号
GestureDetector gesture;
LayoutInflater inlfater;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		
		setContentView(R.layout.welcome_guid_page);
		initProPageView();
	}

	public void initProPageView() {

		 Intent intent = getIntent();
		
				imageResId = new int[] { R.drawable.guid_one, R.drawable.guid_two,
						R.drawable.guid_three };

		imageViews = new ArrayList<View>();

		// 初始化图片资源
		for (int i = 0; i < imageResId.length; i++) {
			View imageView = new View(this);
			imageView.setBackgroundResource(imageResId[i]);
			imageViews.add(imageView);
		}
		inlfater=LayoutInflater.from(WelcomeGuid.this);
	 View v=inlfater.inflate(R.layout.guide_welcome_item,null);
	 imageViews.add(v);
	 v.findViewById(R.id.close_guide).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent in=new Intent(WelcomeGuid.this,MainTabActivity.class);
			startActivity(in);
			finish();
		}
	});

		dots = new ArrayList<View>();
		dots.add(findViewById(R.id.gv_dot0));
		dots.add(findViewById(R.id.gv_dot1));
		dots.add(findViewById(R.id.gv_dot2));
		dots.add(findViewById(R.id.gv_dot3));
		viewPager = (ViewPager) findViewById(R.id.gvp);
		viewPager.setAdapter(new ProPagerViewAdapter(imageViews, this,4));// 设置填充ViewPager页面的适配器
		// 设置一个监听器，当ViewPager中的页面改变时调用
		viewPager.setOnPageChangeListener(new MyPageChangeListener());

	}
	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			currentItem = position;
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
			count=position;
			
		}

		public void onPageScrollStateChanged(int arg0) {
                        
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(WelcomeGuid.this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(WelcomeGuid.this);
	}
}
