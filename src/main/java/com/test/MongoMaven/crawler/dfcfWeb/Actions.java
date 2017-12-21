package com.test.MongoMaven.crawler.dfcfWeb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;


public class Actions implements Runnable{
	private DataUtil util;
	private MongoDbUtil mongo;
	public Actions(DataUtil util,MongoDbUtil mongo){
		this.util=util;
		this.mongo=mongo;
	}
	
	
	public void run() {
		HashMap<String, String> map=new HashMap<String, String>();
	     String url=util.getUrl();
	     String code=util.getCode();
	     String name=util.getName();
		Map<String, String> resultmap=HttpUtil.getHtml(url, map, "utf8", 1,new HashMap<String, String>());
		String html=resultmap.get("html");
	try{
		long t1=System.currentTimeMillis();
		if(!StringUtil.isEmpty(html)&&ParseMethod.htmlFilter(html,"div.articleh")){
//			String id=IKFunction.md5(html);
			//页面抓取正常的，写入3张表
				 HashMap<String, Object> oneJsonMap=new HashMap<String, Object>();
				 List<HashMap<String,Object>> listJsonMap=ParseMethod.parseList2(html);
				 oneJsonMap.put("code", code+name);
				 oneJsonMap.put("name", name);
				 List<HashMap<String,Object>> nlist1=new ArrayList<HashMap<String,Object>>();
				 if(!listJsonMap.isEmpty()){
					 oneJsonMap.put("list", listJsonMap);
					 oneJsonMap.put("id",code);
					 mongo.upsertMapByTableName(oneJsonMap, "ss_east_money_stock_json");
					 for(HashMap<String,Object> map1:listJsonMap){
//						 map1.remove("ucontent");
						 map1.remove("website");
						 map1.remove("lastCommentTime");
//						 if(map1.containsKey("flist")){
//							 List<HashMap<String,Object>> list2= (List<HashMap<String,Object>>)map1.get("flist");
//							 List<HashMap<String,Object>> nlist2=new ArrayList<HashMap<String,Object>>();
//							 for(HashMap<String,Object> map2:list2){
////								 map2.remove("content");
//								 nlist2.add(map2);
//							 }
//							map1.put("flist", nlist2);
//						 }
						 nlist1.add(map1);
					 }
				 }
				String id=code+IKFunction.getTimeNowByStr("yyyy-MM-dd")+"dfcf";
				List<HashMap<String,Object>> listall=listCompliet(id,nlist1);
				 //插入汇总表
				 oneJsonMap.put("id",id );
				 oneJsonMap.remove("code");
				 oneJsonMap.put("list", listall);
				 oneJsonMap.put("stock_code",code );
				 oneJsonMap.put("website","东方财富" );
				 oneJsonMap.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				 mongo.upsertMapByTableName(oneJsonMap, "ss_all_speak");
			 }else{
				 //未知信息！！  更新数据源张表  ？待定   （定义换IP重抓）
//				 System.out.println(html);
//				 System.exit(1);
			 }

	}catch(Exception es){
		es.printStackTrace();
	}
		
		
	}
	
	public synchronized List<HashMap<String,Object>> listCompliet(String id,List<HashMap<String,Object>> nlist1){
		MongoCollection<Document>  collection=mongo.getShardConn("ss_all_speak");
		BasicDBObject doc = new BasicDBObject();
        doc.put("id", id);
		MongoCursor<Document> cursor =collection.find(doc).batchSize(10000).noCursorTimeout(true).iterator();
		if(cursor.hasNext()){
			Document d=cursor.next();
			 Object json=d.get("list");
			JSONArray js=JSONArray.fromObject(json);
			List<HashMap<String,Object>> nlist=new ArrayList<HashMap<String,Object>>();//库里的老数据
			for(int i=0;i<js.size();i++){
				HashMap<String,Object> old_map=toHashMap(js.get(i));
				nlist.add(old_map);
			}
			for(HashMap<String,Object> new_map:nlist1){
				String ntext=new_map.get("ucontent").toString().trim();
				String ntime=new_map.get("utime").toString().trim();
				for(int i=0;i<nlist.size();i++){
					HashMap<String,Object> old_map=nlist.get(i);
					String otext=old_map.get("ucontent").toString().trim();
					String otime=old_map.get("utime").toString().trim();
					if(otext.equals(ntext)&&otime.equals(ntime)){//有重复的的数据就把库里的删除
						nlist.remove(i);
					}
			   }
		  }
			nlist1.addAll(nlist);
		}
		return nlist1;
	}
	
	public HashMap<String, Object> toHashMap(Object json){
		JSONObject js=JSONObject.fromObject(json);
		HashMap<String, Object> map=new HashMap<String, Object>();
		Iterator it = js.keys();
		while (it.hasNext()) {
	           String key = String.valueOf(it.next());  
	           Object value=js.get(key);
	           map.put(key, value);
		}
		return map;
	}	
}
