package com.cosji.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.widget.Checkable;
import android.widget.GridLayout;

@SuppressLint("NewApi")
public class PullTorefersh extends GridLayout implements Checkable {

	
	public PullTorefersh(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	protected void initview() {
		getChildCount();
		getChildAt(1);
	}

         @SuppressLint("NewApi")
		@Override
        protected void onDraw(Canvas canvas) {
        	super.onDraw(canvas);
        }

		@Override
		public boolean isChecked() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setChecked(boolean checked) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void toggle() {
			// TODO Auto-generated method stub
			
		}

}
