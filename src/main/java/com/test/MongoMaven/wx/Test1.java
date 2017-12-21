package com.test.MongoMaven.wx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Test1 {

	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document>  coll=mongo.getShardConn("jg_wx_gzh_good");
		ArrayList<String> list=FileUtil.readFileReturn("date");
		try{
			for(String day:list){
				BasicDBObject doc5 = new BasicDBObject();
				doc5.append("dtime", day.trim());
				MongoCursor<Document> cursor =coll.find(doc5).batchSize(10000).noCursorTimeout(true).iterator();
				HashMap<String, HashMap<String, Object>> dmap=new HashMap<String, HashMap<String, Object>>();
				while(cursor.hasNext()){
					Document doc=cursor.next();
					HashMap<String, Object> map=new HashMap<String, Object>();
					String code=doc.get("code").toString();
					String name=doc.get("name").toString();
					ArrayList<HashMap<String, Object>> dlist=null;
					HashMap<String, Object> map1=new HashMap<String, Object>();
					map1.put("ss", name);
					int supportnum=1;
					if(dmap.containsKey(code)){
						HashMap<String, Object> one=dmap.get(code);
						supportnum=Integer.parseInt(one.get("supportnum").toString())+1;
						dlist=(ArrayList<HashMap<String, Object>>)one.get("list");
					}else{
						dlist=new ArrayList<HashMap<String,Object>>();
					}
					dlist.add(map1);
					map.put("id",code+day.trim());
					map.put("code", code);
					map.put("timedel", day.trim());
					map.put("list", dlist);
					map.put("supportnum", supportnum);
					dmap.put(code, map);
				}
				cursor.close();
				for (Entry<String, HashMap<String, Object>> entry : dmap.entrySet()) {
					HashMap<String, Object> tmap=entry.getValue();
					mongo.upsertMapByTableName(tmap, "jg_wx_gzh_good_all");
				}
				
			}
		}catch(Exception e){
			
		}

	}

}
