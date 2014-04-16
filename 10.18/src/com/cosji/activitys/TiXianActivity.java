package com.cosji.activitys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cosji.utils.HttpConnectionHepler;
import com.cosji.utils.ToastUtil;
import com.cosji.utils.Url;

public class TiXianActivity extends BaseActivity{
	private EditText money;
	private EditText zhifubao_id;
	private EditText name, phone, password;
	private TextView allowedmoney;
	private Button bt;
	private LinearLayout bt_back;
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
setContentView(R.layout.tixian_appliction);
initview();
String url1 = Url.user()+"getContact/";
LoadData(url1	,null,"loading");
}
private void initview() {
    
	TextView title_name=(TextView)findViewById(R.id.mine_secend_title);
	bt_back=(LinearLayout)findViewById(R.id.web_back);
		
	allowedmoney = (TextView)findViewById(R.id.allowed_money);
    money=(EditText)findViewById(R.id.apptixian_money);
    zhifubao_id=(EditText)findViewById(R.id.zhifubao_id);
    name=(EditText)findViewById(R.id.tixian_name);
    phone=(EditText)findViewById(R.id.tisnxian_phone);
    password=(EditText)findViewById(R.id.tixian_loginpassword);
    
    money.setOnFocusChangeListener(focuschange);
    
    bt=(Button)findViewById(R.id.immediately_tixian);
		title_name.setText("提现申请");
		
		bt_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(bt_back.getWindowToken(), 0);
				finish();
			}
		});
    bt.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			List<NameValuePair> arg =new ArrayList<NameValuePair>();
			arg.add(new BasicNameValuePair("type", "2"));//type = 2代表申请提现
			arg.add(new BasicNameValuePair("money",money.getText().toString().trim()));
			arg.add(new BasicNameValuePair("code", zhifubao_id.getText().toString().trim()));
			arg.add(new BasicNameValuePair("realname", name.getText().toString().trim()));
			arg.add(new BasicNameValuePair("mobile", phone.getText().toString().trim()));
			arg.add(new BasicNameValuePair("password", password.getText().toString().trim()));
			
			String url =Url.account()+"apply/";
			String flag = "apply";
			LoadData(url, arg, flag);
		}
	});
    }
private void LoadData(String url,List<NameValuePair> arg,String flag){
	HashMap<String, Object> params = new HashMap<String, Object>();
	params.put("flag", flag);
	params.put("url", url);
	params.put("arg", arg);
	TixianAsytask task = new TixianAsytask();
	task.execute(params);
}
OnFocusChangeListener focuschange = new OnFocusChangeListener() {
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.apptixian_money:
			if(!hasFocus){
				String mon = allowedmoney.getText().toString().trim();
				float has = Float.parseFloat(mon.substring(0, mon.length()-1));
				float number = 0f;
				mon = money.getText().toString().trim();
				if(mon.equals("")||mon==null){
					money.setText(null);
					money.setHint("金额不能为空");
				}else{
					number = Float.parseFloat(mon);
				}
				 if(number>has){
					 money.setText(null);
					 money.setHint("输入额过大");
				 }
			}
			break;
		}
	}
};
		class TixianAsytask extends AsyncTask<HashMap<String, Object>, Integer, HashMap<String, String>>{

			@Override
			protected HashMap<String, String> doInBackground(
					HashMap<String, Object>... params) {
				HttpConnectionHepler helper = new HttpConnectionHepler();
				HashMap<String, String> result = new HashMap<String, String>();
				
				String url = (String)params[0].get("url");
				String flag = (String)params[0].get("flag");
				List<NameValuePair> arg = (List<NameValuePair>)params[0].get("arg");
				if (flag.equals("apply")) {
					result = helper.sendPostRequest(url, arg);
					result.put("flag", flag);
					return result;
				}else if (flag.equals("loading")){
					HashMap<String, String> initInfo = new HashMap<String, String>();
					HashMap<String, String> initFinance = new HashMap<String, String>();
					HashMap<String, String> initfallowednum = new HashMap<String, String>();
					String autherUrl = Url.account()+"getAlipay/";
					String url1 = Url.account()+"balance/";
					List<NameValuePair> param = new ArrayList<NameValuePair>();
					param.add(new BasicNameValuePair("type", "2"));
					initfallowednum = helper.sendPostRequest(url1, param);
					initInfo = helper.sendPostRequest(url, null);
					initFinance = helper.sendPostRequest(autherUrl, null);
					initInfo.put("flag", flag);
					initInfo.putAll(initFinance);
					initInfo.putAll(initfallowednum);
					return initInfo;
				}
				return null;
			}

			@Override
			protected void onPostExecute(HashMap<String, String> result) {
				String flag = result.get("flag");
				String msg = result.get("msg");
				if(flag.equals("apply")){
					
						ToastUtil.showShortToast(TiXianActivity.this, msg);
					
				}else if(flag.equals("loading")){
					zhifubao_id.setText(result.get("alipay"));
					name.setText(result.get("realname"));
					phone.setText(result.get("mobile"));
					allowedmoney.setText(result.get("balance"));
				}
				super.onPostExecute(result);
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
			
		}
}
