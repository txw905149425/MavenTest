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

//计算每支每天评论变化率的平均数
public class MyThink {
	public static MongoDbUtil mongo=new MongoDbUtil();
	public static void main(String[] args) {
		DecimalFormat df=new DecimalFormat("0.0000");
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
				float ltotal=0;
				while(cursor.hasNext()){
					Document doc=cursor.next();
					System.out.println(doc.get("code"));
					Object list=doc.get("list");
					int num=IKFunction.rowsArray(list);
					if(num<=1){
						break;
					}
					float total=0;
					for(int j=2;j<=num;j++){
						Object one=IKFunction.array(list, j);
						float cp=Float.parseFloat(IKFunction.keyVal(one, "cp").toString());
						float ss=Math.abs(cp);
						total+=ss;
					}
					size++;
					float c1=Float.parseFloat(df.format(total/(num-1)));
					ltotal+=c1;
				}
				cursor.close();
				if(size>0){
					String c1=df.format(ltotal/size);
					dmap.put("id", code+flag);
					dmap.put("flag",flag);
					dmap.put("code", code);
					dmap.put("cvg", c1);
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
