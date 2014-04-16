package com.cosji.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;


public class Dialog {
	//打电话的提示框
		public void dialogshow(String message,Context ctx,String url){
		   final Context context = ctx;
		   final String URL = url;
			 AlertDialog.Builder builder = new Builder(ctx);
			  builder.setMessage(message);
			  builder.setTitle("发现新版本");
			  builder.setPositiveButton("Yes",new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					Intent call = new Intent(Intent.ACTION_VIEW,Uri.parse(URL));
					context.startActivity(call);
				}
			});

			  builder.setNegativeButton("No", new OnClickListener() {

			   public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();
			   }
			  });

			  builder.create().show();
		}
}
