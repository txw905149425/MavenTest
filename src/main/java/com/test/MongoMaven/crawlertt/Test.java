package com.test.MongoMaven.crawlertt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

public class Test {
	static MongoDbUtil mongo=new MongoDbUtil();
	public static void main(String[] args) {
		PostData post=new PostData();
		 MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document>  collection=mongo.getShardConn("tt_json_all");
		 MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator();
		 try {
			 Document doc=null;
			 while(cursor.hasNext()){
				 doc=cursor.next();
				doc.remove("_id");
				doc.remove("crawl_time");
				JSONObject json=JSONObject.fromObject(doc);
//				http://localhost:8888/import?type=ww_stock_json
//				 http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=ww_stock_json
				String su= post.postHtml("http://localhost:8888/import?type=tt_stock_json",new HashMap<String, String>(),json.toString(), "utf-8", 1);
				if(su.contains("exception")){
					System.err.println("写入数据异常！！！！  < "+su+" >");
				}
				mongo.upsertDocByTableName(doc, "tt_json_all");
			 }
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} 
	}
	public static List<HashMap<String, Object>>  getIndexInformation(String table){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
		MongoCollection<Document>  collection=mongo.getShardConn(table);
		 MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator();
		 while(cursor.hasNext()){
			 HashMap<String, Object> map=new HashMap<String, Object>();
			 Document doc=cursor.next();
			Object describe= doc.get("describe");
			Object website= doc.get("website");
			Object id= doc.get("title");
			map.put("id",id);
			map.put("describe", describe);
			map.put("website", website);
			list.add(map);
		 }
		 return list;
	} 
	
	
	
}


