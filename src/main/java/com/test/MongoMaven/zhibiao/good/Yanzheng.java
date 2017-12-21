package com.test.MongoMaven.zhibiao.good;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;


public class Yanzheng {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		//获取库里每个指标的准确率
		MongoCollection<Document> collection1=mongo.getShardConn("app_xg_weight_good");
		MongoCursor<Document> cursor1 =collection1.find().batchSize(10000).noCursorTimeout(true).iterator();
		HashMap<String, Object> mapKey=new HashMap<String, Object>();
		while(cursor1.hasNext()){
			Document doc=cursor1.next();
			String name=doc.get("id").toString();
			int total=Integer.parseInt(doc.get("total").toString());
			float weight=Float.parseFloat(doc.get("weight").toString());
			mapKey.put(name,total+","+weight);
		}
		cursor1.close();
		
		//合并表里的所有股票  （从 指标-->股票 |转成| 股票-->指标）
		MongoCollection<Document> collection=mongo.getShardConn("app_xg_other_stock");
		BasicDBObject find=new BasicDBObject();
		find.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
		MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator();
		HashMap<String, HashMap<String, Object > > map=new HashMap<String, HashMap<String, Object >>();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			Object list=doc.get("list");
			Object title=doc.get("title");
			Object time=doc.get("time");
			int num=IKFunction.rowsArray(list);
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(list, i);
				Object sname=IKFunction.keyVal(one, "stockName");
				String scode=IKFunction.keyVal(one, "stockCode").toString();
				if(map.containsKey(scode)){
					HashMap<String, Object > dmap=map.get(scode);
					Object sup=dmap.get("support");
					int supnum=Integer.parseInt(dmap.get("supportnum").toString());
					ArrayList<HashMap<String, Object>> support=toArray(sup);
					HashMap<String, Object > maplist=new HashMap<String, Object>();
					maplist.put("ss", title);
					support.add(maplist);
					dmap.put("support", support);
					dmap.put("supportnum", supnum+1);
					map.put(scode, dmap);
				}else{
					HashMap<String, Object > dmap=new HashMap<String, Object>();
					dmap.put("id", scode+IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					dmap.put("code", scode);
					dmap.put("stockName", sname);	
					dmap.put("selectime", time);
					ArrayList<HashMap<String, Object>> support=new ArrayList<HashMap<String,Object>>();
					HashMap<String, Object > maplist=new HashMap<String, Object>();
					maplist.put("ss", title);
					support.add(maplist);
					dmap.put("support", support);
					dmap.put("supportnum","1");
					map.put(scode, dmap);
				}
			}
			
		}
		cursor.close();
		
		//遍历并加权重存库
		Iterator<Map.Entry<String,HashMap<String, Object>>>iterator=map.entrySet().iterator();
		try{
			DecimalFormat df=new DecimalFormat("0.0000");
			while(iterator.hasNext()){
				Map.Entry<String,HashMap<String, Object>> entry=iterator.next();
				HashMap<String, Object> result=entry.getValue();
				Object support=result.get("support");
				int num=IKFunction.rowsArray(support);
				int all=0;
				float tweight=0;
				for(int i=1;i<=num;i++){
					Object one=IKFunction.array(support, i);
					String name=IKFunction.keyVal(one, "ss").toString();
					if(mapKey.containsKey(name)){
						String val=mapKey.get(name).toString();
						int total=Integer.parseInt(val.split(",")[0]);
						float weight=Float.parseFloat(val.split(",")[1]);
						all=all+total;
						tweight=tweight+(total*weight);
					}
				}
				String c=df.format((float)tweight/all);
				result.put("weight", c);
				mongo.upsertMapByTableName(result, "app_xg_good");
			}	
		}catch(Exception e){
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
	
	public static ArrayList<HashMap<String, Object>> toArray(Object json){
		int size=IKFunction.rowsArray(json);
		ArrayList<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
		for(int j=1;j<=size;j++){
			Object two=IKFunction.array(json, j);
			HashMap<String, Object> map=toHashMap(two);
			list.add(map);
		}
		return list;
		
	}
	
}
