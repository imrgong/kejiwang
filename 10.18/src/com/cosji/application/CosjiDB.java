package com.cosji.application;

import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap.Config;
import android.net.ParseException;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.util.Base64;
import android.util.Log;

import com.baidu.cloudsdk.common.util.Utils;
import com.cosji.bean.TbkItem;

public class CosjiDB {

    private static final int DATABASE_VERSION = 20;
    public static final String PASSWORD_SECRET="cosji_DB";

    private static final String CREATE_TABLE_USERINFO = 
    		"create table if not exists userinfo ( id integer primary key autoincrement, "
            + "VIPgrade text, avater text, nickname text, shengyumoney text, AmassPoint text, "
    		+"jifenbaoPoint text, qiandaostatus boolean, message text, QQ text, email text, mobilephone text, zhifubao text);";
   
    private static final String SETTINGS_TABLE = "userinfo";
    private static final String TABLE_TBKITEM = "tbkitem";
    private static final String TABLE_ZHEKOU = "discount";
    private static final String TABLE_ORDER = "order";
    private static final String TABLE_ACOUNTDETILS = "messageanddetils";
    
    private static final String DATABASE_NAME = "keji_database";
  
    private static final String CREATE_TABLE_TBKITEM = 
    		"create table if not exists tbkitem ( id integer primary key autoincrement, "
            + "num_iid text, seller_id text, dateCreated date, nick text, title text, price text , "
            + "volume text default '', pic_url text default '', item_url text, shop_url text, "
            + "click_url text default '', collect text default 'false');";

    private static final String CREATE_TABLE_ORDER = 
    		"create table if not exists order ( id integer primary key autoincrement, "
            + " time text, order_number text, title text, price text, rebate text , "
            + "number text default '1', pic_url text default '', type text default '', belong text, FOREIGN KEY(belong) REFERENCES userinfo(nickname) ); ";

    private static final String CREATE_TABLE_ACOUNTDETILS = 
    		"create table if not exists messageanddetils ( id integer primary key autoincrement, "
            + "time text, content text, event text default '', "
            + "type text default '', belong text, FOREIGN KEY(belong) REFERENCES userinfo(nickname) ); ";
    
    private static final String CREATE_TABLE_DISCOUNT = 
    		"create table if not exists discount ( id integer primary key autoincrement, "
            + "time text, order_number text, title text, price text, rebate text , "
            + "number text default '1', pic_url text default '', type text default 'taobao', belong text, FOREIGN KEY(belong) REFERENCES userinfo(nickname) ); ";
    
    private SQLiteDatabase db;

    private Context context;
    private SimpleDateFormat sdf;

    public CosjiDB(Context ctx) {
        this.context = ctx;
        sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
        } catch (SQLiteException e) {
            db = null;
            return;
        }

        // Create tables if they don't exist
        db.execSQL(CREATE_TABLE_USERINFO);
        db.execSQL(CREATE_TABLE_TBKITEM);
      //  db.execSQL(CREATE_TABLE_ORDER);
        db.execSQL(CREATE_TABLE_ACOUNTDETILS);
        db.execSQL(CREATE_TABLE_DISCOUNT);
        // Update tables for new installs and app updates
        try {
            int currentVersion = db.getVersion();
            switch (currentVersion) {
                case 0:
                    currentVersion++;
            }
            db.setVersion(DATABASE_VERSION);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * 获取用户信息
     */
    public List<Map<String, Object>> getAccounts(String nickname) {

        if (db == null)
            return new Vector<Map<String, Object>>();
        
        Cursor c = db.query(CREATE_TABLE_USERINFO, new String[] { "VIPgrade",
                "shengyumoney", "AmassPoint", "jifenbaoPoint", "qiandaostatus", "message" ,"avater" ,"QQ" ,"email" ,"mobilephone" ,"zhifubao"},
                " ' nickname =' "+nickname, null, null,
                null, null);

        int numRows = c.getCount();
        c.moveToFirst();
        List<Map<String, Object>> accounts = new Vector<Map<String, Object>>();
        for (int i = 0; i < numRows; i++) {

            String VIPgrade = c.getString(0);
            String shengyumoney = c.getString(1);
            String AmassPoint = c.getString(2);
            String jifenbaoPoint = c.getString(3);
            String qiandaostatus = c.getString(4);
            String message = c.getString(5);
            String avater = c.getString(6);
            String QQ = c.getString(7);
            String email = c.getString(8);
            String mobilephone = c.getString(9);
            String zhifubao = c.getString(10);
                Map<String, Object> thisHash = new HashMap<String, Object>();
                thisHash.put("VIPgrade", VIPgrade);
                thisHash.put("shengyumoney", shengyumoney);
                thisHash.put("AmassPoint", AmassPoint);
                thisHash.put("jifenbaoPoint", jifenbaoPoint);
                thisHash.put("qiandaostatus", qiandaostatus);
                thisHash.put("message", message);
                thisHash.put("avater", avater);
                thisHash.put("QQ", QQ);
                thisHash.put("email", email);
                thisHash.put("mobilephone", mobilephone);
                thisHash.put("zhifubao", zhifubao);
                accounts.add(thisHash);
            c.moveToNext();
        }
        c.close();
        return accounts;
    }
    /*
     * 保存商品
     */
    public boolean saveGoods(TbkItem tbkItem) {
        ContentValues values = new ContentValues();
        values.put("num_iid", tbkItem.getNum_iid());
        values.put("seller_id", tbkItem.getSeller_id());
        values.put("detaCreated",sdf.format(new java.util.Date()));
        values.put("nick", tbkItem.getNick());
        values.put("title", tbkItem.getTitle());
        values.put("price", tbkItem.getPrice());
        values.put("volume", tbkItem.getVolume());
        values.put("pic_url", tbkItem.getPic_url());
        values.put("item_url", tbkItem.getItem_url());
        values.put("shop_url", tbkItem.getShop_url());
        values.put("click_url", tbkItem.getClick_url());
        values.put("collect", tbkItem.isCollect());

        return db.update(TABLE_TBKITEM, values, "num_iid=" + Integer.parseInt(tbkItem.getNum_iid()),
                null) > 0;

    }
    /*
     * 更新商品
     */
    public boolean updateGoods(String num_iid,String click_url) {
        ContentValues values = new ContentValues();
        values.put("click_url", click_url);
        return db.update(TABLE_TBKITEM, values, "num_iid=" + num_iid,
                null) > 0;

    }
    /*
     * 通过关键字取商品
     * @param keywords
     */
    public List<Object> getTbkItem(int num_iid) {

        Cursor c = db.query(SETTINGS_TABLE, new String[] { "num_iid", "seller_id",
                "nick", "title", "price",
                "volume", "pic_url", "item_url" , 
                "shop_url","collect", "click_url","dateCreated", }, 
                 " num_iid = "+num_iid, null, null, null, null);

        int numRows = c.getCount();
        c.moveToFirst();
        int day = getDateDays(sdf.format(new java.util.Date()), c.getString(11));
        List<Object> returnVector = new Vector<Object>();
        if (numRows>0&&day<2) {
                returnVector.add(c.getString(0));
                returnVector.add(c.getString(1));
                returnVector.add(c.getString(2));
                returnVector.add(c.getString(3));
                returnVector.add(c.getString(4));
                returnVector.add(c.getString(5));
                returnVector.add(c.getString(6));
                returnVector.add(c.getString(7));
                returnVector.add(c.getString(8));
                returnVector.add(c.getString(9));
                if (c.getString(10) == null) {
                    returnVector.add("");
                } else {
                    returnVector.add(c.getString(10));
                }
        } else {
            returnVector = null;
        }
        c.close();

        return returnVector;
    }
    /*
     * 通过关键字获取商品信息,(获取的是近两天的存储的商品信息)
     */
    public List<Map<String, Object>> loadTbkItem(String keywords) {

        List<Map<String, Object>> returnVector = new Vector<Map<String, Object>>();
        Cursor c;
            c = db.query(TABLE_TBKITEM, new String[] { "num_iid", "seller_id",
                    "nick", "title", "price",
                    "volume", "pic_url" ,"item_url","shop_url","Collect","click_url","deteCreated" },
                    " title like " + "'%'"+keywords+"'%'",
                     null,null, null, null);
        int numRows = c.getCount();
        c.moveToFirst();
        for (int i = 0; i < numRows; ++i) {
        	int day = getDateDays(sdf.format(new java.util.Date()), c.getString(11));
            if (c.getString(0) != null&&day<2) {
                Map<String, Object> returnHash = new HashMap<String, Object>();
                returnHash.put("num_iid", c.getString(0));
                returnHash.put("seller_id", c.getString(1));
                returnHash.put("nick", c.getString(2));
                returnHash.put("title", c.getInt(3));
                returnHash.put("price", c.getLong(4));
                returnHash.put("volume", c.getString(5));
                returnHash.put("pic_url", c.getString(6));
                returnHash.put("item_url", c.getString(7));
                returnHash.put("shop_url", c.getString(8));
                returnHash.put("Collect", c.getString(9));
                if (c.getString(10) != null)
                returnHash.put("click_url", c.getString(10));
                else
                returnHash.put("click_url", "");
                returnVector.add(i, returnHash);
            }
            c.moveToNext();
        }
        c.close();
        if (numRows == 0) {
            returnVector = null;
        }
        return returnVector;
    }


//    public int updatePost(Post post, int blogID) {
//        int success = 0;
//        if (post != null) {
//
//            ContentValues values = new ContentValues();
//            values.put("blogID", blogID);
//            values.put("title", post.getTitle());
//            values.put("date_created_gmt", post.getDate_created_gmt());
//            values.put("description", post.getDescription());
//            if (post.getMt_text_more() != null)
//                values.put("mt_text_more", post.getMt_text_more());
//            values.put("uploaded", post.isUploaded());
//
//            JSONArray categoriesJsonArray = post.getJSONCategories();
//            if (categoriesJsonArray != null) {
//                values.put("categories", categoriesJsonArray.toString());
//            }
//
//            values.put("localDraft", post.isLocalDraft());
//            values.put("mediaPaths", post.getMediaPaths());
//            values.put("mt_keywords", post.getMt_keywords());
//            values.put("wp_password", post.getWP_password());
//            values.put("post_status", post.getPost_status());
//            values.put("isPage", post.isPage());
//            values.put("wp_post_format", post.getWP_post_format());
//            values.put("isLocalChange", post.isLocalChange());
//            values.put("mt_excerpt", post.getMt_excerpt());
//
//            int pageInt = 0;
//            if (post.isPage())
//                pageInt = 1;
//
//            success = db.update(POSTS_TABLE, values,
//                    "blogID=" + post.getBlogID() + " AND id=" + post.getId()
//                            + " AND isPage=" + pageInt, null);
//
//        }
//        return (success);
//    }
//
//    public List<Map<String, Object>> loadUploadedPosts(int blogID, boolean loadPages) {
//
//        List<Map<String, Object>> returnVector = new Vector<Map<String, Object>>();
//        Cursor c;
//        if (loadPages)
//            c = db.query(POSTS_TABLE,
//                    new String[] { "id", "blogID", "postid", "title",
//                            "date_created_gmt", "dateCreated", "post_status","categories","custom_fields_image" },
//                    "blogID=" + blogID + " AND localDraft != 1 AND isPage=1",
//                    null, null, null, null);
//        else
//            c = db.query(POSTS_TABLE,
//                    new String[] { "id", "blogID", "postid", "title",
//                            "date_created_gmt", "dateCreated", "post_status","categories","custom_fields_image" },
//                    "blogID=" + blogID + " AND localDraft != 1 AND isPage=0",
//                    null, null, null, null);
//
//        int numRows = c.getCount();
//        c.moveToFirst();
//
//        for (int i = 0; i < numRows; ++i) {
//            if (c.getString(0) != null) {
//                Map<String, Object> returnHash = new HashMap<String, Object>();
//                returnHash.put("id", c.getInt(0));
//                returnHash.put("blogID", c.getString(1));
//                returnHash.put("postID", c.getString(2));
//                returnHash.put("title", c.getString(3));
//                returnHash.put("date_created_gmt", c.getLong(4));
//                returnHash.put("dateCreated", c.getLong(5));
//                returnHash.put("post_status", c.getString(6));
//                returnHash.put("categories", c.getString(7));
//                returnHash.put("custom_fields_image", c.getString(8));
//                returnVector.add(i, returnHash);
//            }
//            c.moveToNext();
//        }
//        c.close();
//
//        if (numRows == 0) {
//            returnVector = null;
//        }
//
//        return returnVector;
//    }
//
//    public void deleteUploadedPosts(int blogID, boolean isPage) {
//
//        if (isPage)
//            db.delete(POSTS_TABLE, "blogID=" + blogID
//                    + " AND localDraft != 1 AND isPage=1", null);
//        else
//            db.delete(POSTS_TABLE, "blogID=" + blogID
//                    + " AND localDraft != 1 AND isPage=0", null);
//
//    }
//
//    public List<Object> loadPost(int blogID, boolean isPage, long id) {
//        List<Object> values = null;
//
//        int pageInt = 0;
//        if (isPage)
//            pageInt = 1;
//        Cursor c = db.query(POSTS_TABLE, null, "blogID=" + blogID + " AND id="
//                + id + " AND isPage=" + pageInt, null, null, null, null);
//
//        if (c.getCount() > 0) {
//            c.moveToFirst();
//            if (c.getString(0) != null) {
//                values = new Vector<Object>();
//                values.add(c.getLong(0));
//                values.add(c.getString(1));
//                values.add(c.getString(2));
//                values.add(c.getString(3));
//                values.add(c.getLong(4));
//                values.add(c.getLong(5));
//                values.add(c.getString(6));
//                values.add(c.getString(7));
//                values.add(c.getString(8));
//                values.add(c.getString(9));
//                values.add(c.getInt(10));
//                values.add(c.getInt(11));
//                values.add(c.getString(12));
//                values.add(c.getString(13));
//                values.add(c.getString(14));
//                values.add(c.getString(15));
//                values.add(c.getString(16));
//                values.add(c.getString(17));
//                values.add(c.getString(18));
//                values.add(c.getString(19));
//                values.add(c.getString(20));
//                values.add(c.getString(21));
//                values.add(c.getString(22));
//                values.add(c.getString(23));
//                values.add(c.getDouble(24));
//                values.add(c.getDouble(25));
//                values.add(c.getInt(26));
//                values.add(c.getInt(27));
//                values.add(c.getInt(28));
//                values.add(c.getInt(29));
//                values.add(c.getInt(30));
//                values.add(c.getString(31));
//            }
//        }
//        c.close();
//
//        return values;
//    }
//
//    public List<Map<String, Object>> loadComments(int blogID) {
//
//        List<Map<String, Object>> returnVector = new Vector<Map<String, Object>>();
//        Cursor c = db.query(COMMENTS_TABLE,
//                new String[] { "blogID", "postID", "iCommentID", "author",
//                        "comment", "commentDate", "commentDateFormatted",
//                        "status", "url", "email", "postTitle" }, "blogID="
//                        + blogID, null, null, null, null);
//
//        int numRows = c.getCount();
//        c.moveToFirst();
//
//        for (int i = 0; i < numRows; i++) {
//            if (c.getString(0) != null) {
//                Map<String, Object> returnHash = new HashMap<String, Object>();
//                returnHash.put("blogID", c.getString(0));
//                returnHash.put("postID", c.getInt(1));
//                returnHash.put("commentID", c.getInt(2));
//                returnHash.put("author", c.getString(3));
//                returnHash.put("comment", c.getString(4));
//                returnHash.put("commentDate", c.getString(5));
//                returnHash.put("commentDateFormatted", c.getString(6));
//                returnHash.put("status", c.getString(7));
//                returnHash.put("url", c.getString(8));
//                returnHash.put("email", c.getString(9));
//                returnHash.put("postTitle", c.getString(10));
//                returnVector.add(i, returnHash);
//            }
//            c.moveToNext();
//        }
//        c.close();
//
//        if (numRows == 0) {
//            returnVector = null;
//        }
//
//        return returnVector;
//    }
//
//    public boolean saveComments(List<?> commentValues) {
//        boolean returnValue = false;
//
//        Map<?, ?> firstHash = (Map<?, ?>) commentValues.get(0);
//        String blogID = firstHash.get("blogID").toString();
//        // delete existing values, if user hit refresh button
//
//        try {
//            db.delete(COMMENTS_TABLE, "blogID=" + blogID, null);
//        } catch (Exception e) {
//
//            return false;
//        }
//
//        for (int i = 0; i < commentValues.size(); i++) {
//            try {
//                ContentValues values = new ContentValues();
//                Map<?, ?> thisHash = (Map<?, ?>) commentValues.get(i);
//                values.put("blogID", thisHash.get("blogID").toString());
//                values.put("postID", thisHash.get("postID").toString());
//                values.put("iCommentID", thisHash.get("commentID").toString());
//                values.put("author", thisHash.get("author").toString());
//                values.put("comment", thisHash.get("comment").toString());
//                values.put("commentDate", thisHash.get("commentDate").toString());
//                values.put("commentDateFormatted",
//                        thisHash.get("commentDateFormatted").toString());
//                values.put("status", thisHash.get("status").toString());
//                values.put("url", thisHash.get("url").toString());
//                values.put("email", thisHash.get("email").toString());
//                values.put("postTitle", thisHash.get("postTitle").toString());
//                synchronized (this) {
//                    try {
//                        returnValue = db.insert(COMMENTS_TABLE, null, values) > 0;
//                    } catch (Exception e) {
//
//                        return false;
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return (returnValue);
//
//    }
//
//    public void updateComment(int blogID, int id, Map<?, ?> commentHash) {
//
//        ContentValues values = new ContentValues();
//        values.put("author", commentHash.get("author").toString());
//        values.put("comment", commentHash.get("comment").toString());
//        values.put("status", commentHash.get("status").toString());
//        values.put("url", commentHash.get("url").toString());
//        values.put("email", commentHash.get("email").toString());
//
//        synchronized (this) {
//            db.update(COMMENTS_TABLE, values, "blogID=" + blogID
//                    + " AND iCommentID=" + id, null);
//        }
//
//    }
//
//    public void updateCommentStatus(int blogID, int id, String newStatus) {
//
//        ContentValues values = new ContentValues();
//        values.put("status", newStatus);
//        synchronized (this) {
//            db.update(COMMENTS_TABLE, values, "blogID=" + blogID
//                    + " AND iCommentID=" + id, null);
//        }
//
//    }
//
//    public void clearPosts(String blogID) {
//
//        // delete existing values
//        db.delete(POSTS_TABLE, "blogID=" + blogID, null);
//
//    }
//
//    // Categories
//    public boolean insertCategory(int id, int wp_id, int parent_id, String category_name) {
//
//        ContentValues values = new ContentValues();
//        values.put("blog_id", id);
//        values.put("wp_id", wp_id);
//        values.put("category_name", category_name.toString());
//        values.put("parent_id", parent_id);
//        boolean returnValue = false;
//        synchronized (this) {
//            returnValue = db.insert(CATEGORIES_TABLE, null, values) > 0;
//        }
//
//        return (returnValue);
//    }
//
//    public List<String> loadCategories(int id) {
//
//        Cursor c = db.query(CATEGORIES_TABLE, new String[] { "id", "wp_id",
//                "category_name" }, "blog_id=" + id, null, null, null, null);
//        int numRows = c.getCount();
//        c.moveToFirst();
//        List<String> returnVector = new Vector<String>();
//        for (int i = 0; i < numRows; ++i) {
//            String category_name = c.getString(2);
//            if (category_name != null) {
//                returnVector.add(category_name);
//            }
//            c.moveToNext();
//        }
//        c.close();
//
//        return returnVector;
//    }
//
//    public int getCategoryId(int id, String category) {
//        Cursor c = db.query(CATEGORIES_TABLE, new String[] { "wp_id" },
//                "category_name=? AND blog_id=?", new String[] {category, String.valueOf(id)},
//                null, null, null);
//        if (c.getCount() == 0)
//            return 0;
//        c.moveToFirst();
//        int categoryID = 0;
//        categoryID = c.getInt(0);
//
//        c.close();
//
//        return categoryID;
//    }
//
//    public int getCategoryParentId(int id, String category) {
//        Cursor c = db.query(CATEGORIES_TABLE, new String[] { "parent_id" },
//                "category_name=? AND blog_id=?", new String[] {category, String.valueOf(id)},
//                null, null, null);
//        if (c.getCount() == 0)
//            return -1;
//        c.moveToFirst();
//        int categoryParentID = c.getInt(0);
//
//        c.close();
//
//        return categoryParentID;
//    }
//
//    public void clearCategories(int id) {
//
//        // clear out the table since we are refreshing the whole enchilada
//        db.delete(CATEGORIES_TABLE, "blog_id=" + id, null);
//
//    }
//
//    public boolean addQuickPressShortcut(int accountId, String name) {
//
//        ContentValues values = new ContentValues();
//        values.put("accountId", accountId);
//        values.put("name", name);
//        boolean returnValue = false;
//        synchronized (this) {
//            returnValue = db.insert(QUICKPRESS_SHORTCUTS_TABLE, null, values) > 0;
//        }
//
//        return (returnValue);
//    }
//
//    public List<Map<String, Object>> getQuickPressShortcuts(int accountId) {
//
//        Cursor c = db.query(QUICKPRESS_SHORTCUTS_TABLE, new String[] { "id",
//                "accountId", "name" }, "accountId = " + accountId, null, null,
//                null, null);
//        String id, name;
//        int numRows = c.getCount();
//        c.moveToFirst();
//        List<Map<String, Object>> accounts = new Vector<Map<String, Object>>();
//        for (int i = 0; i < numRows; i++) {
//
//            id = c.getString(0);
//            name = c.getString(2);
//            if (id != null) {
//                Map<String, Object> thisHash = new HashMap<String, Object>();
//
//                thisHash.put("id", id);
//                thisHash.put("name", name);
//                accounts.add(thisHash);
//            }
//            c.moveToNext();
//        }
//        c.close();
//
//        return accounts;
//    }
//
//    public boolean deleteQuickPressShortcut(String id) {
//
//        int rowsAffected = db.delete(QUICKPRESS_SHORTCUTS_TABLE, "id=" + id,
//                null);
//
//        boolean returnValue = false;
//        if (rowsAffected > 0) {
//            returnValue = true;
//        }
//
//        return (returnValue);
//    }
//
//    public static String encryptPassword(String clearText) {
//        try {
//            DESKeySpec keySpec = new DESKeySpec(
//                    PASSWORD_SECRET.getBytes("UTF-8"));
//            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
//            SecretKey key = keyFactory.generateSecret(keySpec);
//
//            Cipher cipher = Cipher.getInstance("DES");
//            cipher.init(Cipher.ENCRYPT_MODE, key);
//            String encrypedPwd = Base64.encodeToString(cipher.doFinal(clearText
//                    .getBytes("UTF-8")), Base64.DEFAULT);
//            return encrypedPwd;
//        } catch (Exception e) {
//        }
//        return clearText;
//    }
//
//    /*
//     * encryption-�����㷨
//     */
    public static String decryptPassword(String encryptedPwd) {
        try {
            DESKeySpec keySpec = new DESKeySpec(
                    PASSWORD_SECRET.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] encryptedWithoutB64 = Base64.decode(encryptedPwd, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainTextPwdBytes = cipher.doFinal(encryptedWithoutB64);
            return new String(plainTextPwdBytes);
        } catch (Exception e) {
        }
        return encryptedPwd;
    }
//
//    private void migratePasswords() {
//
//        Cursor c = db.query(SETTINGS_TABLE, new String[] { "id", "password",
//                "httppassword", "dotcom_password" }, null, null, null, null,
//                null);
//        int numRows = c.getCount();
//        c.moveToFirst();
//
//        for (int i = 0; i < numRows; i++) {
//            ContentValues values = new ContentValues();
//
//            if (c.getString(1) != null) {
//                values.put("password", encryptPassword(c.getString(1)));
//            }
//            if (c.getString(2) != null) {
//                values.put("httppassword", encryptPassword(c.getString(2)));
//            }
//            if (c.getString(3) != null) {
//                values.put("dotcom_password", encryptPassword(c.getString(3)));
//            }
//
//            db.update(SETTINGS_TABLE, values, "id=" + c.getInt(0), null);
//
//            c.moveToNext();
//        }
//        c.close();
//    }
//
//    public int getUnmoderatedCommentCount(int blogID) {
//        int commentCount = 0;
//
//        Cursor c = db
//                .rawQuery(
//                        "select count(*) from comments where blogID=? AND status='hold'",
//                        new String[] { String.valueOf(blogID) });
//        int numRows = c.getCount();
//        c.moveToFirst();
//
//        if (numRows > 0) {
//            commentCount = c.getInt(0);
//        }
//
//        c.close();
//
//        return commentCount;
//    }
//
//    public void saveMediaFile(MediaFile mf) {
//        
//        ContentValues values = new ContentValues();
//        values.put("postID", mf.getPostID());
//        values.put("filePath", mf.getFilePath());
//        values.put("fileName", mf.getFileName());
//        values.put("title", mf.getTitle());
//        values.put("description", mf.getDescription());
//        values.put("caption", mf.getCaption());
//        values.put("horizontalAlignment", mf.getHorizontalAlignment());
//        values.put("width", mf.getWidth());
//        values.put("height", mf.getHeight());
//        values.put("mimeType", mf.getMIMEType());
//        values.put("featured", mf.isFeatured());
//        values.put("isVideo", mf.isVideo());
//        values.put("isFeaturedInPost", mf.isFeaturedInPost());
//        values.put("fileURL", mf.getFileURL());
//        values.put("thumbnailURL", mf.getThumbnailURL());
//        values.put("mediaId", mf.getMediaId());
//        values.put("blogId", mf.getBlogId());
//        values.put("date_created_gmt", mf.getDateCreatedGMT());
//        if (mf.getUploadState() != null)
//            values.put("uploadState", mf.getUploadState());
//        else
//            values.putNull("uploadState");
//
//        synchronized (this) {
//            int result = 0;
//            boolean isMarkedForDelete = false;
//            if (mf.getMediaId() != null) {
//                Cursor cursor = db.rawQuery("SELECT uploadState FROM " + MEDIA_TABLE + " WHERE mediaId=?", new String[] { mf.getMediaId() });
//                if (cursor != null && cursor.moveToFirst()) {
//                    isMarkedForDelete = "delete".equals(cursor.getString(0));
//                    cursor.close();
//                }
//                
//                if (!isMarkedForDelete)
//                    result = db.update(MEDIA_TABLE, values, "blogId=? AND mediaId=?", new String[]{ mf.getBlogId(), mf.getMediaId()});
//            }
//            
//            if (result == 0 && !isMarkedForDelete)
//                db.insert(MEDIA_TABLE, null, values);
//        }
//
//    }
//
//    public MediaFile[] getMediaFilesForPost(Post p) {
//
//        Cursor c = db.query(MEDIA_TABLE, null, "postID=" + p.getId(), null,
//                null, null, null);
//        int numRows = c.getCount();
//        c.moveToFirst();
//        MediaFile[] mediaFiles = new MediaFile[numRows];
//        for (int i = 0; i < numRows; i++) {
//
//            MediaFile mf = new MediaFile();
//            mf.setPostID(c.getInt(1));
//            mf.setFilePath(c.getString(2));
//            mf.setFileName(c.getString(3));
//            mf.setTitle(c.getString(4));
//            mf.setDescription(c.getString(5));
//            mf.setCaption(c.getString(6));
//            mf.setHorizontalAlignment(c.getInt(7));
//            mf.setWidth(c.getInt(8));
//            mf.setHeight(c.getInt(9));
//            mf.setMIMEType(c.getString(10));
//            mf.setFeatured(c.getInt(11) > 0);
//            mf.setVideo(c.getInt(12) > 0);
//            mf.setFeaturedInPost(c.getInt(13) > 0);
//            mf.setFileURL(c.getString(14));
//            mf.setThumbnailURL(c.getString(15));
//            mf.setMediaId(c.getString(16));
//            mf.setBlogId(c.getString(17));
//            mf.setDateCreatedGMT(c.getLong(18));
//            mf.setUploadState(c.getString(19));
//            mediaFiles[i] = mf;
//            c.moveToNext();
//        }
//        c.close();
//
//        return mediaFiles;
//    }
//    
//    /** For a given blogId, get the first media files **/
//    public Cursor getFirstMediaFileForBlog(String blogId) {
//        return db.rawQuery("SELECT id as _id, * FROM " + MEDIA_TABLE + " WHERE blogId=? AND " 
//                + "(uploadState IS NULL OR uploadState IN ('uploaded', 'queued', 'failed', 'uploading')) ORDER BY (uploadState=?) DESC, date_created_gmt DESC LIMIT 1", new String[] { blogId, "uploading" });
//    }
//    
//    /** For a given blogId, get all the media files **/
//    public Cursor getMediaFilesForBlog(String blogId) {
//        return db.rawQuery("SELECT id as _id, * FROM " + MEDIA_TABLE + " WHERE blogId=? AND "
//                + "(uploadState IS NULL OR uploadState IN ('uploaded', 'queued', 'failed', 'uploading')) ORDER BY (uploadState=?) DESC, date_created_gmt DESC", new String[] { blogId, "uploading" });
//    }
//
//    /** For a given blogId, get all the media files with searchTerm **/
//    public Cursor getMediaFilesForBlog(String blogId, String searchTerm) {
//        // Currently on WordPress.com, the media search engine only searches the title. 
//        // We'll match this.
//        
//        String term = searchTerm.toLowerCase(Locale.getDefault());
//        return db.rawQuery("SELECT id as _id, * FROM " + MEDIA_TABLE + " WHERE blogId=? AND title LIKE ? AND (uploadState IS NULL OR uploadState ='uploaded') ORDER BY (uploadState=?) DESC, date_created_gmt DESC", new String[] { blogId, "%" + term + "%", "uploading" });
//    }
//    
//    /** For a given blogId, get the media file with the given media_id **/
//    public Cursor getMediaFile(String blogId, String mediaId) {
//        return db.rawQuery("SELECT * FROM " + MEDIA_TABLE + " WHERE blogId=? AND mediaId=?", new String[] { blogId, mediaId });
//    }
//    
//    public int getMediaCountAll(String blogId) {
//        Cursor cursor = getMediaFilesForBlog(blogId);
//        int count = cursor.getCount();
//        cursor.close();
//        return count;
//    }
//
//
//    public Cursor getMediaImagesForBlog(String blogId) {
//        return db.rawQuery("SELECT id as _id, * FROM " + MEDIA_TABLE + " WHERE blogId=? AND "
//                + "(uploadState IS NULL OR uploadState IN ('uploaded', 'queued', 'failed', 'uploading')) AND mimeType LIKE ? ORDER BY (uploadState=?) DESC, date_created_gmt DESC", new String[] { blogId, "image%", "uploading" });
//    }
//    
//    /** Ids in the filteredIds will not be selected **/
//    public Cursor getMediaImagesForBlog(String blogId, ArrayList<String> filteredIds) {
//        
//        String mediaIdsStr = "";
//        
//        if (filteredIds != null && filteredIds.size() > 0) {
//            mediaIdsStr = "AND mediaId NOT IN (";
//            for (String mediaId : filteredIds) {
//                mediaIdsStr += "'" + mediaId + "',";
//            }
//            mediaIdsStr = mediaIdsStr.subSequence(0, mediaIdsStr.length() - 1) + ")";
//        }
//        
//        return db.rawQuery("SELECT id as _id, * FROM " + MEDIA_TABLE + " WHERE blogId=? AND "
//                + "(uploadState IS NULL OR uploadState IN ('uploaded', 'queued', 'failed', 'uploading')) AND mimeType LIKE ? " + mediaIdsStr + " ORDER BY (uploadState=?) DESC, date_created_gmt DESC", new String[] { blogId, "image%", "uploading" });
//    }
//
//    public int getMediaCountImages(String blogId) {
//        return getMediaImagesForBlog(blogId).getCount();
//    }
//
//    public Cursor getMediaUnattachedForBlog(String blogId) {
//        return db.rawQuery("SELECT id as _id, * FROM " + MEDIA_TABLE + " WHERE blogId=? AND " +
//                "(uploadState IS NULL OR uploadState IN ('uploaded', 'queued', 'failed', 'uploading')) AND postId=0 ORDER BY (uploadState=?) DESC, date_created_gmt DESC", new String[] { blogId, "uploading" });
//    }
//    
//    public int getMediaCountUnattached(String blogId) {
//        return getMediaUnattachedForBlog(blogId).getCount();
//    }
//    
//    public Cursor getMediaFilesForBlog(String blogId, long startDate, long endDate) {
//        return db.rawQuery("SELECT id as _id, * FROM " + MEDIA_TABLE + " WHERE blogId=? AND (uploadState IS NULL OR uploadState ='uploaded') AND (date_created_gmt >= ? AND date_created_gmt <= ?) ", new String[] { blogId , String.valueOf(startDate), String.valueOf(endDate) });
//    }
//    
//    /** For a given blogId, get all the media files for upload **/
//    public Cursor getMediaFilesForUpload(String blogId) {
//        return db.rawQuery("SELECT id as _id, * FROM " + MEDIA_TABLE + " WHERE blogId=? AND uploadState IN ('uploaded', 'queued', 'failed', 'uploading') ORDER BY date_created_gmt ASC", new String[] { blogId });
//    }
//    
//    public Cursor getMediaFiles(String blogId, ArrayList<String> mediaIds) {
//        
//        if (mediaIds == null || mediaIds.size() == 0)
//            return null;
//        
//        String mediaIdsStr = "(";
//        for (String mediaId : mediaIds) {
//            mediaIdsStr += "'" + mediaId + "',";
//        }
//        mediaIdsStr = mediaIdsStr.subSequence(0, mediaIdsStr.length() - 1) + ")";
//        
//        return db.rawQuery("SELECT id as _id, * FROM " + MEDIA_TABLE + " WHERE blogId=? AND mediaId IN " + mediaIdsStr, new String[] { blogId });
//    }
//    
//    public boolean deleteMediaFile(MediaFile mf) {
//
//        boolean returnValue = false;
//
//        int result = 0;
//        result = db.delete(MEDIA_TABLE, "blogId='" + mf.getBlogId() + "' AND id=" + mf.getId(), null);
//
//        if (result == 1) {
//            returnValue = true;
//        }
//
//        return returnValue;
//    }
//
//    public MediaFile getMediaFile(String src, Post post) {
//
//        Cursor c = db.query(MEDIA_TABLE, null, "postID=" + post.getId()
//                + " AND filePath='" + src + "'", null, null, null, null);
//        int numRows = c.getCount();
//        c.moveToFirst();
//        MediaFile mf = new MediaFile();
//        if (numRows == 1) {
//            mf.setPostID(c.getInt(1));
//            mf.setFilePath(c.getString(2));
//            mf.setFileName(c.getString(3));
//            mf.setTitle(c.getString(4));
//            mf.setDescription(c.getString(5));
//            mf.setCaption(c.getString(6));
//            mf.setHorizontalAlignment(c.getInt(7));
//            mf.setWidth(c.getInt(8));
//            mf.setHeight(c.getInt(9));
//            mf.setMIMEType(c.getString(10));
//            mf.setFeatured(c.getInt(11) > 0);
//            mf.setVideo(c.getInt(12) > 0);
//            mf.setFeaturedInPost(c.getInt(13) > 0);
//            mf.setFileURL(c.getString(14));
//            mf.setThumbnailURL(c.getString(15));
//            mf.setMediaId(c.getString(16));
//            mf.setBlogId(c.getString(17));
//            mf.setDateCreatedGMT(c.getLong(18));
//            mf.setUploadState(c.getString(19));
//        } else {
//            c.close();
//            return null;
//        }
//        c.close();
//
//        return mf;
//    }
//
//    public void deleteMediaFilesForPost(Post post) {
//
//        db.delete(MEDIA_TABLE, "blogId='" + post.getBlogID() + "' AND postID=" + post.getId(), null);
//
//    }
//
//    /** Get the queued media files for upload for a given blogId **/
//    public Cursor getMediaUploadQueue(String blogId) {
//        return db.rawQuery("SELECT * FROM " + MEDIA_TABLE + " WHERE uploadState=? AND blogId=?", new String[] {"queued", blogId}); 
//    }
//    
//    /** Update a media file to a new upload state **/
//    public void updateMediaUploadState(String blogId, String mediaId, String uploadState) {
//        if (blogId == null || blogId.equals(""))
//            return;
//        
//        ContentValues values = new ContentValues();
//        if (uploadState == null) values.putNull("uploadState");
//        else values.put("uploadState", uploadState);
//        
//        if (mediaId == null) {
//            db.update(MEDIA_TABLE, values, "blogId=? AND (uploadState IS NULL OR uploadState ='uploaded')", new String[] { blogId });
//        } else {
//            db.update(MEDIA_TABLE, values, "blogId=? AND mediaId=?", new String[] { blogId, mediaId });            
//        }
//    }
//    
//    public void updateMediaFile(String blogId, String mediaId, String title, String description, String caption) {
//        if (blogId == null || blogId.equals("")) {
//            return;
//        }
//        
//        ContentValues values = new ContentValues();
//        
//        if (title == null || title.equals("")) {
//            values.put("title", "");
//        } else {
//            values.put("title", title);            
//        }
//        
//        if (title == null || title.equals("")) {
//            values.put("description", "");
//        } else {
//            values.put("description", description);
//        }
//        
//        if (caption == null || caption.equals("")) {
//            values.put("caption", "");
//        } else {
//            values.put("caption", caption);
//        }
//        
//        db.update(MEDIA_TABLE, values, "blogId = ? AND mediaId=?", new String[] { blogId, mediaId });
//    }
//
//    /** 
//     * For a given blogId, set all uploading states to failed.
//     * Useful for cleaning up files stuck in the "uploading" state.  
//     **/
//    public void setMediaUploadingToFailed(String blogId) {
//        if (blogId == null || blogId.equals(""))
//            return; 
//        
//        ContentValues values = new ContentValues();
//        values.put("uploadState", "failed");
//        db.update(MEDIA_TABLE, values, "blogId=? AND uploadState=?", new String[] { blogId, "uploading" });
//    }
//    
//    /** For a given blogId, clear the upload states in the upload queue **/
//    public void clearMediaUploaded(String blogId) {
//        if (blogId == null || blogId.equals(""))
//            return;
//        
//        ContentValues values = new ContentValues();
//        values.putNull("uploadState");
//        db.update(MEDIA_TABLE, values, "blogId=? AND uploadState=?", new String[] { blogId, "uploaded" });
//    }
//
//    /** Delete a media item from a blog locally **/
//    public void deleteMediaFile(String blogId, String mediaId) {
//        db.delete(MEDIA_TABLE, "blogId=? AND mediaId=?", new String[] { blogId, mediaId });
//    }
//
//    /** Mark media files for deletion without actually deleting them. **/
//    public void setMediaFilesMarkedForDelete(String blogId, List<String> ids) {
//        // This is for queueing up files to delete on the server
//        for (String id : ids)
//            updateMediaUploadState(blogId, id, "delete");
//    }
//    
//    /** Mark media files as deleted without actually deleting them **/
//    public void setMediaFilesMarkedForDeleted(String blogId) {
//        // This is for syncing our files to the server:
//        // when we pull from the server, everything that is still 'deleted' 
//        // was not downloaded from the server and can be removed via deleteFilesMarkedForDeleted()
//        updateMediaUploadState(blogId, null, "deleted");
//    }
//    
//    /** Delete files marked as deleted **/
//    public void deleteFilesMarkedForDeleted(String blogId) {
//        db.delete(MEDIA_TABLE, "blogId=? AND uploadState=?", new String[] { blogId, "deleted" });
//    }
//    
//    /** Get a media file scheduled for delete for a given blogId **/
//    public Cursor getMediaDeleteQueueItem(String blogId) {
//        return db.rawQuery("SELECT blogId, mediaId FROM " + MEDIA_TABLE + " WHERE uploadState=? AND blogId=? LIMIT 1", new String[] {"delete", blogId}); 
//    }
//    
//    
//    public int getWPCOMBlogID() {
//        int id = -1;
//        Cursor c = db.query(SETTINGS_TABLE, new String[] { "id" },
//                "dotcomFlag=1", null, null, null, null);
//        int numRows = c.getCount();
//        c.moveToFirst();
//        if (numRows > 0) {
//            id = c.getInt(0);
//        }
//
//        c.close();
//
//        return id;
//    }
//
//    public void clearComments(int blogID) {
//
//        db.delete(COMMENTS_TABLE, "blogID=" + blogID, null);
//
//    }
//
//    public boolean findLocalChanges() {
//        Cursor c = db.query(POSTS_TABLE, null,
//                "isLocalChange=1", null, null, null, null);
//        int numRows = c.getCount();
//        if (numRows > 0) {
//            return true;
//        }
//        c.close();
//
//        return false;
//    }
//    
//    public boolean saveTheme(Theme theme) {
//        boolean returnValue = false;
//        
//        ContentValues values = new ContentValues();
//        values.put("themeId", theme.getThemeId());
//        values.put("name", theme.getName());
//        values.put("description", theme.getDescription());
//        values.put("screenshotURL", theme.getScreenshotURL());
//        values.put("trendingRank", theme.getTrendingRank());
//        values.put("popularityRank", theme.getPopularityRank());
//        values.put("launchDate", theme.getLaunchDateMs());
//        values.put("previewURL", theme.getPreviewURL());
//        values.put("blogId", theme.getBlogId());
//        values.put("isCurrent", theme.isCurrent());
//        values.put("isPremium", theme.isPremium());
//        values.put("features", theme.getFeatures());
//        
//        synchronized (this) {
//            int result = db.update(
//                    THEMES_TABLE,
//                    values,
//                    "themeId=?", 
//                    new String[]{ theme.getThemeId() });
//            if (result == 0)
//                returnValue = db.insert(THEMES_TABLE, null, values) > 0;
//        }
//
//        return (returnValue);
//    }
//    
//    public Cursor getThemesAtoZ(String blogId) {
//        return db.rawQuery("SELECT _id, themeId, name, screenshotURL, isCurrent, isPremium FROM " + THEMES_TABLE + " WHERE blogId=? ORDER BY name COLLATE NOCASE ASC", new String[] { blogId });
//    }
//    
//    public Cursor getThemesTrending(String blogId) {
//        return db.rawQuery("SELECT _id, themeId, name, screenshotURL, isCurrent, isPremium FROM " + THEMES_TABLE + " WHERE blogId=? ORDER BY trendingRank ASC", new String[] { blogId });
//    }
//    
//    public Cursor getThemesPopularity(String blogId) {
//        return db.rawQuery("SELECT _id, themeId, name, screenshotURL, isCurrent, isPremium FROM " + THEMES_TABLE + " WHERE blogId=? ORDER BY popularityRank ASC", new String[] { blogId });
//    }
//    
//    public Cursor getThemesNewest(String blogId) {
//        return db.rawQuery("SELECT _id, themeId, name, screenshotURL, isCurrent, isPremium FROM " + THEMES_TABLE + " WHERE blogId=? ORDER BY launchDate DESC", new String[] { blogId });
//    }
//    
//    public Cursor getThemesPremium(String blogId) {
//        return db.rawQuery("SELECT _id, themeId, name, screenshotURL, isCurrent, isPremium FROM " + THEMES_TABLE + " WHERE blogId=? AND price > 0 ORDER BY name ASC", new String[] { blogId });
//    }
//    
//    public Cursor getThemesFriendsOfWP(String blogId) {
//        return db.rawQuery("SELECT _id, themeId, name, screenshotURL, isCurrent, isPremium FROM " + THEMES_TABLE + " WHERE blogId=? AND themeId LIKE ? ORDER BY popularityRank ASC", new String[] { blogId, "partner-%" });
//    }
//    
//    public Cursor getCurrentTheme(String blogId) {
//        return db.rawQuery("SELECT _id,  themeId, name, screenshotURL, isCurrent, isPremium FROM " + THEMES_TABLE + " WHERE blogId=? AND isCurrentTheme='true'", new String[] { blogId });
//    }
//    
//    public void setCurrentTheme(String blogId, String themeId) {
//        
//        // update any old themes that are set to true to false
//        ContentValues values = new ContentValues();
//        values.put("isCurrent", false);
//        db.update(THEMES_TABLE, values, "blogID=? AND isCurrent='1'", new String[] { blogId });
//        
//        values = new ContentValues();
//        values.put("isCurrent", true);
//        db.update(THEMES_TABLE, values, "blogId=? AND themeId=?", new String[] { blogId, themeId });
//    }
//    
//    public int getThemeCount(String blogId) {
//        return getThemesAtoZ(blogId).getCount();
//    }
//    
//    public Cursor getThemes(String blogId, String searchTerm) {
//        return db.rawQuery("SELECT _id,  themeId, name, screenshotURL, isCurrent, isPremium FROM " + THEMES_TABLE + " WHERE blogId=? AND (name LIKE ? OR description LIKE ?) ORDER BY name ASC", new String[] {blogId, "%" + searchTerm + "%", "%" + searchTerm + "%"});
//        
//    }
//    
//    public Theme getTheme(String blogId, String themeId) {
//        Cursor cursor = db.rawQuery("SELECT name, description, screenshotURL, previewURL, isCurrent, isPremium, features FROM " + THEMES_TABLE + " WHERE blogId=? AND themeId=?", new String[]{blogId, themeId});
//        if (cursor.moveToFirst()) {
//            String name = cursor.getString(0);
//            String description = cursor.getString(1);
//            String screenshotURL = cursor.getString(2);
//            String previewURL = cursor.getString(3);
//            boolean isCurrent = cursor.getInt(4) == 1;
//            boolean isPremium = cursor.getInt(5) == 1;
//            String features = cursor.getString(6);
//            
//            Theme theme = new Theme();
//            theme.setThemeId(themeId);
//            theme.setName(name);
//            theme.setDescription(description);
//            theme.setScreenshotURL(screenshotURL);
//            theme.setPreviewURL(previewURL);
//            theme.setCurrent(isCurrent);
//            theme.setPremium(isPremium);
//            theme.setFeatures(features);
//            
//            cursor.close();
//            
//            return theme;
//        } else {
//            return null;    
//        }
//    }
//
//    public ArrayList<Note> getLatestNotes() {
//        return getLatestNotes(20);
//    }
//
//    public ArrayList<Note> getLatestNotes(int limit) {
//        Cursor cursor = db.query(NOTES_TABLE, new String[] {"note_id", "raw_note_data", "placeholder"},
//                null, null, null, null, "timestamp DESC", "" + limit);
//        ArrayList<Note> notes = new ArrayList<Note>();
//        while (cursor.moveToNext()) {
//            String note_id = cursor.getString(0);
//            String raw_note_data = cursor.getString(1);
//            boolean placeholder = cursor.getInt(2) == 1;
//            try {
//                Note note = new Note(new JSONObject(raw_note_data));
//                note.setPlaceholder(placeholder);
//                notes.add(note);
//            } catch (JSONException e) {
//                Log.e(WordPress.TAG, "Can't parse notification with note_id:" + note_id + ", exception:" + e);
//            }
//        }
//        cursor.close();
//        return notes;
//    }
//
//    public void removePlaceholderNotes() {
//        db.delete(NOTES_TABLE, "placeholder=1", null);
//    }
//
//    public void addNote(Note note, boolean placeholder) {
//        ContentValues values = new ContentValues();
//        values.put("type", note.getType());
//        values.put("timestamp", note.getTimestamp());
//        values.put("placeholder", placeholder);
//        values.put("raw_note_data", note.toJSONObject().toString()); // easiest way to store schema-less data
//
//        if (note.getId().equals("0") || note.getId().equals("")) {
//            values.put("id", generateIdFor(note));
//            values.put("note_id", "0");
//        } else {
//            values.put("id", note.getId());
//            values.put("note_id", note.getId());
//        }
//
//        db.insertWithOnConflict(NOTES_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//    }
//
//    public static int generateIdFor(Note note) {
//        if (note == null) {
//            return 0;
//        }
//        return StringUtils.getMd5IntHash(note.getSubject() + note.getType()).intValue();
//    }
//
//    public void saveNotes(List<Note> notes) {
//        db.beginTransaction();
//        try {
//            for (Note note: notes)
//                addNote(note, false);
//            db.setTransactionSuccessful();
//        } finally {
//            db.endTransaction();
//        }
//    }
//
//    public Note getNoteById(int id) {
//        Cursor cursor = db.query(NOTES_TABLE, new String[] {"raw_note_data"},  "id=" + id, null, null, null, null);
//        cursor.moveToFirst();
//
//        try {
//            JSONObject jsonNote = new JSONObject(cursor.getString(0));
//            return new Note(jsonNote);
//        } catch (JSONException e) {
//            Log.e(WordPress.TAG, "Can't parse JSON Note: " + e);
//            return null;
//        } catch (CursorIndexOutOfBoundsException e) {
//            Log.v(WordPress.TAG, "No Note with this id: " + e);
//            return null;
//        }
//    }
//
//    public void clearNotes() {
//        db.delete(NOTES_TABLE, null, null);
//    }
    
    private int getDateDays (String date1, String date2)
    {       
            long betweenTime = 0;
            try { 
                    Date date = sdf.parse(date1);// 通过日期格式的parse()方法将字符串转换成日期              
                    Date dateBegin = sdf.parse(date2);
                    betweenTime = date.getTime() - dateBegin.getTime(); 
                    betweenTime  = betweenTime  / 1000 / 60 / 60 / 24; 
                 } catch(Exception e)
                 {
                  }
            return (int)betweenTime; 
    }
}
