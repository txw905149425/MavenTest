package com.test.MongoMaven.crawler.thsApp;

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
		 map.put("User-Agent","platform=gphone&version=G037.08.216.1.32");
	     map.put("Host","t.10jqka.com.cn");
	     map.put("If-Modified-Since","29 Mar 2017 07:44:28 UTC");
	     String url=util.getUrl();
	     String code=util.getCode();
	     String html=HttpUtil.getHtml(url, map, "utf8", 1,new HashMap<String, String>()).get("html");
//	     System.out.println(html);
		if(!StringUtil.isEmpty(html)&&html.length()>200){
//			String id=IKFunction.md5(html);
//			MongoDbUtil mongo=new MongoDbUtil();
//				 //数据表
				try{ 
				HashMap<String,Object> jsonResult=ParseMethod.parseJson(html);
				 mongo.upsertMapByTableName(jsonResult, "ss_ths_talk_stock_json");
				 List<HashMap<String,Object>> listJsonMap=(List<HashMap<String,Object>>)jsonResult.get("list");
				 List<HashMap<String,Object>> nlist1=new ArrayList<HashMap<String,Object>>();
				 if(!listJsonMap.isEmpty()){
					 for(HashMap<String,Object> map1:listJsonMap){
						 map1.remove("website");
						 map1.remove("lastCommentTime");
//						 if(map1.containsKey("flist")){
//							 List<HashMap<String,Object>> list2= (List<HashMap<String,Object>>)map1.get("flist");
//							 List<HashMap<String,Object>> nlist2=new ArrayList<HashMap<String,Object>>();
//							 for(HashMap<String,Object> map2:list2){
//								 nlist2.add(map2);
//							 }
//							map1.put("flist", nlist2);
//						 }
						 nlist1.add(map1);
					 }
				 }
				 String id=code+IKFunction.getTimeNowByStr("yyyy-MM-dd")+"ths";
				List<HashMap<String,Object>> listall=listCompliet(id,nlist1);
				 //插入汇总表
				jsonResult.put("id",id );
				jsonResult.remove("code");
				jsonResult.put("list", listall);
				jsonResult.put("stock_code",code );
				jsonResult.put("website","同花顺" );
				jsonResult.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				mongo.upsertMapByTableName(jsonResult, "ss_all_speak");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				 System.out.println("OoO");
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
	
	public  HashMap<String, Object> toHashMap(Object json){
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
