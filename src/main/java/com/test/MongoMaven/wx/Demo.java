package com.test.MongoMaven.wx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Demo {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document>  coll=mongo.getShardConn("jg_wx_gzh_all");
		ArrayList<String> list=FileUtil.readFileReturn("wx_gzh");
	
		for(int i=0;i<list.size();i++){
			String str=list.get(i);
			String name=str.split("=")[1];
			BasicDBObject doc5 = new BasicDBObject();
			doc5.put("name", name);
			MongoCursor<Document> cursor =coll.find(doc5).batchSize(10000).noCursorTimeout(true).iterator();
			while(cursor.hasNext()){
				Document doc=cursor.next();
				String time=doc.getString("time");
				String dtime="";
				if(i<14){
					if(compileTime(time)){
						dtime=time.split(" ")[0];
						dtime=getDateByIndexDay(1,dtime);
					}else{
						dtime=time.split(" ")[0];
					}
				}else if(i<40){
					if(compileTime(time)){
						dtime=time.split(" ")[0];
						dtime=getDateByIndexDay(2,dtime);
						
					}else{
						dtime=time.split(" ")[0];
						dtime=getDateByIndexDay(1,dtime);
					}
				}else{
					if(compileTime(time)){
						dtime=time.split(" ")[0];
						dtime=getDateByIndexDay(3,dtime);
					}else{
						dtime=time.split(" ")[0];
						dtime=getDateByIndexDay(2,dtime);
					}
				}
				dtime=isWeekend(dtime);
				doc.remove("_id");
				doc.append("dtime",dtime);
				mongo.upsertDocByTableName(doc, "jg_wx_gzh_all");
			}
			cursor.close();
		}
			
	}
	
	
	public static boolean compileTime(String t1){
		boolean flag=false;
		SimpleDateFormat fff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        try {  
        	String t2=t1.split(" ")[0]+" 09:00:00";
//        	System.out.println(t1);
//        	System.out.println(t2);
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
