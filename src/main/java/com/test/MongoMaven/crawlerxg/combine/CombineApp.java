package com.test.MongoMaven.crawlerxg.combine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class CombineApp {
	
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document> collection=mongo.getShardConn("xg_zhibiao_all_web_last_all");
		MongoCollection<Document> collection1=mongo.getShardConn("xg_stock_last_json_all");
		ArrayList<String> list=FileUtil.readFileReturn("mydate");
		for(String date:list){
			BasicDBObject find=new BasicDBObject();	
			find.put("selectime", date);
			MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator();
			MongoCursor<Document> cursor1 =collection1.find(find).batchSize(10000).noCursorTimeout(true).iterator();
			HashMap<String,Document> map1 =new HashMap<String,Document>(); 
			while(cursor.hasNext()){
				Document doc=cursor.next();
				int num=doc.getInteger("supportnum");
				if(num<=2){
					continue;
				}
				String code=doc.getString("code");
				map1.put(code, doc);
			}
			cursor.close();
			HashMap<String,Document> map2 =new HashMap<String,Document>();
			while(cursor1.hasNext()){
				Document doc=cursor1.next();
				String code=doc.getString("code");
				map2.put(code, doc);
			}
			cursor.close();
			
			Map<String, HashMap<String, Object>> map = new HashMap<String, HashMap<String, Object>>();
			for (String key : map1.keySet()) {
				Document doc1=map1.get(key);
				Object id=doc1.get("id");
				Object name=doc1.get("stockName");
				Object selectime=doc1.get("selectime");
				HashMap<String, Object> tmap=new HashMap<String, Object>();
				tmap.put("id", id);
				tmap.put("stockName", name);
				tmap.put("code", key);
				tmap.put("selectime", selectime);
				if(map2.containsKey(key)){
					Document doc2=map2.get(key);
					int num1=doc1.getInteger("supportnum");
					int num2=doc2.getInteger("supportnum");
					int num=num1+num2;
					Object one1=doc1.get("support");
					ArrayList<HashMap<String, Object>> list1=toList(one1);
					Object one2=doc1.get("support");
					ArrayList<HashMap<String, Object>> list2=toList(one2);
					ArrayList<HashMap<String, Object>> last=combineList(list1, list2);
					tmap.put("support", last);
					tmap.put("supportnum", last.size());
				}
				if(!tmap.containsKey("support")){
					Object supportnum=doc1.get("supportnum");	
					Object support=doc1.get("support");	
					tmap.put("support", support);
					tmap.put("supportnum", supportnum);
				}
				map.put(key, tmap);
			}
			
			for (String key : map2.keySet()) {
				if(map.containsKey(key)){
					continue;
				}
				Document doc1=map2.get(key);
				Object id=doc1.get("id");
				Object name=doc1.get("stockName");
				Object selectime=doc1.get("selectime");
				Object supportnum=doc1.get("supportnum");	
				Object support=doc1.get("support");
				HashMap<String, Object> tmap=new HashMap<String, Object>();
				tmap.put("id", id);
				tmap.put("stockName", name);
				tmap.put("code", key);
				tmap.put("selectime", selectime);
				tmap.put("support", support);
				tmap.put("supportnum", supportnum);
				map.put(key, tmap);
			}
			if(!map.isEmpty()){
				for (String key : map.keySet()) {
					HashMap<String, Object> tmap=map.get(key);
					try {
						mongo.upsertMapByTableName(tmap, "xg_all_app_combine");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			
		}
		
		
	}
	public static ArrayList<HashMap<String, Object>> toList(Object ljson){
		ArrayList<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
		int num=IKFunction.rowsArray(ljson);
		for(int i=1;i<=num;i++){
			Object one=IKFunction.array(ljson, i);
			HashMap<String, Object> map=toHashMap(one);
			list.add(map);
		}
		return list;
	}
	
	public static HashMap<String, Object> toHashMap(Object json){
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
	
	public static ArrayList<HashMap<String, Object>> combineList(ArrayList<HashMap<String, Object>> list1,ArrayList<HashMap<String, Object>> list2){
		ArrayList<HashMap<String, Object>> last=new ArrayList<HashMap<String,Object>>();
		ArrayList<String> list=new ArrayList<String>();
		for(HashMap<String, Object> map:list2){
			String ss=map.get("ss").toString().trim();
			list.add(ss);
		}
		for(HashMap<String, Object> map:list1){
			String ss=map.get("ss").toString().trim();
			if(list.contains(ss)){
				continue;
			}
			list.add(ss);
		}
		if(!list.isEmpty()){
			HashMap<String, Object> map=null;
			for(String ss:list){
				map=new HashMap<String, Object>();
				map.put("ss", ss);
				last.add(map);
			}
		}
		return list1;
	}
	
}
