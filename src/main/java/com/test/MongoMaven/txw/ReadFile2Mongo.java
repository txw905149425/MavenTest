package com.test.MongoMaven.txw;

import java.util.ArrayList;

import net.sf.json.JSONObject;

import org.bson.Document;
import org.jboss.netty.handler.codec.http.multipart.FileUpload;

import com.mongodb.BasicDBObject;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.test.MongoMaven.db.Collenction1;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class ReadFile2Mongo {
	
	public static void main(String[] args) {
		 Collenction1 IDCcollection=new Collenction1();
		 MongoDatabase IDCmongo=null;
		 try {
			 IDCmongo=IDCcollection.getMongoDataBase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 MongoCollection<Document> mogoCollection=IDCmongo.getCollection("task_keyword1");
		 FileUtil fu=new FileUtil();
		 ArrayList<String> str=null;
		 for(int i=1;i<41;i++){
			System.out.println(i);
			str=fu.readFileReturn("d:/key/name_result"+i);
			for(String json:str){
				JSONObject js=JSONObject.fromObject(json);
				Object id=js.get("word");
				Object name=js.get("name");
				if(!StringUtil.isEmpty(id.toString())){
					Document doc=new Document();
					doc.append("id", id);
					doc.append("name", name);
					boolean flag=upsertDOC(doc,mogoCollection);
//					System.out.println(id+"  "+name);
					
				}
			}
		 }

	}
	
	private static boolean upsertDOC(Document records,MongoCollection<Document> collectionconn) {
 		boolean isInsertSuccess = false;
 		
 		BasicDBObject searchQuery = new BasicDBObject(); // 插入文档更新的条件
 		if (records.containsKey("id")) {
 			String id = records.get("id").toString();
 			searchQuery.append("id", id);
// 			records.remove("id");           //需要插入的数据为删除id后的   records 
 			BasicDBObject updateDocument = new BasicDBObject();
 			updateDocument.append("$set", records);
 			UpdateOptions options = new UpdateOptions().upsert(true);
 			try{
 				UpdateResult result = collectionconn.updateOne(searchQuery, updateDocument, options);
	 			if (result!= null) {
	 				isInsertSuccess = true;
	 			} 
 			}catch(Exception e){
 				e.printStackTrace();
 				System.out.println(records.get("id"));
 			}
 			
 		}
 		return isInsertSuccess;
 	}
	
	
	
	
}
