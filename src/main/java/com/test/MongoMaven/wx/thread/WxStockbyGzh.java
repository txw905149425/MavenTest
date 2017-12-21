package com.test.MongoMaven.wx.thread;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

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
		mongo.getShardConn("jg_wx_gzh_new").deleteMany(Filters.exists("id"));
		MongoCollection<org.bson.Document>  coll=mongo.getShardConn("jg_wx_gzh");
		BasicDBObject doc5 = new BasicDBObject();
		try{
			MongoCursor<org.bson.Document> cursor =coll.find(doc5).batchSize(10000).noCursorTimeout(true).iterator();
			HashMap<String, HashMap<String, Object>> dmap=new HashMap<String, HashMap<String, Object>>();
			HashMap<String, HashMap<String, Object>> dwmap=new HashMap<String, HashMap<String, Object>>();
			while(cursor.hasNext()){
				Document doc=cursor.next();
				if(doc.containsKey("code_flag1")){
					String name=doc.get("name").toString();
					String date=doc.get("timedel").toString();
					String code_str=doc.get("code_flag1").toString();
					if(code_str.contains(",")){
						String[] lstr=code_str.split(",");
						for(int i=0;i<lstr.length;i++){
							String code=lstr[i].trim();
							HashMap<String, Object> map=new HashMap<String, Object>();
							ArrayList<HashMap<String, Object>> list=null;
							HashMap<String, Object> map1=new HashMap<String, Object>();
							map1.put("ss", name);
							if(!dwmap.containsKey(name)){
								HashMap<String, Object> wmap=WeightMove.test(name,date);
								if(!wmap.isEmpty()){
									dwmap.put(name, wmap);
								}
							 }
							int supportnum=1;
							if(dmap.containsKey(code)){
								HashMap<String, Object> tmp=dmap.get(code);
								supportnum=Integer.parseInt(tmp.get("supportnum").toString())+1;
								list=(ArrayList<HashMap<String, Object>>) tmp.get("list");
							}else{
								list=new ArrayList<HashMap<String,Object>>();
							}
							list.add(map1);
							map.put("id",code+date);
							map.put("code", code);
							map.put("timedel", date);
							map.put("list", list);
							map.put("supportnum", supportnum);
							dmap.put(code, map);
						}
					}else{
						code_str=code_str.trim();
						HashMap<String, Object> map=new HashMap<String, Object>();
						ArrayList<HashMap<String, Object>> list=null;
						HashMap<String, Object> map1=new HashMap<String, Object>();
						map1.put("ss", name);
						if(!dwmap.containsKey(name)){
							HashMap<String, Object> wmap=WeightMove.test(name,date);
							if(!wmap.isEmpty()){
								dwmap.put(name, wmap);
							}
						}
						int supportnum=1;
						boolean flag=false;
						if(dmap.containsKey(code_str)){
							HashMap<String, Object> tmp=dmap.get(code_str);
							list=(ArrayList<HashMap<String, Object>>) tmp.get("list");
							for(HashMap<String, Object> ddd:list){
								String sname=ddd.get("ss").toString();
								if(sname.equals(name)){
									flag=true;
								}
							}
							if(!flag){
								supportnum=Integer.parseInt(tmp.get("supportnum").toString())+1;	
							}
						}else{
							list=new ArrayList<HashMap<String,Object>>();
						}
						if(!flag){
							list.add(map1);	
						}
						map.put("id",code_str+date);
						map.put("code", code_str);
						map.put("timedel", date);
						map.put("list", list);
						map.put("supportnum", supportnum);
						dmap.put(code_str, map);
					}
				}
			}
			cursor.close();
			DecimalFormat df=new DecimalFormat("0.0000");
			for (Entry<String, HashMap<String, Object>> entry : dmap.entrySet()) {
				HashMap<String, Object> tmap=entry.getValue();
				ArrayList<HashMap<String, Object>> list=(ArrayList<HashMap<String, Object>>) tmap.get("list");
				float all=0;
				int num=0;
				for(HashMap<String, Object> md:list){
					String name=md.get("ss").toString();
					if(dwmap.containsKey(name)){
						HashMap<String, Object> wmap=dwmap.get(name);
						String we=wmap.get("weight").toString();
						if(we.length()>0){
							float weight=Float.parseFloat(we);
							all=all+weight;
							num=num+1;
						}
					}
				}
				if(num>0){
					String la=df.format((float)all/num);
					tmap.put("weight", la);
					mongo.upsertMapByTableName(tmap, "jg_wx_gzh_new");
					mongo.upsertMapByTableName(tmap, "jg_wx_gzh_new_all");
				}				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
