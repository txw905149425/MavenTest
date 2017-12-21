package com.test.MongoMaven.crawlerxg;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.client.ClientProtocolException;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class ComplieData2 {
	static MongoDbUtil mongo=new MongoDbUtil();
	static PostData post=new PostData();
	//循环取title(指标)
	@SuppressWarnings({ "null", "deprecation", "unchecked" })
	public static void main(String[] args) {
		mongo.getShardConn("xg_all_website1").deleteMany(Filters.exists("id"));
//		Date date=new Date();
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//		String ttaday=sdf.format(date);
		try{
		insert2Table("xg_gpdt_stock");
		insert2Table("xg_tzyj_stock");
		insert2Table("xg_ypgpt_stock");
		insert2Table("xg_yxg_stock");
		insert2Table("xg_znxg_stock");
		insert2Table("xg_xdgp_stock");
		complie2Table("xg_all_website1");
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
			 Object list=doc.get("list");
			 int num=IKFunction.rowsArray(list);
			 for(int i=1;i<=num;i++){
				 records=new HashMap<String, Object>();
				 Object one=IKFunction.array(list, i);
				 Object name=IKFunction.keyVal(one, "stockName");
				 if(name.toString().contains("ST")||name.toString().contains("st")){
					 continue;
				 }
				 Object code=IKFunction.keyVal(one, "code");
				 records.put("id", title+""+code+time+website);
				 records.put("stockName", name);
				 records.put("code", code);
				 records.put("title", title);
				 records.put("selectime", time);
				 records.put("website", website);
				 mongo.upsertMapByTableName(records, "xg_all_website1"); 
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
			 records.put("newprice", "");
			 records.put("supportnum", supportnum);
			 records.put("support", list1);
			 list.add(records);
		 }
		   cursor.close();
		   List<HashMap<String, Object >> listOne=new ArrayList<HashMap<String,Object>>();
			for(HashMap<String,Object > record:list){
				String t1=record.get("supportnum").toString();
				int time=Integer.parseInt(t1);
				if(time<=2){
					continue;
				}
				listOne.add(record);
			}
			 Collections.sort(listOne, new Comparator<HashMap<String, Object >>() {
		            public int compare(HashMap<String, Object > a, HashMap<String, Object > b) {
		                String  t1 =a.get("supportnum").toString();
		                String t2 = b.get("supportnum").toString();
		                int time=Integer.parseInt(t1);
		                int time1=Integer.parseInt(t2);
//		                return t2.compareTo(t1);
		                return time1-time;
		            }
		        });
//		   MongoCollection<Document> collectiondele=mongo.getShardConn("xg_stock_last_json");
//		   MongoCursor<Document> cursordele =collectiondele.find().batchSize(10000).noCursorTimeout(true).iterator();
//		   while(cursordele.hasNext()){
//			   Document d  = cursordele .next(); //遍历每一条数据
//			   d.remove("_id");
//			   mongo.upsertDocByTableName(d, "xg_stock_last_json_all");
//		   }
		   mongo.upsetManyMapByTableName(listOne, "xg_all_app_combine");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
