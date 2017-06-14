package com.test.MongoMaven.crawlerjy.ypgpt;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

public class Demo {

	
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
	    MongoCollection<Document>  collection=mongo.getShardConn("mm_ypgpt_name");
	    MongoCursor<Document> cursor =collection.find()/*.filter(filter)*/.batchSize(10000).noCursorTimeout(true).iterator(); 
	    Document doc=null;
		 while(cursor.hasNext()){
			 doc=cursor.next();
			 doc.remove("_id");
			 mongo.upsertDocByTableName(doc, "mm_ypgpt_name1");
			 
		 }
	}
}
