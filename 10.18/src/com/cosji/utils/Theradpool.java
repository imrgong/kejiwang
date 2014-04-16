package com.cosji.utils;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.drawable.Drawable;
import android.os.SystemClock;

public class Theradpool {
	 private ExecutorService executorService = Executors.newFixedThreadPool(5);
	  
	          // 引入线程池来管理多线程
	          private void loadImage3(final String url, final int id) {
	                  executorService.submit(new Runnable() {
	                          public void run() {
	                                  try {
	                                         final Drawable drawable = Drawable.createFromStream(
	                                                          new URL(url).openStream(), "image.png");
	                                          // 模拟网络延时
	                                          SystemClock.sleep(2000);
//	                                          handler.post(new Runnable() {
//	                                                  public void run() {
//	                                                          ((ImageView) MainActivity.this.findViewById(id))
//	                                                                          .setImageDrawable(drawable);
//	                                                  }
//	                                          });
	                                  } catch (Exception e) {
	                                          throw new RuntimeException(e);
	                                  }
	                         }
	                  });
	          }
}
