package com.test.MongoMaven.crawler.ajk;


import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.Jsoup;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;



/***
 * 新浪股吧
 * */
public class CrawlerAJK {
	
	public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document>  collection=mongo.getShardConn("ajk_detail_url");
		 Bson filter = Filters.exists("crawl", false);
		 MongoCursor<Document> cursor =collection.find(filter).batchSize(10000).noCursorTimeout(true).iterator(); 
		 try{
			 while(cursor.hasNext()){
				 Document doc=cursor.next();
				 String url=doc.get("id").toString();
				 Object uid=doc.get("uid");
				 Map<String, String> resultmap=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1);
				 if(resultmap.isEmpty()){
					 
				 }else{
					String html=resultmap.get("html");
					if(!StringUtil.isEmpty(html)&&ParseMethod.htmlFilter(html, ".wrapper>div>h3")){
						 HashMap<String , Object> records= ParseMethod.parseDetail(html);
						 records.put("url", url);
						 records.put("uid",uid);
						 mongo.upsertMapByTableName(records, "ajk_house_information");
						 HashMap<String, Object > map=new HashMap<String, Object>();
						 map.put("id", url);
						 map.put("crawl", "1");
						mongo.upsertMapByTableName(map, "ajk_detail_url");
						 System.out.println("0.0");
					 }else if(ParseMethod.htmlFilter(html, ".info>p")){
						 org.jsoup.nodes.Document dd=Jsoup.parse(html);
						 String text=dd.select(".info>p").get(0).text();
						 System.err.println(text);
					 }else {
						 System.err.println(html);
					 }
				 Thread.sleep(7000);
			 }
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		 cursor.close();
	}
	


	
	
}
