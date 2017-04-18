package com.test.MongoMaven.uitil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

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


public class MyMongoUtil {
	private Set<String> collectionsUniqueIndex = new  HashSet<String>(); 
	private MongoDatabase mongoDataBase = null;
	private HashMap<String, MongoCollection<Document>> mongoCollectionMap = null;
	String pool = null;
	Integer log_level = 1;
	String host = null; // Mongodb服务器地址

	public MyMongoUtil() {
		mongoCollectionMap = new HashMap<String, MongoCollection<Document>>();
	}

	public MyMongoUtil(String _pool) {if(_pool.startsWith("host:")){
		host = _pool.substring("host:".length());
	}else{
		pool = _pool;
	}
	mongoCollectionMap = new HashMap<String, MongoCollection<Document>>();
	}

	private MongoDatabase getMongoDataBase() throws Exception {
		if (mongoDataBase != null) {
			return mongoDataBase;
		}
		String userdb = "bigcrawler"; // Mongodb认证库
		String username = "crawler"; // Mongodb用户名
		String password = "bjgdFristCralwer123"; // Mongodb密码
		Integer port = 3010; // Mongodb端口
		String dbname = "bigcrawler"; // 使用的数据库名
//		if (com.gaodig.bigcrawler.stormv3.generic.TopologyBase.static_debug_for_local_pc && host == null) {
			host = "trans.workEnv.gaodig.com";
//		}
		ServerAddress svrAddr = new ServerAddress(host, port);
		MongoCredential credential = MongoCredential.createCredential(username, userdb, password.toCharArray());
		@SuppressWarnings("resource")
		MongoClient mongoClient = new MongoClient(svrAddr, Arrays.asList(credential));
//		mongoClient.setWriteConcern(WriteConcern.JOURNALED);
		// WriteConcern.MAJORITY
		mongoDataBase = mongoClient.getDatabase(dbname);
		return mongoDataBase;
	}

	/**
	 * 所有的ShardCollectionConnection存储在一个列表中
	 * 
	 * @param tableName
	 * @return ShardCollectionConn
	 */
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
//			RedisApi.error(pool, "LogDBMongoConn", "Exceptions:" + StringUtil.getError(e), Constants.ERROR);
			e.printStackTrace();
		}
		return collectionConn;
	}

	/**
	 * 一张表一次写入多文档
	 * @param tableName
	 * @param recordList
	 * @return
	 * @throws Exception
	 */
	public Boolean insertMultiDoc(String tableName, JSONArray recordList) throws Exception {
		Boolean isAllInsert = true;
//		log_level = RedisApi.getIntConfig(pool, "config", "logLevel", 1);
		if (recordList == null || StringUtils.isEmpty(tableName) || recordList.isEmpty()) {
//			RedisApi.error(pool, "Log_DBMongo", "tablename or recordList is null or empty!!!", Constants.ERROR);
			return false;
		}
		for (Object recordObject : recordList) {
			JSONObject record = JSONObject.fromObject(recordObject.toString());
			record.put("crawl_time", System.currentTimeMillis());
			if(record.containsKey("cookie")){
				record.remove("cookie");
			}
			Boolean isInserted = insertDoc(tableName, record);
			if (isInserted == false) {
				isAllInsert = false;
			}
		}
		return isAllInsert;
	}

//	public Boolean insertMultiDoc(String tableName, List<HashMap<String,Object>> recordList) throws Exception {
//		return insertMultiDoc( tableName, JSONArray.parseArray(JSON.toJSONString(recordList)));
//		
//	}
	
	public Boolean insertDoc(String tableName, JSONObject records) throws Exception {
		Boolean isInsertSuccess = false;
		try{
		if (records.containsKey("id")) {
			String id = records.get("id").toString();
			if (StringUtils.isEmpty(id)) {
//				RedisApi.error(pool, "Log_DBMongo", "id is null ，can not write into database!!!", Constants.ERROR);
				return false;
			}
			return upsert(tableName, records);
			/*
			 * Boolean isRecordExists = JudgeRcordexists(tableName,id);
			 * //存在记录调取更新记录的相关函数 if(isRecordExists){ isInsertSuccess =
			 * updateExistsDoc(tableName, records); }else { isInsertSuccess =
			 * insertNewDoc(tableName, records); }
			 */
		}}catch(Exception es){
//			RedisApi.error(pool, "LogDBMongoQuery", "Exceptions:" + StringUtil.getError(es), log_level);
			es.printStackTrace();
		}
		return isInsertSuccess;
	}

	/**
	 * id在表中不存在,则插入新记录
	 * 
	 * @param tableName
	 * @param records
	 * @return
	 * @throws Exception
	 */
	private Boolean insertNewDoc(String tableName, HashMap<String, Object> records) throws Exception {
		Boolean isInsertSuccess = false;
		MongoCollection<Document> collectionconn = getShardConn(tableName);
		Document newDoc = new Document();
		if (records.containsKey("id")) {
			// String id = records.get("id").toString();
			for (Entry<String, Object> record : records.entrySet()) {
				String key = record.getKey();
				Object value = record.getValue();
				newDoc.append(key, value);
			}
			collectionconn.insertOne(newDoc);
			isInsertSuccess = true;
		}
		return isInsertSuccess;
	}

	/**
	 * 根据id更新相关文档
	 * 
	 * @param tableName
	 * @param records
	 * @return
	 * @throws Exception
	 */
	public Boolean updateExistsDoc(String tableName, HashMap<String, Object> records) throws Exception {
		Boolean isInsertSuccess = false;
		MongoCollection<Document> collectionconn = getShardConn(tableName);
		BasicDBObject newDocument = new BasicDBObject(); // 需要插入的文档
		BasicDBObject searchQuery = new BasicDBObject(); // 插入文档更新的条件
		for (Entry<String, Object> record : records.entrySet()) {
			String key = record.getKey();
			Object value = record.getValue();
			if (key.equals("id")) { // 存在是否存在的问题
				searchQuery.append("id", value);
				continue;
			}
			if (key.equals("_id")) {
				searchQuery.append("_id", value);
				continue;
			}
			newDocument.append(key, value);
		}
		if (searchQuery.isEmpty()) {
			return false;
		}
		BasicDBObject updateDocument = new BasicDBObject();
		updateDocument.append("$set", newDocument); // 只更新不覆盖原有的字段
		UpdateResult result = collectionconn.updateOne(searchQuery, updateDocument);
		if (result != null && result.getMatchedCount() > 0) {
			isInsertSuccess = true;
		}
		return isInsertSuccess;
	}

	/**
	 * 根据id更新相关文档
	 * 
	 * @param tableName
	 * @param records
	 * @return
	 * @throws Exception
	 */
//	public  Boolean upsert(String tableName, JSONObject records) {
//		Boolean isInsertSuccess = true;
//		MongoCollection<Document> collectionconn = null;
////		DBCollection d = null;
//		String id4log = "";
//		try {
//			collectionconn = getShardConn(tableName);
//		} catch (Exception e) {
////			RedisApi.error(pool, "LogMongoWriteState", "Exception:" + StringUtil.getError(e), log_level);
//		}
//		BasicDBObject newDocument = new BasicDBObject(); // 需要插入的文档
//		BasicDBObject searchQuery = new BasicDBObject(); // 插入文档更新的条件
//		if (records.containsKey("id")) {
//			String id = records.get("id").toString();
//			id4log = id;
//			for (Entry<String, Object> record : records.entrySet()) {
//				String key = record.getKey();
//				Object value = record.getValue();
//				if (key.equals("id")) { // 存在是否存在的问题
//					searchQuery.append("id", id);
//				}
//				newDocument.append(key, value);
//			}
//			BasicDBObject updateDocument = new BasicDBObject();
//			updateDocument.append("$set", newDocument);
//			UpdateOptions options = new UpdateOptions().upsert(true);
//			// UpdateResult result =collectionconn.updateMany(searchQuery,
//			// updateDocument);
//			UpdateResult result = null;
//			try{
//				result = collectionconn.updateOne(searchQuery, updateDocument, options);
//			}catch(Exception es){
//				isInsertSuccess =false;
//			}
//			if (result != null  ) {
//				isInsertSuccess = true;
//				if(result.getMatchedCount()>0){
//					RedisApi.debug(pool, "LogMongoWriteState", "  update "+tableName+" old one: "+id4log, log_level);
//				}else{
//					RedisApi.debug(pool, "LogMongoWriteState", "  insert "+tableName+" new one: "+id4log, log_level);
//				}
//			} else {
//				
//				RedisApi.debug(pool, "LogMongoWriteState", "mongodb update  failed!!! : " + id4log, log_level);
//			}
//		}
//		return isInsertSuccess;
//	}

	public  Boolean upsert(String tableName, Map<String,Object> records) {
		Boolean isInsertSuccess = false;
		MongoCollection<Document> collectionconn = null;
		DBCollection d = null;
		String id4log = "";
		try {
			collectionconn = getShardConn(tableName);
		} catch (Exception e) {
//			RedisApi.error(pool, "LogMongoWriteState", "Exception:" + StringUtil.getError(e), log_level);
		}
		BasicDBObject newDocument = new BasicDBObject(); // 需要插入的文档
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
				newDocument.append(key, value);
			}
			BasicDBObject updateDocument = new BasicDBObject();
			updateDocument.append("$set", newDocument);
			UpdateOptions options = new UpdateOptions().upsert(true);
			// UpdateResult result =collectionconn.updateMany(searchQuery,
			// updateDocument);
			UpdateResult result = null;
			try{
				result = collectionconn.updateOne(searchQuery, updateDocument, options);
			}catch(Exception es){
				
			}
			if (result != null  ) {
				isInsertSuccess = true;
				if(result.getMatchedCount()>0){
//					RedisApi.debug(pool, "LogMongoWriteState", "  update "+tableName+" old one: "+id4log, log_level);
				}else{
//					RedisApi.debug(pool, "LogMongoWriteState", "  insert "+tableName+" new one: "+id4log, log_level);
				}
			} else {
//				RedisApi.debug(pool, "LogMongoWriteState", "mongodb update  failed!!! : " + id4log, log_level);
			}
		}
		return isInsertSuccess;
	}
	/**
	 * 根据id判重
	 * 
	 * @param tablename
	 * @param idValue
	 * @return
	 */
	public Boolean judgeExistsById(String tablename, String idValue) {
		Boolean isExists = false;
		try {
			Bson filter = Filters.eq("id", idValue); // 查询条件
			Document querydoc = queryDocuments(tablename, filter, "id").first() ;
			if (querydoc != null && !querydoc.isEmpty()) {
				isExists = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
//			RedisApi.error(pool, "LogDBMongoQuery", "Exceptions:" + StringUtil.getError(e), log_level);
		}
		return isExists;
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

	public static void main(String[] args) throws Exception {
		MyMongoUtil mongoUtil = new MyMongoUtil();
		Boolean isExists = mongoUtil.judgeExistsById("companyInfo_cleaned", "d上海东方书报刊服务有限公司");
		System.out.println("isExists:"+isExists);
	}
}