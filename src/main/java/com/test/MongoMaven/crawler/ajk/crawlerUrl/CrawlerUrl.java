package com.test.MongoMaven.crawler.ajk.crawlerUrl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;


public class CrawlerUrl {
		static int threadNum=10;
		public static void main(String[] args)  {
			MongoDbUtil mongo=new MongoDbUtil();
			 ExecutorService executor = Executors.newFixedThreadPool(threadNum);
			 MongoCollection<Document>  collection=mongo.getShardConn("ajk_list_url");
			 Bson filter = Filters.exists("crawl", false);
			 MongoCursor<Document> cursor =collection.find(filter).batchSize(10000).noCursorTimeout(true).iterator(); 
			 try{
				 DataUtil util=null;
				 while(cursor.hasNext()){
					 Document doc=cursor.next();
					 String url=doc.get("id").toString();
					 String uid=doc.get("uid").toString();
					 if(url==null){
						 continue;
					 }else{
						 util=new DataUtil();
						 util.setUrl(url);
						 util.setCode(uid.toString());
						 executor.execute(new Actions(util));
				     }
				 }
				 cursor.close();
				 executor.shutdown();	 
			 }catch(Exception e){
				 e.printStackTrace();
			 }
		  cursor.close();
			 
		}
		
		
	
}
		
		
		
