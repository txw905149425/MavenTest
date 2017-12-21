package com.test.MongoMaven.crawler1.doctor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import net.sf.json.JSONObject;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

public class EsSave {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		PostData post=new PostData();
		String time=IKFunction.getTimeNowByStr("yyyy-MM-dd");
	  try{
		 MongoCollection<Document>  collection=mongo.getShardConn("ww_ask_online_all");
		 String web="股金在线";
		 BasicDBObject doc = new BasicDBObject();
		 doc.append("timedel", time);
		 doc.append("website",web);
		 MongoCursor<Document> cursor =collection.find(doc).batchSize(10000).noCursorTimeout(false).iterator();
		 while(cursor.hasNext()){
			   Document doc1=cursor.next();
			   doc1.remove("_id");
			   doc1.remove("crawl_time");
			   JSONObject json=JSONObject.fromObject(doc1);
//					 http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=ww_stock_json
//					 http://localhost:8888/import?type=ww_stock_json
				String su= post.postHtml("http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=ww_stock_json",new HashMap<String, String>(),json.toString(), "utf-8", 1);
				if(su.contains("exception")){
					System.err.println("写入数据异常！！！！  < "+su+" >");
				}
				System.out.println(json);
		 }
		 cursor.close();
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	}

}
