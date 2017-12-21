package com.test.MongoMaven.wd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class DeleteTable {
	
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		   MongoCollection<Document> collectiondele=mongo.getShardConn("xg_stock_last_json");
		   MongoCursor<Document> cursor =collectiondele.find().batchSize(10000).noCursorTimeout(true).iterator();
		   while(cursor.hasNext()){
			   Document d  = cursor .next(); //遍历每一条数据
			   d.remove("_id");
			   mongo.upsertDocByTableName(d, "xg_stock_last_json_all");
		   }

		cursor.close();
	}
	
}
