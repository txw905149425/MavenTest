package com.test.MongoMaven.wd;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.InsertManyOptions;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class SplitSSData {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		ArrayList<String> list = FileUtil.readFileReturn("date");
		for(String day:list){
			String d="d.ss"+day.replace("-","").replace("\n","");
			System.out.println(d);
			MongoCollection<Document> coll=mongo.getShardConn(d);
		}
		
	}
}
