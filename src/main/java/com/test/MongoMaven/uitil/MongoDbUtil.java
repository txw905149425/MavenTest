package com.test.MongoMaven.uitil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;
import org.jsoup.Jsoup;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

public class MongoDbUtil {
	
	
	  
	
	private  boolean upsertDOC(Document records,MongoCollection<Document> collectionconn) {
	 		boolean isInsertSuccess = false;
	 		BasicDBObject searchQuery = new BasicDBObject(); // 插入文档更新的条件
	 		if (records.containsKey("id")) {
	 			String id = records.get("id").toString();
	 			searchQuery.append("id", id);
	 			records.remove("id");           //需要插入的数据为删除id后的   records 
	 			BasicDBObject updateDocument = new BasicDBObject();
	 			updateDocument.append("$set", records);
	 			UpdateOptions options = new UpdateOptions().upsert(true);
	 			UpdateResult result = collectionconn.updateOne(searchQuery, updateDocument, options);
	 			if (result!= null) {
	 				isInsertSuccess = true;
	 			} 
	 		}
	 		return isInsertSuccess;
	 }
	
	
	public  Boolean upsertMap(HashMap<String, Object> records,MongoCollection<Document> collection,String tableName) {
		Boolean isInsertSuccess = false;
		DBCollection d = null;
		String id4log = "";
		BasicDBObject insertDocument = new BasicDBObject(); // 需要插入的文档
		BasicDBObject searchQuery = new BasicDBObject(); // 插入文档更新的条件
		if (records.containsKey("id")) {
			String id = records.get("id").toString();
			id4log = id;
			for (Entry<String, Object> record : records.entrySet()) {
				String key = record.getKey();
				Object value = record.getValue();
				if (key.equals("id")) { // 存在是否存在的问题
					searchQuery.append("id", id);
				}
				insertDocument.append(key, value);
			}
			BasicDBObject updateDocument = new BasicDBObject();
			updateDocument.append("$set", insertDocument);
			UpdateOptions options = new UpdateOptions().upsert(true);
			UpdateResult result = null;
			try{
				result = collection.updateOne(searchQuery, updateDocument, options);
			}catch(Exception es){
				
			}
			
			if (result != null  ) {
				isInsertSuccess = true;
				if(result.getMatchedCount()>0){
					System.out.println("Mongodb写数据：  update "+tableName+" old one: "+id4log);
				}else{
					System.out.println("Mongodb写数据：  insert "+tableName+" old one: "+id4log);
				}
			} else {
				System.out.println(" Mongodb ： mongodb update  failed!!! "+tableName);
			}
		}
		return isInsertSuccess;
	}
	
	
	public Boolean insertManyDoc(List<HashMap<String, Object>> recordList,MongoCollection<Document> collection,String tableName) throws Exception {
		Boolean isAllInsert = true;
		if (recordList == null || StringUtil.isEmpty(tableName) || recordList.isEmpty()) {
			System.out.println("tablename or recordList is null or empty!!!");
			return false;
		}
		for (HashMap<String, Object> record : recordList) {
			record.put("crawl_time", System.currentTimeMillis());
			if(record.containsKey("cookie")){
				record.remove("cookie");
			}
			Boolean isInserted = upsertMap(record, collection,tableName);
			if (isInserted == false) {
				isAllInsert = false;
			}
		}
		return isAllInsert;
	}
	
	
	
	
}
