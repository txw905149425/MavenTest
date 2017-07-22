package com.test.MongoMaven.crawlerjy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.client.ClientProtocolException;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

public class Test {
	
	//https://gupiao.nicaifu.com/app/game/trading
		public static void main(String[] args) {
			MongoDbUtil mongo=new MongoDbUtil();
			 PostData post=new PostData();
			MongoCollection<org.bson.Document>  collection=mongo.getShardConn("mm_deal_dynamic_all");
			Bson filter = Filters.eq("AddTime", "2017-06-19 09:31:51");
//			org.bson.Document  sort=new org.bson.Document("_id",-1);
			MongoCursor<org.bson.Document> cursor =collection.find(filter)/*.sort(sort)*/.batchSize(10000).noCursorTimeout(true).iterator();
			org.bson.Document doc=null;
			try{
			while(cursor.hasNext()){
				 doc=cursor.next();
				doc.remove("_id");
				doc.remove("crawl_time");
				 JSONObject json=JSONObject.fromObject(doc);
//				 http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=ww_stock_json
				String su= post.postHtml("http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=mm_stock_json",new HashMap<String, String>(),json.toString(), "utf-8", 1);
				if(su.contains("exception")){
					System.err.println("写入数据异常！！！！  < "+su+" >");
				}
//				mongo.upsertDocByTableName(doc, "ww_test");
				System.out.println("000000");
			 }
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println("*****");
		}
		
		
}
