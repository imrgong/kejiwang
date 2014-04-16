package com.cosji.utils;
import com.cosji.activitys.R;

import android.content.Context;
import android.view.Gravity;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;
/**
 * 状态按钮的监听事件
 * 
 * @author wwj
 * 
 */
public class ToggleListener implements OnCheckedChangeListener {
	private Context context;
	private String settingName;
	private ToggleButton toggle;
	private ImageButton toggle_Button;

	public ToggleListener(Context context, String settingName,
			ToggleButton toggle, ImageButton toggle_Button) {
		this.context = context;
		this.settingName = settingName;
		this.toggle = toggle;
		this.toggle_Button = toggle_Button;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// 保存设置
		if ("Wifi开关".equals(settingName)) {
			SettingUtils.set(context, SettingUtils.WIFI_SWITCH, isChecked);
		}
		// 播放动画
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toggle_Button
				.getLayoutParams();
		if (isChecked) {
			// 调整位置
			params.addRule(RelativeLayout.ALIGN_RIGHT, -1);
			if ("Wifi开关".equals(settingName)) {
				params.addRule(RelativeLayout.ALIGN_LEFT, R.id.toggle_Wifi);
			} 
			toggle_Button.setLayoutParams(params);
			toggle_Button.setImageResource(R.drawable.progress_thumb_selector);
			toggle.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			// 播放动画 右→左
			TranslateAnimation animation = new TranslateAnimation(
					DisplayUtils.dip2px(context, 20),0, 0, 0);
			animation.setDuration(50);
			toggle_Button.startAnimation(animation);
		} else {
			// 调整位置
			if ("Wifi开关".equals(settingName)) {
				params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.toggle_Wifi);
			} 
			params.addRule(RelativeLayout.ALIGN_LEFT, -1);
			toggle_Button.setLayoutParams(params);
			toggle_Button
					.setImageResource(R.drawable.progress_thumb_off_selector);

			toggle.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			// 播放动画 左→右
			TranslateAnimation animation = new TranslateAnimation(
					DisplayUtils.dip2px(context, -20), 0, 0, 0);
			animation.setDuration(50);
			toggle_Button.startAnimation(animation);
		}
	}

}
