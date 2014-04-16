package com.cosji.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.cosji.application.Base;
import com.cosji.bean.TbkItem;
import com.cosji.utils.ImageLoader;
import com.cosji.utils.IsIntent;
import com.cosji.view.CustomDialog;

public class GoodsDetialsActivitys extends BaseActivity{
 
	private Button goto_shopping;
	private ImageView fanli;
	private ImageView details_image;
	private TbkItem tbkItem;
	private ImageLoader imageloader;
	
	private LinearLayout web_bak;
	private String click_url;
	private int h;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goodsdetials);
		imageloader = Base.imageLoader;
		h = getWindowManager().getDefaultDisplay().getHeight();
		initViews();
	}

	private void initViews() {
		((TextView)findViewById(R.id.mine_secend_title)).setText("商品详情");
		details_image = (ImageView)findViewById(R.id.detial_image);
		
		fanli = (ImageView)findViewById(R.id.fanli_image);
		web_bak = (LinearLayout) findViewById(R.id.web_back);
		
		Intent intent = getIntent();
		
		if(intent.hasExtra("tbkitem")){
			tbkItem = intent.getParcelableExtra("tbkitem");
			click_url = tbkItem.getClick_url();
			((TextView)findViewById(R.id.detial_introdutice)).setText(tbkItem.getTitle());
			((TextView)findViewById(R.id.old_price_number)).setText(tbkItem.getPrice());
			
				// 设置布局垂直显示
				imageloader.DisplayImage(tbkItem.getPic_url(), details_image, false);
		}
		goto_shopping = (Button)findViewById(R.id.goto_shopping_now);
		if(click_url!=null&&click_url.length()>0){
			goto_shopping.setEnabled(true);
			fanli.setBackgroundResource(R.drawable.rebate_yes);
		}else
			goto_shopping.setEnabled(false);
		goto_shopping.setOnClickListener(myClicklistener);
		web_bak.setOnClickListener(myClicklistener);
	}
	
	OnClickListener myClicklistener = new OnClickListener() {
		 
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.goto_shopping_now:
				
				if(!Base.isTaobaoEnabled){
					CustomDialog.Builder buidler = new CustomDialog.Builder(GoodsDetialsActivitys.this);
				    buidler.setMessage(R.string.Taobao_not_enabled_message);
				    buidler.setTitle(R.string.Taobao_not_enabled_title);
				    buidler.setNegativeButton(R.string.cancal, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//取消
							dialog.dismiss();
						}
					});
				    buidler.setPositiveButton(R.string.Download_Taobao, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//去下载淘宝客户端
							dialog.dismiss();
							try {
								Uri uri = Uri.parse("market://search?q=com.taobao.taobao");
								Intent intent = new Intent(Intent.ACTION_VIEW, uri);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				    buidler.create().show();
				}else{
					shopping(true);
				}
				break;
			case R.id.web_back:
				finish();
				break;
			}
		}
	};
	private void shopping(boolean flag){
				IsIntent.ToWeb1(GoodsDetialsActivitys.this, click_url, "商品详情", flag);
	}
}
