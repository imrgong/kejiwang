package com.cosji.tbkapihelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cosji.bean.TbkItem;


public class JsonUtil {
	
	public List<TbkItem> jsonToObject(String json){
		List<TbkItem> items = new ArrayList<TbkItem>();
			try {
				JSONObject response = new JSONObject(json);
				JSONArray name = response.names();
					JSONObject data = response.getJSONObject((String)name.get(0));
						JSONObject tbk_items = data.getJSONObject("tbk_items");
						JSONArray itemArray = tbk_items.getJSONArray("tbk_item");
						
						for(int i = 0;i<itemArray.length();i++){
							JSONObject goods = itemArray.getJSONObject(i);
							Iterator<String> data_key = goods.keys();
							TbkItem tbkitem = new TbkItem();
							while(data_key.hasNext()){
								
								String key = data_key.next();
								if(key.equals("item_url"))
									tbkitem.setItem_url(goods.getString(key));
								else if(key.equals("nick"))
									tbkitem.setNick(goods.getString(key));
								else if(key.equals("num_iid"))
									tbkitem.setNum_iid(goods.getString(key));
								else if(key.equals("pic_url"))
									tbkitem.setPic_url(goods.getString(key));
								else if(key.equals("price"))
									tbkitem.setPrice(goods.getString(key));
								else if(key.equals("seller_id"))
									tbkitem.setSeller_id(goods.getString(key));
								else if(key.equals("shop_url"))
									tbkitem.setShop_url(goods.getString(key));
								else if(key.equals("title"))
									tbkitem.setTitle(goods.getString(key));
								else if(key.equals("volume"))
									tbkitem.setVolume(goods.getString(key));
								else if(key.equals("click_url"))
									tbkitem.setClick_url(goods.getString(key));
							}
							items.add(tbkitem);
						}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		return items;
	}
	
	public String getClickUrl(String result){
		try {
			JSONObject response = new JSONObject(result);
			JSONArray name = response.names();
			//	JSONObject data = response.getJSONObject((String)name.get(0));
			//		JSONArray tbk_items = data.names();
					JSONObject data = response.getJSONObject((String)name.get(0));
					JSONObject tbk_items = data.getJSONObject("tbk_items");
					JSONArray itemArray = tbk_items.getJSONArray("tbk_item");
					for(int i = 0;i<itemArray.length();i++){
						JSONObject goods = itemArray.getJSONObject(i);
						Iterator<String> data_key = goods.keys();
						while(data_key.hasNext()){
							
							String key = data_key.next();
							if(data_key.equals("click_url"))
								System.out.println("getClickUrl:"+goods.getString(key));
								return goods.getString(key);
						}
						return null;
					}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * 获取商品总条数
	 */
	public int getTotle(String result){
		int totle = 0;
		try {
			JSONObject response = new JSONObject(result);
			JSONArray name = response.names();
				JSONObject data = response.getJSONObject((String)name.get(0));
				    name = data.names();
				    for(int i=0;i<name.length();i++){
				    	String key = (String) name.get(i);
				    	if(key.equals("total_results")){
				    		totle = Integer.parseInt(data.getString(key));
				    	}
				    }
					
		} catch (JSONException e) {
			totle = 0;
			e.printStackTrace();
		}
		return totle;
	}
}
