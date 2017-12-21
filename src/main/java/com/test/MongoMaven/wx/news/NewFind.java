package com.test.MongoMaven.wx.news;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class NewFind {

	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document>  coll=mongo.getShardConn("jg_wx_gzh_all");
		ArrayList<String> list=FileUtil.readFileReturn("wx_gzh1");
		for(String str:list){
			String name=str.split("=")[1];
			BasicDBObject doc5 = new BasicDBObject();
			doc5.put("name", name);
			long size=coll.count(doc5);
			if(size>20){
				MongoCursor<Document> cursor =coll.find(doc5).batchSize(10000).noCursorTimeout(true).iterator();
				while(cursor.hasNext()){
					Document doc=cursor.next();
					Object code_list=doc.get("code_list");
					String time=doc.get("time").toString();
					String dtime="";
					if(compileTime(time)){
						dtime=time.split(" ")[0];
						dtime=getDateByIndexDay(1,dtime);
					}else{
						dtime=time.split(" ")[0];
					}
					dtime=isWeekend(dtime);
					Object id=doc.get("id");
					int num=IKFunction.rowsArray(code_list);
					for(int j=1;j<=num;j++){
						Object one=IKFunction.array(code_list, j);
						Object code=IKFunction.keyVal(one, "code");
						HashMap<String, Object> dmap=new HashMap<String, Object>();
						dmap.put("id", id+""+code);
						dmap.put("name", name);
						dmap.put("code", code);
						dmap.put("time", time);
						dmap.put("dtime", dtime);
						try {
							mongo.upsertMapByTableName(dmap, "jg_wx_gzh_good");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			    }
				cursor.close();
			}
		}
		
		
	}

	public static boolean compileTime(String t1){
		boolean flag=false;
		SimpleDateFormat fff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        try {  
        	String t2=t1.split(" ")[0]+" 09:00:00";
        	Date date1 = fff.parse(t1);
        	Date date2 = fff.parse(t2);
        	long d1=date1.getTime();
        	long d2=date2.getTime();
        	if(d1>d2){
        		flag=true;
        	}
        } catch (ParseException e) {  
            e.printStackTrace();  
        }  
     return flag;
	}
	
	
	public static String getDateByIndexDay(int d,String day) {
		 SimpleDateFormat fff = new SimpleDateFormat("yyyy-MM-dd");  
	        Date date = null;  
	        String time="";
	        try {  
	            date = fff.parse(day);  
	            Calendar calendar = new GregorianCalendar();  
		    	calendar.setTime(date);  
		    	calendar.add(calendar.DATE,d);//把日期往后增加一天.整数往后推,负数往前移动  
		    	date=calendar.getTime(); //这个时间就是日期往后推一天的结果   
		    	time=fff.format(date);
	        } catch (ParseException e) {  
	            e.printStackTrace();  
	        }  
	        return time;
	    }
	
	public static String isWeekend(String time){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt=format.parse(time);
			String week=IKFunction.getWeekOfDate(dt);
			if(week.equals("星期日")){
				time=getDateByIndexDay(1,time);
			}else if(week.equals("星期六")){
				time=getDateByIndexDay(2,time);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return time;
	}
	
}
