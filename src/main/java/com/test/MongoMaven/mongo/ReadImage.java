package com.test.MongoMaven.mongo;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

public class ReadImage {

	public static void main(String[] args) {
		String userdb = "bigcrawler"; // Mongodb认证库
		String username = "crawler"; // Mongodb用户名
		String password = "bjgdFristCralwer123"; // Mongodb密码
		String host = "trans.workEnv.gaodig.com"; // Mongodb服务器地址
//		String host = "172.16.0.115"  ;// Mongodb服务器地址
		Integer port = 3010; // Mongodb端口
		String dbname = "bigcrawler"; // 使用的数据库名

		ServerAddress svrAddr = new ServerAddress(host,port);
		// auth
		MongoCredential credential = MongoCredential.createCredential(username,userdb, password.toCharArray());
		// conn
//		@SuppressWarnings("resource")
		MongoClient mongoClient = new MongoClient(svrAddr,Arrays.asList(credential));
		DB db = mongoClient.getDB(dbname);
		
		
		
	}
	public static void readFile(DB db) throws IOException{
		DBObject query=new BasicDBObject();
     GridFS gridFs=new GridFS(db,"a_a_test_img");
    //查询的结果：  
     List<GridFSDBFile> listfiles=gridFs.find(query);  
    GridFSDBFile gridDBFile=listfiles.get(0);
    //获得其中的文件名  
    //注意 ： 不是fs中的表的列名，而是根据调试gridDBFile中的属性而来  
    String fileName=(String)gridDBFile.get("filename");  
    System.out.println("从Mongodb获得文件名为："+fileName);  
    File writeFile=new File("d:/a.png");  
    if(!writeFile.exists()){
         writeFile.createNewFile();  
      }  
     
    //把数据写入磁盘中  
    //查看相应的提示  
    gridDBFile.writeTo("d:/a.png");  
    //写入文件中  
    gridDBFile.writeTo(writeFile); 
     
	}
	
}
