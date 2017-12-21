package com.test.MongoMaven.zhibiao.good;

import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Think {
	public static void main(String[] args) {
		String html=FileUtil.read("d:/mytext1");
		MongoDbUtil mongo=new MongoDbUtil();
		HashMap<String , String> web=new HashMap<String, String>();
		ArrayList<String> tlist=FileUtil.readFileReturn("web2tab");
		for(String text:tlist){
			String key=text.split(":")[0];
			String tab=text.split(":")[1];
			web.put(key, tab);
		}
		Object js=IKFunction.jsonFmt(html);
		Object jlist=IKFunction.keyVal(js, "data");
		int num=IKFunction.rowsArray(jlist);
		HashMap<String , Object> up1=new HashMap<String, Object>(); //第一天涨的
		HashMap<String , Object> down1=new HashMap<String, Object>(); //第一天跌的
//		HashMap<String , Object> up2=new HashMap<String, Object>(); //第二天涨的
//		HashMap<String , Object> down2=new HashMap<String, Object>(); //第二天跌的
//		HashMap<String , Object> up3=new HashMap<String, Object>(); //第三天涨的
//		HashMap<String , Object> down3=new HashMap<String, Object>(); //第三天跌的
		for(int i=1;i<=num;i++){
			Object one=IKFunction.array(jlist, i);
			String day1=IKFunction.keyVal(one,"day1").toString();
			Object list=IKFunction.keyVal(one, "list");
			Object time=IKFunction.keyVal(one, "time");
			Object code=IKFunction.keyVal(one, "code");
			int row=IKFunction.rowsArray(list);
			float d1=100;
//			float d2=100;
//			float d3=100;
			if(!StringUtil.isEmpty(day1)&&!day1.endsWith("no")){
				 d1=Float.parseFloat(day1);
			}
//			String day2=IKFunction.keyVal(one,"day2").toString();
//			if(!StringUtil.isEmpty(day2)&&!day2.endsWith("no")){
//				d2=Float.parseFloat(day2);
//			}
//			String day3=IKFunction.keyVal(one,"day3").toString();
//			if(!StringUtil.isEmpty(day3)&&!day3.endsWith("no")){
//				d3=Float.parseFloat(day3);
//			}
			
			if(d1!=100){
				if(d1>=0){
					for(int j=1;j<=row;j++){
						Object two=IKFunction.array(list, j);
						String name=IKFunction.keyVal(two, "ss").toString();
						String tab=web.get(name); //获取网站对应的数据库表
						MongoCollection<Document> collection=mongo.getShardConn(tab);
						BasicDBObject find=new BasicDBObject();
						find.put("selectime", time);
						find.put("code", code);
						MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator();//根据数据库查询数据
						while(cursor.hasNext()){
							Document d=cursor.next();
							Object support=d.get("support");
							int size=IKFunction.rowsArray(support);
							for(int z=1;z<=size;z++){
								Object three=IKFunction.array(support, z);
								String key=IKFunction.keyVal(three, "ss").toString();
								if(key.equals("逃顶器")||key.equals("超赢短线")||key.equals("底部酝酿")||key.equals("疯狂十字星")){
									System.out.println(name);
								}
								if(up1.containsKey(key)){
									int up=Integer.parseInt(up1.get(key).toString());
									up1.put(key,(up+1));
								}else{
									up1.put(key,1);
								}
							}
						}
					}
				}else{
					for(int j=1;j<=row;j++){
						Object two=IKFunction.array(list, j);
						String name=IKFunction.keyVal(two, "ss").toString();
						String tab=web.get(name); //获取网站对应的数据库表
						MongoCollection<Document> collection=mongo.getShardConn(tab);
						BasicDBObject find=new BasicDBObject();
						find.put("selectime", time);
						find.put("code", code);
						MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator();//根据数据库查询数据
						while(cursor.hasNext()){
							Document d=cursor.next();
							Object support=d.get("support");
							int size=IKFunction.rowsArray(support);
							for(int z=1;z<=size;z++){
								Object three=IKFunction.array(support, z);
								String key=IKFunction.keyVal(three, "ss").toString();
								if(down1.containsKey(key)){
									int down=Integer.parseInt(down1.get(key).toString());
									down1.put(key,(down+1));
								}else{
									down1.put(key,1);
								}
							}
						}
				    }
			   }
			}
//			if(d2!=100){
//				if(d2>0){
//					for(int j=1;j<=row;j++){
//						Object two=IKFunction.array(list, j);
//						String name=IKFunction.keyVal(two, "ss").toString();
//						if(up2.containsKey(name)){
//							int up=Integer.parseInt(up2.get(name).toString());
//							up2.put(name,(up+1));
//						}else{
//							up2.put(name,1);
//						}
//					}
//				}else{
//					for(int j=1;j<=row;j++){
//						Object two=IKFunction.array(list, j);
//						String name=IKFunction.keyVal(two, "ss").toString();
//						if(down2.containsKey(name)){
//							int down=Integer.parseInt(down2.get(name).toString());
//							down2.put(name,(down+1));
//						}else{
//							down2.put(name,1);
//						}
//				    }
//			   }
//			}
			
//			if(d3!=100){
//				if(d3>0){
//					for(int j=1;j<=row;j++){
//						Object two=IKFunction.array(list, j);
//						String name=IKFunction.keyVal(two, "ss").toString();
//						if(up3.containsKey(name)){
//							int up=Integer.parseInt(up3.get(name).toString());
//							up3.put(name,(up+1));
//						}else{
//							up3.put(name,1);
//						}
//					}
//				}else{
//					for(int j=1;j<=row;j++){
//						Object two=IKFunction.array(list, j);
//						String name=IKFunction.keyVal(two, "ss").toString();
//						if(down3.containsKey(name)){
//							int down=Integer.parseInt(down3.get(name).toString());
//							down3.put(name,(down+1));
//						}else{
//							down3.put(name,1);
//						}
//				    }
//			   }
//			}
		}
		HashMap<String , Object> last=new HashMap<String, Object>(); 
		last.put("up1",up1);
//		last.put("up2",up2);
//		last.put("up3",up3);
		last.put("down1",down1);
//		last.put("down2",down2);
//		last.put("down3",down3);
		last.put("id", "good");
		try {
			mongo.upsertMapByTableName(last, "app_xg_think");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
