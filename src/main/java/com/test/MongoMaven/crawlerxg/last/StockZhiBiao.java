package com.test.MongoMaven.crawlerxg.last;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class StockZhiBiao {
	static MongoDbUtil mongo=new MongoDbUtil();
  public static void main(String[] args) {
	try{
		ArrayList<String> list=FileUtil.readFileReturn("/home/jcj/crawler/xg_bat/zhibiao");
		ArrayList<HashMap<String, Object >> dlist=new  ArrayList<HashMap<String,Object>>();
		for(String str:list){
			MongoCollection<Document> collection=mongo.getShardConn(str);
			 MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator();
			 while(cursor.hasNext()){
				 Document doc=cursor.next();
				 HashMap<String, Object > records=new HashMap<String, Object>();
				 Object name= doc.get("stockName");
				 String scode1= doc.get("code").toString();
				 Object supp= doc.get("supportnum");
				 Object time= doc.get("selectime");
				 Object website= doc.get("website");
					 records.put("stockName", name);
					 records.put("code", scode1);
					 records.put("supportnum", supp);
					 records.put("website", website);
					 records.put("time", time);
					 dlist.add(records);
			 }
			 cursor.close();
		}
		
		HashMap<String, HashMap<String, Object >> s=new HashMap<String, HashMap<String,Object>>();
		
		for(HashMap<String, Object> records:dlist){
			Object stockName=records.get("stockName");
			String code=records.get("code").toString();
			Object supportnum=records.get("supportnum");
			int su=Integer.parseInt(supportnum.toString());
			Object website=records.get("website");
			Object time=records.get("time");
			HashMap<String, Object > map1=new HashMap<String, Object>();
			map1.put("ss", website);
			List<HashMap<String, Object >> list1=null;
			if(s.containsKey(code)){
				HashMap<String, Object > tmp=s.get(code);
				Object tnum=tmp.get("supportnum");
				list1=(List<HashMap<String, Object >>)tmp.get("list");
				 list1.add(map1);
				int su1=Integer.parseInt(tnum.toString());
				 su=su+su1;
			}else{
				list1=new ArrayList<HashMap<String,Object>>();
				list1.add(map1);
			}
			records.put("stockName", stockName);
		    records.put("id", code+time);
			records.put("supportnum", su);
			records.put("code", code);
			records.put("time", time);
			records.put("list", list1);
			s.put(code,records);
		}
		
//		 MongoCollection<Document> collectiondele=mongo.getShardConn("xg_all_zhibiao_complite");
//		 MongoCursor<Document> cursordele =collectiondele.find().batchSize(10000).noCursorTimeout(true).iterator();
//		 while(cursordele.hasNext()){
//			   Document d  = cursordele .next(); //遍历每一条数据
//			   d.remove("_id");
//			   mongo.upsertDocByTableName(d, "xg_all_zhibiao_complite_all");
//		 }
	   mongo.getShardConn("xg_all_zhibiao_complite").deleteMany(Filters.exists("id"));
		
		for(Map.Entry<String, HashMap<String, Object >> entry : s.entrySet()){  
//            System.out.println(entry.getKey()+"="+entry.getValue());
			HashMap<String,Object> records=entry.getValue();
			mongo.upsertMapByTableName(records, "xg_all_zhibiao_complite");
			mongo.upsertMapByTableName(records, "xg_all_zhibiao_complite_all");
        }  
		
	}catch(Exception e){
			e.printStackTrace();
		}
}
	
	
}
