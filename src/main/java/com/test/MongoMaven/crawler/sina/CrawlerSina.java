package com.test.MongoMaven.crawler.sina;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;



/***
 * 新浪股吧
 * */
public class CrawlerSina {
	static int threadNum=30;
	public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
		 ExecutorService executor = Executors.newFixedThreadPool(threadNum);
		 MongoCollection<Document>  collection=mongo.getShardConn("stock_code");
		 Bson filter = Filters.exists("name", true);
//		 Bson filter1 = Filters.exists("sina_crawl", false);
		 MongoCursor<Document> cursor =collection.find(filter).batchSize(10000).noCursorTimeout(true).iterator(); 
		 DataUtil util=null;
		 while(cursor.hasNext()){
			 Document doc=cursor.next();
			 Object code=doc.get("id");
			 Object name=doc.get("name");
			 String url="http://guba.sina.com.cn/?s=bar&name="+code;
			 util=new DataUtil();
			 util.setCode(code.toString());
			 util.setUrl(url);
			 util.setName(name.toString());
			 executor.execute(new Actions(util)); 
		 }
	  cursor.close();
	  executor.shutdown();
	}
	

	
	
}
