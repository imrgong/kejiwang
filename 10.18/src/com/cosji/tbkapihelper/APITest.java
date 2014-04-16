package com.cosji.tbkapihelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.cosji.utils.Contact;

/**
 * Top api用来按照淘宝规则封装参数的
 * @author Administrator
 *
 */

public class APITest {
	 
	 private Long userId;
     protected static String testUrl = Contact.SERVERURL;//沙箱环境调用地址
     protected static String appkey = Contact.APPKEY;
     protected static String secret = Contact.SECRET;
     /**
      * keyword method page_size fileds
      * defualt: 
      * keyword:情趣内衣 
      * method:"taobao.tbk.items.get"
      * page_size:5
      * fileds:"num_iid,seller_id,nick,title,price,volume,pic_url,item_url,shop_url"
      * @param params
      * @return
      */
     public static String get(TreeMap<String, String> params){
         TreeMap<String, String> apiparamsMap = new TreeMap<String, String>();
         apiparamsMap.put("format", "json");
         apiparamsMap.put("method", "taobao.tbk.items.get");
         apiparamsMap.put("sign_method","md5");
         apiparamsMap.put("app_key",Contact.APPKEY);
         apiparamsMap.put("v", "2.0");
         apiparamsMap.put("is_mobile", "true");
         apiparamsMap.put("start_commissionRate", "1");
         apiparamsMap.put("end_commissionRate", "9999");
         String fileds = "num_iid,seller_id,nick,title,price,volume,pic_url,item_url,shop_url";
         String timestamp =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
         apiparamsMap.put("timestamp",timestamp);
         apiparamsMap.put("fields",fileds);//需要获取的字段
         //生成签名
         if(params.containsKey("keyword")){
        	 apiparamsMap.remove("keyword");
         }else if(params.containsKey("page_no")){
        	 apiparamsMap.remove("page_no");
         }else if(params.containsKey("page_size")){
        	 apiparamsMap.remove("page_size");
         }else if(params.containsKey("method")){
        	 apiparamsMap.remove("method");
         }else if(params.containsKey("fileds")){
        	 apiparamsMap.remove("fileds");
         }else if (params.containsKey("num_iids")){
        	 apiparamsMap.remove("keyword");
        	 apiparamsMap.remove("start_commissionRate");
        	 apiparamsMap.remove("end_commissionRate");
         }else if (params.containsKey("method")){
        	 apiparamsMap.remove("method");
         }
         apiparamsMap.putAll(params);
         
         String sign = Utils.md5Signature(apiparamsMap,secret);
         apiparamsMap.put("sign", sign);
        
         StringBuilder param = new StringBuilder();
         for (Iterator<Map.Entry<String, String>> it = apiparamsMap.entrySet()
         .iterator(); it.hasNext();) {
             Map.Entry<String, String> e = it.next();
             param.append("&").append(e.getKey()).append("=").append(e.getValue());
         }
         return param.toString().substring(1);
     }
}