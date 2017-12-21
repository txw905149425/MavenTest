package com.test.MongoMaven.wd.sscount;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class RulesFind {

	public static void main(String[] args) {
		DecimalFormat df=new DecimalFormat("0.0000");
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document> collection=mongo.getShardConn("ss_data_time_rule");
//		BasicDBObject find=new BasicDBObject();
		MongoCursor<Document> cursor =collection.find().batchSize(1000).noCursorTimeout(true).iterator();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			Object list=doc.get("list");
			System.out.println(doc.get("code"));
			int num=IKFunction.rowsArray(list);
			ArrayList<String> klist=new ArrayList<String>();
			HashMap<String, Integer> mapcomm=new HashMap<String, Integer>();
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(list, i);
				String time=IKFunction.keyVal(one, "time").toString();
				String comments=IKFunction.keyVal(one,"comments").toString();
				int num2=Integer.parseInt(comments);
				mapcomm.put(time, num2);
				klist.add(time);
			}
			ArrayList<HashMap<String, Object>> dlist=new ArrayList<HashMap<String,Object>>();
			String t1=klist.get(0);
			int co1=mapcomm.get(t1);
			HashMap<String, Object> ttmp=new HashMap<String, Object>();
			ttmp.put("cp", "0");
			ttmp.put("comments", co1);
			ttmp.put("time", t1);
			dlist.add(ttmp);
			for(int i=0;i<klist.size()-1;i++){
				String key=klist.get(i);
				int comment=mapcomm.get(key);
				String key1=klist.get(i+1);
				int comment1=mapcomm.get(key1);
//				String c1=df.format((float)(comment-co1)/co1);
				String c1=df.format((float)(comment1-comment)/comment);
				if(comment==0){
					if(comment1<10){
						c1="0";
					}else{
						c1="1.0";
					}
				}
				HashMap<String, Object> tmp=new HashMap<String, Object>();
				tmp.put("cp", c1);
				tmp.put("comments", comment1);
				tmp.put("time", key1);
				dlist.add(tmp);
			}
			doc.remove("_id");
			doc.put("list", dlist);
			mongo.upsertDocByTableName(doc,"ss_data_time_rule");
		}
		cursor.close();
		
	}
}
