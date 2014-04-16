package com.cosji.utils;

public class Url {
	/*
	 * 返利地址参数标志位
	 */
	public static final String hotmallfanurl="hot_MallCache";
	public static final String moremallfanurl="more_MallCache";
	public static final String taogdstycache="tao_GdsTpCache";
//	/*
//	 * 商城返利标志跳转
//	 */
	public static final String Domain = "http://rest.cosjii.com";
	public static final String ZHEMAI = "http://www.zhemai.com";
	public static final String COSJII = "http://www.cosjii.com/";
	public static final String mallurlto="mall";
	public static final String jiuurlto="九元购";

	public static final String mallfanurlapi=Domain+"/mall/getUrl/";//商城返利地址；传递参数：id：123

	public static final String taobaogdsapi=Domain+"/taobao/search/";//淘宝商品那个搜索地址：传递参数：keyowrd:衣服，num：15，page：1；（num：返回条数，page：返回页）
	
	public static final String baidushare_image="http://apps.bdimg.com/store/static/kvt/2fc859fd0b77b95b664c614ad8ccbf21.png";//百度分享的app图片
	
	public static final String rebateurl="http://mobile/product/getProfitUrl/";//id=167&cateId=1
	public static final String mallmoreapi=Domain+"/mall/getAll/";//更多
	public static final String hotmallapi=Domain+"/mall/hot/";//首页热门商城api
	public static final String jiuapi=Domain+"/product/ship/";//9元购api
	public static final String jiufanapi=Domain+"/product/getProfitUrl/";//id=167&cateId=1
	

	public static final String[]ProGoods={"鞋子","包包","化妆品","连衣裙"};
	public static final String taogdsapi=Domain+"/taobao/category/";
	public static final String Slideshow=Domain+"/slide/acquire/?num=3";  //幻灯片API
	/**
	 * 用户操作的API
	 * @return
	 */
       public static String user(){
    	   return Domain+"/user/";
       }
       /**
        * 版本控制API
        * @return
        */
       public static String version(){
    	   return Domain+"/version/check/";
       }
       /**
        * 站内消息的API
        * @return
        */
       public static String message(){
    	   return Domain+"/message/";
       }
       /**
        * 账户明细的API
        * @return
        */
       public static String account(){
    	   return Domain+"/account/";
       }
       /**
        * 签到的API
        * @return
        */
       public static String registry(String action){
    	   if(action.equals("status")){
    		   return Domain+"/registry/status";
    	   }else{
    		   return Domain+"/registry/sign";
    	   }
       }
       /**
        * 返利订单的API
        * @return
        */
       public static String order(String action){
    	   if(action.equals("molist")){
    		   return Domain+"/order/moList/";
    	   }else if(action.equals("polist")){
    		   return Domain+"/order/poList/";
    	   }
    	   return Domain+"/order/toList";
       }
       /**
        * 答疑的API
        * @return
        */
       public static String help(){
    	   return Domain+"/help/";
       }
}
