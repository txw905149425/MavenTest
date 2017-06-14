package com.test.MongoMaven.crawlerjy.gsjl;

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
 * 股市教练
 * */
public class CrawlerOnline {
	static int threadNum=5;
	public static void main(String[] args) {
			 MongoDbUtil mongo=new MongoDbUtil();
			 ExecutorService executor = Executors.newFixedThreadPool(threadNum);
			 MongoCollection<Document>  collection=mongo.getShardConn("stock_code");
			 Bson filter = Filters.exists("name", true);
			 MongoCursor<Document> cursor =collection.find(filter).batchSize(10000).noCursorTimeout(true).iterator(); 
			 Document doc=null;
			 DataUtil util=null;
		 while(cursor.hasNext()){
			  doc=cursor.next();
			 Object code=doc.get("id");
			 Object name=doc.get("name");
			 String url="http://t.10jqka.com.cn/api.php?method=newtrace.getTacticTrend&code="+code+"&type=0&rid=0";
			 util=new DataUtil();
			 util.setCode(code.toString());
			 util.setUrl(url);
			 util.setName(name.toString());
			 executor.execute(new Actions(util,mongo)); 
		 }
	  cursor.close();
	  executor.shutdown();
	}
	

	
	
}
