package com.test.MongoMaven.wd.sscount;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import net.sf.json.JSONObject;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

//从ss_data_count1中把所有数据都更新到ss_data_count中去（新增了统计出来的言论变化率，人数变化率以及股票涨跌幅）
public class FindRule {

	public static void main(String[] args) {
		DecimalFormat df=new DecimalFormat("0.0000");
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document> collection=mongo.getShardConn("ss_data_count1");
//		BasicDBObject find=new BasicDBObject();
		Bson filter=Filters.exists("flag",false);
//		find.put("website","同花顺");
		MongoCursor<Document> cursor =collection.find().filter(filter).batchSize(1000).noCursorTimeout(true).iterator();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			Object list=doc.get("list");
			System.out.println(doc.get("id"));
			int num=IKFunction.rowsArray(list);
			ArrayList<String> klist=new ArrayList<String>();
			HashMap<String, Integer> mapname=new HashMap<String, Integer>();
			HashMap<String, Integer> mapcomm=new HashMap<String, Integer>();
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(list, i);
				String time=IKFunction.keyVal(one, "time").toString();
				Date d=null;
				try {
					d = format.parse(time);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	           String we=IKFunction.getWeekOfDate(d);
	           if(we.equals("星期日")||we.equals("星期六")){
	        	   continue;
	           }
				String names=IKFunction.keyVal(one, "names").toString();
				String comments=IKFunction.keyVal(one,"comments").toString();
				int num1=Integer.parseInt(names);
				int num2=Integer.parseInt(comments);
				mapname.put(time, num1);
				mapcomm.put(time, num2);
				klist.add(time);
			}
			ArrayList<HashMap<String, Object>> dlist=new ArrayList<HashMap<String,Object>>();
			for(int i=0;i<klist.size()-1;i++){
				String key=klist.get(i);
				int name=mapname.get(key);
				int comment=mapcomm.get(key);
				String key1=klist.get(i+1);
				int name1=mapname.get(key1);
				int comment1=mapcomm.get(key1);
				String n1="";
				if(name==0){
					if(name1>10){
						n1=""+name1;	
					}else if(name1>0){
						n1="1";
					}else{
						n1="0";
					}
				}else{
					 n1=df.format((float)(name1-name)/name);
				}
				String c1="";
				if(comment==0){
					if(comment1>10){
						c1=""+comment1;
					}else if (comment1>0){
						c1="1";
					}else{
						c1="0";
					}
				}else{
					c1=df.format((float)(comment1-comment)/comment);
				}
				HashMap<String, Object> tmp=new HashMap<String, Object>();
				tmp.put("np", n1);
				tmp.put("cp", c1);
				tmp.put("name", name1);
				tmp.put("comment", comment1);
				tmp.put("time", key1);
				dlist.add(tmp);
			}
			doc.remove("_id");
			doc.put("list", dlist);
			doc.put("flag", "1");
			mongo.upsertDocByTableName(doc,"ss_data_count");
		}
		cursor.close();
		
	}
	
	public static String findKey(Object json){
		JSONObject js=JSONObject.fromObject(json);
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Iterator it = js.keys();
		String dkey="";
		try{
			while (it.hasNext()) {
	           String key = String.valueOf(it.next());
	           Date d=format.parse(key);
	           String we=IKFunction.getWeekOfDate(d);
	           if(we.equals("星期日")||we.equals("星期六")){
	        	   continue;
	           }
	           dkey=key;
			}
//			Collections.sort(list, new Comparator<String>(){
//				public int compare(String o1, String o2) {
//					// TODO Auto-generated method stub
//					long t1=str2Muil(o1);
//					long t2=str2Muil(o2);
//					String t=(t1-t2)+"";
//					return Integer.parseInt(t);
//				}
//			});
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return dkey;
	}
	
	public static long str2Muil(String str){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		long t1=0;
		try {
			t1=format.parse(str).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  t1;
	}
}
