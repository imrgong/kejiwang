package com.cosji.bean;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.cosji.application.Base;

public class TbkItem implements Parcelable {
	/**
	 * 
	 */
	private String num_iid;
	private String seller_id;
	private String nick;
	private String title;
	private String price;
	private String volume;
	private String pic_url;
	private String item_url;
	private String shop_url;
	private String click_url;
	private String Collect;
	
	
	public TbkItem(Parcel p) {
		click_url = p.readString();
		item_url = p.readString();
		nick = p.readString();
		num_iid = p.readString();
		pic_url = p.readString();
		price = p.readString();
		seller_id = p.readString();
		shop_url = p.readString();
		title = p.readString();
		volume = p.readString();
	}
	public TbkItem() {
	}
	
	 public TbkItem(int num_iid) throws Exception{
	        // Instantiate an existing blog
	        List<Object> blogVals = Base.cosjiDb.getTbkItem(num_iid);

	        if (blogVals != null) {
	            this.num_iid = blogVals.get(0).toString();
	            this.seller_id = blogVals.get(1).toString();
	            this.nick = blogVals.get(2).toString();
	            this.title = blogVals.get(3).toString();
	            this.price = blogVals.get(4).toString();
	            this.volume = blogVals.get(5).toString();
	            this.pic_url = blogVals.get(6).toString();
	            this.item_url = blogVals.get(7).toString();
	            this.shop_url = blogVals.get(8).toString();
	            //these were accidentally set up to contain null values :(
	            if (blogVals.get(9) != null)
	            	this.click_url = blogVals.get(9).toString();
	            if (blogVals.get(10) != null)
	            	this.Collect = blogVals.get(10).toString();
	        } else {
	            throw new Exception();
	        }
	    }
	
	public String getNum_iid() {
		return num_iid;
	}
	public void setNum_iid(String num_iid) {
		this.num_iid = num_iid;
	}
	public String getSeller_id() {
		return seller_id;
	}
	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getPic_url() {
		return pic_url;
	}
	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}
	public String getItem_url() {
		return item_url;
	}
	public void setItem_url(String item_url) {
		this.item_url = item_url;
	}
	public String getShop_url() {
		return shop_url;
	}
	public void setShop_url(String shop_url) {
		this.shop_url = shop_url;
	}
	public String getClick_url() {
		return click_url;
	}
	public void setClick_url(String click_url) {
		this.click_url = click_url;
	}
	

	public String isCollect() {
		return Collect;
	}
	public void setCollect(String collect) {
		Collect = collect;
	}
	public String toString(){
		return "num_iid:"+num_iid+"seller_id"+seller_id+"nick:"+nick+"title:"+
	title+"price:"+price+"volume:"+volume+"pic_url:"+pic_url
				+"item_url:"+item_url+"shop_url:"+shop_url+"click_url:"+click_url;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(click_url);
		dest.writeString(item_url);
		dest.writeString(nick);
		dest.writeString(num_iid);
		dest.writeString(pic_url);
		dest.writeString(price);
		dest.writeString(seller_id);
		dest.writeString(shop_url);
		dest.writeString(title);
		dest.writeString(volume);
	}
	
	public static final Parcelable.Creator<TbkItem> CREATOR = new Creator<TbkItem>() {
		
		@Override
		public TbkItem[] newArray(int size) {
			// TODO Auto-generated method stub
			return new TbkItem[size];
		}
		
		@Override
		public TbkItem createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new TbkItem(source);
		}
	};
}
