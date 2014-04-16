package com.cosji.activitys;

import android.os.Bundle;
import android.widget.TextView;

public class NotificationActivity  extends BaseActivity{
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	TextView tv=new TextView(NotificationActivity.this);
	tv.setText("sdfsdf");
	setContentView(tv);
}
}
