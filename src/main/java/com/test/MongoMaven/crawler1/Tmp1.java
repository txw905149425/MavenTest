package com.test.MongoMaven.crawler1;

import java.io.IOException;
import java.util.HashMap;

import net.sf.json.JSONObject;

import org.apache.http.client.ClientProtocolException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.Jsoup;

import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

public class Tmp1 {
	public static void main(String[] args) {
		PostData post=new PostData();
		 MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document>  collection=mongo.getShardConn("ww_test");
//		 Bson filter1 = Filters.eq("website", "牛仔网");
		 MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator();
		 try {
			 Document doc=null;
			 while(cursor.hasNext()){
				 doc=cursor.next();
				if(!doc.containsKey("answer")){
					continue;
				}
//				doc.append("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				doc.remove("_id");
				doc.remove("crawl_time");
				Object answer=doc.get("answer");
				doc.remove("answer");
				if(answer.toString().length()<4){
					answer="";
					doc.remove("ifanswer");
					doc.append("ifanswer", "0");
				}
				doc.append("answer", answer);
				JSONObject json=JSONObject.fromObject(doc);
//				http://localhost:8888/import?type=ww_stock_json
//				 http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=ww_stock_json
				String su= post.postHtml("http://localhost:8888/import?type=ww_stock_json",new HashMap<String, String>(),json.toString(), "utf-8", 1);
				if(su.contains("exception")){
					System.err.println("写入数据异常！！！！  < "+su+" >");
				}
				mongo.upsertDocByTableName(doc, "ww_test");
			 }
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} 
	}
	
	public static String exChange(String str){  
	    StringBuffer sb = new StringBuffer();  
	    if(str!=null){  
	        for(int i=0;i<str.length();i++){  
	            char c = str.charAt(i);  
	            if(Character.isUpperCase(c)){  
	                sb.append(Character.toLowerCase(c));  
	            }else{
	            	sb.append(c);  
	            } 
	        }  
	    }  
	      
	    return sb.toString();  
	}  
	
	
	public static String getMd5Value(String sSecret) {  
        try {  
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");  
            bmd5.update(sSecret.getBytes());  
            int i;  
            StringBuffer buf = new StringBuffer();  
            byte[] b = bmd5.digest();// 加密  
            for (int offset = 0; offset < b.length; offset++) {  
                i = b[offset];  
                if (i < 0)  
                    i += 256;  
                if (i < 16)  
                    buf.append("0");  
                buf.append(Integer.toHexString(i));  
            }  
            return buf.toString();  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        return "";  
    } 
}
