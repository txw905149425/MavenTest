package com.test.MongoMaven.wd.sscount;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Rules {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document> collection=mongo.getShardConn("ss_data_time");
		BasicDBObject find1=new BasicDBObject();
//		Bson filter=Filters.exists("flag",false);
//		find1.put("id","600038");
		MongoCollection<Document> collection1=mongo.getShardConn("ss_data_count1");
		MongoCursor<Document> cursor =collection.find(find1).batchSize(1000).noCursorTimeout(true).iterator();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			String code=doc.getString("id");
			Object list=doc.get("list");
			BasicDBObject find=new BasicDBObject();
			find.put("id", code);
			ArrayList<String> tlist=new ArrayList<String>();
			ArrayList<Integer> clist=new ArrayList<Integer>();
//			HashMap<String,Integer> dmap=new HashMap<String, Integer>();
			MongoCursor<Document> cursor1 =collection1.find(find).batchSize(1000).noCursorTimeout(true).iterator();
			if(cursor1.hasNext()){
				Document doc1=cursor1.next();
				Object list1=doc1.get("list");
				int num=IKFunction.rowsArray(list1);
				for(int i=1;i<=num;i++){
					Object one=IKFunction.array(list1, i);
					String time=IKFunction.keyVal(one, "time").toString();
					int comments=Integer.parseInt(IKFunction.keyVal(one, "comments").toString());
//					dmap.put(time, cp);
					if(isWeekend(time)){
						continue;
					}
					tlist.add(time);
					clist.add(comments);
				}
			}
			cursor1.close();
			int num=IKFunction.rowsArray(list);
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(list, i);
				String stime=IKFunction.keyVal(one, "stime").toString();
				String etime=IKFunction.keyVal(one, "etime").toString();
				String flag=IKFunction.keyVal(one, "flag").toString();
				int b=0;
				int e=0;
				if(tlist.contains(stime)){
					b=tlist.indexOf(stime);
				}
				if(tlist.contains(etime)){
					e=tlist.indexOf(etime);
				}
				ArrayList<HashMap<String, Object>> llist=new ArrayList<HashMap<String,Object>>();
				if(e>b){
					for(int x=b;x<e;x++){
						HashMap<String, Object> dmap=new HashMap<String, Object>();
						String tmp1=tlist.get(x);
						int tmp2=clist.get(x);
						dmap.put("time",tmp1);
						dmap.put("comments",tmp2);
						llist.add(dmap);
					}
			   }
			  if(!llist.isEmpty()){
				  HashMap<String, Object> dmap=new HashMap<String, Object>();
				  dmap.put("id", code+i);
				  dmap.put("flag", flag);
				  dmap.put("code", code);
				  dmap.put("list", llist);
				  try {
					mongo.upsertMapByTableName(dmap, "ss_data_time_rule");
					System.out.println(code+"  "+i);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			  }	
			}
		}
		cursor.close();
	}
	
	public static boolean isWeekend(String time){
		boolean flag=false;
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d = format.parse(time);
			String we=IKFunction.getWeekOfDate(d);
		       if(we.equals("星期日")||we.equals("星期六")){
		    	  flag=true;
		       }
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
		return flag;
	}
	
}
