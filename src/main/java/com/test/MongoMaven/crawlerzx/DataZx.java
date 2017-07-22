package com.test.MongoMaven.crawlerzx;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class DataZx {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document>  collection=mongo.getShardConn("stock_code");
		 Bson filter = Filters.eq("deltime", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
		 MongoCursor<Document> cursor =collection.find(filter).batchSize(10000).noCursorTimeout(true).iterator();
		 
	}
}
