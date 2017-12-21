package com.test.MongoMaven.wx.test;

import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class CountGzhDown {
	public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document> collection=mongo.getShardConn("jg_test_count");
			BasicDBObject find=new BasicDBObject();
//			BasicDBObject s1=new BasicDBObject("name",-1);
			MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator();
			HashMap<String, Object> dmap=new HashMap<String, Object>();
			HashMap<String, Object> dmap1=new HashMap<String, Object>();
			HashMap<String, Object> dmap2=new HashMap<String, Object>();
			HashMap<String, Object> dmap3=new HashMap<String, Object>();
			while(cursor.hasNext()){
				Document doc=cursor.next();
				String name=doc.getString("name");
				String we=doc.getString("weight");
				float d=Float.parseFloat(we);
				String id=doc.getString("id");
				if(dmap.containsKey(name)){
					String tmp=dmap.get(name).toString();
					String dt=tmp.split(name)[0];
					double d1=Double.parseDouble(dt);
					if(d>d1){
						dmap.put(name, d+id);
					}
				}else{
					dmap.put(name, d+id);
				}
			}
			cursor.close();
			for (String key : dmap.keySet()) {
				String up=dmap.get(key).toString();
				String day=up.split(key)[1];
				if(day.equals("1")){
					dmap1.put(key, up);
				}else if(day.equals("2")){
					dmap2.put(key, up);
				}else if(day.equals("3")){
					dmap3.put(key, up);
				}
			}
			for (String key : dmap1.keySet()) {
				Object up=dmap1.get(key);
				System.out.println(up);
			}
			for (String key : dmap2.keySet()) {
				Object up=dmap2.get(key);
				System.out.println(up);
			}
			
			for (String key : dmap3.keySet()) {
				Object up=dmap3.get(key);
				System.out.println(up);
			}
			System.out.println(dmap.size());
			System.out.println(dmap1.size());
			System.out.println(dmap2.size());
			System.out.println(dmap3.size());
			System.out.println(dmap1.size()+dmap2.size()+dmap3.size());
	}
}
