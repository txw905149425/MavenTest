package com.test.MongoMaven.crawler1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.bson.BSON;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

public class Data2Web {
	public static void main(String[] args){
		 PostData post=new PostData();
		MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document>  collection=mongo.getShardConn("ww_ask_online_all");
		 Document sort=new Document("time",-1);
	  try {
			 MongoCursor<Document> cursor =collection.find().sort(sort).batchSize(10000).noCursorTimeout(true).iterator();
			 Document doc=null;
			 while(cursor.hasNext()){
				 doc=cursor.next();
				if(!doc.containsKey("answer")){
					continue;
				}
				doc.remove("json_str");
				doc.remove("_id");
				mongo.upsertDocByTableName(doc, "ww_test");
				String su= post.postHtml("http://wisefinance.chinaeast.cloudapp.chinacloudapi.cn:8000/wf/import?type=ww_stock_json",new HashMap<String, String>(),doc.toJson(), "utf-8", 1);
				if(su.contains("exception")){
					System.err.println("写入数据异常！！！！  < "+su+" >");
				}
			 }
		} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		 
	}
	
}
