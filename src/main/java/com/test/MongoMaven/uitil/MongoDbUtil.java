package com.test.MongoMaven.uitil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.Jsoup;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

public class MongoDbUtil {
	private HashMap<String, MongoCollection<Document>> mongoCollectionMap = null;
	private MongoDatabase mongoDataBase = null;
	private Set<String> collectionsUniqueIndex ; 
	
	public MongoDbUtil() {
		mongoCollectionMap=new HashMap<String, MongoCollection<Document>>();
		collectionsUniqueIndex = new  HashSet<String>();
	}
	private MongoDatabase getMongoDataBase() throws Exception {
		if (mongoDataBase != null) {
			return mongoDataBase;
		}
		String userdb = "crawler"; // Mongodb认证库
		String username = "group2017"; // Mongodb用户名
		String password = "group2017666"; // Mongodb密码
		String host = "localhost"; // Mongodb服务器地址
		Integer port = 27017; // Mongodb端口
		String dbname = "crawler"; // 使用的数据库名
		ServerAddress svrAddr = new ServerAddress(host, port);
		MongoCredential credential = MongoCredential.createCredential(username,userdb, password.toCharArray());
		@SuppressWarnings("resource")
		MongoClient mongoClient = new MongoClient(svrAddr,Arrays.asList(credential));
		mongoDataBase = mongoClient.getDatabase(dbname);
		return mongoDataBase;
	}
	
	
	
	public MongoCollection<Document> getShardConn(String tableName) {
		MongoCollection<Document> collectionConn = mongoCollectionMap.get(tableName);
			try {
				if (collectionConn != null) {
					return collectionConn;
				}
				// Boolean hasCollection = false;
				MongoDatabase db = getMongoDataBase();
				if (db == null) {
					mongoDataBase = null;
					db = getMongoDataBase()/*.withWriteConcern(WriteConcern.MAJORITY)*/;
				}
				/*
				 * MongoIterable<String> Collections = db.listCollectionNames();
				 * for(String collection: Collections){
				 * if(collection.equals(tableName)){ hasCollection = true; break; }
				 * } if(hasCollection == false){ db.createCollection(tableName); }
				 */
				collectionConn = db.getCollection(tableName)/*.withWriteConcern(WriteConcern.MAJORITY)*/;
				mongoCollectionMap.put(tableName, collectionConn);
				if(!collectionsUniqueIndex.contains(tableName)){
					try{
					ListIndexesIterable<Document> index = collectionConn.listIndexes() ;
					boolean hasUniqueIndex = false ;
						for(Document one : index){
						if(one.getBoolean("id", true)){
							hasUniqueIndex = true ;
							break ;
						}
					}
						if(!hasUniqueIndex){
							Document key = new Document("id", 1) ;
							collectionConn.createIndex(key, new IndexOptions().unique(true).background(true).name("id_unique")) ;	
							collectionsUniqueIndex.add(tableName);
						}else{
							collectionsUniqueIndex.add(tableName);
						}}catch(Exception es){
							
						}
					}
			} catch (Exception e) {
//				RedisApi.error(pool, "LogDBMongoConn", "Exceptions:" + StringUtil.getError(e), Constants.ERROR);
				e.printStackTrace();
			}
			return collectionConn;
		}
	
	public  boolean upsertDocByCollection(Document records,MongoCollection<Document> collectionconn,String tableName) {
	 		boolean isInsertSuccess = false;
	 		BasicDBObject searchQuery = new BasicDBObject(); // 插入文档更新的条件
	 		if (records.containsKey("id")) {
	 			String id = records.get("id").toString();
	 			searchQuery.append("id", id);
	 			records.append("crawl_time", System.currentTimeMillis());
	 			records.remove("id");           //需要插入的数据为删除id后的   records 
	 			BasicDBObject updateDocument = new BasicDBObject();
	 			updateDocument.append("$set", records);
	 			UpdateOptions options = new UpdateOptions().upsert(true);
	 			UpdateResult result = collectionconn.updateOne(searchQuery, updateDocument, options);
	 			if (result!= null) {
	 				isInsertSuccess = true;
//	 				if(result.getMatchedCount()>0){
//						System.out.println("Mongodb写数据：  update << "+tableName+" >> old one: "+id);
//					}else{
//						System.out.println("Mongodb写数据：  insert << "+tableName+" >> old one: "+id);
//					}
	 			} 
	 		}
	 		return isInsertSuccess;
	 }
	
	public  boolean upsertDocByTableName(Document records,String tableName) {
 		boolean isInsertSuccess = false;
 		MongoCollection<Document> collectionconn = getShardConn(tableName);
 		BasicDBObject searchQuery = new BasicDBObject(); // 插入文档更新的条件
 		if (records.containsKey("id")) {
 			String id = records.get("id").toString();
 			searchQuery.append("id", id);
 			records.append("crawl_time", System.currentTimeMillis());
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
	
	
	public  Boolean upsertMapByCollection(HashMap<String, Object> records,MongoCollection<Document> collection,String tableName) {
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
			insertDocument.append("crawl_time", System.currentTimeMillis());
			BasicDBObject updateDocument = new BasicDBObject();
			updateDocument.append("$set", insertDocument);
			UpdateOptions options = new UpdateOptions().upsert(true);
			UpdateResult result = null;
			try{
				result = collection.updateOne(searchQuery, updateDocument, options);
			}catch(Exception es){
				es.printStackTrace();
			}
			
			if (result != null  ) {
				isInsertSuccess = true;
//				if(result.getMatchedCount()>0){
//					System.out.println("Mongodb写数据：  update << "+tableName+" >> old one: "+id4log);
//				}else{
//					System.out.println("Mongodb写数据：  insert << "+tableName+" >> old one: "+id4log);
//				}
			} else {
				System.out.println(" Mongodb ： mongodb update  failed!!! --- "+tableName);
			}
		}
		return isInsertSuccess;
	}
	
	public  Boolean upsertMapByTableName(HashMap<String, Object> records,String tableName) {
		Boolean isInsertSuccess = false;
		MongoCollection<Document> collection = getShardConn(tableName);
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
			insertDocument.append("crawl_time", System.currentTimeMillis());
			BasicDBObject updateDocument = new BasicDBObject();
			updateDocument.append("$set", insertDocument);
			UpdateOptions options = new UpdateOptions().upsert(true);
			UpdateResult result = null;
			try{
				result = collection.updateOne(searchQuery, updateDocument, options);
			}catch(Exception es){
				es.printStackTrace();
			}
			
			if (result != null  ) {
				isInsertSuccess = true;
//				if(result.getMatchedCount()>0){
//					System.out.println("Mongodb写数据：  update << "+tableName+" >> old one: "+id4log);
//				}else{
//					System.out.println("Mongodb写数据：  insert << "+tableName+" >> old one: "+id4log);
//				}
			} else {
				System.out.println(" Mongodb ： mongodb update  failed!!! ---- "+tableName);
			}
		}
		return isInsertSuccess;
	}
	
	
	public Boolean upsetManyDocByCollection(List<Document> recordList,MongoCollection<Document> collection,String tableName) throws Exception {
		Boolean isAllInsert = true;
		if (recordList == null || StringUtil.isEmpty(tableName) || recordList.isEmpty()) {
			System.out.println("tablename or recordList is null or empty!!!");
			return false;
		}
		for (Document record : recordList) {
			record.put("crawl_time", System.currentTimeMillis());
			if(record.containsKey("cookie")){
				record.remove("cookie");
			}
			Boolean isInserted = upsertDocByCollection(record, collection,tableName);
			if (isInserted == false) {
				isAllInsert = false;
			}
		}
		return isAllInsert;
	}
	
	
	public Boolean upsetManyDocByTableName(List<Document> recordList,String tableName) throws Exception {
		Boolean isAllInsert = true;
		if (recordList == null || StringUtil.isEmpty(tableName) || recordList.isEmpty()) {
			System.out.println("tablename or recordList is null or empty!!!");
			return false;
		}
		MongoCollection<Document> collection = getShardConn(tableName);
		for (Document record : recordList) {
			record.put("crawl_time", System.currentTimeMillis());
			if(record.containsKey("cookie")){
				record.remove("cookie");
			}
			Boolean isInserted = upsertDocByTableName(record,tableName);
			if (isInserted == false) {
				isAllInsert = false;
			}
		}
		return isAllInsert;
	}
	
	public Boolean upsetManyMapByCollection(List<HashMap<String, Object>> recordList,MongoCollection<Document> collection,String tableName) throws Exception {
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
			Boolean isInserted = upsertMapByCollection(record, collection,tableName);
			if (isInserted == false) {
				isAllInsert = false;
			}
		}
		return isAllInsert;
	}
	
	public Boolean upsetManyMapByTableName(List<HashMap<String, Object>> recordList,String tableName) throws Exception {
		Boolean isAllInsert = true;
		if (recordList == null || StringUtil.isEmpty(tableName) || recordList.isEmpty()) {
			System.out.println("tablename or recordList is null or empty!!!");
			return false;
		}
		MongoCollection<Document> collection = getShardConn(tableName);
		for (HashMap<String, Object> record : recordList) {
			record.put("crawl_time", System.currentTimeMillis());
			if(record.containsKey("cookie")){
				record.remove("cookie");
			}
			Boolean isInserted = upsertMapByCollection(record, collection,tableName);
			if (isInserted == false) {
				isAllInsert = false;
			}
		}
		return isAllInsert;
	}
	
	/**
	 * 查询通用模块
	 * 
	 * @param tableName
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private FindIterable<Document> queryDocuments(String tableName, Bson filter, String key)  {
		FindIterable<Document> queryResults = null;
		try{
		MongoCollection<Document> collectionconn = getShardConn(tableName);
	    queryResults = collectionconn.find(filter).projection(Projections.include(key)).projection(Projections.excludeId()); // collection查询模块
		}catch(Exception es){
			es.printStackTrace();
//			RedisApi.error(pool, "LogDBMongoQuery", "Exceptions:" + StringUtil.getError(es), log_level);
		}
		return queryResults;
	}

	public Boolean judgeExistsByKV(String tablename, String key, String value) {
		Boolean isExists = false;
		try {
			Bson filter = Filters.eq(key, value); // 查询条件
			Document querydoc = queryDocuments(tablename, filter, key).first();
			if (querydoc != null && !querydoc.isEmpty()) {
				isExists = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
//			RedisApi.error(pool, "LogDBMongoQuery", "Exceptions:" + StringUtil.getError(e), log_level);
		}
		return isExists;
	}
	
	
}
