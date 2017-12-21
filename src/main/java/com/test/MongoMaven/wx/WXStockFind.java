package com.test.MongoMaven.wx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.json.JSONArray;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class WXStockFind {
	public static void main(String[] args) {

		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<org.bson.Document>  coll=mongo.getShardConn("gd_wx_gzh");
		MongoCollection<org.bson.Document>  coll1=mongo.getShardConn("stock_code");
		MongoCursor<org.bson.Document> cursor1 =coll1.find().batchSize(10000).noCursorTimeout(true).iterator();
		HashMap<String , String > map=new HashMap<String, String>();;
		while(cursor1.hasNext()){
			Document doc=cursor1.next();
			String code=doc.get("id").toString();
			String name=doc.get("name").toString();
			map.put(code, name);
		}
		cursor1.close();
		BasicDBObject doc5 = new BasicDBObject();
//        doc5.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
//        doc5.put("name","股票短线牛股");//不按阅读量
		Bson f=Filters.exists("code_flag1", false);
		MongoCursor<org.bson.Document> cursor =coll.find(doc5).filter(f).batchSize(10000).noCursorTimeout(true).iterator();
//		ArrayList<String> listall=new  ArrayList<String>();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			String name=doc.getString("name");
			String title=doc.getString("title");
			if(title.contains("利空")){
				continue;
			}
			Object contentlist=doc.get("contentlist");
			if(!contentlist.toString().isEmpty()){
				 JSONArray js=JSONArray.fromObject(contentlist);
				 int num=js.size();
				 ArrayList<String> listone=new  ArrayList<String>();
				 if(name.equals("每日一只牛股")||name.equals("明日股票推荐")){
					 for(int i=1;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 String tmp=IKFunction.regexp(text,"(\\d{6,})");
							Set<String> keySet = map.keySet();
					        for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){
					            String key = iterator.next();
					            if(text.contains(key)){
					            	if(tmp.length()==6){
					            		listone.add(key);
					            	}
					            }
					        }
					 }
				 }else if(name.equals("每日推荐牛股一只")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 String tmp=IKFunction.regexp(text,"(\\d{6,})");
							Set<String> keySet = map.keySet();  
					        for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
					            String key = iterator.next();  
					            if(text.contains(key)){
					            	if(tmp.length()==6){
					            		listone.add(key);
					            		break;
					            	}
					            }
					        }
					 }
				 }else if(name.equals("每日一只股票")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("明日个股精选")){
							 String tmp=IKFunction.regexp(text,"(\\d{6,})");
								Set<String> keySet = map.keySet();  
						        for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
						            String key = iterator.next();  
						            if(text.contains(key)){
						            	if(tmp.length()==6){
						            		listone.add(key);
						            	}
						            }
						        }
						  }
					  }
				 }else if(name.equals("中短线操盘达人")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("短    线    股    关   注")){
							 String tmp=IKFunction.regexp(text,"(\\d{6,})");
								Set<String> keySet = map.keySet();  
						        for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
						            String key = iterator.next();  
						            if(text.contains(key)){
						            	if(tmp.length()==6){
						            		listone.add(key);
						            	}
						            }
						        }
						  }
					  }
				 }else if(name.equals("紫色的牛股原创")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("操作思路")){
							 break;
						 }
						 String tmp=IKFunction.regexp(text,"(\\d{6,})");
						 Set<String> keySet = map.keySet();  
						 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
				            String key = iterator.next();  
				            if(text.contains(key)){
				            	if(tmp.length()==6){
				            		listone.add(key);
				            	}
				            }
						  }
					  }
				 }else if(name.equals("黑马营每日一股")||name.equals("小毛驴每日牛股推荐")||name.equals("每日一只好股")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("基本面")){
							 break;
						 }
						 String tmp=IKFunction.regexp(text,"(\\d{6,})");
						 Set<String> keySet = map.keySet();  
						 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
				            String key = iterator.next();  
				            if(text.contains(key)){
				            	if(tmp.length()==6){
				            		listone.add(key);
				            	}
				            }
						  }
					  }
				 }else if(name.equals("每日精选一只股票")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("精选理由")){
							 break;
						 }
						 String tmp=IKFunction.regexp(text,"(\\d{6,})");
						 Set<String> keySet = map.keySet();  
						 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
				            String key = iterator.next();  
				            if(text.contains(key)){
				            	if(tmp.length()==6){
				            		listone.add(key);
				            	}
				            }
						  }
					  }
				 }else if(name.equals("A股股票池")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("推荐理由")){
							 break;
						 }
						 String tmp=IKFunction.regexp(text,"(\\d{6,})");
						 Set<String> keySet = map.keySet();  
						 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
				            String key = iterator.next();  
				            if(text.contains(key)){
				            	if(tmp.length()==6){
				            		listone.add(key);
				            	}
				            }
						  }
					  }
				 }else if(name.equals("微中投")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 String tmp=IKFunction.regexp(text,"(\\d{6,})");
						 if(text.contains("荐股跟踪")){
							String txt=text.split("荐股跟踪")[0];
							 Set<String> keySet = map.keySet();  
							 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
					            String key = iterator.next();  
					            if(txt.contains(key)){
					            	if(tmp.length()==6){
					            		listone.add(key);
					            	}
					            }
							  }
							 break;
						 }else{
							 Set<String> keySet = map.keySet();  
							 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
					            String key = iterator.next();  
					            if(text.contains(key)){
					            	if(tmp.length()==6){
					            		listone.add(key);
					            	}
					            }
							  }
						 }
						
					  }
				 }else if(name.equals("无锋论市")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 String tmp=IKFunction.regexp(text,"(\\d{6,})");
						 if(text.contains("明日股票推荐")){
							String txt=text.split("明日股票推荐")[1];
							 Set<String> keySet = map.keySet();  
							 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
					            String key = iterator.next();  
					            if(txt.contains(key)){
					            	if(tmp.length()==6){
					            		listone.add(key);
					            	}
					            }
							  }
							 break;
						 }else{
							 Set<String> keySet = map.keySet();  
							 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
					            String key = iterator.next();  
					            if(text.contains(key)){
					            	if(tmp.length()==6){
					            		listone.add(key);
					            	}
					            }
							  }
						 }
						
					  }
				 }else if(name.equals("每日一只股票")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("投资理由")){
							 break;
						 }
						 String tmp=IKFunction.regexp(text,"(\\d{6,})");
						 Set<String> keySet = map.keySet();  
						 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
				            String key = iterator.next();  
				            if(text.contains(key)){
				            	if(tmp.length()==6){
				            		listone.add(key);
				            	}
				            }
						  }
					  }
				 }else if(name.equals("老张股票实战交流群")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("仓位操作指南")){
							 break;
						 }
						 String tmp=IKFunction.regexp(text,"(\\d{6,})");
						 Set<String> keySet = map.keySet();  
						 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
				            String key = iterator.next();  
				            if(text.contains(key)){
				            	if(tmp.length()==6){
				            		listone.add(key);
				            	}
				            }
						  }
					  }
				 }else if(name.equals("老王荐股")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("机构最新净买入")){
							 break;
						 }
						 String tmp=IKFunction.regexp(text,"(\\d{6,})");
						 Set<String> keySet = map.keySet();  
						 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
				            String key = iterator.next();  
				            if(text.contains(key)){
				            	if(tmp.length()==6){
				            		listone.add(key);
				            	}
				            }
						  }
					  }
				 }else if(name.equals("王亚伟荐股")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("短线之星")){
							 String tmp=IKFunction.regexp(text,"(\\d{6,})");
							 Set<String> keySet = map.keySet();  
							 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
					            String key = iterator.next();  
					            if(text.contains(key)){
					            	if(tmp.length()==6){
					            		listone.add(key);
					            	}
					            }
							  }
							 break;
						 }else{
							 String tmp=IKFunction.regexp(text,"(\\d{6,})");
							 Set<String> keySet = map.keySet();  
							 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
					            String key = iterator.next();  
					            if(text.contains(key)){
					            	if(tmp.length()==6){
					            		listone.add(key);
					            	}
					            }
							  } 
						 }
					  }
				 }else if(name.equals("黑马涨停牛股推荐")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("个股关注")){
							 String tmp=IKFunction.regexp(text,"(\\d{6,})");
							 Set<String> keySet = map.keySet();  
							 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
					            String key = iterator.next();  
					            if(text.contains(key)){
					            	if(tmp.length()==6){
					            		listone.add(key);
					            	}
					            }
							  }	 
						 }
					  }
				 }else if(name.equals("每日推荐一只金股")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("个股推送")){
							 text=text.split("个股推送")[1];
							 String tmp=IKFunction.regexp(text,"(\\d{6,})");
							 Set<String> keySet = map.keySet();  
							 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
					            String key = iterator.next();  
					            if(text.contains(key)){
					            	if(tmp.length()==6){
					            		listone.add(key);
					            	}
					            }
							  }	
							 break;
						 }else if(text.contains("个股分享")){
							 text=text.split("个股分享")[1];
							 String tmp=IKFunction.regexp(text,"(\\d{6,})");
							 Set<String> keySet = map.keySet();  
							 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
					            String key = iterator.next();  
					            if(text.contains(key)){
					            	if(tmp.length()==6){
					            		listone.add(key);
					            	}
					            }
							  }
							 break;
						 }
					  }
				 }else if(name.equals("股市热点每日一股")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("新股申购")){
							 break;
						 }
						 String tmp=IKFunction.regexp(text,"(\\d{6,})");
						 Set<String> keySet = map.keySet();  
						 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
				            String key = iterator.next();  
				            if(text.contains(key)){
				            	if(tmp.length()==6){
				            		listone.add(key);
				            	}
				            }
						  }	 
						 
					  }
				 }else if(name.equals("A股股票推荐涨停板预报每日一股")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("明天模拟推荐")){
							 String tmp=IKFunction.regexp(text,"(\\d{6,})");
							 Set<String> keySet = map.keySet();  
							 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
					            String key = iterator.next();  
					            if(text.contains(key)){
					            	if(tmp.length()==6){
					            		listone.add(key);
					            	}
					            }
							  }
							 break;
						 }
					  }
				 }else if(name.equals("收盘前股票推荐")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("股票推荐")){
							 block=js.get(i+1);
							 json=IKFunction.jsonFmt(block);
							 text=IKFunction.keyVal(json, "cont").toString();
							 String tmp=IKFunction.regexp(text,"(\\d{6,})");
							 Set<String> keySet = map.keySet();  
							 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
					            String key = iterator.next();  
					            if(text.contains(key)){
					            	if(tmp.length()==6){
					            		listone.add(key);
					            	}
					            }
							  }
							 break;
						 }
					  }
				 }else if(name.equals("股票推荐短线王")){
						 String text=doc.getString("title");
						 String tmp=IKFunction.regexp(text,"(\\d{6,})");
				         if(tmp.length()==6){
				            listone.add(tmp);
				         }
				 }else if(name.equals("股票短线牛股")){
					 String text=doc.getString("title");
					 String tmp=IKFunction.regexp(text,"(\\d{6,})");
			         if(tmp.length()==6){
			            listone.add(tmp);
			         }
			    }else if(name.equals("三只金股")){
					 if(title.contains("推荐")){
						 for(int i=0;i<num;i++){
							 Object  block=js.get(i);
							 Object json=IKFunction.jsonFmt(block);
							 String text=IKFunction.keyVal(json, "cont").toString();
								 String tmp=IKFunction.regexp(text,"(\\d{6,})");
								 Set<String> keySet = map.keySet();
								 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
						            String key = iterator.next();
						            if(text.contains(key)){
						            	if(tmp.length()==6){
						            		listone.add(key);
						            	}
						            }
								 }
						  }
					 }
			     }else if(name.equals("牛哥股票池")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("牛哥股票池")){
							 block=js.get(i+1);
							 json=IKFunction.jsonFmt(block);
							 text=IKFunction.keyVal(json, "cont").toString();
							 String tmp=IKFunction.regexp(text,"(\\d{6,})");
							 Set<String> keySet = map.keySet();  
							 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
					            String key = iterator.next();  
					            if(text.contains(key)){
					            	if(tmp.length()==6){
					            		listone.add(key);
					            	}
					            }
							  }
							 break;
						 }
					  }
				 }else if(name.equals("送牛股")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("每日一股")){
							 String tmp=IKFunction.regexp(text,"(\\d{6,})");
			            	 if(tmp.length()==6){
			            		listone.add(tmp);
			            	 }
					         break;
						  }
					  }
				 }
				 else if(name.equals("绝密牛股")){
					 for(int i=1;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("绝密牛股")){
							 text=text.split("绝密牛股")[0];
							 String tmp=IKFunction.regexp(text,"(\\d{6,})");
			            	 if(tmp.length()==6){
			            		listone.add(tmp);
			            	 }
					         break;
						  }
					  }
				 }else if(name.equals("今日牛股推荐")){
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("每日激进股")){
							 block=js.get(i+1);
							 json=IKFunction.jsonFmt(block);
							 text=IKFunction.keyVal(json, "cont").toString();
							 String tmp=IKFunction.regexp(text,"(\\d{6,})");
							 Set<String> keySet = map.keySet();  
							 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
					            String key = iterator.next();  
					            if(text.contains(key)){
					            	if(tmp.length()==6){
					            		listone.add(key);
					            	}
					            }
							  }
							 break;
						 }
					  }
				 }else if(name.equals("大智慧荐股")){
					 int begin=0;
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.contains("大智慧专家们推荐的牛股")){
							 begin=i;
							 break;
						 }
					  }
					 for(int i=begin;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 String tmp=IKFunction.regexp(text,"(\\d{6,})");
						 Set<String> keySet = map.keySet();  
						 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
				            String key = iterator.next();  
				            if(text.contains(key)){
				            	if(tmp.length()==6){
				            		listone.add(key);
				            	}
				            }
						  }
					  }
				 }
				 else{
					 for(int i=0;i<num;i++){
						 Object  block=js.get(i);
						 Object json=IKFunction.jsonFmt(block);
						 String text=IKFunction.keyVal(json, "cont").toString();
						 if(text.length()>50){
							 if(text.contains("个股跟踪")){
								text=text.split("个股跟踪")[0];
							 }else if(text.contains("重   点   关   注")){
								 text=text.split("重   点   关   注")[0];
							 }else if(text.contains("重点关注")){
								 text=text.split("重点关注")[0];
							 }else if(text.contains("上周分享的")){
								 text=text.split("上周分享的")[0];
							 }else if(text.contains("历史回顾")){
								 text=text.split("历史回顾")[0];
							 }else if(text.contains("跟踪个股")){
								 text=text.split("跟踪个股")[0];
							 }
						 }else{
							 if(text.contains("上期回顾")||text.contains("个股回顾")||text.contains("笔者实盘")||text.contains("机构最新净买入")||text.contains("历史回顾")||text.contains("个股跟踪")||text.contains("本周建仓的")||text.contains("重点消息面")||text.contains("跟踪个股")||text.contains("今日早盘资讯")){
								 break;
							 }
						 }
						 String tmp=IKFunction.regexp(text,"(\\d{6,})");
						 Set<String> keySet = map.keySet();  
						 for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
				            String key = iterator.next();  
				            if(text.contains(key)){
				            	if(tmp.length()==6){
				            		listone.add(key);
				            	}
				            }
						  }
					  }
				 }
				 if(!listone.isEmpty()&&listone.size()<=5){
					 ArrayList<String> last=removeDuplicate(listone);
					 doc.remove("_id");
					 Object id=doc.get("id");
					 String codelist=last.toString().replace("[", "").replace("]", "");
					 ArrayList<HashMap<String, Object>> ddlist=new ArrayList<HashMap<String,Object>>();
					 for(String scode:last){
						 HashMap<String, Object> ddmap=new HashMap<String, Object>();
						 ddmap.put("code", scode.trim());
						 ddlist.add(ddmap);
					 }
					 doc.append("code_flag1", codelist);
					 doc.append("code_list", ddlist);
					 mongo.upsertDocByTableName(doc, "gd_wx_gzh");
//					 doc.append("id", id);
//					 mongo.upsertDocByTableName(doc, "jg_wx_gzh_all");
				 }
			}
		}
		cursor.close();
	}
	
	public static ArrayList<String> removeDuplicate(ArrayList<String> arlList){      
		HashSet<String> h = new HashSet<String>(arlList);      
		arlList.clear();      
		arlList.addAll(h);  
		return arlList;
	}
}
