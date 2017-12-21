package com.test.MongoMaven.wd.sscount;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class RuleCount {
	public static MongoDbUtil mongo=new MongoDbUtil();
	public static void main(String[] args) {
		DecimalFormat df=new DecimalFormat("0.0000");
		MongoCollection<Document> collection=mongo.getShardConn("ss_data_rule");
		ArrayList<String> clist=getCode();
		for(String code:clist){
			BasicDBObject find=new BasicDBObject();
			find.put("code", code);
			MongoCursor<Document> cursor =collection.find(find).batchSize(1000).noCursorTimeout(true).iterator();
			HashMap<String, Object> map=new HashMap<String, Object>();
			while(cursor.hasNext()){
				Document doc=cursor.next();
				String id=doc.getString("id");
				if(!id.contains("0.3")){
					continue;
				}
				if(!doc.containsKey("comment")){
					continue;
				}
				int comment=doc.getInteger("comment");
				if(comment<40){
					continue;
				}
				int up=0;
				if(doc.containsKey("up")){
					up=doc.getInteger("up");	
				}
				int down=0;
				if(doc.containsKey("down")){
					down=doc.getInteger("down");
				}
				
				int total=up+down;
				if(total<8){
					continue;
				}
				String n1=df.format((float)(up)/total);
				if(id.contains("+0.3")){
					map.put("plus1", n1);
					map.put("total1", total);
					map.put("up1", up);
					map.put("comment1", comment);
				}else if(id.contains("-0.3")){
					map.put("plus2", n1);
					map.put("total2", total);
					map.put("up2", up);
					map.put("comment2", comment);
				}
			}
			cursor.close();
			map.put("id", code);
			try {
				mongo.upsertMapByTableName(map, "ss_data_rule_last");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static ArrayList<String> getCode(){
		MongoCollection<Document> collection=mongo.getShardConn("stock_code");
		MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator();
		ArrayList<String> clist=new ArrayList<String>();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			String code=doc.getString("id").trim();
			if(code.length()==6){
				clist.add(code);
			}
		}
		cursor.close();
		return clist;
	}

}
