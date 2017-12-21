package com.test.MongoMaven.wx;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class CountGzh {
	static MongoDbUtil mongo=new MongoDbUtil();
	public static void main(String[] args) {
		String name="无锋论市";
		ArrayList<String> tlist=getTime(name);
//		2017-10-24 21:34:52
//		2017-10-23 21:27:13
//		2017-10-22 21:03:19
//		2017-10-17 22:04:17
		String time="2017-10-24 21:34:52";
		int begin=tlist.indexOf(time);
		int end=0;
		if(begin<30){
			end=begin+30;
		}
		HashMap<String , Object> up1=new HashMap<String, Object>(); //第一天涨的
		HashMap<String , Object> down1=new HashMap<String, Object>(); //第一天跌的
		for(int i=begin;i<end;i++){
			String date=tlist.get(i);
			System.out.println(date);
			countUpDown(date,up1,down1);				
		}
		DecimalFormat df=new DecimalFormat("0.0000");
//		if(!up1.isEmpty()){
//			int up=Integer.parseInt(up1.get(name).toString());
//			int down=Integer.parseInt(down1.get(name).toString());
//			int total=up+down;
//			String c=df.format((float)up/total);
//			HashMap<String , Object> map=new HashMap<String, Object>();
//			map.put("id", name+time);
//			map.put("name", name);
//			map.put("time", time);
//			map.put("total", total);
//			map.put("weight", c);
//		}
		
	}
	
	public static void countUpDown(String time,HashMap<String, Object> up1,HashMap<String, Object> down1){
		MongoCollection<Document> collection=mongo.getShardConn("gd_wx_gzh_day");
		BasicDBObject find=new BasicDBObject();
		find.put("time", time);
		MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			if(doc.containsKey("rose")){
				Object rose=doc.get("rose");
				String name=doc.get("name").toString();
				if(!StringUtil.isEmpty(rose.toString())){
					float d=Float.parseFloat(rose.toString());
					if(d>0){
							if(up1.containsKey(name)){
								int up=Integer.parseInt(up1.get(name).toString());
								up1.put(name,(up+1));
							}else{
								up1.put(name,1);
							}
					}else{
							if(down1.containsKey(name)){
								int down=Integer.parseInt(down1.get(name).toString());
								down1.put(name,(down+1));
							}else{
								down1.put(name,1);
							}
						}
				}
			}
		}
		cursor.close();
	}

	
	public static ArrayList<String> getTime(String name){
		ArrayList<String> list=new ArrayList<String>();
		MongoCollection<Document> collection=mongo.getShardConn("gd_wx_gzh");
		BasicDBObject find=new BasicDBObject();
		find.put("name", name);
		MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			String time=doc.get("time").toString();
			list.add(time);
		}
		cursor.close();
		return list;
	} 
}
