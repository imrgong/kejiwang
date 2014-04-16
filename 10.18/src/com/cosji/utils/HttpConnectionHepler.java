package com.cosji.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class HttpConnectionHepler {
	public static int data_total;
	private static HashMap<String, String> CookieContiner = new HashMap<String, String>();
private HashMap<String, String> taogdtitlehash;



	public HashMap<String, String> getTaoGdTitleHash() {
	return taogdtitlehash;
}

public void setTaoGdTitleHash(HashMap<String, String>taogdtitlehash) {
	this.taogdtitlehash = taogdtitlehash;
}

	/**
	 * Login methed
	 * 
	 * @param url
	 * @return
	 */
	public HashMap<String, String> UserLogin(String url) {
		HashMap<String, String> cookie = new HashMap<String, String>();
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			// 保存服务器返回来的Cookie
			SaveCookies(response);
			HttpEntity entitiy = response.getEntity();
			if (entitiy != null) {
				String t = EntityUtils.toString(response.getEntity());
				String json = t.substring(2, t.length());
				String key;
				String value;
				JSONObject jsonhead = new JSONObject(json)
						.getJSONObject("head");
				@SuppressWarnings("unchecked")
				Iterator<String> headiter = jsonhead.keys();
				while (headiter.hasNext()) {
					key = headiter.next();
					value = (String) jsonhead.getString(key);
					cookie.put(key, value);
				}
				if (cookie.get("msg") != null
						&& cookie.get("msg").equals("success")) {
					JSONObject jsonbody = new JSONObject(json)
							.getJSONObject("body");
					@SuppressWarnings("unchecked")
					Iterator<String> keyiter = jsonbody.keys();
					while (keyiter.hasNext()) {
						key = keyiter.next();
						value = (String) jsonbody.getString(key);
						cookie.put(key, value);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cookie;
	}

	/**
	 * 保存Cookie
	 * 
	 * @param httpresponse
	 */
	private void SaveCookies(HttpResponse httpresponse) {
		Header[] headers = httpresponse.getHeaders("Set-Cookie");
		if (headers == null)
			return;
		for (int i = 0; i < headers.length; i++) {
			String cookie = headers[i].getValue();
			String[] cookievalues = cookie.split(";");
			for (int j = 0; j < cookievalues.length; j++) {
				String[] keyPair = cookievalues[j].split("=");
				String key = keyPair[0].trim();
				String value = keyPair.length > 1 ? keyPair[1].trim() : "";
				CookieContiner.put(key, value);
			}
		}
	}

	/**
	 * 增加Cookie
	 * 
	 * @param request
	 */

	public void AddCookies(HttpPost request) {
		StringBuilder sb = new StringBuilder();
		Iterator iter = CookieContiner.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = (Entry<String, String>) iter
					.next();
			String key = entry.getKey().toString();
			String val = entry.getValue().toString();
			sb.append(key);
			sb.append("=");
			sb.append(val);
			sb.append(";");
		}
		request.addHeader("cookie", sb.toString());
	}

	/**
	 * 用户登录以后，基本上所有的请求都是post方式，且统一返回map形式的数据
	 * 
	 * @param uri
	 * @param List
	 *            <NameValuePair> params
	 * @return
	 */
	public HashMap<String, String> sendPostRequest(String uri,
			List<NameValuePair> params) {
		HashMap<String, String> result = new HashMap<String, String>();
		HttpPost post = new HttpPost(uri);
		if(!uri.equals(Url.version())){//url为版本更新时，不需要添加cookie
			AddCookies(post);
		}
		try {
			if (params != null) {
				post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			}
			HttpResponse httpResponse = new DefaultHttpClient().execute(post);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 取出回应字串
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				String json = strResult.substring(2, strResult.length());
				JSONObject jsonhead = new JSONObject(json)
						.getJSONObject("head");
				String key;
				String value;
				Iterator<String> headiter = jsonhead.keys();
				while (headiter.hasNext()) {
					key = headiter.next();
					value = (String) jsonhead.getString(key);
					result.put(key, value);
				}
				;
				if (result.get("msg").equals("success")) {
					JSONObject jsonbody = new JSONObject(json)
							.getJSONObject("body");
					Iterator<String> keyiter = jsonbody.keys();
					while (keyiter.hasNext()) {
						key = keyiter.next();
						value = (String) jsonbody.getString(key);
						result.put(key, value);
					}
				}
			} else {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Debug
		System.out.println("返回结果："+result.toString());
		return result;
	}

	public static int getData_total() {
		return data_total;
	}

	public static void setData_total(int data_total) {
		HttpConnectionHepler.data_total = data_total;
	}

	/**
	 * 获取网络图片（图片下载）
	 */
	public Bitmap getimage(String params){
		 try {
			URL url = new URL(params);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream inputStream = conn.getInputStream();
			return BitmapFactory.decodeStream(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; 
	}
	/**
	 * 获取网络数据
	 * 
	 * @param uri
	 * @param params
	 * @return
	 */

	public List<HashMap<String, String>> sendPostRequestArray(String uri,
			List<NameValuePair> params) {
		List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> result = new HashMap<String, String>();
		HttpPost post = new HttpPost(uri);
		AddCookies(post);
		try {
			if (params != null) {
				post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			}
			HttpResponse httpResponse = new DefaultHttpClient().execute(post);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 取出回应字串
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				//Log.e("strResult", strResult);
				String json = strResult.substring(2, strResult.length());
				JSONObject jsonhead = new JSONObject(json)
						.getJSONObject("head");
				String key;
				String value;
				if (jsonhead.getString("msg").equals("success")) {
					JSONObject jsonbody = new JSONObject(json)
							.getJSONObject("body");
					JSONArray jsonarray = new JSONArray(
							(String) jsonbody.getString("record"));
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject dataItem = jsonarray.getJSONObject(i);
						Iterator<String> data_key = dataItem.keys();
						HashMap<String, String> MapItem = new HashMap<String, String>();
						while (data_key.hasNext()) {
							key = data_key.next();
							value = (String) dataItem.getString(key);
							MapItem.put(key, value);
						}
						data.add(MapItem);
					}
					// 获取总条数
					key = "total";
					value = jsonbody.getString("total");
					result.put(key, value);
					data.add(result);
				}
				return data;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return data;
	}

	/**
	 * 获取网络数据
	 * 
	 * @param uri
	 * @param params
	 * @return
	 */
	public List<HashMap<String, String>> sendPostReArray(Context context,
			String uri, List<NameValuePair> params, String cskey) {
		List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

		String strResult = null;
			try {
				HttpPost post = new HttpPost(uri);
				post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
				HttpResponse httpResponse = new DefaultHttpClient()
						.execute(post);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					strResult = EntityUtils.toString(httpResponse.getEntity());
					
					AnalysisJson(strResult, data);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

//		}
			//Debug
			System.out.println("返回数据:"+data.toString());
		return data;
	}

	public void AnalysisJson(String strResult,
			List<HashMap<String, String>> data) {
		String json = strResult.substring(2, strResult.length());
		JSONObject jsonhead;
		try {
			jsonhead = new JSONObject(json).getJSONObject("head");
           
			JSONObject jsonbody = new JSONObject(json).getJSONObject("body");
			@SuppressWarnings("unchecked")
			Iterator<String> keyiter = jsonbody.keys();
			@SuppressWarnings("unchecked")
			Iterator<String> headiter = jsonhead.keys();
			String key;
			String value;
			
			if (jsonhead.getString("msg").equals("success")) {
				int m = 0;
				JSONArray jsonarray = new JSONArray(
						(String) jsonbody.getString("record"));
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject dataItem = (JSONObject) jsonarray.opt(i);
					Iterator<String> data_key = dataItem.keys();
					HashMap<String, String> MapItem = new HashMap<String, String>();

					while (data_key.hasNext()) {
						key = data_key.next();
						value = (String) dataItem.getString(key);
						// ty add
						value = value.replace("'", "");
						MapItem.put(key, value);
			
					}
				
					data.add(MapItem);
                   
				}
				setData_total(Integer.parseInt(jsonbody.getString("total")
						.toString()));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<HashMap<String, String>> sendPostMallReArray(Context context,
			String uri, List<NameValuePair> params, String cskey) {
		List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

		String strResult = "";
		//SettingUtils.Stringset(context,cskey, null);

		if (!cskey.equals("0")) {
			//strResult = SettingUtils.Stringget(context, cskey, "");
		}

	
		if (!strResult.equals("")) {
			AnalysisJson1(context,strResult, data,cskey);
		} else {
			try {
				HttpPost post = new HttpPost(uri);
				post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
				HttpResponse httpResponse = new DefaultHttpClient()
						.execute(post);
				strResult = EntityUtils.toString(httpResponse.getEntity());
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					
					AnalysisJson1(context,strResult, data,cskey);
				}

			} catch (Exception e) {
				// TODO: handle exception
			}
	}
		System.out.println("商店的个数:"+data.size());
		return data;
	}
	
	public void AnalysisJson1(Context context,String strResult,
			List<HashMap<String, String>> data,String cskey) {
		try {
			String json = strResult.substring(2, strResult.length());
			JSONObject jsonhead = new JSONObject(json).getJSONObject("head");
			JSONArray jsonarray = new JSONObject(json).getJSONArray("body");
			
			String key;
			String value;
			
			if (jsonhead.getString("msg").equals("success")) {
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject dataItem = (JSONObject) jsonarray.opt(i);
					Iterator<String> data_key = dataItem.keys();
					HashMap<String, String> MapItem = new HashMap<String, String>();
					while (data_key.hasNext()) {
						key = data_key.next();
						value = (String) dataItem.getString(key);
						MapItem.put(key, value);
					}
					data.add(MapItem);
				}
				if (!cskey.equals("0")&&data.size()>0) {
					SettingUtils.Stringset(context, cskey, strResult);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	
	public List<HashMap<String, String>>sendPostTaoGdsTpReArray(Context context,
			String uri, List<NameValuePair> params) {
		List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	
		String strResult=SettingUtils.Stringget(context, Url.taogdstycache, "s");
	
		
	if (!strResult.equals("s")) {
		
			AnalysisJson2(strResult, data);
		

		} else {

			try {
				HttpPost post = new HttpPost(uri);
				post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
				HttpResponse httpResponse = new DefaultHttpClient()
						.execute(post);
				strResult = EntityUtils.toString(httpResponse.getEntity());
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					// 取出回应字串
						//SettingUtils.Stringset(context, Url.taogdstycache, strResult);
				
					AnalysisJson2(strResult, data);
									}
								
								} catch (Exception e) {
			
			}
	}
		return data;
	}

	public void AnalysisJson2(String strResult,
			List<HashMap<String, String>> data) {
		
		try {
						String json = strResult.substring(2, strResult.length());
						JSONObject jsonhead = new JSONObject(json).getJSONObject("head");
						JSONObject jsonbody = new JSONObject(json).getJSONObject("body");

						@SuppressWarnings("unchecked")
						Iterator<String> headiter = jsonhead.keys();
						Iterator<String> bodyiter = jsonbody.keys();
						String key;
						String value;
						
						if (jsonhead.getString("msg").equals("success")) {
						
							int a=0,i=0;
								HashMap<String, String> gdtyItem = new HashMap<String, String>();
								while (bodyiter.hasNext()) {
									key = bodyiter.next();
									value = (String) jsonbody.getString(key);
							  
									JSONArray tysarray = new JSONObject(value).getJSONArray("child");
								    gdtyItem.put("tyn"+i,new JSONObject(value).getString("name"));
								
							
								i++;
								        setTaoGdTitleHash(gdtyItem);
								       
								        for (int j = 0; j < tysarray.length(); j++) {
								        	
											JSONObject dataItem = (JSONObject) tysarray.opt(j);
											Iterator<String> data_key = dataItem.keys();
											HashMap<String, String> MapItem = new HashMap<String, String>();
											while (data_key.hasNext()) {
											key = data_key.next();
											value = (String) dataItem.getString(key);
										     MapItem.put(key, value);
										
                                             }
											
											data.add(MapItem);
											}  
								        a++;
								        }
								
										}
									
								
							
							} catch (Exception e) {
		
		}
	}
}
