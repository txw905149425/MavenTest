package com.test.MongoMaven.crawlerxg.wx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class WeiXinStock {
	
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<org.bson.Document>  coll=mongo.getShardConn("gd_weixin");
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
//        doc5.put("type","2");//不按阅读量
		MongoCursor<org.bson.Document> cursor =coll.find(doc5).batchSize(10000).noCursorTimeout(true).iterator();
		ArrayList<String> listall=new  ArrayList<String>();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			Object contentlist=doc.get("contentlist");
			if(!contentlist.toString().isEmpty()){
				 JSONArray js=JSONArray.fromObject(contentlist);
				 int num=js.size();
				 ArrayList<String> listone=new  ArrayList<String>();
				 for(int i=0;i<num;i++){
					 Object  block=js.get(i);
					 Object json=IKFunction.jsonFmt(block);
					 String text=IKFunction.keyVal(json, "cont").toString();
					 ArrayList<String> list=anyCode(text,map);
					 listone.addAll(list);
				 }
				 listall.addAll(listone);
				 if(!listone.isEmpty()){
					 ArrayList<String> last=removeDuplicate(listone);
					 doc.remove("_id");
					 String codelist=last.toString().replace("[", "").replace("]", "");
					 doc.append("code_flag", codelist);
//					 doc.append("filter_flag", "1");
					 mongo.upsertDocByTableName(doc, "gd_weixin");
				 }
			}
		}
		cursor.close();
//		HashMap<String, Object> dmap=new HashMap<String, Object>();
//		ArrayList<String> lastall=removeDuplicate(listall);
//		String code_str=lastall.toString().replace("[", "").replace("]", "");
//		dmap.put("code_str",code_str);
//		dmap.put("id", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
//		dmap.put("website", "微信公众号推荐");
//		try {
//			mongo.upsertMapByTableName(dmap, "jg_weixin_stock");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	public static ArrayList<String> anyCode(String text,HashMap<String, String> map){
		ArrayList<String> list=new ArrayList<String>();
		if(StringUtil.isEmpty(text)){
			return list;
		}
		String tmp=IKFunction.regexp(text,"(\\d{6,})");
		Set<String> keySet = map.keySet();  
        for(Iterator<String> iterator = keySet.iterator();iterator.hasNext();){  
            String key = iterator.next();  
            if(text.contains(key)){
            	if(tmp.length()==6){
            		list.add(key);
            	}
            }
        }  
        return list;
	}
	
	public static ArrayList<String> removeDuplicate(ArrayList<String> arlList){      
		HashSet<String> h = new HashSet<String>(arlList);      
		arlList.clear();      
		arlList.addAll(h);  
		return arlList;
	}   
}
