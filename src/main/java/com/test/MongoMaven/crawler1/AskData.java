package com.test.MongoMaven.crawler1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.bson.BSON;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

public class AskData {
	public static void main(String[] args){
		 PostData post=new PostData();
		 MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document>  collection=mongo.getShardConn("ww_ask_online_all");
		 Document sort=new Document("_id",-1);
	  try {
			 MongoCursor<Document> cursor =collection.find().sort(sort).batchSize(10000).noCursorTimeout(true).iterator();
			 Document doc=null;
			 int size=1;
			 while(cursor.hasNext()){
				 if(size==700){
					break;
				 }
				 doc=cursor.next();
				if(!doc.containsKey("answer")){
					continue;
				}
				String timedel=doc.get("time").toString();
				if(!IKFunction.timeOK(timedel)){
					size++;
					continue;
				}
				doc.remove("_id");
				doc.remove("crawl_time");
				 JSONObject json=JSONObject.fromObject(doc);
//				 http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=ww_stock_json
//				 http://localhost:8888/import?type=ww_stock_json
				String su= post.postHtml("http://localhost:8888/import?type=ww_stock_json",new HashMap<String, String>(),json.toString(), "utf-8", 1);
				if(su.contains("exception")){
					System.err.println("写入数据异常！！！！  < "+su+" >");
				}
				mongo.upsertDocByTableName(doc, "ww_test");
				size++;
			 }
	  }catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		 
	}
	
}
