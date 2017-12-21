package com.test.MongoMaven.crawler.sslast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class CombineDataByDay {

	public static MongoDbUtil mongo=new MongoDbUtil();
	
	public static void main(String[] args) {
		
		String table="d.ss20170811";
		for(String str:args){
			if(str.startsWith("flag=")){
				table=str.substring(5);
			}
		}
		MongoCollection<Document> collection=mongo.getShardConn(table);
		ArrayList<String> clist=getCode();
		for(String code:clist){
			for(int i=1;i<=2;i++){
				String web="";
				String website="";
				if(i==1){
					web="同花顺";
					website="ths";
				}else if(i==2){
					web="东方财富";
					website="dfcf";
				}
				BasicDBObject find=new BasicDBObject();
				find.put("stock_code",code);
				find.put("website",web);
				ArrayList<HashMap<String, Object>> listMap=new ArrayList<HashMap<String,Object>>();
				MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator();
				System.out.println("########");
				String name="";
				String time="";
				while(cursor.hasNext()){
					Document doc=cursor.next();
					 name=doc.getString("name");
					 time=doc.getString("timedel");
					 if(!doc.containsKey("list")){
						 continue;
					 }
					 Object list=doc.get("list");
					 int num=IKFunction.rowsArray(list);
					 for(int j=1;j<=num;j++){
						 Object  block=IKFunction.array(list,j);
						 if(StringUtil.isEmpty(block.toString())){
							continue; 
						 }
						 HashMap<String, Object> map=toHashMap(block);
						 map.remove("website");
//							 map.remove("lastCommentTime");
						 if(listMap.isEmpty()){
							 listMap.add(map); 
						 }else{
							 listMap=isMap(listMap,map);
						 }
					 }
					 
					
				}
				cursor.close();
				if(!listMap.isEmpty()){
					HashMap<String, Object> dmap=new HashMap<String, Object>();
					dmap.put("id", code+time+website);
					dmap.put("time", time);
					dmap.put("website", web);
					dmap.put("code", code);
					dmap.put("name", name);
					dmap.put("list", listMap);
					try {
						mongo.upsertMapByTableName(dmap, "ss_data_test");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		
	}
	
	//获取库里所有的股票代码
	public static ArrayList<String> getCode(){
		MongoCollection<Document> collection=mongo.getShardConn("stock_code");
		MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator();
		ArrayList<String> clist=new ArrayList<String>();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			String code=doc.getString("id").trim();
			if(code.length()==6){
				clist.add(code);
			}
		}
		cursor.close();
		return clist;
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
	
	public static ArrayList<HashMap<String, Object>> isMap(ArrayList<HashMap<String, Object>> listMap,HashMap<String, Object> map){
		if(map.isEmpty()){
			return listMap;
		}
		ArrayList<HashMap<String, Object>> list=(ArrayList<HashMap<String, Object>>) listMap.clone();
		String utime=map.get("utime").toString();
		String ftime=map.get("lastCommentTime").toString();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			Date date = format.parse(ftime);
			Long t=date.getTime();
			boolean flag=false;
			for(int i=0;i<listMap.size();i++){
				HashMap<String, Object> m=listMap.get(i);
				String utime1=m.get("utime").toString();
				String ftime1=m.get("lastCommentTime").toString();
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
	
}
