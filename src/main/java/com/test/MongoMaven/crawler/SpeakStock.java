package com.test.MongoMaven.crawler;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.crawler.sina.Actions;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class SpeakStock {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
//		str2TimeMuli("2017-04-17 22:17:15");
		 MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document>  collection=mongo.getShardConn("ths_talk_stock_json");
		 MongoCollection<Document>  collection1=mongo.getShardConn("east_money_stock_json");
		 MongoCollection<Document>  collection2=mongo.getShardConn("sina_talk_stock_json");
		 MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator(); 
		 while(cursor.hasNext()){
			 HashMap<String, Object> records=new HashMap<String, Object>();
			 Document doc=cursor.next();
			 Object id=doc.get("id");
			 Object name=doc.get("name");
			 System.out.println(id);
			 Document filter=new Document("id",id);
			 Document doc1= collection1.find(filter).first();
			 Document doc2= collection2.find(filter).first();
			 Object list=doc.get("list");
			 Object list1=doc1.get("list");
			 Object list2=doc2.get("list");
			 List<HashMap<String, Object>> listMap=new ArrayList<HashMap<String,Object>>();
			 JSONArray js=JSONArray.fromObject(list);
			 int num=js.size();
			 for(int i=0;i<num;i++){
				 Object  block=js.get(i);
				 HashMap<String, Object> map=toHashMap(block);
				 listMap.add(map);
			 }
			 JSONArray js1=JSONArray.fromObject(list1);
			 num=js1.size();
			 for(int i=0;i<num;i++){
				 Object  block=js1.get(i);
				 HashMap<String, Object> map=toHashMap(block);
				 listMap.add(map);
			 }
			 JSONArray js2=JSONArray.fromObject(list2);
			 num=js2.size();
			 for(int i=0;i<num;i++){
				 Object  block=js2.get(i);
				 HashMap<String, Object> map=toHashMap(block);
				 listMap.add(map);
				 
			 }
			 Collections.sort(listMap, new Comparator<HashMap<String, Object >>() {
		            public int compare(HashMap<String, Object > a, HashMap<String, Object > b) {
		                String  one =a.get("utime").toString();
		                String two = b.get("utime").toString();
		                int time=str2TimeMuli(one);
		                int time1=str2TimeMuli(two);
		                return time1 - time;
		            }
		        });
			 
			 for(HashMap<String, Object> map:listMap){
				 System.out.println(map.toString());
			 }
			 
			 System.exit(1);
			 
			 
		 }
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
	
	public static void combineData(List<HashMap<String, Object>> list){
		int num=list.size();
		for(int i=0;i<num-1;i++){
			HashMap<String, Object>  map=list.get(i);
			String  timeStr=map.get("ctime").toString();
			long time=str2TimeMuli(timeStr);
			for(int j=0;j<num-1-i;j++){
				HashMap<String, Object>  map1=list.get(j);
				String  timeStr1=map1.get("ctime").toString();
//				Long time1=str2TimeMuli(timeStr1);
//				if(time1>time){
////					HashMap<String, Object> tmp=list[i];
//					System.out.println();
//				}
			}
			
//			Collections.sort(list, new Comparator() {
//				public int compare(Object o1, Object o2) {
//					HashMap<K, V>
//					return 0;
//				}
//				
//			});
			
		}
		
	}
	
	
	public static int str2TimeMuli(String str){
		if(str.contains("-")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date d=sdf.parse(str);
				int dd=(int)d.getTime()/1000;
				return dd;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			return Integer.parseInt(str);
		}
		return 0;
	}
	
	
}
