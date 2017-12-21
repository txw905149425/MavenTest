package com.test.MongoMaven.wd;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class SSupdateData {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document> collection=mongo.getShardConn("ss_all_stock_json_count");
		int num=1;
		Document doc=new Document();
		MongoCursor<Document> cursor =collection.find(doc).batchSize(10000).noCursorTimeout(true).iterator();
		while(cursor.hasNext()){
			Document d=cursor.next();
			d.remove("_id");
			mongo.upsertDocByTableName(d, "ss_test");
			num++;
		}
		cursor.close();
		collection.deleteMany(doc);
		System.out.println("当日总数据："+num);
			
	}
}
