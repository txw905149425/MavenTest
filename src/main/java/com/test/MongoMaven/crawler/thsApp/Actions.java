package com.test.MongoMaven.crawler.thsApp;

import java.util.HashMap;
import java.util.Map;

import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
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
	     String html=HttpUtil.getHtml(url, map, "utf8", 1,new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>200){
			String id=IKFunction.md5(html);
			MongoDbUtil mongo=new MongoDbUtil();
//				 //数据源表
//				 jsonResult.remove("id");
//				 jsonResult.remove("code");
//				 jsonResult.remove("html");
//				 jsonResult.put("id", code);
//				 jsonResult.put("ths_crawl", "1");
//				 mongo.upsertMapByTableName(jsonResult,tableName3);
				 //最新数据表
//				 jsonResult.clear();
				 HashMap<String,Object> jsonResult=ParseMethod.parseJson(html);
				 mongo.upsertMapByTableName(jsonResult, "ss_ths_talk_stock_json");
				//总数据表
				 jsonResult.put("id", id);
				 jsonResult.put("stock_code",code );
				 jsonResult.put("website","同花顺" );
				 mongo.upsertMapByTableName(jsonResult, "ss_all_stock_json_count");
				 System.out.println("OoO");
		  
		}
		
		
	}

}
