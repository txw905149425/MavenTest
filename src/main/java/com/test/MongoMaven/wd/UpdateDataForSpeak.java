package com.test.MongoMaven.wd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class UpdateDataForSpeak {
	
	public static MongoDbUtil mongo=new MongoDbUtil();
	
	public static void main(String[] args) {
		ArrayList<String> lcode=getCode();
		String date="2017-08-11";
			for(String code:lcode){
				BasicDBObject find=new BasicDBObject();
				find.put("timedel",date);
				find.put("stock_code",code);
				find.put("website", "同花顺");
				String table="d.ss"+date.replace("-", "");
				System.out.println(table);
				MongoCollection<Document> collection=mongo.getShardConn(table);
				MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator();
				ArrayList<HashMap<String, Object>> listMap=new ArrayList<HashMap<String,Object>>();
				Object name=null;
				Object time=null;
				System.out.println(date+" >>> "+code);
				while(cursor.hasNext()){
					Document doc=cursor.next();
					name=doc.get("name");
					time=doc.get("timedel");
					Object ftime=doc.get("crawl_time");
					 if(doc.containsKey("list")){
						 Object list=doc.get("list");
						 JSONArray js=JSONArray.fromObject(list);
						 int num=js.size();
						 for(int i=0;i<num;i++){
							 Object  block=js.get(i);
							 HashMap<String, Object> map=toHashMap(block);
							 map.remove("website");
							 map.remove("lastCommentTime");
							 map.put("ftime", ftime);
							 if(listMap.isEmpty()){
								 listMap.add(map); 
							 }
							 listMap=isMap(listMap,map);
						 }
					 }
					
				}
				cursor.close();
				if(!listMap.isEmpty()){
					HashMap<String, Object> dmap=new HashMap<String, Object>();
					dmap.put("id", code+time);
					dmap.put("time", time);
					dmap.put("code", code);
					dmap.put("name", name);
					dmap.put("list", listMap);
					try {
						mongo.upsertMapByTableName(dmap, "ss_data_update");
						HashMap<String, Object> flag=new HashMap<String, Object>();
						flag.put("id", code);
						flag.put("flag_ss", "1");
						mongo.upsertMapByTableName(flag, "stock_code");
//						collection.deleteMany(find);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
//		}
		
	}
	
	public static ArrayList<HashMap<String, Object>> isMap(ArrayList<HashMap<String, Object>> listMap,HashMap<String, Object> map){
		if(map.isEmpty()){
			return listMap;
		}
		ArrayList<HashMap<String, Object>> list=(ArrayList<HashMap<String, Object>>) listMap.clone();
		String utime=map.get("utime").toString();
		String ftime=map.get("ftime").toString();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			Date date = format.parse(ftime);
			Long t=date.getTime();
			boolean flag=false;
			for(int i=0;i<listMap.size();i++){
				HashMap<String, Object> m=listMap.get(i);
				String utime1=m.get("utime").toString();
				String ftime1=m.get("ftime").toString();
				Date date1 = format.parse(ftime1);
				Long t1=date1.getTime();
				if(utime1.equals(utime)){
					if(t>t1){
						list.remove(i);
					}else{
						flag=true;
					}
					break;
				}
			}
			if(!flag){
				list.add(map);	
			}
		}catch(Exception e){
			e.printStackTrace();
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
	
	public static ArrayList<String> getCode(){
		MongoCollection<Document> collection=mongo.getShardConn("stock_code");
		Bson filter = Filters.exists("name", true);
		 MongoCursor<Document> cursor =collection.find().filter(filter).batchSize(10000).noCursorTimeout(true).iterator(); 
		 Document doc=null;
		 ArrayList<String> list=new ArrayList<String>();
		 while(cursor.hasNext()){
			 doc=cursor.next();
			 String name=doc.get("name").toString();
			 if(doc.containsKey("flag_ss")){
				 continue;
			 }
			 if(!StringUtil.isEmpty(name)){
				 String code=doc.get("id").toString();
				 list.add(code);
			 }
		 }
		 cursor.close();
		 return list;
	}
}
