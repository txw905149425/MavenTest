package com.test.MongoMaven.wx.news;

import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Test1 {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document>  coll=mongo.getShardConn("jg_wx_gzh_good");
		String [] str={"2017-11-23","2017-11-22","2017-11-21","2017-11-20","2017-11-17","2017-11-16"};
		for(int i=0;i<str.length;i++){
			String dtime=str[i];
			BasicDBObject doc5 = new BasicDBObject();
			doc5.put("dtime", dtime);
			MongoCursor<Document> cursor =coll.find(doc5).batchSize(10000).noCursorTimeout(true).iterator();
			HashMap<String, Integer> dmap=new HashMap<String, Integer>();
			while(cursor.hasNext()){
				Document doc=cursor.next();
				Object modul=doc.get("modul");
				int num=IKFunction.rowsArray(modul);
				for(int j=1;j<=num;j++){
					Object one=IKFunction.array(modul, j);
					String mname=IKFunction.keyVal(one,"modul").toString();
					if(dmap.containsKey(mname)){
						dmap.put(mname,dmap.get(mname)+1);
					}else{
						dmap.put(mname, 1);
					}
				}
			}
			cursor.close();
		   cursor =coll.find(doc5).batchSize(10000).noCursorTimeout(true).iterator();
//		   System.out.println(dmap.toString());
		   while(cursor.hasNext()){
				Document doc=cursor.next();
				Object modul=doc.get("modul");
				int num=IKFunction.rowsArray(modul);
				ArrayList<HashMap<String, Object >> list=new ArrayList<HashMap<String,Object>>();
				int modulnum=0;
				for(int j=1;j<=num;j++){
					Object one=IKFunction.array(modul, j);
					String mname=IKFunction.keyVal(one,"modul").toString();
					String type=IKFunction.keyVal(one,"type").toString();
					if(dmap.containsKey(mname)){
						HashMap<String, Object > map=new HashMap<String, Object>();
						int n=dmap.get(mname);
						if(n>modulnum){
							modulnum=n;
						}
						map.put("modul;", mname);
						map.put("type", type);
						map.put("modulnum", n);
						list.add(map);
					}
				}
				doc.remove("_id");
				doc.remove("modul");
				doc.append("modul",list);
				doc.append("modulnum",modulnum);
				mongo.upsertDocByTableName(doc, "jg_wx_gzh_good");
			}
			cursor.close();
			
		}
	}
}
