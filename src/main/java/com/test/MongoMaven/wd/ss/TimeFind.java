package com.test.MongoMaven.wd.ss;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class TimeFind {
	
	public static MongoDbUtil mongo=new MongoDbUtil();
	
	public static void main(String[] args) throws FileNotFoundException {
		PrintWriter pw=new PrintWriter(new File("ss_comment"));
		MongoCollection<Document> collection=mongo.getShardConn("ss_data_time_tmp");
		ArrayList<String> codeList=getTime();
		for(String code:codeList){
			BasicDBObject find1=new BasicDBObject();
			find1.put("code",code);
			find1.put("flag","1");
			System.out.println(code);
	//		MongoCollection<Document> collection1=mongo.getShardConn("ss_data_count1");
			MongoCursor<Document> cursor =collection.find(find1).batchSize(1000).noCursorTimeout(true).iterator();
			String time1="";
			String comment1="";
			while(cursor.hasNext()){
				Document doc=cursor.next();
				Object list=doc.get("list");
				Object end=doc.get("etime");
				Object start=doc.get("stime");
				int num=IKFunction.rowsArray(list);
				for(int i=1;i<=num;i++){
					Object one=IKFunction.array(list, i);
					Object time=IKFunction.keyVal(one, "time");
					Object comment=IKFunction.keyVal(one, "comments");
					time1=time1+time+",";
					comment1=comment1+comment+",";
				}
				time1=time1.substring(0, time1.length()-1)+"["+start+"-"+end+"]";
				comment1=comment1.substring(0, comment1.length()-1)+"["+start+"-"+end+"]";
			}
			cursor.close();
			if(time1.length()<2){
				continue;
			}
			pw.println(code);
			pw.println(time1);
			pw.println(comment1);
//			System.out.println(time1);
//			System.out.println(comment1);
	    }
		pw.close();

	}
	
	public static ArrayList<String> getTime(){
		ArrayList<String> list=new ArrayList<String>();
		MongoCollection<Document> collection=mongo.getShardConn("ss_data_time_rule");
		MongoCursor<String> cursor =collection.distinct("code", String.class).iterator();
		while(cursor.hasNext()){
			String t=cursor.next();
//			System.out.println(t);
			if(t.length()==6){
				list.add(t);
			}
		}
		cursor.close();
		return list;
	}
	
}
