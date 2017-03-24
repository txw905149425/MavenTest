package com.test.MongoMaven.db;

import java.util.Arrays;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

public class Collenction1 {
	private static MongoDatabase ShardDataConn = null;
	
	public MongoDatabase getMongoDataBase() throws Exception {
		if (ShardDataConn != null) {
			return ShardDataConn;
		}
		String userdb = "admin"; // Mongodb认证库
		String username = "root"; // Mongodb用户名
		String password = "root"; // Mongodb密码
		String host = "localhost"; // IDC Mongodb服务器地址 外部地址
//		String host = "11112.69.1022.4245"; // IDC Mongodb服务器地址 内部地址
		Integer port = 27017; // Mongodb端口
		String dbname = "root"; // 使用的数据库名
		ServerAddress svrAddr = new ServerAddress(host, port);
		MongoCredential credential = MongoCredential.createCredential(username,userdb, password.toCharArray());
		MongoClient mongoClient = new MongoClient(svrAddr,Arrays.asList(credential));
		ShardDataConn = mongoClient.getDatabase(dbname);

		return ShardDataConn;
	}

}
