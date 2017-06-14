package com.test.MongoMaven.crawler.ajk.shenzhen.fangzi.durl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.crawler.ajk.thread.ParseMethod;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;


public class CrawlerUrl {
		public static void main(String[] args)  {
			MongoDbUtil mongo=new MongoDbUtil();
			 MongoCollection<Document>  collection=mongo.getShardConn("ajk_guangzhou_list_url");
			 Bson filter = Filters.exists("crawl", false);
			 MongoCursor<Document> cursor =collection.find(filter).batchSize(10000).noCursorTimeout(true).iterator(); 
			 try{
				 while(cursor.hasNext()){
					 Document doc=cursor.next();
					 String url=doc.get("id").toString();
					 String uid=doc.get("uid").toString();
					 if(url==null){
						 continue;
					 }
					 Map<String, String> resultmap=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1,applyProxy());
						String html=resultmap.get("html");
						if(html==null){
							continue;
						}
						if(!StringUtil.isEmpty(html)&&ParseMethod.htmlFilter(html,"p.title>span")){
							List<HashMap<String, Object >>  list=parseList(html,uid);
							mongo.upsetManyMapByTableName(list, "ajk_guangzhou_detail_url");
						    HashMap<String, Object > map=new HashMap<String, Object>();
							map.put("id", url);
							map.put("crawl", "1");
							mongo.upsertMapByTableName(map, "ajk_list_url");
							System.out.println("o.o");
						}else if(ParseMethod.htmlFilter(html, ".info>p")){
							 org.jsoup.nodes.Document dd=Jsoup.parse(html);
							 String text=dd.select(".info>p").get(0).text();
							 System.err.println(text);
						  }
							 
						}
			 }catch(Exception e){
				 e.printStackTrace();
			 }
		  cursor.close();
			 
		}
		public static HashMap<String, String> applyProxy(){
			HashMap<String, String> map=new HashMap<String, String>();
			map.put("ip", "proxy.abuyun.com");
			map.put("port", "9020");
			map.put("user", "H9J817853G9IE02D");
			map.put("pwd", "3687A33E59E93C69");
			map.put("need", "need");
			return map;	
		}
		
		public static List<HashMap<String, Object >> parseList(String html,String uid){
			List<HashMap<String, Object > > list=new ArrayList<HashMap<String,Object>>();
			org.jsoup.nodes.Document doc=Jsoup.parse(html);
			Elements es=doc.select("p.title>span");
			int num=es.size();
			for(int i=0;i<num;i++){
				HashMap<String, Object > map=new HashMap<String, Object >();
				String url=IKFunction.jsoupListAttrByDoc(doc, "p.title>span", "href", i);
				map.put("id",url);
				map.put("uid",uid);
				list.add(map);
			}
			return list;
		}
}
