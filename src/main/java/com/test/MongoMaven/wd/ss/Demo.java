package com.test.MongoMaven.wd.ss;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import net.sf.json.JSONObject;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Demo {


	public static void main(String[] args) {
		DecimalFormat df=new DecimalFormat("0.0000");
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document> collection=mongo.getShardConn("ss_data_count");
		MongoCursor<Document> cursor =collection.find().batchSize(1000).noCursorTimeout(true).iterator();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			Object list=doc.get("list");
			System.out.println(doc.get("id"));
			int num=IKFunction.rowsArray(list);
			ArrayList<HashMap<String, Object>> dlist=new ArrayList<HashMap<String,Object>>();
			for(int i=1;i<num;i++){
				Object one=IKFunction.array(list, i);
				String cp=IKFunction.keyVal(one, "cp").toString();
				String comment=IKFunction.keyVal(one,"comment").toString();
				if(cp.equals("-1")&&comment.equals("0")){
					continue;
				}
				HashMap<String, Object> dmap=toHashMap(one);
				dlist.add(dmap);
			}
			doc.remove("_id");
			doc.put("list", dlist);
			doc.put("flag", "2");
			mongo.upsertDocByTableName(doc,"ss_data_count");
		}
		cursor.close();
		
	}
	
	public static String findKey(Object json){
		JSONObject js=JSONObject.fromObject(json);
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Iterator it = js.keys();
		String dkey="";
		try{
			while (it.hasNext()) {
	           String key = String.valueOf(it.next());
	           Date d=format.parse(key);
	           String we=IKFunction.getWeekOfDate(d);
	           if(we.equals("星期日")||we.equals("星期六")){
	        	   continue;
	           }
	           dkey=key;
			}
//			Collections.sort(list, new Comparator<String>(){
//				public int compare(String o1, String o2) {
//					// TODO Auto-generated method stub
//					long t1=str2Muil(o1);
//					long t2=str2Muil(o2);
//					String t=(t1-t2)+"";
//					return Integer.parseInt(t);
//				}
//			});
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return dkey;
	}
	
	public static long str2Muil(String str){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		long t1=0;
		try {
			t1=format.parse(str).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  t1;
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
