package com.test.MongoMaven.zhibiao.zb;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;

import net.sf.json.JSONObject;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Weight {

	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document> collection=mongo.getShardConn("app_xg_think");
		BasicDBObject find=new BasicDBObject();
		find.put("id", "zhibiao");
		MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator();
		Document doc=cursor.next();
		Object up1=doc.get("up1");
		Object down1=doc.get("down1");
		HashMap<String, Object> map1=toHashMap(up1);
		HashMap<String, Object> map2=toHashMap(down1);
		Iterator<String> iter=map1.keySet().iterator();
		DecimalFormat df=new DecimalFormat("0.0000");
		while (iter.hasNext()) {
			Object key = iter.next();
			int val1=Integer.parseInt(map1.get(key).toString());
			int  val2=0;
			if(map2.containsKey(key)){
				 val2 = Integer.parseInt(map2.get(key).toString());
			}
			int val=val1+val2;
			if(val>10){
				HashMap<String, Object> map=new HashMap<String, Object>();		
				String c=df.format((float)val1/val);
				map.put("id", key);
				map.put("weight", c);
				map.put("total", val);
				try {
					mongo.upsertMapByTableName(map, "app_xg_weight_zhibiao");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
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

}
