package com.test.MongoMaven.wx.thread;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import net.sf.json.JSONArray;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class WeightMove {
	static MongoDbUtil mongo=new MongoDbUtil();
	public static void main(String[] args) {
		MongoCollection<Document> collection=mongo.getShardConn("jg_test");
		ArrayList<String> dlist=FileUtil.readFileReturn("date");
		try{
		for(String time:dlist){
			String ztime=getSpecifiedDayBefore(time);
			BasicDBObject doc5 = new BasicDBObject();
			doc5.append("timedel", ztime);
			HashMap<String, HashMap<String, Object>> map=new HashMap<String, HashMap<String, Object>>();
			MongoCursor<Document> cursor =collection.find(doc5).batchSize(10000).noCursorTimeout(true).iterator();
			 DecimalFormat df=new DecimalFormat("0.00");
			 while(cursor.hasNext()){
				   Document doc=cursor.next();
				   Object list=doc.get("list");
				   JSONArray js=JSONArray.fromObject(list);
				   float tweight=0;
				   int num=0;
				   for(int i=1;i<=js.size();i++){
					   Object one=IKFunction.array(js, i);
					   String name=IKFunction.keyVal(one, "ss").toString();
					   HashMap<String, Object> wmap=null;
					   if(map.containsKey(name)){
						   wmap=map.get(name);   
					   }else{
						   wmap=test(name,ztime);
					   }
					   if(!wmap.isEmpty()){
						   tweight=tweight+Float.parseFloat(wmap.get("weight").toString());
						   num=num+1;
					   }
				   }
				   if(num>0){
					   String weight= df.format(tweight/(float)num);
					   doc.remove("_id");
					   doc.append("weight",weight);
					   mongo.upsertDocByTableName(doc, "jg_test_day2");
				   }
				   
		     }
			 cursor.close();
		}
		 
	    
	     }catch(Exception e){
	    	 e.printStackTrace();
	     }
	}
	
	/*
	 *
	 *name 公众号
	 *time 时间
	 *计算公众号在时间之前的准确度 
	 * */
	public static HashMap<String, Object> test(String name,String time){
		time=time+" 00:00:00";
		MongoCollection<Document> collection=mongo.getShardConn("jg_wx_gzh_all");
		BasicDBObject doc5 = new BasicDBObject();
		doc5.append("name", name);
	    HashMap<String, Object> dmap=new HashMap<String, Object>();
		HashMap<String, Integer> map=new HashMap<String, Integer>();
		MongoCursor<Document> cursor =collection.find(doc5).batchSize(10000).noCursorTimeout(true).iterator();
		 while(cursor.hasNext()){
			   Document doc=cursor.next();
			   String t=doc.getString("time");
			   if(timeCom(time,t)){//该条数据的时间如果大于传入的时间time(2017-10-08 00:00:00)
				   continue;
			   }
			   Object code_list=doc.get("code_list");
			   JSONArray js=JSONArray.fromObject(code_list);
			   for(int i=1;i<=js.size();i++){
				   Object one=IKFunction.array(js, i);
				   String rose=IKFunction.keyVal(one, "rose1").toString();
				   if(StringUtil.isEmpty(rose)){
					   continue;
				   }
				   float r=Float.parseFloat(rose);
				   if(r>0){
					   if(map.containsKey("up")){
						   int num=map.get("up");  
						   map.put("up", num+1);
					   }else{
						   map.put("up", 1);
					   }
				   }else if(r<0){
					   if(map.containsKey("down")){
						   int num=map.get("down");  
						   map.put("down", num+1);
					   }else{
						   map.put("down", 1);
					   }
				   }
			   }
		 }
		 cursor.close();
		  if(!map.isEmpty()){
			  DecimalFormat df=new DecimalFormat("0.00");
			  if(map.containsKey("up")&&map.containsKey("down")){
				  int up=map.get("up");
				   int down=map.get("down");
				   int total=up+down;
				   if(total>8){
					   String weight= df.format(up/(float)total);
					   dmap.put("weight", weight);
					   dmap.put("total", total);
				   }  
			  }
		   }
		 return dmap;
	}
	
	//判断时间t是否大于time
	public static boolean timeCom(String time,String t){
		boolean flag=false;
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        try {
			long start = sdf.parse(time).getTime();
			long end = sdf.parse(t).getTime();
			if(end>start){
				flag=true;
			}
        } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return flag;
	}
	
	public static String getSpecifiedDayBefore(String specifiedDay){ 
		Calendar c = Calendar.getInstance(); 
		Date date=null; 
		try {
			date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay); 
		} catch (ParseException e) { 
		   e.printStackTrace();
		}
		c.setTime(date);
		int day=c.get(Calendar.DATE);
		c.set(Calendar.DATE,day-1);
		String dayBefore=new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		return dayBefore;
	} 

	
}
