package com.test.MongoMaven.wx;

import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class EveryDayStock {

	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
//		mongo.getShardConn("gd_wx_gzh_day").deleteMany(Filters.exists("id"));
		MongoCollection<org.bson.Document>  coll=mongo.getShardConn("gd_wx_gzh");
//		BasicDBObject doc5 = new BasicDBObject();
		try{
			MongoCursor<org.bson.Document> cursor =coll.find().batchSize(10000).noCursorTimeout(true).iterator();
			while(cursor.hasNext()){
				Document doc=cursor.next();
				if(doc.containsKey("code_flag")){
					String name=doc.get("name").toString();
					String date=doc.get("time").toString();
					String code_str=doc.get("code_flag").toString();
					if(code_str.contains(",")){
						String[] lstr=code_str.split(",");
						for(int i=0;i<lstr.length;i++){
							String code=lstr[i];
							HashMap<String, Object> map=new HashMap<String, Object>();
							map.put("id",name+code+date);
							map.put("code", code);
							map.put("name", name);
							map.put("time", date);
							mongo.upsertMapByTableName(map, "gd_wx_gzh_day");
						}
					}else{
						HashMap<String, Object> map=new HashMap<String, Object>();
						map.put("id",name+code_str+date);
						map.put("code", code_str);
						map.put("name", name);
						map.put("time", date);
						mongo.upsertMapByTableName(map, "gd_wx_gzh_day");
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}

}
