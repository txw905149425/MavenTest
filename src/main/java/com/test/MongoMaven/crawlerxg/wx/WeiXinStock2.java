package com.test.MongoMaven.crawlerxg.wx;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;


public class WeiXinStock2 {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		mongo.getShardConn("app_xg_wx_last").deleteMany(Filters.exists("id"));
		MongoCollection<org.bson.Document>  coll=mongo.getShardConn("app_xg_wx");
		try{
			HashMap<String, HashMap<String, Object>> dmap=new HashMap<String, HashMap<String, Object>>();
			MongoCursor<org.bson.Document> cursor =coll.find().batchSize(10000).noCursorTimeout(true).iterator();
			while(cursor.hasNext()){
				Document doc=cursor.next();
				String code=doc.get("code").toString();
				String name=doc.get("name").toString();
				String date=doc.get("time").toString();
				List<HashMap<String, Object >> list1=new ArrayList<HashMap<String,Object>>();
				int supportnum=1;
				if(dmap.containsKey(code)){
					HashMap<String, Object> tmp=dmap.get(code);
					list1=(List<HashMap<String, Object >>)tmp.get("list");
					supportnum=Integer.parseInt(tmp.get("supportnum").toString())+1;
					HashMap<String, Object > map1=new HashMap<String, Object>();
					map1.put("ss", name);
					list1.add(map1);
				}else{
					HashMap<String, Object > map1=new HashMap<String, Object>();
					map1.put("ss", name);
					list1.add(map1);
				}
				HashMap<String, Object> nmap=new HashMap<String, Object>();
				nmap.put("id", date+code);
				nmap.put("supportnum",supportnum);
				nmap.put("code", code);
				nmap.put("time", date);
				nmap.put("list", list1);
				dmap.put(code, nmap);
			}
			
			Iterator<Entry<String, HashMap<String, Object>>>iterator=dmap.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<String,HashMap<String, Object>>entry=iterator.next();
				HashMap<String, Object> result=entry.getValue();
				mongo.upsertMapByTableName(result, "app_xg_wx_last");
				mongo.upsertMapByTableName(result, "app_xg_wx_last_all");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
