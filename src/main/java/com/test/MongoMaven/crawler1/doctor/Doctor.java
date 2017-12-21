package com.test.MongoMaven.crawler1.doctor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.storm.shade.org.yaml.snakeyaml.constructor.SafeConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Doctor {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		ArrayList<String> listStr=FileUtil.readFileReturn("ww_website");
		String time=IKFunction.getTimeNowByStr("yyyy-MM-dd");
		 MongoCollection<Document>  collection=mongo.getShardConn("ww_ask_online_all");
		 int num=listStr.size();
		 for(int i=0;i<num;i++){
			 String web=listStr.get(i).trim();
			 BasicDBObject doc = new BasicDBObject();
			 doc.append("timedel", time);
			 doc.append("website",web);
			 MongoCursor<Document> cursor =collection.find(doc).batchSize(100).noCursorTimeout(false).iterator();
			 if(!cursor.hasNext()){
				 cursor=null;
				 System.out.println("网站：  "+web+"    当天("+time+")内无数据");
				 doc.append("timedel", getYestime());
				 cursor =collection.find(doc).batchSize(100).noCursorTimeout(false).iterator();
				 if(!cursor.hasNext()){
				  System.err.println("网站：  "+web+"   昨日("+getYestime()+")内无数据！！！！！！"); 
				 }
			 }
			 cursor=null;
		 }
	}
	
	public static String getYestime(){
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE,-1);
		Date time=cal.getTime();
		return new SimpleDateFormat("yyyy-MM-dd").format(time);
		
	}
}


