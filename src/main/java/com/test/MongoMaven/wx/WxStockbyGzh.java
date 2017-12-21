package com.test.MongoMaven.wx;

import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class WxStockbyGzh {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<org.bson.Document>  coll=mongo.getShardConn("gd_wx_gzh");
		BasicDBObject doc5 = new BasicDBObject();
		doc5.append("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
		try{
			MongoCursor<org.bson.Document> cursor =coll.find(doc5).batchSize(10000).noCursorTimeout(true).iterator();
			while(cursor.hasNext()){
				Document doc=cursor.next();
				if(doc.containsKey("code_flag1")){
					String name=doc.get("name").toString();
					String date=doc.get("time").toString();
					String code_str=doc.get("code_flag1").toString();
					if(code_str.contains(",")){
						String[] lstr=code_str.split(",");
						for(int i=0;i<lstr.length;i++){
							String code=lstr[i];
							HashMap<String, Object> map=new HashMap<String, Object>();
							map.put("id",name+code+date);
							map.put("code", code);
							map.put("name", name);
							map.put("time", date);
							mongo.upsertMapByTableName(map, "gd_wx_gzh_new");
						}
					}else{
						HashMap<String, Object> map=new HashMap<String, Object>();
						map.put("id",name+code_str+date);
						map.put("code", code_str);
						map.put("name", name);
						map.put("time", date);
						mongo.upsertMapByTableName(map, "gd_wx_gzh_new");
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
