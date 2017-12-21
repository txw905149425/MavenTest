package com.test.MongoMaven.crawlerxg.last;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class ComplieStock {
	
	static MongoDbUtil mongo=new MongoDbUtil();
	static PostData post=new PostData();
	//循环取title(指标)
	@SuppressWarnings({ "null", "deprecation", "unchecked" })
	public static void main(String[] args) {
	  try{
		 mongo.getShardConn("xg_good_all_web").deleteMany(Filters.exists("id"));
		ArrayList<String> list=FileUtil.readFileReturn("/home/jcj/crawler/xg_bat/name_good");
//		ArrayList<String> list=FileUtil.readFileReturn("name_good");
		for(String table:list){
			insert2Table(table);
		}
		complie2Table("xg_good_all_web");
	  }catch(Exception e){
		e.printStackTrace();
	  }
	}
	
	public static void insert2Table(String table) throws ClientProtocolException, IOException{
		MongoCollection<Document> collection=mongo.getShardConn(table);
//		Bson filter = Filters.eq("time", condition);
		try{
		 MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator(); 
		 Document doc=null;
		 HashMap<String, Object > records=null;
		 while(cursor.hasNext()){
			 doc=cursor.next();
			 Object title=doc.get("title");
			 Object time=doc.get("time");
			 Object website=doc.get("website");
			 if(doc.containsKey("list")){
				 Object list=doc.get("list");
				 int num=IKFunction.rowsArray(list);
				 for(int i=1;i<=num;i++){
					 records=new HashMap<String, Object>();
					 Object one=IKFunction.array(list, i);
					 Object name=IKFunction.keyVal(one, "stockName");
					 if(name.toString().contains("ST")||name.toString().contains("st")){
						 continue;
					 }
					 Object code=IKFunction.keyVal(one, "stockCode");
					 records.put("id", title+""+code+time+website);
					 records.put("website", website);
					 records.put("stockName", name);
					 records.put("code", code);
					 records.put("title", title);
					 records.put("selectime", time);
					 mongo.upsertMapByTableName(records,"xg_good_all_web");
				 }
			 }else{
				 Object code=doc.get("stockCode");
				 Object name=doc.get("stockName");
				 records=new HashMap<String, Object>();
				 records.put("id", title+""+code+time+website);
				 records.put("website", website);
				 records.put("stockName", name);
				 records.put("code", code);
				 records.put("title", title);
				 records.put("selectime", time);
				 mongo.upsertMapByTableName(records,"xg_good_all_web");
			 }
		 }
	    cursor.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void complie2Table(String table) throws ClientProtocolException, IOException{
	  try{ 
		MongoCollection<Document> collection=mongo.getShardConn(table);
		 MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator(); 
		 Document doc=null;
		 HashMap<String, Object > records=null;
		 List<HashMap<String, Object >> list=new ArrayList<HashMap<String,Object>>();
		 while(cursor.hasNext()){
			 records=new HashMap<String, Object>();
			 doc=cursor.next();
			Object name= doc.get("stockName");
			Object code= doc.get("code");
			Object title= doc.get("title");
			Object time= doc.get("selectime");
			Object website= doc.get("website");
			int supportnum=1;
			List<HashMap<String, Object >> list1=new ArrayList<HashMap<String,Object>>();
			HashMap<String, Object > map1=new HashMap<String, Object>();
			map1.put("ss", title);
			int num=-1;
			for(int p=0;p<list.size();p++){
				HashMap<String, Object> map=list.get(p);
				if(map.get("code").toString().equals(code)){
					supportnum=Integer.parseInt(map.get("supportnum").toString())+1;
					list1=(List<HashMap<String, Object >>)map.get("support");
					num=p;
				}
			}
			if(num!=-1){
				 list.remove(num);
			}
			 list1.add(map1);
			 records.put("id", code+""+time);
			 records.put("stockName", name);
			 records.put("code", code);
			 records.put("selectime", time);
			 records.put("supportnum", supportnum);
			 records.put("website", website);
			 records.put("support", list1);
			 list.add(records);
		 }
		   cursor.close();
			 Collections.sort(list, new Comparator<HashMap<String, Object >>() {
		            public int compare(HashMap<String, Object > a, HashMap<String, Object > b) {
		                String  t1 =a.get("supportnum").toString();
		                String t2 = b.get("supportnum").toString();
		                int time=Integer.parseInt(t1);
		                int time1=Integer.parseInt(t2);
//		                return t2.compareTo(t1);
		                return time1-time;
		            }
		        });
		   mongo.getShardConn(table+"_last").deleteMany(Filters.exists("id"));
		   mongo.upsetManyMapByTableName(list, "xg_good_all_web_last");
		   mongo.upsetManyMapByTableName(list, "xg_good_all_web_last_all");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
