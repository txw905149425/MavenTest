package com.test.MongoMaven.crawler.ajk.beijing.xiaoqu;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class XiaoquInformation {
	static int threadNum=1;
	public static void main(String[] args) {
		 ExecutorService executor = Executors.newFixedThreadPool(threadNum);
		 MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document>  collection=mongo.getShardConn("ajk_beijing_community_name");
//		 Bson filter = Filters.exists("url", true);
		 Bson filter1 = Filters.exists("crawl", false);
		 MongoCursor<Document> cursor =collection.find(filter1).filter(filter1).batchSize(10000).noCursorTimeout(true).iterator(); 
		 try{
			DataUtil util=null;
			 while(cursor.hasNext()){
				 Document doc=cursor.next();
				 Object code=doc.get("id");//小区名
				 Object  url=doc.get("url");
				 if(url==null||StringUtil.isEmpty(url.toString())){
					 continue;
				 }
				 util=new DataUtil();
				 util.setUrl(url.toString());
				 util.setCode(code.toString());
				 executor.execute(new Actions(util));
			 }
			 cursor.close();
			 executor.shutdown();	
		 }catch (Exception e){
			 e.printStackTrace();
		 }
	}
	
	
	
	 public static boolean htmlFilter(String html,String css){
			boolean flag=false;
			if(IKFunction.isEmptyString(html)){
				return false;
			}
			org.jsoup.nodes.Document doc=Jsoup.parse(html);
			Elements es = doc.select(css);
			if (es.size() > 0) {		
				flag=true;
			}
			return flag;
		}
	 
	 public static HashMap<String, String> applyProxy(){
//			HashMap<String, String> map=new HashMap<String, String>();
//			map.put("ip", "proxy.abuyun.com");
//			map.put("port", "9020");
//			map.put("user", "H9J817853G9IE02D");
//			map.put("pwd", "3687A33E59E93C69");
//			map.put("need", "need");
//			return map;	
			HashMap<String, String> map=new HashMap<String, String>();
			map.put("ip", "proxy.abuyun.com");
			map.put("port", "9020");
			map.put("user", "H8EAM4FP9BPH5M8D");
			map.put("pwd", "D4637C9BA1C25183");
			map.put("need", "need");
			return map;	
		}
	
}
