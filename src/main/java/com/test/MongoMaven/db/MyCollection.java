package com.test.MongoMaven.db;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MyCollection {
	private static MongoDatabase db = null;
	String monoghost = "localhost";
	int monogport = 27017;
	String dbname = "root";
	
	public MongoDatabase getMongoDataBase() throws Exception {
		if (db != null) {
			return db;
		}
		@SuppressWarnings("resource")
		MongoClient mongoClient = new MongoClient(monoghost,monogport);
		db = mongoClient.getDatabase(dbname);
		return db;
	}
	
}
