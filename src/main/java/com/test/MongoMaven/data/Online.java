package com.test.MongoMaven.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

//判断各个爬虫是否正常运行
public class Online {
	public static MongoDbUtil mongo=new MongoDbUtil();
	public static void main(String[] args) throws FileNotFoundException {
		PrintWriter pw=new PrintWriter(new File("d:/spider_data_count/count.txt"));
		String today=IKFunction.getTimeNowByStr("yyyy-MM-dd");
		String[] weblist={"东方财富","同花顺"};
		for(String web:weblist){
			HashMap<String, Long> map=countSS(today,web);
			long num=map.get(web);
			System.out.println("说说数据: "+web+"在"+today+"抓取到数据:【"+num+"】条");
			pw.println("#说说数据: "+web+"在[ "+today+" ]抓取到数据:【"+num+"】条");
		}
		ArrayList<String> listStr=FileUtil.readFileReturn("website_ww");
		for(String web:listStr){
			web=web.trim();
			HashMap<String, Long> map=countWW(today,web);
			long num=map.get(web);
			System.out.println("问问数据: "+web+"在"+today+"抓取到数据:【"+num+"】条");
			pw.println("$问问数据: "+web+"在["+today+"]抓取到数据:【"+num+"】条");
			
		}
		
		
		pw.close();
	}
	
	public static HashMap<String, Long> countSS(String time,String web){
		HashMap<String, Long> map=new HashMap<String, Long>();
		MongoCollection<org.bson.Document>  coll=mongo.getShardConn("ss_all_speak");
		BasicDBObject doc5 = new BasicDBObject();
		doc5.put("timedel", time);
		doc5.put("website", web);
		long size=coll.count(doc5);
		map.put(web, size);
		return map;
	}
	
	public static HashMap<String, Long> countWW(String time,String web){
		HashMap<String, Long> map=new HashMap<String, Long>();
		MongoCollection<Document>  collection=mongo.getShardConn("ww_ask_online_all");
		BasicDBObject doc5 = new BasicDBObject();
		doc5.put("timedel", time);
		doc5.put("website", web);
		long size=collection.count(doc5);
		map.put(web, size);
		return map;
	}
	
	public static HashMap<String, Long> countXG(String time,String web){
		HashMap<String, Long> map=new HashMap<String, Long>();
		String table="";
		if(){
			
		}
		
		MongoCollection<Document>  collection=mongo.getShardConn("ww_ask_online_all");
		BasicDBObject doc5 = new BasicDBObject();
		doc5.put("timedel", time);
		doc5.put("website", web);
		long size=collection.count(doc5);
		map.put(web, size);
		return map;
	}
	
}
