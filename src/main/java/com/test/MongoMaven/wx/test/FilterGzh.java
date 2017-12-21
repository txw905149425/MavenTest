package com.test.MongoMaven.wx.test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class FilterGzh {
	public static MongoDbUtil mongo=new MongoDbUtil();
	public static void main(String[] args) {
		HashMap<String, Integer> map1u=day("jg_test","$gt");
		HashMap<String, Integer> map1d=day("jg_test","$lt");
		HashMap<String, Integer> map2u=day("jg_test_day2","$gt");
		HashMap<String, Integer> map2d=day("jg_test_day2","$lt");
		HashMap<String, Integer> map3u=day("jg_test_day3","$gt");
		HashMap<String, Integer> map3d=day("jg_test_day3","$lt");
		DecimalFormat df=new DecimalFormat("0.0000");
//		HashMap<String, HashMap<String, Object>> map1=new HashMap<String, HashMap<String,Object>>();
		for (String key : map1u.keySet()) {
			int up=map1u.get(key);
			if(map1d.containsKey(key)){
				HashMap<String, Object> dmap=new HashMap<String, Object>();
				int down=map1d.get(key);
				String d=df.format((float)up/(up+down));
				dmap.put("up",up);
				dmap.put("down",down);
				dmap.put("total",(up+down));
				dmap.put("weight",d);
				dmap.put("name", key);
				dmap.put("id", key+"1");
				try {
					mongo.upsertMapByTableName(dmap, "jg_test_count");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		for (String key : map2u.keySet()) {
			int up=map2u.get(key);
			if(map2d.containsKey(key)){
				HashMap<String, Object> dmap=new HashMap<String, Object>();
				int down=map2d.get(key);
				String d=df.format((float)up/(up+down));
				dmap.put("up",up);
				dmap.put("down",down);
				dmap.put("total",(up+down));
				dmap.put("weight",d);
				dmap.put("name", key);
				dmap.put("id", key+"2");
				try {
					mongo.upsertMapByTableName(dmap, "jg_test_count");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		for (String key : map3u.keySet()) {
			int up=map3u.get(key);
			if(map3d.containsKey(key)){
				HashMap<String, Object> dmap=new HashMap<String, Object>();
				int down=map3d.get(key);
				String d=df.format((float)up/(up+down));
				dmap.put("up",up);
				dmap.put("down",down);
				dmap.put("total",(up+down));
				dmap.put("weight",d);
				dmap.put("name", key);
				dmap.put("id", key+"3");
				try {
					mongo.upsertMapByTableName(dmap, "jg_test_count");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
//		 //这里将map.entrySet()转换成list
//        List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(map.entrySet());
//        //然后通过比较器来实现排序
//        Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {
//            //升序排序
//            public int compare(Entry<String, Integer> o1,Entry<String, Integer> o2) {
//                return o2.getValue().compareTo(o1.getValue());
//            }
//            
//        });
//        for(Map.Entry<String,Integer> mapping:list){ 
//            System.out.println(mapping.getKey()+":"+mapping.getValue()); 
//       }
//        System.out.println(map.toString());
        
	}
	
	/**
	 *根据条件查询统计出符合的列
	 *@prama table:表名,每天的表名都不一样 
	 *@prama pre :查询表达式如：$gt
	 *@return HashMap<String, Integer>
	 */
	public static HashMap<String, Integer>  day(String table,String pre){
		MongoCollection<Document> collection=mongo.getShardConn(table);
		BasicDBObject find=new BasicDBObject();	
		BasicDBObject find1 = new BasicDBObject();
		find1.put(pre, 0.00);
		find.append("rose", find1);
		MongoCursor<Document> cursor =collection.find(find).batchSize(10000).noCursorTimeout(true).iterator();
		HashMap<String, Integer> map=new HashMap<String, Integer>();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			Object list=doc.get("list");
//			System.out.println(doc.get("rose"));
			int num=IKFunction.rowsArray(list);
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(list,i);
				String name=IKFunction.keyVal(one, "ss").toString();
				if(map.containsKey(name)){
					map.put(name, map.get(name)+1);
				}else{
					map.put(name, 1);
				}
			}
		}
		cursor.close();
		return map;
	}
}
