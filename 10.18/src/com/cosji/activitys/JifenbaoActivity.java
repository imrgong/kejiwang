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

public class JifenbaoActivity extends BaseActivity {

	private EditText apply_for_cash,zhihubao_count,person_name,phone_number,personal_password;
	private TextView jifenbaonum;
	private Button submitjifenbaoApply;
	private LinearLayout back;
	private List<EditText> editlist = new ArrayList<EditText>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jifenbao);
		initView();
		String url1 = Url.user()+"getContact/";
		LoadData(url1	,null,"loading");
	}

	private void initView() {
		//title
		TextView title = (TextView)findViewById(R.id.mine_secend_title);
		title.setText(getResources().getString(R.string.shenqing_jifenbao));
		back = (LinearLayout)findViewById(R.id.web_back);
		//body
		jifenbaonum = (TextView)findViewById(R.id.jifenbaonum);
		apply_for_cash = (EditText)findViewById(R.id.Jifenbao_apply_for_cash);
		zhihubao_count = (EditText)findViewById(R.id.zhifubao_count);
		person_name = (EditText)findViewById(R.id.income_person_name);
		phone_number = (EditText)findViewById(R.id.personal_phone_number);
		personal_password = (EditText)findViewById(R.id.jifenbao_personal_password);
		submitjifenbaoApply = (Button)findViewById(R.id.submitjifenbaoApply);
		//设置编辑框的监听器断输入是否合法和部分显示效果
		apply_for_cash.setOnFocusChangeListener(myFocuslistener);
		zhihubao_count.setOnFocusChangeListener(myFocuslistener);
		person_name.setOnFocusChangeListener(myFocuslistener);
		phone_number.setOnFocusChangeListener(myFocuslistener);
		personal_password.setOnFocusChangeListener(myFocuslistener);
		back.setOnClickListener(myclicklistener);
		submitjifenbaoApply.setOnClickListener(myclicklistener);
	}


	OnClickListener myclicklistener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.web_back:
				final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(back.getWindowToken(), 0);
				finish();
				break;
			case R.id.submitjifenbaoApply:
				//页面动画，及提交数据的操作
				List<NameValuePair> arg =new ArrayList<NameValuePair>();
				arg.add(new BasicNameValuePair("type", "1"));//type = 1代表提集分宝
				arg.add(new BasicNameValuePair("money",apply_for_cash.getText().toString().trim()));
				arg.add(new BasicNameValuePair("code", zhihubao_count.getText().toString().trim()));
				arg.add(new BasicNameValuePair("realname", person_name.getText().toString().trim()));
				arg.add(new BasicNameValuePair("mobile", phone_number.getText().toString().trim()));
				arg.add(new BasicNameValuePair("password", personal_password.getText().toString().trim()));
				
				String url =Url.account()+"apply/";
				String flag = "apply";
				System.out.println("支付宝账号:"+arg.get(2));
				LoadData(url, arg, flag);
				break;
			}
		}
	};
	
OnFocusChangeListener myFocuslistener = new OnFocusChangeListener() {
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.Jifenbao_apply_for_cash:
			if (!hasFocus) {
				//自动判断输入金额大小
				int tixian = 0;
				if(!apply_for_cash.getText().toString().trim().equals("")){
					tixian =Integer.parseInt(apply_for_cash.getText().toString().trim());
				}else{
					apply_for_cash.setHint("金额不能为空");
				}
			 String has = jifenbaonum.getText().toString().trim();
			has = has.substring(0, has.lastIndexOf("."));
			 int hasnum = Integer.parseInt(has);
			 if(tixian>hasnum||tixian==0){
				 apply_for_cash.setText(null);
				 apply_for_cash.setHint("输入额过大或不能为0");
			 }
			}
			break;
		case R.id.zhifubao_count:
			
			break;
		case R.id.income_person_name:
			
			break;
		case R.id.personal_phone_number:
			
			break;
		case R.id.jifenbao_personal_password:
			
			break;
		}
	}
};
private void LoadData(String url,List<NameValuePair> arg,String flag){
	HashMap<String, Object> params = new HashMap<String, Object>();
	params.put("flag", flag);
	params.put("url", url);
	params.put("arg", arg);
	TixianAsytask task = new TixianAsytask();
	task.execute(params);
}
class TixianAsytask extends AsyncTask<HashMap<String, Object>, Integer, HashMap<String, String>>{

	@Override
	protected HashMap<String, String> doInBackground(
			HashMap<String, Object>... params) {
		HttpConnectionHepler helper = new HttpConnectionHepler();
		HashMap<String, String> result = new HashMap<String, String>();
		
		String url = (String)params[0].get("url");
		String flag = (String)params[0].get("flag");
		List<NameValuePair> arg = (List<NameValuePair>)params[0].get("arg");
		if(flag.equals("apply")){
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
			param.add(new BasicNameValuePair("type", "1"));
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
			
				ToastUtil.showShortToast(JifenbaoActivity.this, msg);
			
		}else if(flag.equals("loading")){
			zhihubao_count.setText(result.get("alipay"));
			person_name.setText(result.get("realname"));
			phone_number.setText(result.get("mobile"));
			jifenbaonum.setText(result.get("balance").substring(0,result.get("balance").indexOf(".")));
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
