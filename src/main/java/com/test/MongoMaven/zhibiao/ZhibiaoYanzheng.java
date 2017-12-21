package com.test.MongoMaven.zhibiao;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.json.JSONArray;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class ZhibiaoYanzheng {
	
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document> collection=mongo.getShardConn("app_xg_weight");
//		BasicDBObject find=new BasicDBObject();
//		find.put("id", "all");
		MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator();
		HashMap<String, Object> map=new HashMap<String, Object>();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			String name=doc.get("id").toString();
			int total=Integer.parseInt(doc.get("total").toString());
			float weight=Float.parseFloat(doc.get("weight").toString());
			map.put(name,total+","+weight);
		}
		cursor.close();
		MongoCollection<Document> collection1=mongo.getShardConn("xg_stock_last_json_all");
		ArrayList<String> date=FileUtil.readFileReturn("d:/distinct_date.txt");
		for(String d:date){
//			String d="2017-10-17";
			BasicDBObject find=new BasicDBObject();
			find.put("selectime", d);
			MongoCursor<Document> cursor1 =collection1.find(find).batchSize(10000).noCursorTimeout(true).iterator();
			DecimalFormat df=new DecimalFormat("0.0000");
			while(cursor1.hasNext()){
				Document doc=cursor1.next();
				Object support=doc.get("support");
				Object code=doc.get("code");
//				Object js=IKFunction.arrayFmt(support);
				int num=IKFunction.rowsArray(support);
				int all=0;
				float tweight=0;
				for(int i=1;i<=num;i++){
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
//				System.out.println(code+":  "+tweight+"/"+all+"="+tweight/all);		
				String c=df.format((float)tweight/all);
				doc.put("weight", c);
				doc.remove("_id");
				mongo.upsertDocByTableName(doc, "xg_stock_last_json_all");
			}	
			cursor1.close();
		}
		
		
	}
	
}
