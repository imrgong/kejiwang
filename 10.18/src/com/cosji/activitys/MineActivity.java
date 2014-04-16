package com.cosji.activitys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cosji.activitys.MainTabActivity.MyHandler;
import com.cosji.application.Base;
import com.cosji.utils.Bitmaptool;
import com.cosji.utils.Exit;
import com.cosji.utils.HttpConnectionHepler;
import com.cosji.utils.SettingUtils;
import com.cosji.utils.ToastUtil;
import com.cosji.utils.Url;

public class MineActivity extends BaseActivity{
	private ImageView iv_set;// 图片
	private TextView getcoins;//+15积分动画
	private Button sign ;//签到按钮
//	private TextView messageNum;
	private Animation anim_out,shake,hidden,anim_in;
	private String ids=null;
	private MyHandler handler;
	private Handler imagehandler;
	private static final int USER_AVATAR = 200;
	/***
	 * 初始化view
	 */
	void InitView() {
		Base app =(Base)getApplication();
		handler = app.getHandler();
		
		sign = (Button)findViewById(R.id.sign_in_cash);
		//messageNum = (TextView)findViewById(R.id.inner_message_num);
		getcoins = (TextView)findViewById(R.id.getcoins);
		ImageView titlename=(ImageView)findViewById(R.id.title_name);
		titlename.setImageResource(R.drawable.mine_title_name);
		iv_set=(ImageView)findViewById(R.id.menu_view);
		iv_set.setImageResource(R.drawable.mine_setting);
		
OnClickListener MyClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.apply_for_cash:
					if(Base.getUserId()!=null&&Base.getUserId().length()>0){
						Intent cash = new Intent(MineActivity.this,TiXianActivity.class);
						startActivity(cash);
					}else{
						ToastUtil.showShortToast(MineActivity.this,"您还没有登录");
					}
					//申请提现
					break;
				case R.id.apply_for_jifenbao:
					//盛情集分宝
                     if(Base.getUserId()!=null&&Base.getUserId().length()>0){
                    	 Intent cash = new Intent(MineActivity.this,JifenbaoActivity.class);
 						startActivity(cash);
					}else{
						ToastUtil.showShortToast(MineActivity.this,"您还没有登录");
					}
					break;
				case R.id.sign_in_cash:
					boolean sign_out = SettingUtils.get(getApplicationContext(), SettingUtils.SIGN_STATE, false);
					 if(Base.getUserId()!=null&&Base.getUserId().length()>0){
							if(sign_out){
								sign.startAnimation(shake);
								ToastUtil.showShortToast(MineActivity.this,"明天再来吧");
							}else {
								anim_out.setStartOffset(150l);
						        sign.setAnimation(anim_out);
								sign.startAnimation(anim_out);
						            Sign_out();//异步发送签到请求到服务器
							}
						}else{
							ToastUtil.showShortToast(MineActivity.this,"您还没有登录");
						}
					//签到提现
					
					break;
				case R.id.rebate_indent:
					//返利订单
					  if(Base.getUserId()!=null&&Base.getUserId().length()>0){
						  Intent rebate = new Intent(MineActivity.this,RebateActivity.class);
							startActivity(rebate);
						}else{
							ToastUtil.showShortToast(MineActivity.this,"您还没有登录");
						}
					
					break;
//				case R.id.referrd_bonuses:
//					//推荐奖励
//					 if(SettingUtils.get(getApplicationContext(), SettingUtils.USER_STATE, false)){
//							
//						}else{
//							ToastUtil.showShortToast(MineActivity.this,"您还没有登录呢");
//						}
//					break;
				case R.id.acount_detils:
					//账户明细
					 if(Base.getUserId()!=null&&Base.getUserId().length()>0){
							Intent acount = new Intent(MineActivity.this,AcountDetils.class);
							startActivity(acount);
						}else{
							ToastUtil.showShortToast(MineActivity.this,"您还没有登录");
						}
					break;
				case R.id.inner_message:
					//站内信息
					 if(Base.getUserId()!=null&&Base.getUserId().length()>0){
						 //readmessage
						 if(ids!=null&&!ids.equals("")){
							 String url = Url.message()+"read/";
							 HashMap<String, Object> param = new HashMap<String, Object>();
							 List<NameValuePair> idss = new ArrayList<NameValuePair>();
							 idss.add(new BasicNameValuePair("ids", ids));
							 param.put("flag", "read");
							 param.put("url1", url);
							 param.put("params", idss);
							 mytask task = new mytask();
							task.execute(param);
						 }
							Intent message = new Intent(MineActivity.this,InnerMessageActivity.class);
							startActivity(message);
						}else{
							ToastUtil.showShortToast(MineActivity.this,"您还没有登录");
						}
					break;
				case R.id.acount_setting:
					//账户设置
					 if(Base.getUserId()!=null&&Base.getUserId().length()>0){
							Intent setting = new Intent(MineActivity.this,SettingActivity.class);
							startActivity(setting);
						}else{
							ToastUtil.showShortToast(MineActivity.this,"您还没有登录");
						}
					break;
				case R.id.User_login_to_cosji:
					//用户登录
					Intent login = new Intent(MineActivity.this,LoginActivity.class);
					startActivity(login);
					break;
				case R.id.User_register_to_cosji:
					//用户注册
					Intent register = new Intent(MineActivity.this,RegisterActivity.class);
					startActivity(register);
					break;
				case R.id.menu_view:
					//设置按钮
						handler.sendEmptyMessage(500);
				}	
			}
		};

		this.findViewById(R.id.apply_for_jifenbao).setOnClickListener(MyClickListener);
		this.findViewById(R.id.apply_for_cash).setOnClickListener(MyClickListener);
		this.findViewById(R.id.sign_in_cash).setOnClickListener(MyClickListener);
		this.findViewById(R.id.rebate_indent).setOnClickListener(MyClickListener);
		this.findViewById(R.id.acount_detils).setOnClickListener(MyClickListener);
		this.findViewById(R.id.inner_message).setOnClickListener(MyClickListener);
		this.findViewById(R.id.acount_setting).setOnClickListener(MyClickListener);
		this.findViewById(R.id.User_login_to_cosji).setOnClickListener(MyClickListener);
		this.findViewById(R.id.User_register_to_cosji).setOnClickListener(MyClickListener);
		this.findViewById(R.id.menu_view).setOnClickListener(MyClickListener);
		// 点击监听
		//layout_right.setOnTouchListener(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		InitView();
	}

	private void Sign_out() {
		if (Base.isNetworkConnected(MineActivity.this)){
			String url1 = Url.registry("sign");
			HashMap<String, Object> params =new HashMap<String, Object>();
			params.put("url1", url1);
			params.put("flag", "sign");
			mytask task = new mytask();
			task.execute(params);
		}else{
			ToastUtil.showShortToast(MineActivity.this, "网络出错,请检测网络...");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onResume() {
		boolean flag = false;
		String userId = Base.getUserId();
		if(userId!=null&&userId.length()>0){
			flag =true;
		}
		shake = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
		shake.setInterpolator(new CycleInterpolator(3f));
		
		anim_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.check_version_out);
        anim_out.setAnimationListener(myanim);
        
        anim_in= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.check_version_in);
        
        hidden = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hidden);
        if(Base.getUserId()!=null&&Base.getUserId().length()>0&&
				SettingUtils.get(getApplicationContext(), SettingUtils.SIGN_STATE, false)){
			sign.setText("已签到");
		}
		if(flag){
			//已经登录了
			findViewById(R.id.for_login).setVisibility(View.GONE);
			findViewById(R.id.for_logined).setVisibility(View.VISIBLE);
			String url1 = Url.user()+"profile/";
			String url2 = Url.message()+"newMessage/";
			String url3 = Url.registry("status");
			HashMap<String, Object> params =new HashMap<String, Object>();
			params.put("url1", url1);
			params.put("url2", url2);
			params.put("url3", url3);
			params.put("flag", "userinfo");
			mytask task = new mytask();
			task.execute(params);
		}else{
			//没有登录
			findViewById(R.id.for_login).setVisibility(View.VISIBLE);
			findViewById(R.id.for_logined).setVisibility(View.GONE);
			sign.setText("签到赚现");
		}
		
		super.onResume();
	}
	//动画监听
AnimationListener myanim = new AnimationListener() {
	
	@Override
	public void onAnimationStart(Animation animation) {
		
	}
	
	@Override
	public void onAnimationRepeat(Animation animation) {
	}
	
	@Override
	public void onAnimationEnd(Animation animation) {
		  sign.setText("已签到");
        sign.setAnimation(anim_in);
        anim_in.startNow();
        getcoins.setAnimation(hidden);
        hidden.setAnimationListener(myhidden);
        getcoins.setVisibility(View.VISIBLE);
        hidden.setStartOffset(300l);
        getcoins.startAnimation(hidden);
	}
};
AnimationListener myhidden = new AnimationListener() {
	
	@Override
	public void onAnimationStart(Animation animation) {
	}
	
	@Override
	public void onAnimationRepeat(Animation animation) {
	}
	
	@Override
	public void onAnimationEnd(Animation animation) {
		getcoins.setVisibility(View.GONE);
	}
};
//异步加载数据
class mytask extends AsyncTask<HashMap<String, Object>, Integer, HashMap<String, String>>{

	@Override
	protected HashMap<String, String> doInBackground(
			HashMap<String, Object>... params) {
		String flag = (String)params[0].get("flag");
		HttpConnectionHepler helper = new HttpConnectionHepler();
		HashMap<String, String> result1 = new HashMap<String, String>();
		HashMap<String, String> result2 = new HashMap<String, String>();
		String url1 =(String)params[0].get("url1");
		
		if(flag!=null&&flag.equals("userinfo")){
		String url2 =(String)params[0].get("url2");
		String url3 =(String)params[0].get("url3");
		HashMap<String, String> result3 = new HashMap<String, String>();
		result1 = helper.sendPostRequest(url1, null);
		result2 = helper.sendPostRequest(url2, null);
		result3 = helper.sendPostRequest(url3, null);
		
		result2.remove("msg");
		result2.remove("code");
		result3.remove("msg");
		result3.remove("code");
		
		result1.put("flag", flag);
		result1.putAll(result2);
		result1.putAll(result3);
		return result1;
		}else if (flag!=null&&flag.equals("read")){
			List<NameValuePair> idss = (List<NameValuePair>) params[0].get("params");
			result1 = helper.sendPostRequest(url1, idss);
			result1.put("flag", flag);
			return result1;
		}else{
			result1 = helper.sendPostRequest(url1, null);
			result1.put("flag", flag);
			String msg = result1.get("msg");
			if(msg!=null&&msg.equals("success")){
				String url4 = Url.user()+"profile/";
				result2 = helper.sendPostRequest(url4, null);
				result1.putAll(result2);
			}
			return result1;
		}
	}

	//跟新界面
	@Override
	protected void onPostExecute(HashMap<String, String> result) {
		if(result.get("flag").equals("userinfo")){
		((TextView)findViewById(R.id.nickname)).setText(result.get("username"));
		((TextView)findViewById(R.id.userVIPgrade)).setText("VIP"+result.get("level"));
		((TextView)findViewById(R.id.shengyumoney)).setText(result.get("balance")+"元");
		((TextView)findViewById(R.id.AmassPoint)).setText(result.get("score"));
		((TextView)findViewById(R.id.jifenbaoPoint)).setText(result.get("jifenbao"));
		 //头像的异步加载放入私有方法中
		loadavatar(result);
				//签到功能初始化
				String status = result.get("status");
				if(status.equals("0")){
					//未签到
					 sign.setText("签到赚现");
					SettingUtils.set(getApplicationContext(), SettingUtils.SIGN_STATE, false);
				}else{
					//已签到
					 sign.setText("已签到");
					SettingUtils.set(getApplicationContext(), SettingUtils.SIGN_STATE, true);
				}
				String id = result.get("ids");
				//处理是否有新消息而显示数字的逻辑
				ids = id.substring(1,id.length()-1);
				if(ids.equals("")){
				//	messageNum.setText("");
				}else{
					String[] newid = ids.split(",");
				//	messageNum.setText(""+newid.length);
				}
				Base.setUserId(result.get("userId"));
		
		}else if(result.get("flag").equals("read")){
			if(result.get("msg").equals("success")){
			//	messageNum.setText("");
			}
		}else {
			String msg = result.get("msg");
			if(msg!=null&&msg.equals("success")){
	            SettingUtils.set(getApplicationContext(), SettingUtils.SIGN_STATE, true);
				ToastUtil.showShortToast(MineActivity.this, "签到成功，+15积分");
				((TextView)findViewById(R.id.AmassPoint)).setText(result.get("score"));
			}
		}
		
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}
}


@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
// TODO Auto-generated method stub
 if(keyCode == KeyEvent.KEYCODE_BACK){ 
	 
Exit et=new Exit();
et.exit(MineActivity.this);
 }
return false;
}
private void loadavatar(HashMap<String, String> result){
	final String url = result.get("avatar");
	imagehandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == USER_AVATAR&&msg.arg1==200){
				Bitmap bm = (Bitmap)msg.obj;
				final ImageView image = (ImageView)findViewById(R.id.changePhoto);
				image.setImageBitmap(bm);
			}
			super.handleMessage(msg);
		} 
    };
new Thread(new Runnable() {
		@Override
		public void run() {
			Bitmaptool tools = new Bitmaptool();
			HttpConnectionHepler helper = new HttpConnectionHepler();
			Bitmap roundBitmap =null; //Bitmap.createBitmap(R.drawable.initial_photo);
			Message msg = new Message();
			try {
				roundBitmap = helper.getimage(url);
				roundBitmap = tools.toRoundBitmap(roundBitmap);
				msg.arg1 = 200;
			} catch (Exception e) {
				msg.arg1 = 250;
			}
			
			
			msg.what = USER_AVATAR;
			msg.obj = roundBitmap;
			imagehandler.sendMessage(msg);
		}
	}).start();
}

}