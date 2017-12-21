package com.test.MongoMaven.wx;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class UserAsk {
	
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<org.bson.Document>  coll=mongo.getShardConn("gd_wx_gzh_new");
		ArrayList<String> list=FileUtil.readFileReturn("wx_gzh1");
		DecimalFormat df=new DecimalFormat("0.0000");
		for(String str:list){
			String name=str.split("=")[1].trim();
			BasicDBObject doc5 = new BasicDBObject();
			doc5.put("name", name);
//			if(name.equals("每日一支短线牛股")){
//			System.out.println("222");	
//			}
//			System.out.println(name);
			int up=0;
			int total=0;
			HashMap<String, Object> tmap=new HashMap<String, Object>();
			MongoCursor<org.bson.Document> cursor =coll.find(doc5).batchSize(10000).noCursorTimeout(true).iterator();
			while(cursor.hasNext()){
				Document doc=cursor.next();
				String time=doc.get("time").toString();
				tmap.put(time, "1");
				if(doc.containsKey("rose")){
					String rose=doc.get("rose").toString();
					float f=Float.parseFloat(rose);
					if(f>0){
						up++;
					}
					total++;
				}
			}
			if(total>0){
				String c=df.format((float)up/total);
				HashMap<String, Object > map=new HashMap<String, Object>();
				map.put("id",name);
				map.put("total",total);
				map.put("up",up);
				map.put("weight",c);
				map.put("day",tmap.size());
				try {
					mongo.upsertMapByTableName(map, "app_xg_weight_wx1");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
