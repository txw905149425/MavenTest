package com.test.MongoMaven.crawler.ajk.shanghai.xiaoqu;

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
	static int threadNum=50;
	public static void main(String[] args) {
		 ExecutorService executor = Executors.newFixedThreadPool(threadNum);
		 MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document>  collection=mongo.getShardConn("ajk_shanghai_community_name");
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
				 executor.execute(new Actions(util,mongo));
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
	 
	 	
}
