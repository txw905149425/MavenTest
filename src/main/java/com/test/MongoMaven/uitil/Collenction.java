package com.test.MongoMaven.uitil;

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
		String userdb = "sdsdsd"; // Mongodb认证库
		String username = "testtttt"; // Mongodb用户名
		String password = "1223t3t"; // Mongodb密码
		String host = "1764154.41654646.4"; // Mongodb服务器地址
		Integer port = 15164; // Mongodb端口
		String dbname = "test"; // 使用的数据库名
		ServerAddress svrAddr = new ServerAddress(host, port);
		MongoCredential credential = MongoCredential.createCredential(username,userdb, password.toCharArray());
		@SuppressWarnings("resource")
		MongoClient mongoClient = new MongoClient(svrAddr,Arrays.asList(credential));
		ShardDataConn = mongoClient.getDatabase(dbname);
		return ShardDataConn;
	}

}



