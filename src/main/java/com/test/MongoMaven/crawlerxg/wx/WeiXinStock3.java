package com.test.MongoMaven.crawlerxg.wx;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class WeiXinStock3 {
	
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<org.bson.Document>  coll=mongo.getShardConn("app_xg_wx_last_all");
		MongoCursor<org.bson.Document> cursor =coll.find().batchSize(10000).noCursorTimeout(true).iterator();
		HashMap<String , Object> up1=new HashMap<String, Object>(); //第一天涨的
		HashMap<String , Object> down1=new HashMap<String, Object>(); //第一天跌的
		 while(cursor.hasNext()){
			 Document doc=cursor.next();
			 if(doc.containsKey("rose")){
				 Object rose=doc.get("rose");
				 String time=doc.get("time").toString();
				 if(time.equals("2017-10-24")){
					continue; 
				 }
					Object support=doc.get("list");
					int row=IKFunction.rowsArray(support);
					if(!StringUtil.isEmpty(rose.toString())){
						float d=Float.parseFloat(rose.toString());
						if(d>0){
							for(int i=1;i<=row;i++){
								Object one=IKFunction.array(support, i);
								String name=IKFunction.keyVal(one,"ss").toString();
								if(up1.containsKey(name)){
									int up=Integer.parseInt(up1.get(name).toString());
									up1.put(name,(up+1));
								}else{
									up1.put(name,1);
								}
							}
						}else{
							for(int i=1;i<=row;i++){
								Object one=IKFunction.array(support, i);
								String name=IKFunction.keyVal(one,"ss").toString();
								if(down1.containsKey(name)){
									int down=Integer.parseInt(down1.get(name).toString());
									down1.put(name,(down+1));
								}else{
									down1.put(name,1);
								}
							}
						}
					}

			 }
		 }
		 cursor.close();
		 Iterator<String> iter=up1.keySet().iterator();
			DecimalFormat df=new DecimalFormat("0.0000");
			HashMap<String, Object> map=new HashMap<String, Object>();//map 存储指标对应的 准确率和总数量
			while (iter.hasNext()) {
				String key = iter.next().toString();
				int val1=Integer.parseInt(up1.get(key).toString());
				int  val2=0;
				if(down1.containsKey(key)){
					 val2 = Integer.parseInt(down1.get(key).toString());
				}
				int val=val1+val2;
				if(val>10){
					String c=df.format((float)val1/val);
					map.put(key, val+","+c);
				}
			}	
			
			MongoCollection<Document> collection=mongo.getShardConn("app_xg_wx_last");
			cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator();
			while(cursor.hasNext()){
				Document doc=cursor.next();
				Object support=doc.get("list");
//				HashMap<String, Object> result=entry.getValue();
				int size=IKFunction.rowsArray(support);
				int all=1;
				float tweight=0;
				for(int i=1;i<=size;i++){
					Object one=IKFunction.array(support, i);
					String name=IKFunction.keyVal(one, "ss").toString();
					if(map.containsKey(name)){
						String val=map.get(name).toString();
						int total=Integer.parseInt(val.split(",")[0]);
						float weight=Float.parseFloat(val.split(",")[1]);
						all=all+total;
						tweight=tweight+(total*weight);
					}
				}
				String c=df.format((float)tweight/all);//准确度
				doc.remove("_id");
				doc.put("weight", c);
				mongo.upsertDocByTableName(doc, "app_xg_wx_last_all");
			}
			cursor.close();
		
	}
	
}
