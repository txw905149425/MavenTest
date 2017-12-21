package com.test.MongoMaven.wd.sscount;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

//统计所有的股票在现有时间内的所有数据（写入表ss_data_count1）
public class Count {
	public static MongoDbUtil mongo=new MongoDbUtil();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> clist=getCode();
		ArrayList<String> clist1=getCode1();
		for(String code:clist){
			if(clist1.contains(code)){
				continue;
			}
			System.out.println(code);
			MongoCollection<Document> collection=mongo.getShardConn("ss_all_speak");
			BasicDBObject find=new BasicDBObject();
			find.put("stock_code",code);
//			find.put("website","同花顺");
			MongoCursor<Document> cursor =collection.find(find).batchSize(1000).noCursorTimeout(true).iterator();
			HashMap<String, HashMap<String, Object>> dmap=new HashMap<String, HashMap<String,Object>>();
			while(cursor.hasNext()){
				Document doc=cursor.next();
				Object list=doc.get("list");
				String time=doc.getString("timedel").trim();
				HashMap<String, Object> map=countNamesAndComments(list,time);
				map.put("time",time);
				if(dmap.containsKey(time)){
					HashMap<String, Object> tmp=dmap.get(time);
					int a1=Integer.parseInt(map.get("names").toString());
					int b1=Integer.parseInt(map.get("comments").toString());
					int a2=Integer.parseInt(tmp.get("names").toString());
					int b2=Integer.parseInt(tmp.get("comments").toString());
					tmp.put("names", a1+a2);
					tmp.put("comments", b1+b2);
					dmap.put(time, tmp);
				}else{
					dmap.put(time, map);
				}
			}
			cursor.close();
			ArrayList<HashMap<String, Object>> dlist=new ArrayList<HashMap<String,Object>>();
			for(String key:dmap.keySet()){
				HashMap<String, Object> tmp=dmap.get(key);
				dlist.add(tmp);
			}
			Collections.sort(dlist, new Comparator<HashMap<String, Object >>() {
	            public int compare(HashMap<String, Object > a, HashMap<String, Object > b) {
	            	String  one =a.get("time").toString();
	                String two = b.get("time").toString();
	                int time=str2TimeMuli(one);
	                int time1=str2TimeMuli(two);
	                return time - time1;
	            }
	        });
			if(!dlist.isEmpty()){
				HashMap<String, Object> lastmap=new HashMap<String, Object>();
				lastmap.put("id", code);
				lastmap.put("list", dlist);
//				System.out.println(lastmap.toString());
				try {
					mongo.upsertMapByTableName(lastmap, "ss_data_count1");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static HashMap<String, Object> countNamesAndComments(Object dlist,String time){
		HashMap<String, Object> dmap=new HashMap<String, Object>();
		int num=IKFunction.rowsArray(dlist);
		if(num<=0){
			dmap.put("names",0);
			dmap.put("comments",0);
			return dmap;
		}
		HashMap<String, Object> map=new HashMap<String, Object>();
		for(int i=1;i<=num;i++){
			Object one=IKFunction.array(dlist, i);
			String name=IKFunction.keyVal(one, "uname").toString().trim();
			String utime=IKFunction.keyVal(one, "utime").toString().trim();
			if(!utime.contains(time)){
				continue;
			}
			if(map.containsKey(name)){
				map.put(name, (Integer.parseInt(map.get(name).toString())+1));
			}else{
				map.put(name, 1);
			}
			if(one.toString().contains("flist")){
				Object flist=IKFunction.keyVal(one,"flist");
				int ss=IKFunction.rowsArray(flist);
				for(int j=1;j<=ss;j++){
					String fname=IKFunction.keyVal(one, "uname").toString().trim();
					if(map.containsKey(fname)){
						map.put(fname, (Integer.parseInt(map.get(fname).toString())+1));
					}else{
						map.put(fname, 1);
					}
				}
			}
		}
		int total=0;
		int tmp=0;
		if(!map.isEmpty()){
			tmp=map.size();
			for(String key:map.keySet()){
				int count=Integer.parseInt(map.get(key).toString());
				total=total+count;
			}
		}
		dmap.put("names", tmp);
		dmap.put("comments",total);
		return dmap;
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
	
	public static ArrayList<String> getCode1(){
		MongoCollection<Document> collection=mongo.getShardConn("ss_data_count1");
		BasicDBObject find=new BasicDBObject();
//		find.put("flag","1");
		MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator();
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
	
	public static int str2TimeMuli(String str){
		if(str.contains("-")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date d=sdf.parse(str);
				int dd=(int)(d.getTime()/10000);
				return dd;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			return Integer.parseInt(str);
		}
		return 0;
	}
	
}
