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
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

public class SpeakStock {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
//		str2TimeMuli("2017-04-17 22:17:15");
		PostData post=new PostData();
		 MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document>  collection=mongo.getShardConn("ss_ths_talk_stock_json");
		 MongoCollection<Document>  collection1=mongo.getShardConn("ss_east_money_stock_json");
//		 MongoCollection<Document>  collection2=mongo.getShardConn("ss_sina_talk_stock_json");
		 MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator(); 
		 try{
				 while(cursor.hasNext()){
					 long t1=System.currentTimeMillis();
					 HashMap<String, Object> records=new HashMap<String, Object>();
					 Document doc=cursor.next();
					 Object id=doc.get("id");
					 Object name=doc.get("name");
//					 System.out.println(id);
					 if("300496".equals(id.toString())){
						System.out.println(id); 
					 }
					 Document filter=new Document("id",id);
					 List<HashMap<String, Object>> listMap=new ArrayList<HashMap<String,Object>>();
					 if(doc.containsKey("list")){
						 Object list=doc.get("list");
						 JSONArray js=JSONArray.fromObject(list);
						 int num=js.size();
						 for(int i=0;i<num;i++){
							 Object  block=js.get(i);
							 HashMap<String, Object> map=toHashMap(block);
							 listMap.add(map);
						 }
					 }
					
					 Document doc1= collection1.find(filter).first();
					 if(doc1!=null&&doc1.containsKey("list")){
						 Object list1=doc1.get("list");
						 JSONArray js1=JSONArray.fromObject(list1);
						 int num=js1.size();
						 for(int i=0;i<num;i++){
							 Object  block=js1.get(i);
							 HashMap<String, Object> map=toHashMap(block);
							 listMap.add(map);
						 }
					 }
					
//						 Document doc2=collection2.find(filter).first();
//						 if(doc2!=null&&doc2.containsKey("list")){
//							 Object list2=doc2.get("list");
//							 JSONArray js2=JSONArray.fromObject(list2);
//							int  num=js2.size();
//							 for(int i=0;i<num;i++){
//								 Object  block=js2.get(i);
//								 HashMap<String, Object> map=toHashMap(block);
//								 listMap.add(map);
//								 
//							 }
//						 } 
					 records.put("id", id);
					 records.put("name", name);
					 records.put("code", id+""+name);
					 records.put("list",listMap);
					 mongo.upsertMapByTableName(records, "test");
					 long t2=System.currentTimeMillis();
					 System.out.println("插入到本地耗时：    "+(t2-t1));
//					 JSONObject json=JSONObject.fromObject(records);
						String su=post.postHtml("http://wisefinance.chinaeast.cloudapp.chinacloudapi.cn:8000/wf/import?type=ss_stock_json",new HashMap<String, String>(), records.toString(), "utf-8", 1);
						if(su.contains("exception")){
							System.err.println("写入数据异常！！！！  < "+su+" >");
						}
						long t3=System.currentTimeMillis();
					 System.out.println("插入到gavinduan耗时：    "+(t3-t2));
//					 txw@jiangcaijing.partner.onmschina.cn
//					 Jcj2017666@
		//			 Collections.sort(listMap, new Comparator<HashMap<String, Object >>() {
		//		            public int compare(HashMap<String, Object > a, HashMap<String, Object > b) {
		//		                String  one =a.get("utime").toString();
		//		                String two = b.get("utime").toString();
		//		                int time=str2TimeMuli(one);
		//		                int time1=str2TimeMuli(two);
		//		                return time1 - time;
		//		            }
					 
//					/go.asp?svid=4&id=4950674&tpages=3&ttimes=1&tzone=8&tcolor=24&sSize=1920,1080&referrer=https%3A//www.baidu.com/link%3Furl%3DodipX6AQ3SQ9-X7U05bfE5xetij_h_WSzzB98oQYKo7usPzkQROBOumhhIFv21Mb%26wd%3D%26eqid%3D871bcdef0001a28f0000000658f9a8d6&vpage=http%3A//www.justwinit.cn/post/6728/&vvtime=1492756710602 HTTP/1.1
		//		        });
		//			 
		//			 for(HashMap<String, Object> map:listMap){
		//				 System.out.println(map.toString());
		//			 }
		//			 
		//			 System.exit(1);
				 }
		 }catch(Exception e){
			 System.out.println("哦哦哦，有网站没有评论哟！！");
			 e.printStackTrace();
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
