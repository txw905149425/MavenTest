package com.test.MongoMaven.crawlerjy.niu;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;


/***
 * 东方财富股吧数据
 * */
public class CrawlerJY {
	static int threadNum=10;
	public static void main(String[] args) {
		ExecutorService executor = Executors.newFixedThreadPool(threadNum);
			 MongoDbUtil mongo=new MongoDbUtil();
			 MongoCollection<Document>  collection=mongo.getShardConn("mm_ngw_genius");
//			 BasicDBObject query=new BasicDBObject();
//			 query.put("id", 1);
//			 query.put("describe", 1);
			 MongoCursor<Document> cursor =collection.find()/*.filter(filter)*/.batchSize(10000).noCursorTimeout(true).iterator(); 
			 Document doc=null;
			 DataUtil util=null;
			 while(cursor.hasNext()){
				 doc=cursor.next();
				 util=new DataUtil();
				 Object id=doc.get("id");
				 String url="https://dynamic.niuguwang.com/Api/getdynamicta.ashx?usertoken=OJPMsUn4AIsUa60OQmF9ZE29TG_26velk6ZRS27N32k*&userID="+id+"&size=50&index=1&type=1&s=xiaomi&version=3.7.0&packtype=1";
				 util.setCode(id.toString());
				 util.setUrl(url);
				 util.setDescribe(doc.get("describe").toString());
				 executor.execute(new ThreadActions(util)); 
			 }
		   cursor.close();
		  executor.shutdown();
	}

	
}
