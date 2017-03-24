package com.test.MongoMaven.txw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.bson.BasicBSONObject;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.test.MongoMaven.db.Collenction1;
import com.test.MongoMaven.db.MysqlCollection;

public class MongoDbClean {

static String regexp="(^[A-Za-z0-9]+$)|(.*社$)|(.*站$)|(.*家$)|(.*路$)|(.*街$)|(.*道$)|(.*煤矿$)|(.*室$)|(.*号$)|(.*口$)|(.*客车$)|(.*楼$)|(.*厅$)|(.*行$)|(.*园$)|(.*店$)|(.*所$)|(.*庄 $)|(.*部$)|(.*市$)|(.*屋$)|(.*场$)|(.*摊$)|(.*通讯$)|(.*点$)|(.*处$)|(.*中心$)|(.*户$)|(.*坊$)|(.*房$)|(.*基地$)|(.*养殖$)|(.*队$)|(.*吧$)|(.*照明 $)|(.*货运$)|(.*院$)|(.*木艺$)|(.*城$)|(.*ＫＴＶ$)|(.*KTV$)|(.*门窗$)|(.*库$)|(.*服饰$)|(.*服装$)|(.*总汇$)|(.*业$)|(.*部）$)|(.*摊床$)|(.*代办$)|(.*电器 $)|(.*寄賣$)|(.*寄卖$)|(.*铺$)|(.*内衣 $)|(.*经销$)|(.*装潢$)|(.*馆$)|(.*斋$)|(.*大棚$)|(.*组$)|(.*班$)|(.*间$)|(.*家私$)|(.*柜$)|(.*分厂$)|(.*团$)|(.*画廊$)|(.*会$)|(.*苗圃$)|(.*阁$)|(.*运输$)|(.*村$)|(.*分公司.*)|(.*经销部.*)|(.*电站.*)|(.*分厂.*)";
	
	public static void main(String[] args) {
    	 Collenction1 IDCcollection=new Collenction1() ;
//    	 MysqlCollection  mycoll = new MysqlCollection();  
//    	 String oldCollection="tianyanchaJsonCompanyBase_index";
//    	 String newCollection="tyc_top_manager";
//    	 String fileName="legal_person";
//    	 copyFileName2Mongo(IDCcollection,fileName,oldCollection,newCollection);    
    	 String oldCollection="lost_credit_info";
    	 String newCollection="txw_lost_credit_info2";
    	 String fileName="legal_person";
    	 cleanMongoDB(IDCcollection, fileName, oldCollection, newCollection);
//    	 String name="上海金山钢材市场经营部管理有限公司";
//    	 System.out.println(!regexpMatch(name));
    	 
	}
	
	
     public static void copyFileName2Mongo(Collenction1 IDCcollection,String fileName,String oldCollection,String newCollection){
    	 boolean flag=false;
     	MongoDatabase IDCmongo=null;
     	try {
 			IDCmongo=IDCcollection.getMongoDataBase();
 		} catch (Exception e) {
 			try{
 				System.out.println("IDC服务器mogodb连接异常，尝试重新连接...");
 				IDCmongo=IDCcollection.getMongoDataBase();
 			}catch(Exception e1) {
 				System.out.println("IDC服务器mogodb连接异常...");
 				e1.printStackTrace();
 				System.exit(0);
 			}
 		}    	
     try{ 
     	MongoCollection<Document> mogoCollection=IDCmongo.getCollection(oldCollection);                 //数据源的collection表 
     	
     	MongoCollection<Document> newMogoCollection=IDCmongo.getCollection(newCollection);   //清理后存储的collection表
     	
     	BasicDBObject need=new BasicDBObject();             //取出需要的字段          
     	need.put(fileName, 1);
//     	Bson filter = Filters.exists("cleanDown", false);
     	MongoCursor<Document> cursor =mogoCollection.find().projection(need).batchSize(10000).noCursorTimeout(true).iterator(); 
     	while(cursor.hasNext()){    		
     		Document doc=cursor.next();
     		if(doc.containsKey(fileName)){
     			String value="";
     			try{
     				value=doc.get(fileName).toString();
     			}catch(Exception e){
     				System.out.println(">>>>>: "+value);
     			}
     			if(!regexpMatch(value)){
	     			Document insertDoc=new Document();
	     			insertDoc.append("id", value);
	     			boolean success=upsertDOC(insertDoc,newMogoCollection);
	     			if(success){
	     				System.out.println("更新成功:  "+value);
	     			}else{
	     				System.out.println("更新失败:  "+value);
	     			}
     			}
     		}
     		
     	}
     	cursor.close();
     	flag=true;
     	}catch(Exception e){
     		e.printStackTrace();
     		flag=false;
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
 			UpdateResult result = collectionconn.updateOne(searchQuery, updateDocument, options);
 			if (result!= null) {
 				isInsertSuccess = true;
 			} 
 		}
 		return isInsertSuccess;
 	}
     
     
     
     private static boolean regexpMatch(String value) {
  		boolean ismatched= false;
  		StringBuffer str=new StringBuffer();
//  		str.
  		if(value==""){
  			return true;  //在此需要返回true 
  		}
  		 Pattern p =Pattern.compile(regexp);
		  Matcher m = p.matcher(value.toString().trim());
			if (m.find()) {
				ismatched=true;
//				System.out.println(">"+m.group()+"<");
			}
  		return ismatched;
  	}
     
     public static void cleanMongoDB(Collenction1 IDCcollection,String fileName,String oldCollection,String newCollection){
    	 boolean flag=false;
     	MongoDatabase IDCmongo=null;
     	try {
 			IDCmongo=IDCcollection.getMongoDataBase();
 		} catch (Exception e) {
 			try{
 				System.out.println("IDC服务器mogodb连接异常，尝试重新连接...");
 				IDCmongo=IDCcollection.getMongoDataBase();
 			}catch(Exception e1) {
 				System.out.println("IDC服务器mogodb连接异常...");
 				e1.printStackTrace();
 				System.exit(0);
 			}
 		}    	
     try{ 
     	MongoCollection<Document> mogoCollection=IDCmongo.getCollection(oldCollection);                 //数据源的collection表 
     	MongoCollection<Document> oldMogoCollection=IDCmongo.getCollection(oldCollection); 
     	MongoCollection<Document> newMogoCollection=IDCmongo.getCollection(newCollection);   //清理后存储的collection表
     	
//     	BasicDBObject need=new BasicDBObject();             //取出需要的字段          
//     	need.put(fileName, 1);
     	Bson filter = Filters.exists(fileName,false);
     	MongoCursor<Document> cursor =mogoCollection.find(filter).batchSize(10000).noCursorTimeout(true).iterator(); 
     	while(cursor.hasNext()){    		
     		Document doc=cursor.next();
     		Document oldDoc=cursor.next();
     		if(doc.containsKey("id")){
     			Object id=doc.get("id");
     			doc.remove("_id");
	     		boolean success=cleanDoc(doc,newMogoCollection);
	     		if(success){
	     			oldDoc.append(fileName, "1111");
	     			upsertDOC(oldDoc,oldMogoCollection);
	     			System.out.println("更新成功:  "+id);
	     		}else{
	     			System.out.println("更新失败:  "+id);
	     		}
     		}
     		
     	}
     	cursor.close();
     	flag=true;
     	}catch(Exception e){
     		e.printStackTrace();
     		flag=false;
     	} 
     }
          
     private static boolean cleanDoc(Document records,MongoCollection<Document> collectionconn) {
  		boolean isInsertSuccess = false;
  		BasicDBObject searchQuery = new BasicDBObject(); // 插入文档更新的条件
  		try{
	  		if (records.containsKey("basicInformation")) {
	  			Object basicInformation = records.get("basicInformation");
	  			String id=keyVal(basicInformation,"caseCode").toString();
	  			searchQuery.append("id", id);
	  			records.remove("id");           //需要插入的数据为删除id后的   records 
	  			records.append("id", id);
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
     
  
     
     public static Object keyVal(Object obj, Object key) {
 		if (key.toString().contains("bOcr")) {
 			JSONObject json = (JSONObject) obj;
 			Object value = json.get(key.toString());
 			System.out.println();
 		}
 		Object value = null;
 		if (obj instanceof JSONObject) {
 			JSONObject json = (JSONObject) obj;
 			value = json.get(key.toString());
 		} else {
 			try {
 				JSONObject json = JSONObject.fromObject(obj);
 				value = json.get(key.toString());
 			} catch (Exception e) {
 			}
 		}
 		return value == null ? "" : value;
 	}
     
}
