package com.test.MongoMaven.wd.sscount;

import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class FindRule1 {

	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document> collection=mongo.getShardConn("ss_data_count");
		BasicDBObject find=new BasicDBObject();
//		find.put("stock_code",code);
		MongoCursor<Document> cursor =collection.find(find).batchSize(1000).noCursorTimeout(true).iterator();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			Object list=doc.get("list");
			Object code=doc.get("id");
			System.out.println(code);
			int num=IKFunction.rowsArray(list);
			HashMap<String, Object> map=new HashMap<String, Object>();
			int comm1=0;
			int ss1=0;
			int comm2=0;
			int ss2=0;
			HashMap<String, Object> map1=new HashMap<String, Object>();
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(list, i);
				String cp=IKFunction.keyVal(one, "cp").toString();
				if(!StringUtil.isEmpty(cp)){
					float com=0;
					try{
						com=Float.parseFloat(cp);
					}catch(Exception e){
						continue;
					}
					int comment=Integer.parseInt(IKFunction.keyVal(one, "comment").toString());
					if(com>0.3){
						String rose1=IKFunction.keyVal(one, "rose1").toString();
						if(StringUtil.isEmpty(rose1)){
							continue;
						}
						float r1=Float.parseFloat(rose1);
						if(r1>0.01){
							if(map.containsKey("up")){
								int s=Integer.parseInt(map.get("up").toString());
								map.put("up",s+1);
							}else{
								map.put("up",1);
							}
						}else{
							if(map.containsKey("down")){
								int s=Integer.parseInt(map.get("down").toString());
								map.put("down",s+1);
							}else{
								map.put("down",1);
							}
						}
//						map.put("comment", comment);
						ss1++;
						comm1+=comment;
					}else if(com<-0.3){
						String rose1=IKFunction.keyVal(one, "rose1").toString();
						if(StringUtil.isEmpty(rose1)){
							continue;
						}
						float r1=Float.parseFloat(rose1);
						if(r1>0.01){
							if(map1.containsKey("up")){
								int s=Integer.parseInt(map1.get("up").toString());
								map1.put("up",s+1);
							}else{
								map1.put("up",1);
							}
						}else{
							if(map1.containsKey("down")){
								int s=Integer.parseInt(map1.get("down").toString());
								map1.put("down",s+1);
							}else{
								map1.put("down",1);
							}
						}
//						map1.put("comment", comment);
						ss2++;
						comm2+=comment;
					}
				}
			}
			if(comm1>0&&ss1>0){
				int lcomm=comm1/ss1;
				map.put("comment", lcomm);
			}
			map.put("id", code+"+0.3");
			map.put("total",num);
			map.put("code", code);
			if(comm2>0&&ss2>0){
				int lcomm=comm2/ss2;
				map1.put("comment", lcomm);
			}
			map1.put("id", code+"-0.3");
			map1.put("total",num);
			map1.put("code", code);
			try {
				mongo.upsertMapByTableName(map, "ss_data_rule");
				mongo.upsertMapByTableName(map1, "ss_data_rule");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		cursor.close();
		
	}

}
