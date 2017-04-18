package com.test.MongoMaven.crawler.sina;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
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
	     String url=util.getUrl();
	     String code=util.getCode();
	     String name=util.getName();
		Map<String, String> resultmap=HttpUtil.getHtml(url, map, "utf8", 1);
		String html=resultmap.get("html");
	try{
			 if(!StringUtil.isEmpty(html)&&ParseMethod.htmlFilter(html,".table_content>table>tbody>tr")){//页面抓取正常的，写入3张表
				 MongoDbUtil mongo=new MongoDbUtil();
				 MongoCollection<Document> collection=mongo.getShardConn("sina_talk_stock_json");
//				 PostData post=new PostData();
				 HashMap<String, Object> oneJsonMap=new HashMap<String, Object>();
				 //解析链表页
				 List<HashMap<String,Object>> listJsonMap=ParseMethod.parseList2(html);
				 oneJsonMap.put("code", code+name);
				 oneJsonMap.put("name", name);
//				 oneJsonMap.put("crawl_time", System.currentTimeMillis());
				 oneJsonMap.put("list", listJsonMap);
				 //数据表
				 oneJsonMap.put("id", code);
				 mongo.upsertMapByCollection(oneJsonMap,collection ,"sina_talk_stock_json");
//				 System.exit(1);
				 //更新任务表
//				 Document doc=new Document();
//				 doc.append("id", code);
//				 doc.append("sina_crawl", "1");
//				 mongo.upsertDocByTableName(doc, "stock_code");
				 //插入汇总表
//				 oneJsonMap.remove("id");
//				 oneJsonMap.put("id",id );
//				 oneJsonMap.put("code",code );
//				 mongo.upsertMapByCollection(oneJsonMap, collection, "sina_talk_stock_json_count");//(oneJsonMap, "sina_talk_stock_json_count");
				 
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
