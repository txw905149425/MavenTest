package com.test.MongoMaven.db;

import java.util.Arrays;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
/**
 * 获取mongodb的连接
 * */
public class Collenction {
	private static MongoDatabase ShardDataConn = null;
	String monoghost = "";
	
	public MongoDatabase getMongoDataBase() throws Exception {
		if (ShardDataConn != null) {
			return ShardDataConn;
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
		ShardDataConn = mongoClient.getDatabase(dbname);
		return ShardDataConn;
	}
	
	

}



