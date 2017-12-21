package com.test.MongoMaven.crawler.dfcfWeb;

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
 * 东方财富股吧数据
 * */
public class CrawlerEastMoney {
	static int threadNum=15;
	public static void main(String[] args) {
		ExecutorService executor = Executors.newFixedThreadPool(threadNum);
			 MongoDbUtil mongo=new MongoDbUtil();
			 MongoCollection<Document>  collection=mongo.getShardConn("stock_code");
			 Bson filter = Filters.exists("name", true);
//			 Bson filter = Filters.eq("id", "601398");
			 MongoCursor<Document> cursor =collection.find().filter(filter).batchSize(10000).noCursorTimeout(true).iterator(); 
			 Document doc=null;
			 DataUtil util=null;
			 while(cursor.hasNext()){
				 doc=cursor.next();
				 util=new DataUtil();
				 Object code=doc.get("id");
				 Object name=doc.get("name");
				 String url="http://guba.eastmoney.com/list,"+code+",f.html";
				 util.setCode(code.toString());
				 util.setName(name.toString());
				 util.setUrl(url);
				 executor.execute(new Actions(util,mongo)); 
			 }
		   cursor.close();
		  executor.shutdown();
	}
	
}
