package com.test.MongoMaven.txw.mongo;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.test.MongoMaven.uitil.Collenction1;
import com.test.MongoMaven.uitil.FileUtil;

public class FileWriteToMongo {
   
	
	public static void main(String[] args) {
		String collectionName="task_keyword";
		String uri="d:/4阶分词.txt";
		Collenction1 conn=new Collenction1();
		file2Mongo(conn,collectionName,uri);
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
