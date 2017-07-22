package com.test.MongoMaven.mongo;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.Jsoup;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.db.Collenction1;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Test {

	public static void main(String[] args) throws IOException {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document>  collection=mongo.getShardConn("ww_ask_online_all");
		MongoCursor<org.bson.Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator();
		Document doc=null;
		PrintWriter pw=new PrintWriter(new FileWriter(new File("d:/ww_answer.txt"),true));
		try {
		while(cursor.hasNext()){
			doc=cursor.next();
			if(doc.containsKey("answer")){
			  Object answer=doc.get("answer");
			  if(StringUtil.isEmpty(answer.toString().trim())||answer.toString().trim().length()<4){
				 continue;	
			  }
			  pw.println(answer.toString().trim());
			}
			
		}	
		pw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
