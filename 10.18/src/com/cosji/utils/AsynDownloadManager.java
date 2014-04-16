package com.cosji.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.widget.ImageView;

import com.cosji.activitys.R;
import com.cosji.application.Base;

public class AsynDownloadManager {
        private static String ThumbFolder = "Cosji/thumb";//缩略图文件夹
        private static String ImageFolder="Cosji/Image";//大图文件夹
    	
        private static Handler handler;//UI handler
        private static ExecutorService pool;//线程池
        /*
         * ImageLoder
         */
        private static final long DELAY_BEFORE_PURGE =1000;
         private static final int MAX_CAPACITY =200;
    	private static ConcurrentHashMap<String, SoftReference<Object>> mSoftCache = new ConcurrentHashMap<String, SoftReference<Object>>(
    			MAX_CAPACITY / 2);;
    	private static HashMap<String, Object> mHardCache = new LinkedHashMap<String, Object>(
    			MAX_CAPACITY / 2, 0.75f, true) {
    		private static final long serialVersionUID = 1L;

    	};	
    	private Runnable mClearCache = new Runnable() {
    		@Override
    		public void run() {
    			clear();
    		}
    	};
    	private void clear() {
    		mHardCache.clear();
    		mSoftCache.clear();
    	}
    	private void resetPurgeTimer() {
    		handler.removeCallbacks(mClearCache);
    		handler.postDelayed(mClearCache, DELAY_BEFORE_PURGE);
    	}
    	public static void addImage2Cache(String url, Bitmap value) {
    		if (value == null || url == null) {
    			return;
    		}
    		synchronized (mHardCache) {
    			mHardCache.put(url, value);
    		}
    	}
          /**
           * 创建目录
           */
        static {
                if (isSDCardAvailable()) {
                        File file = new File(Environment.getExternalStorageDirectory(),ThumbFolder);
                        if (!file.exists()) {
                                file.mkdirs();
                        }
                        File file1 = new File(Environment.getExternalStorageDirectory(),ImageFolder);
                        if (!file1.exists()) {
                                file1.mkdirs();
                        }
                }
        }
        
        public AsynDownloadManager() {
              
                pool = Executors.newFixedThreadPool(5);
        }
        static AsynDownloadManager manager=null;
        //**********************************************************************************供外部调用的主方法
//        /**
//         * 获取加载器
//         * @return AsynDownloadManager
//         */
//        public static AsynDownloadManager getInterface(){
//                if(manager==null){
//                        manager=new AsynDownloadManager();
//                }
//                return manager;
//        }
        /**
         * 加载图片(缩略图)
         * @param url
         * @param imageView
         */
        public void loadThembBitmap(final Context context,final String url, final ImageView imageView,final boolean isscard) {
                pool.submit(new Runnable() {
                        public void run() {
                                /*
                                 * ImageLoder
                                 */
                                Bitmap bitmapTemp= getBitmapFromCache(url);
                                
                                if(bitmapTemp==null&&isscard){
                               //从SDcard中获取                
                                         bitmapTemp=getFromSDcard(url, ThumbFolder,false);
										}
                                if (SettingUtils.get(context, SettingUtils.WIFI_SWITCH,false)) {
                                	if (bitmapTemp==null) {
                                		bitmapTemp=getFromURL(url, ThumbFolder,false);
									}
                               }else{
                                        	 if (Base.isWifiConnected(context)&&bitmapTemp==null) {
                                        		
                                                     //从URL获取
                                                     bitmapTemp=getFromURL(url, ThumbFolder,false);}
                                                     else {
                                                    	 if (bitmapTemp==null) {
                                                    		 bitmapTemp=BitmapFactory.decodeResource(context.getResources(),R.drawable.default_goods);
														}
                                                		
                                                	 }
                                }
                                final Bitmap bitmap=bitmapTemp;
                                handler.post(new Runnable() {//发送到Handler队列，依次显示
                                        public void run() {
                                        	if (url.equals(imageView.getTag())) {
                                        		imageView.setImageBitmap(bitmap);
											}
                                        	else{
                                        		imageView.setImageResource(R.drawable.default_goods);
                                        	}
                                        }
                                });
                        }
                });
        }
        /**
         * 加载图片（大图）
         * @param url
         * @param imageView
         * @param isZip 是否压缩
         */
        public  void  loadBitmap(final String url, final ImageView imageView,final boolean isZip){
                pool.submit(new Runnable() {
                        public void run() {
                                //因为单图较大，不用SoftReference
                                Bitmap bitmapTemp=null;
                               //从SDcard中获取                
                                         bitmapTemp=getFromSDcard(url, ImageFolder,isZip);
                                         
                                         if(bitmapTemp==null){
                                                 //从URL获取
                                                 bitmapTemp=getFromURL(url, ImageFolder,isZip);
                                         }
                                final Bitmap bitmap=bitmapTemp;
                                handler.post(new Runnable() {//发送到Handler队列，依次显示
                                        public void run() {
                                        	if (url.equals(imageView.getTag())) {
                                        		imageView.setImageBitmap(bitmap);
											}
                                        	else{
                                        		imageView.setImageResource(R.drawable.default_goods);
                                        	}
                                        }
                                });
                        }
                });
        }
             	/**
             	 * 返回缓存，如果没有则返回null
             	 * 
             	 * @param url
             	 * @return
             	 */
             	public Bitmap getBitmapFromCache(String url) {
             		Bitmap bitmap = null;
             		synchronized (mHardCache) {
             			bitmap = (Bitmap) mHardCache.get(url);
             			if (bitmap != null) {
             				mHardCache.remove(url);
             				mHardCache.put(url, bitmap);
             				return bitmap;
             			}
             		}

             		SoftReference<Object> softReference = mSoftCache.get(url);
             		if (softReference != null) {
             			bitmap = (Bitmap) softReference.get();
             			if (bitmap == null) {// 已经被gc回收了
             				mSoftCache.remove(url);
             			}
             		}
             		return bitmap;
             	}
                 /**
                  * 从SDcard获取（下载并返回）
                  * @param imageUrl
                  * @param folderPath
                  * @return Bitmap
                  */
                 public static Bitmap getFromURL(String imageUrl, String folderPath,boolean isZip){
                               URL mUrl;
                               InputStream is = null;
                               Bitmap bitmap = null; 
                               if (imageUrl.contains("http")) {
                               try {
                                       mUrl = new URL(imageUrl);
                                       is = (InputStream) mUrl.getContent();
                                       if(isSDCardAvailable()){
                                               String fileName = imageUrl.substring(imageUrl.lastIndexOf('/')+1);
                                               File basePathFile = new File(Environment.getExternalStorageDirectory(), folderPath);
                                               File file = new File(basePathFile, fileName+".tmp");
                                               if(!file.exists()){
                                                       file.createNewFile();
                                               }
                                               FileOutputStream outputStream = new FileOutputStream(file);
                                               byte[] b = new byte[512];
                                               int offset;
                                               while((offset=is.read(b))!=-1){
                                                       outputStream.write(b, 0, offset);
                                               }
                                               outputStream.flush();
                                               outputStream.close();
                                               is.close();
                                               basePathFile = new File(basePathFile, fileName);
                                               if(basePathFile.exists()){
                                                       basePathFile.delete();
                                               }
                                               if(file.renameTo(basePathFile)){
                                                       is = new FileInputStream(basePathFile);
                                               }
                                               // 读取图片
                                               bitmap=getBitmap(basePathFile.getAbsolutePath(), isZip);
                                               
                                               if(isZip==false){
                                            	   /*
                                                    * ImageLoder
                                                    */
                                            	   addImage2Cache(imageUrl,bitmap); 
                                               }
                                                
                                       }
                               } catch (Exception e) {
                                       e.printStackTrace();
                                       return null;
                               }
                               }
                               return bitmap;
                       }
                 
                 /**
                  * 从SDcard获取
                  * @param url
                  * @param folderPath
                  * @return Bitmap
                  */
                 public static Bitmap getFromSDcard(String url,String folderPath,boolean isZip){
                         Bitmap bitmap = null;
                         String fileName = url.substring(url.lastIndexOf('/')+1);
                         File basePathFile = new File(Environment.getExternalStorageDirectory(), folderPath);
                         basePathFile = new File(basePathFile, fileName);
//                         if(basePathFile.exists()&&url.contains("http")){
                                 bitmap=getBitmap(basePathFile.getAbsolutePath(), isZip);
//                         }                  
                         return bitmap;
                 }
               /**
                * 检查SDCard是否可用
                * 
                * @return
                */
               public static final boolean isSDCardAvailable() {
                       return Environment.getExternalStorageState().equals(
                                       Environment.MEDIA_MOUNTED);
               }

                /**
            * 读取SDcard中的图片资源（解决内存溢出问题）
            * @param pathName 文件路径名
            * @param isZip  是否降低采样率
            * @return Bitmap
            */
               public static Bitmap getBitmap(String pathName,boolean isZip){
                       Bitmap bitmap=null;
                       if(isZip==true){
                               BitmapFactory.Options options = new BitmapFactory.Options();
                   options.inSampleSize = 2;//图片宽高都为原来的二分之一，即图片为原来的四分之一
                   bitmap=BitmapFactory.decodeFile(pathName, options);
                       }else{
                                bitmap=BitmapFactory.decodeFile(pathName);
                       }
                       return  bitmap;
               }
        
               public static String getThumbFolder() {
                       return ThumbFolder;
               }

               public static void setThumbFolder(String thumbFolder) {
                       ThumbFolder = thumbFolder;
               }

               public static String getImageFolder() {
                       return ImageFolder;
               }

               public static void setImageFolder(String imageFolder) {
                       ImageFolder = imageFolder;
               }

               public Handler getHandler() {
                       return handler;
               }

               public void setHandler(Handler handler) {
                       this.handler = handler;
               }
               }
