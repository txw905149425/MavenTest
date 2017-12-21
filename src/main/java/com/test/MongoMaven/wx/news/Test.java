package com.test.MongoMaven.wx.news;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Test {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document>  coll=mongo.getShardConn("jg_wx_gzh_good");
		MongoCollection<Document>  coll1=mongo.getShardConn("lzx_code_block");
		String [] str={"2017-11-23","2017-11-22","2017-11-21","2017-11-20","2017-11-17","2017-11-16"};
		Document d=FileUtil.readFileReturnDoc("weight");
		for(int i=0;i<str.length;i++){
			String dtime=str[i];
			BasicDBObject doc5 = new BasicDBObject();
			doc5.put("dtime", dtime);
			MongoCursor<Document> cursor =coll.find(doc5).batchSize(10000).noCursorTimeout(true).iterator();
			while(cursor.hasNext()){
				Document doc=cursor.next();
				String name=doc.getString("name");
				String code=doc.getString("code").trim();
				if(d.containsKey(name)){
					Object value=d.get(name);
					doc.append("weight", value);
				}
				HashMap<String, Object> map=searchModul(code,coll1);
				Object stockarray=map.get(code);
				doc.remove("_id");
				doc.append("modul", stockarray);
				mongo.upsertDocByTableName(doc, "jg_wx_gzh_good");
			}
			cursor.close();
		}
	}
	
	public static HashMap<String, Object> searchModul(String code,MongoCollection<Document>  coll1){
		BasicDBObject doc3 = new BasicDBObject();
		doc3.put("id",code);
		HashMap<String, Object> map=new HashMap<String, Object>();
		MongoCursor<Document> cursor1 =coll1.find(doc3).batchSize(10000).noCursorTimeout(true).iterator();
		if(cursor1.hasNext()){
			Document dd=cursor1.next();
			Object stockarray=dd.get("stockarray");
			map.put(code, stockarray);
		}
		cursor1.close();
		return map;
	}
	
	
}	
