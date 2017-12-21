package com.test.MongoMaven.wd.sscount;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class MyThink1 {
	public static MongoDbUtil mongo=new MongoDbUtil();
	public static void main(String[] args) {
		DecimalFormat df=new DecimalFormat("0.00");
		MongoCollection<Document> collection=mongo.getShardConn("ss_data_time_rule");
		ArrayList<String> clist=getTime();
		for(String code:clist){
			for(int i=1;i<=2;i++){
				String flag="";
				if(i==1){
					flag="1";
				}else{
					flag="2";
				}
				BasicDBObject find=new BasicDBObject();
				find.put("code", code);
				find.put("flag", flag);
				HashMap<String, Object> dmap=new HashMap<String, Object>();
				MongoCursor<Document> cursor =collection.find(find).batchSize(1000).noCursorTimeout(true).iterator();
				int size=0;
				int day=0;
				double ltotal=0;
				while(cursor.hasNext()){
					Document doc=cursor.next();
					System.out.println(doc.get("code"));
					Object list=doc.get("list");
					int num=IKFunction.rowsArray(list);
					if(num<=1){
						break;
					}
					double total=0;
					ArrayList<Double> flist=new ArrayList<Double>();
					for(int j=1;j<=num;j++){
						Object one=IKFunction.array(list, j);
						String comments=IKFunction.keyVal(one, "comments").toString();
						double ss=Double.parseDouble(comments);
						flist.add(ss);
						total+=ss;
					}
					double cvg=Double.parseDouble(df.format(total/num));
//					System.out.println("cvg:"+cvg);
					double fc= 0;
					for(Double comm:flist){
						Double tmp=(comm-cvg)*(comm-cvg);
						fc=fc+tmp;
					}
					double d=fc/flist.size();
					double  last=Double.parseDouble(df.format(Math.sqrt(d)));
					ltotal+=last;
					size++;
					day+=num;
//					System.out.println("fc:"+last);
				}
				cursor.close();
				if(size>0){
					String c1=df.format(ltotal/size);
					int cday=day/size;
					dmap.put("id", code+flag);
					dmap.put("day",cday);
					dmap.put("std",c1 );
					try {
						mongo.upsertMapByTableName(dmap,"ss_data_time_cvg");
					} catch (Exception e) {
						 //TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	public static ArrayList<String> getTime(){
		ArrayList<String> list=new ArrayList<String>();
		MongoCollection<Document> collection=mongo.getShardConn("ss_data_time_rule");
		MongoCursor<String> cursor =collection.distinct("code", String.class).iterator();
		while(cursor.hasNext()){
			String t=cursor.next();
//			System.out.println(t);
			if(t.length()==6){
				list.add(t);
			}
		}
		cursor.close();
		return list;
	}

}
