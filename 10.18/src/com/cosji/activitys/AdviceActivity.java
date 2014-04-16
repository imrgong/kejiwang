package com.cosji.activitys;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cosji.adapter.AdviceAdapter;
import com.cosji.utils.HttpConnectionHepler;
import com.cosji.utils.ToastUtil;
import com.cosji.utils.Url;
import com.umeng.analytics.MobclickAgent;

public class AdviceActivity extends BaseActivity{
	EditText comEditText;
	ListView complaintcontents;
	AdviceAdapter adapter;
	LinearLayout bt_back;
	ArrayList<HashMap<String, Object>> data;
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
setContentView(R.layout.complaintinfo);
initView();

}

private void initView(){
TextView title_name=(TextView)findViewById(R.id.mine_secend_title);
bt_back=(LinearLayout)findViewById(R.id.web_back);
	complaintcontents=(ListView)findViewById(R.id.complaintcontents);
	title_name.setText("意见反馈");
 comEditText=(EditText)findViewById(R.id.advice_content);
Button bt=(Button)findViewById(R.id.complaintcontents_send);
//complaint_title.setText("意见反馈");
bt_back.setOnClickListener(lin);

bt.setOnClickListener(lin);
adapter=new AdviceAdapter(this,initGetData());
complaintcontents.setAdapter(adapter);
};
OnClickListener lin=new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.web_back:
			final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(bt_back.getWindowToken(), 0);
			finish();
			break;
		case R.id.complaintcontents_send:
			send();
		break;
		default:
			break;
		}
	}
};
/*
 * 使用数据库存储历史
 */
public ArrayList<HashMap<String, Object>> initGetData() {
	 data = new ArrayList<HashMap<String, Object>>();
	return data;
}
/*
 * 发送信息
 */
private void send()
{
	String contString = comEditText.getText().toString();
	if (contString.length() > 0)
	{
		String url = Url.message()+"send/";
		HashMap<String, Object> map = new HashMap<String, Object>();
		SendData(url, contString, "send");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		map.put("time", sdf.format(new Date(System.currentTimeMillis())));
		map.put("contents", contString);
		data.add(map);
		adapter.notifyDataSetChanged();
		complaintcontents.setSelection(complaintcontents.getCount() - 1);
		comEditText.setText(null);
	}
	}
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	MobclickAgent.onResume(AdviceActivity.this);
}
@Override
protected void onPause() {
	// TODO Auto-generated method stub
	super.onPause();
	MobclickAgent.onPause(AdviceActivity.this);
}
class mytask extends AsyncTask<HashMap<String, Object>, Integer, List<HashMap<String, String>>>{

	@Override
	protected List<HashMap<String, String>> doInBackground(
			HashMap<String, Object>... params) {
		HttpConnectionHepler helper = new HttpConnectionHepler();
		List<HashMap<String, String>> result = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> flagmap = new HashMap<String, String>();
		
		String url = (String)params[0].get("url");
		String flag = (String)params[0].get("flag");
		List<NameValuePair> arg = (List<NameValuePair>)params[0].get("arg");
		if(flag!=null&&flag.equals("send")){
			HashMap<String, String> temp1 = new HashMap<String, String>();
			HashMap<String, String> temp2 = new HashMap<String, String>();
			flagmap.put("flag", flag);
			temp1 = helper.sendPostRequest(url, arg);
			temp2.put("total", "0");
			result.add(temp1);
			result.add(temp2);
			result.add(flagmap);
			return result;
		}
		return null;
	}


	@Override
	protected void onPostExecute(List<HashMap<String, String>> result) {
		String flag = result.get(result.size()-1).get("flag");
		int total = Integer.valueOf( result.get(result.size()-2).get("total"));
		
		result.remove(result.size()-1);
		result.remove(result.size()-1);

		String msg = result.get(0).get("msg");
		if(msg!=null&&msg.equals("success")){
			ToastUtil.showShortToast(AdviceActivity.this, "感谢您提出的宝贵意见！");
		}
		super.onPostExecute(result);
	}


	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}
	
}

/*
 * 异步加载实现
 * @param url
 * @param page
 * @param num
 */
private void SendData(String url,String content,String flag){
	HashMap<String, Object> params = new HashMap<String, Object>();
	List<NameValuePair> arg =new ArrayList<NameValuePair>();
	arg.add(new BasicNameValuePair("content", content));
	params.put("flag", flag);
	params.put("url", url);
	params.put("arg", arg);
	mytask task = new mytask();
	task.execute(params);
}
}
