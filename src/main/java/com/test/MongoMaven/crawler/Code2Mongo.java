package com.test.MongoMaven.crawler;

import java.util.ArrayList;
import java.util.Collection;








import java.util.HashMap;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.InsertManyOptions;
import com.test.MongoMaven.db.Collenction;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

import clojure.main;

public class Code2Mongo {
	 public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
//		 MongoCollection<Document>  collection=mongo.getShardConn("stock_code");
		 ArrayList<String> list=FileUtil.readFileReturn("d:/code.txt");
		 List<Document> list1=new ArrayList<Document>();
		 Document doc=null;
		 for(String code:list){
			doc=new Document();
			doc.append("id", code);
			list1.add(doc);
//			collection.insertOne(doc);
		 }
		for(Document d:list1){
			System.out.println(d.toString());
		}
	}
		 
	 
	 
}
