package com.test.MongoMaven.crawler.dfcfWeb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;


public class Actions implements Runnable{
	private DataUtil util;
	public Actions(DataUtil util){
		this.util=util;
	}
	
	
	public void run() {
		HashMap<String, String> map=new HashMap<String, String>();
	     String url=util.getUrl();
	     String code=util.getCode();
	     String name=util.getName();
		Map<String, String> resultmap=HttpUtil.getHtml(url, map, "utf8", 1,new HashMap<String, String>());
		String html=resultmap.get("html");
	try{
		if(!StringUtil.isEmpty(html)&&ParseMethod.htmlFilter(html,"div.articleh")){
			String id=IKFunction.md5(html);
			//页面抓取正常的，写入3张表
				MongoDbUtil mongo=new MongoDbUtil();
				 MongoCollection<Document> collection=mongo.getShardConn("ss_east_money_stock_json");
//				PostData post=new PostData();
				 HashMap<String, Object> oneJsonMap=new HashMap<String, Object>();
				 List<HashMap<String,Object>> listJsonMap=ParseMethod.parseList2(html);
				 oneJsonMap.put("code", code+name);
				 oneJsonMap.put("name", name);
				 if(!listJsonMap.isEmpty()){
					 oneJsonMap.put("list", listJsonMap);
				 }
//				JSONObject json=JSONObject.fromObject(oneJsonMap);
//				String su=post.postHtml("http://gavinduan.mynetgear.com:8000/wf/import?type=talk_stock_json", json, "utf-8", 1);
//				if(su.contains("exception")){
//					System.err.println("写入数据异常！！！！  < "+su+" >");
//				}
//				System.err.println(su);
				oneJsonMap.put("id",code);
				//最新数据表
				 mongo.upsertMapByCollection(oneJsonMap,collection,"ss_east_money_stock_json");
				 //更新任务表
//				 Document doc=new Document();
//				 doc.append("id", code);
//				 doc.append("east_crawl", "2");
//				 mongo.upsertDocByTableName(doc, "stock_code");
				 //插入汇总表
				 oneJsonMap.put("id",id );
				 oneJsonMap.put("stock_code",code );
				 oneJsonMap.put("website","东方财富" );
				 mongo.upsertMapByTableName(oneJsonMap, "ss_all_stock_json_count");
			 }else{
				 //未知信息！！  更新数据源张表  ？待定   （定义换IP重抓）
//				 System.out.println(html);
//				 System.exit(1);
			 }

	}catch(Exception es){
		es.printStackTrace();
	}
		
		
	}

}
