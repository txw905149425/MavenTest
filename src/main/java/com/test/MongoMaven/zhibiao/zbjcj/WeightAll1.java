package com.test.MongoMaven.zhibiao.zbjcj;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class WeightAll1 {
	public static MongoDbUtil mongo=new MongoDbUtil();
	
	public static void main(String[] args) {
		ArrayList<String> tlist=getTime();
//		for(int x=(tlist.size()-20);x<tlist.size()-2;x++){
			String time=tlist.get(tlist.size()-1);
			String today=IKFunction.getTimeNowByStr("yyyy-MM-dd");
			if(time.equals(today)){
				time=tlist.get(tlist.size()-2);
			}
//			String time=tlist.get(x);
//			time="2017-10-09";
//			System.out.println(time);
			if(tlist.contains(time)){
				int num=tlist.indexOf(time);
				int begin=0;
				if(num>31){
					begin=num-31;
				}
				HashMap<String , HashMap<String, Integer>> ddmap=new HashMap<String, HashMap<String, Integer>>(); //第一天涨的
				for(int i=begin;i<num-1;i++){
					String date=tlist.get(i);
//					System.out.println(date);
					countUpDown(date,ddmap);				
				}
				Iterator<String> iter=ddmap.keySet().iterator();
//				System.out.println(ddmap.size());
				DecimalFormat df=new DecimalFormat("0.0000");
				HashMap<String, Object> map=new HashMap<String, Object>();//map 存储指标对应的 准确率和总数量
				while (iter.hasNext()) {
					String key = iter.next().toString();
					HashMap<String, Integer> dmap=ddmap.get(key);
					if(dmap.containsKey("up")&&dmap.containsKey("down")){
						int up=dmap.get("up");
						int down=dmap.get("down");
						int total=up+down;
						if(total>10){
							String c=df.format((float)up/total);
							map.put(key,c);
						}
					}
				}
//				System.out.println(map.size()+" "+map.toString());
				//写数据库
				
					MongoCollection<Document> collection=mongo.getShardConn("xg_stock_last_json_all");
					BasicDBObject find=new BasicDBObject();	
					find.put("selectime", time);
					MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator();
					while(cursor.hasNext()){
						Document doc=cursor.next();
						Object support=doc.get("support");
//						HashMap<String, Object> result=entry.getValue();
						int size=IKFunction.rowsArray(support);
						int all=0;
						float tweight=0;
						for(int i=1;i<=size;i++){
							Object one=IKFunction.array(support, i);
							String name=IKFunction.keyVal(one, "ss").toString();
							if(map.containsKey(name)){
								String val=map.get(name).toString();
								float weight=Float.parseFloat(val);
								tweight=tweight+weight;
								all++;
							}
						}
						if(all>0){
							String c=df.format((float)tweight/all);//准确度
							doc.remove("_id");
							doc.put("weight", c);
							mongo.upsertDocByTableName(doc, "app_xg_our1");
						}
					}
					cursor.close();
			}
//		}
		
	}
	
	public static ArrayList<String> getTime(){
		ArrayList<String> list=new ArrayList<String>();
		MongoCollection<Document> collection=mongo.getShardConn("xg_stock_last_json_all");
		MongoCursor<String> cursor =collection.distinct("selectime", String.class).iterator();
		while(cursor.hasNext()){
			String t=cursor.next();
//			System.out.println(t);
			list.add(t);
		}
		cursor.close();
		return list;
	}
	
	public static void countUpDown(String time,HashMap<String, HashMap<String, Integer>> ddmap){
		MongoCollection<Document> collection=mongo.getShardConn("xg_stock_last_json_all");
		BasicDBObject find=new BasicDBObject();
		find.put("selectime", time);
		MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			if(doc.containsKey("rose1")){
				Object rose=doc.get("rose1");
				Object support=doc.get("support");
				int row=IKFunction.rowsArray(support);
				if(!StringUtil.isEmpty(rose.toString())){
					float d=Float.parseFloat(rose.toString());
					if(d>0){
						for(int i=1;i<=row;i++){
							Object one=IKFunction.array(support, i);
							String name=IKFunction.keyVal(one,"ss").toString();
							if(ddmap.containsKey(name)){
								HashMap<String, Integer> dmap=ddmap.get(name);
								if(dmap.containsKey("up")){
									int up=dmap.get("up");
									dmap.put("up",up+1);
									if(dmap.containsKey("down")){
										int down=dmap.get("down");
										dmap.put("down",down);	
									}
									ddmap.put(name, dmap);
								}else{
									dmap.put("up", 1);
									if(dmap.containsKey("down")){
										int down=dmap.get("down");
										dmap.put("down",down);
									}
									ddmap.put(name, dmap);
								}
							}else{
								HashMap<String, Integer> dmap=new HashMap<String, Integer>();
								dmap.put("up", 1);
								ddmap.put(name, dmap);
							}
						}
					}else{
						for(int i=1;i<=row;i++){
							Object one=IKFunction.array(support, i);
							String name=IKFunction.keyVal(one,"ss").toString();
							if(ddmap.containsKey(name)){
								HashMap<String, Integer> dmap=ddmap.get(name);
								if(dmap.containsKey("down")){
									int down=dmap.get("down");
									dmap.put("down",down+1);
									if(dmap.containsKey("up")){
										int up=dmap.get("up");
										dmap.put("up",up);	
									}
									ddmap.put(name, dmap);
								}else{
									dmap.put("down", 1);
									if(dmap.containsKey("up")){
										int up=dmap.get("up");
										dmap.put("up",up);	
									}
									ddmap.put(name, dmap);
								}
							}else{
								HashMap<String, Integer> dmap=new HashMap<String, Integer>();
								dmap.put("down", 1);
								ddmap.put(name, dmap);
							}
						}
					}
				}
				
			}
		}
		cursor.close();
	}
	
	
}
