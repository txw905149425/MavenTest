package com.test.MongoMaven.txw.mongo;

import java.util.ArrayList;

import org.bson.BsonArray;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.test.MongoMaven.db.Collenction1;
import com.test.MongoMaven.uitil.FileUtil;

public class FileWriteToMongo {
   
	
	public static void main(String[] args){
		String collectionName="txwTest";
//		Collenction1 conn=new Collenction1();
//		MongoDatabase con=null;
		
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		MongoDatabase db = mongoClient.getDatabase("root");
		MongoCollection<Document> mongo=db.getCollection(collectionName);
		Document key = new Document("id", 1) ;
		mongo.createIndex(key, new IndexOptions().unique(true).background(true).name("id_unique"))  ;	
//		try {
//			con=conn.getMongoDataBase();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("mongodb 连接异常...");
//		}
		
//		MongoCollection<Document> mongo=con.getCollection(collectionName);
		Document doc=new Document();
		doc.append("id", "2");
		doc.append("name", "txw");
		doc.append("sex", "man");
		doc.append("love", "吃..吃..吃");
		doc.append("abstract", "please call me xiang wu");
		mongo.insertOne(doc);
	}
	
	/**
	 * @prama conn Collenction1类的对象
	 * @prama collectionName mongodb表名  String
	 * @prama uri 读取的文件路径String
	 * */
	public static void file2Mongo(Collenction1 conn,String collectionName,String uri){
		ArrayList<String> list=FileUtil.readFileReturn(uri);
		MongoDatabase con=null;
		try {
			con=conn.getMongoDataBase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("mongodb 连接异常...");
		}
		MongoCollection<Document> mongo=con.getCollection(collectionName);
		if (mongo == null) {
			con.createCollection(collectionName);
			mongo = con.getCollection(collectionName);
		}
		for(String str:list){
			Document doc=new Document();
			doc.append("id", str);
			boolean suc=updateCollection(doc,mongo);
			if(!suc){
				System.out.println("失败：  "+str );
			}
		}
		System.out.println("***************************");
	}
	
	  private static boolean updateCollection(Document records,MongoCollection<Document> collectionconn) {
	  		boolean isInsertSuccess = false;
	  		BasicDBObject searchQuery = new BasicDBObject(); // 插入文档更新的条件
	  		try{
		  		if (records.containsKey("id")) {
		  			Object id = records.get("id");
		  			searchQuery.append("id", id);
		  			BasicDBObject updateDocument = new BasicDBObject();
		  			updateDocument.append("$set", records);
		  			UpdateOptions options = new UpdateOptions().upsert(true);
		  			UpdateResult result = collectionconn.updateOne(searchQuery, updateDocument, options);
		  			if (result!= null) {
		  				isInsertSuccess = true;
		  			} 
		  		}
	  		}catch(Exception e){
	  			e.printStackTrace();
	  		}
	  		return isInsertSuccess;
	  	}
	
	
	
}
