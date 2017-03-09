package com.test.MongoMaven.mongo;


import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.Collenction1;

public class Test {

	public static void main(String[] args) {
		Collenction1 coll=new Collenction1();
		test(coll);
	}
	
	
	public static void test(Collenction1 coll){
		MongoDatabase IDCmongo=null;
		try {
			IDCmongo=coll.getMongoDataBase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MongoCollection<Document> mogoCollection=IDCmongo.getCollection("tianyanchaJsonCompanyBase_index");

//	    Bson bson=new BasicDBObject();
	    
	    BasicDBObject keys = new BasicDBObject();
	    keys.put("legal_person", 1);
//	    keys.put("id", 1);
	    MongoCursor<Document> cursor=mogoCollection.find().projection(keys).batchSize(100000).noCursorTimeout(true).iterator();
	    while(cursor.hasNext()){
	    	Document doc=cursor.next();
	    	System.out.println(doc.toString());
	    }
//	    DBCursor cursor = dao.getMongoTemplate().getCollection("status").find(condition, keys).addOption(Bytes.QUERYOPTION_NOTIMEOUT);

//	    Iterator<DBObject> iterator = cursor.iterator();
		
	}
	
}
