package com.test.MongoMaven.crawler.thsApp;

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
 *  同花顺APP论股数据
 *  优化代码(1.循环创建对象)
 * */
public class CrawlerTHS {
	static int threadNum=20;
	public static void main(String[] args) {
		  ExecutorService executor = Executors.newFixedThreadPool(threadNum);
			 MongoDbUtil mongo=new MongoDbUtil();
			 MongoCollection<Document>  collection=mongo.getShardConn("stock_code");
			 Bson filter = Filters.exists("name", true);
//			 Bson filter1 = new Document("id","601206");
			 MongoCursor<Document> cursor =collection.find(filter).batchSize(10000).noCursorTimeout(true).iterator(); 
			 DataUtil util=null;
			 while(cursor.hasNext()){
				 Document doc=cursor.next();
				 Object code=doc.get("id");
				 String url="http://t.10jqka.com.cn/api.php?method=group.getLatestPost&limit=20&page=0&pid=0&return=json&allowHtml=0&uid=384369689&code="+code;
				 util=new DataUtil();
				 util.setCode(code.toString());
				 util.setUrl(url);
				 executor.execute(new Actions(util)); 
			 }
		  cursor.close();
		  executor.shutdown();
	}
	
	
}
