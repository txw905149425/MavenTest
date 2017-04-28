package com.test.MongoMaven.crawler.ajk.thread;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;



public class CrawlerAJK {
	static int threadNum=15;
	public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
		 ExecutorService executor = Executors.newFixedThreadPool(threadNum);
		 MongoCollection<Document>  collection=mongo.getShardConn("ajk_detail_url");
		 Bson filter = Filters.exists("crawl", false);
		 MongoCursor<Document> cursor =collection.find(filter).batchSize(10000).noCursorTimeout(true).iterator(); 
		 try{
			DataUtil util=null;
			 while(cursor.hasNext()){
				 Document doc=cursor.next();
				 Object url=doc.get("id");
				 if(url==null||StringUtil.isEmpty(url.toString())){
					 continue;
				 }
				 Object uid=doc.get("uid");
				 util=new DataUtil();
				 util.setUrl(url.toString());
				 util.setCode(uid.toString());
				 executor.execute(new Actions(util));
			 }
			 cursor.close();
			 executor.shutdown();	 
		}catch(Exception e){
			e.printStackTrace();
		}
		 cursor.close();
	}
	

	
	
}
