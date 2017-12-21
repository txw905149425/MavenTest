package com.test.MongoMaven.wx.test;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;


//date文件里面的日期， 将其当天推荐的股票整理出来
public class HuiCe {
	static MongoDbUtil mongo=new MongoDbUtil();


	public static void main(String[] args) {
		 ArrayList<String> rlist=FileUtil.readFileReturn("date");
		 for(String time:rlist){
			 time=time.trim();
			 System.out.println(time);
			 String ztime=getSpecifiedDayBefore(time);
			 HashMap<String, HashMap<String, Object>> dmap=new HashMap<String, HashMap<String,Object>>();
			 BasicDBObject doc5 = new BasicDBObject();
			 BasicDBObject doc3 = new BasicDBObject();
			 Pattern p = Pattern.compile(".*"+time+".*");
		     doc3.put("$regex", p);
		     doc5.put("time", doc3);
		     formatData(doc5, dmap, time);
		     BasicDBObject doc4 = new BasicDBObject();
		     BasicDBObject doc2 = new BasicDBObject();
			 Pattern p1 = Pattern.compile(".*"+ztime+".*");
		     doc2.put("$regex", p1);
		     doc4.put("time", doc2);
		     formatData(doc4, dmap, time);
		     System.out.println(dmap.size());
			for (Entry<String, HashMap<String, Object>> entry : dmap.entrySet()) {
				HashMap<String, Object> tmap=entry.getValue();
					tmap.put("timedel", ztime);
					try {
						mongo.upsertMapByTableName(tmap, "jg_test_day3");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			  }
		 }
	}
	
	/** 
	* 获得指定日期的前一天 
	* @param specifiedDay 
	* @return 
	* @throws Exception 
	*/ 
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

	
	public static HashMap<String, HashMap<String, Object>> formatData(BasicDBObject doc5,HashMap<String, HashMap<String, Object>> dmap,String time){
		MongoCollection<Document> collection=mongo.getShardConn("jg_wx_gzh_all");
		String ztime=getSpecifiedDayBefore(time);
		MongoCursor<Document> cursor =collection.find(doc5).batchSize(10000).noCursorTimeout(true).iterator();
	     try{
	     while(cursor.hasNext()){
			   Document doc=cursor.next();
			   String t=doc.getString("time");
			   boolean flag=IKFunction.judgeTime(ztime+" 09:00:00", t, 24);
			   if(flag){
					if(doc.containsKey("code_flag1")){
						String name=doc.get("name").toString();
						String date=doc.get("timedel").toString();
						String code_str=doc.get("code_flag1").toString();
						if(code_str.contains(",")){
							String[] lstr=code_str.split(",");
							for(int i=0;i<lstr.length;i++){
								String code=lstr[i];
								HashMap<String, Object> map=new HashMap<String, Object>();
								ArrayList<HashMap<String, Object>> list=null;
								HashMap<String, Object> map1=new HashMap<String, Object>();
								map1.put("ss", name);
								int supportnum=1;
								boolean flag1=false;
								if(dmap.containsKey(code)){
									HashMap<String, Object> tmp=dmap.get(code);
									list=(ArrayList<HashMap<String, Object>>) tmp.get("list");
									for(HashMap<String, Object> ddd:list){
										String sname=ddd.get("ss").toString();
										if(sname.equals(name)){
											flag1=true;
										}
									}
									if(!flag1){
										supportnum=Integer.parseInt(tmp.get("supportnum").toString())+1;	
									}
								}else{
									list=new ArrayList<HashMap<String,Object>>();
								}
								if(!flag1){
									list.add(map1);
								}
								map.put("id",code+date);
								map.put("code", code);
								map.put("timedel", date);
								map.put("list", list);
								map.put("supportnum", supportnum);
								dmap.put(code, map);
							}
						}else{
							HashMap<String, Object> map=new HashMap<String, Object>();
							ArrayList<HashMap<String, Object>> list=null;
							HashMap<String, Object> map1=new HashMap<String, Object>();
							map1.put("ss", name);
							int supportnum=1;
							if(dmap.containsKey(code_str)){
								HashMap<String, Object> tmp=dmap.get(code_str);
								supportnum=Integer.parseInt(tmp.get("supportnum").toString())+1;
								list=(ArrayList<HashMap<String, Object>>) tmp.get("list");
							}else{
								list=new ArrayList<HashMap<String,Object>>();
							}
							list.add(map1);
							map.put("id",code_str+date);
							map.put("code", code_str);
							map.put("timedel", date);
							map.put("list", list);
							map.put("supportnum", supportnum);
							dmap.put(code_str, map);
						}
					}

			   }
		 }
	     cursor.close();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		return dmap;
	}
	
	
}
