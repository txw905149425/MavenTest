package com.test.MongoMaven.crawler.dzhApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
 *  大智慧APP论股数据
 *  优化代码(1.循环创建对象)
 * */
public class CrawlerDZH {
	public static void main(String[] args) throws FileNotFoundException {
			 MongoDbUtil mongo=new MongoDbUtil();
			 MongoCollection<Document>  collection=mongo.getShardConn("stock_code");
			 Bson filter = Filters.exists("name", true);
			 MongoCursor<Document> cursor =collection.find(filter).batchSize(10000).noCursorTimeout(true).iterator(); 
			 PrintWriter pw=new PrintWriter(new File("d:/stockDic.txt"));
			 while(cursor.hasNext()){
				 Document doc=cursor.next();
				 Object code=doc.get("id");
				 Object name=doc.get("name");
				 pw.println(code);
				 pw.println(name);
			 }
			 pw.close();
			 cursor.close();
	}
	
	
}
