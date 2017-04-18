package com.test.MongoMaven.crawler.thsApp;

import java.util.HashMap;
import java.util.Map;
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
		HashMap<String, String> map=new HashMap<String, String>();
		 map.put("User-Agent","platform=gphone&version=G037.08.216.1.32");
	     map.put("Host","t.10jqka.com.cn");
	     map.put("If-Modified-Since","29 Mar 2017 07:44:28 UTC");
	     String url=util.getUrl();
	     String code=util.getCode();
		Map<String, String> resultmap=HttpUtil.getHtml(url, map, "utf8", 1);
		String html=resultmap.get("html");
		if(!StringUtil.isEmpty(html)){
			MongoDbUtil mongo=new MongoDbUtil();
			int htmlCode=ParseMethod.htmlFilter(html);
			 if(htmlCode==0){//页面抓取正常的，写入3张表
				//第一张总数据表
//				 HashMap<String,Object> jsonResult=ParseMethod.parseAllJson(html);
//				 mongo.upsertMapByTableName(jsonResult, tableName1);
//				 //第二张表数据源表
//				 jsonResult.remove("id");
//				 jsonResult.remove("code");
//				 jsonResult.remove("html");
//				 jsonResult.put("id", code);
//				 jsonResult.put("ths_crawl", "1");
//				 mongo.upsertMapByTableName(jsonResult,tableName3);
				 //第三张最新数据表
//				 jsonResult.clear();
				 HashMap<String,Object> jsonResult=ParseMethod.parseJson(html);
				 mongo.upsertMapByTableName(jsonResult, "ths_talk_stock_json");
			 }
			 else if(htmlCode==14){
				 //htmlCode  //更新数据源张表 该论股堂暂时不支持讨论。
				 HashMap<String,Object> jsonResult=new HashMap<String, Object>();
				 jsonResult.put("id", code);
				 jsonResult.put("nonsupport", "1");
				 mongo.upsertMapByTableName(jsonResult, "stock_code");
			 }else{
				 //未知信息！！  更新数据源张表  ？待定   （定义换IP重抓）
			 }
		  
		}
		
		
	}

}
