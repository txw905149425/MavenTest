package com.test.MongoMaven.crawler1.znxg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
/***
 *  智能选股
 * */
public class Crawler {
	public static void main(String[] args) {
		  ExecutorService executor = Executors.newFixedThreadPool(10);
			 MongoDbUtil mongo=new MongoDbUtil();
			 PostData post=new PostData();
			 MongoCollection<Document>  collection=mongo.getShardConn("stock_code");
			 Bson filter = Filters.exists("name", true);
			 MongoCursor<Document> cursor =collection.find().filter(filter).batchSize(10000).noCursorTimeout(true).iterator(); 
			 DataUtil util=null;
			 while(cursor.hasNext()){
				 Document doc=cursor.next();
				 Object code=doc.get("id");
				 Object name=doc.get("name");
				 String url="";
				 if(code.toString().startsWith("6")){
					 url="http://robot.rxhui.com/robot/semantic//stock-analysis-service/trend/analysis/sh/"+code;
				 }else{
					 url="http://robot.rxhui.com/robot/semantic//stock-analysis-service/trend/analysis/sz/"+code;
				 }
				 util=new DataUtil();
				 util.setCode(code.toString());
				 util.setUrl(url);
				 util.setName(name.toString());
				 executor.execute(new Actions(util,mongo,post)); 
			 }
		  cursor.close();
		  executor.shutdown();
	}
	
	
}
