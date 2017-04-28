package com.test.MongoMaven.crawler.ajk.thread;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;

import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Actions implements Runnable{
	private DataUtil util;
	
	public Actions(DataUtil util){
		this.util=util;
	}
	public void run() {
		// TODO Auto-generated method stub
		
		String url=util.getUrl();
		String uid=util.getCode();
		try{
				 Map<String, String> resultmap=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1,applyProxy());
				 if(resultmap.isEmpty()){
					 
				 }else{
					String html=resultmap.get("html");
					if(!StringUtil.isEmpty(html)&&ParseMethod.htmlFilter(html, ".wrapper>div>h3")){
						MongoDbUtil mongo=new MongoDbUtil();
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
			 } 
		 }catch(Exception e){
		 
	 }

		
		
		
	}
	
	public static HashMap<String, String> applyProxy(){
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("ip", "proxy.abuyun.com");
		map.put("port", "9020");
		map.put("user", "H82OD0G5138892VD");
		map.put("pwd", "D2FA69ADD68A4853");
		map.put("need", "need");
		return map;	
		
	}
	
//	public static HashMap<String, String> applyProxy1(){
//		HashMap<String, String> map=new HashMap<String, String>();
//		map.put("ip", "proxy.abuyun.com");
//		map.put("port", "9010");
//		map.put("user", "HOQB6443940X7DJP");
//		map.put("pwd", "64597F78C50587EB");
//		map.put("need", "need");
//		return map;	
//		
//	}
	
}