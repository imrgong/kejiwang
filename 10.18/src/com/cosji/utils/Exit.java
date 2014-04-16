package com.cosji.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class Exit {

private static Boolean isExit = false;  
private static Boolean hasTask = false;  
 Timer tExit = new Timer();  
  TimerTask task = new TimerTask() {  
       
    @Override  
    public void run() {  
        isExit = false;  
        hasTask = true;  
    }  
};
public boolean exit( Context context){
 if(isExit == false ) {  
         isExit = true;  
         ToastUtil.showShortToast(context,  "再按一次退出程序");
        
         if(!hasTask) {  
                 tExit.schedule(task, 2000);  
         }} else {  
                                                          
 ((Activity)context).finish();  
         System.exit(0); 
 }  
 return false;  
}                       
}
