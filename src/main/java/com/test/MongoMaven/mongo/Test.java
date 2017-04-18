package com.test.MongoMaven.mongo;


import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.db.Collenction1;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Test {

	public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document>  collection=mongo.getShardConn("ths_talk_stock_json");
		 MongoCollection<Document>  collection1=mongo.getShardConn("east_money_stock_json");
		 MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator(); 
	}
	
	
	
	
}
