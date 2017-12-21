package com.test.MongoMaven.wx.thread;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

//每天更新今天推荐的股票的涨跌幅
public class UpdateTodayStockRose {
	public static void main(String[] args) {
		   MongoDbUtil mongo=new MongoDbUtil();
		   for(int z=0;z<3;z++){
			   String table="";
			   if(z==0){
				   table="xg_stock_last_json";
			   }else if(z==1){
				   table="jg_wx_gzh_new";
			   }else if(z==2){
				   table="jg_wx_gzh";
			   }
			   if(z==2){
				   MongoCollection<Document> collectiondele=mongo.getShardConn(table);
				   MongoCursor<Document> cursor =collectiondele.find().batchSize(10000).noCursorTimeout(true).iterator();
				   DecimalFormat df=new DecimalFormat("0.00");
				   while(cursor.hasNext()){
					   Document doc=cursor.next();
					   test(doc,df); 
					   mongo.upsertDocByTableName(doc, table+"_all");
				   }
			   }else{
				   MongoCollection<Document> collectiondele=mongo.getShardConn(table);
				   MongoCursor<Document> cursor =collectiondele.find().batchSize(10000).noCursorTimeout(true).iterator();
				   DecimalFormat df=new DecimalFormat("0.00");
				   while(cursor.hasNext()){
					   Document doc=cursor.next();
					   String code=doc.get("code").toString().trim();
					   String url="";
						if(code.startsWith("6")){
							url="http://hq.sinajs.cn/list=sh"+code;
						}else{
							url="http://hq.sinajs.cn/list=sz"+code;
						}
						String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
						if(!StringUtil.isEmpty(html)&&html.length()>50){
							HashMap<String, Object > map=IKFunction.parseSina(html);
							if(!map.isEmpty()){
								String shou= map.get("priceE").toString();
								String now=map.get("priceNow").toString();
								if(shou.length()>0&&now.length()>0){
									String fudu=df.format((Float.parseFloat(now)-Float.parseFloat(shou))*100/Float.parseFloat(shou));
									doc.remove("_id");
									doc.append("rose", fudu);
									mongo.upsertDocByTableName(doc, table+"_all");
								}
								
							}
						}
				   }
			   }
		   }
		   
	}
	
	public static Document test(Document doc,DecimalFormat df){
	   Object dlist=doc.get("code_list");
	   JSONArray js=JSONArray.fromObject(dlist);
	   ArrayList<HashMap<String, Object>> tmp=new ArrayList<HashMap<String,Object>>();
	   for(int i=0;i<js.size();i++){
		   HashMap<String, Object> map=toHashMap(js.get(i));
		   String code=map.get("code").toString().trim();
		   String url="";
			if(code.startsWith("6")){
				url="http://hq.sinajs.cn/list=sh"+code;
			}else{
				url="http://hq.sinajs.cn/list=sz"+code;
			}
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)&&html.length()>50){
				HashMap<String, Object > dmap=IKFunction.parseSina(html);
				if(!map.isEmpty()){
					String shou= dmap.get("priceE").toString();
					String now=dmap.get("priceNow").toString();
					if(shou.length()>0&&now.length()>0){
						String fudu=df.format((Float.parseFloat(now)-Float.parseFloat(shou))*100/Float.parseFloat(shou));
						map.put("rose", fudu);
						tmp.add(map);
					}
				}
			}
	  }
	   doc.remove("_id");
	   doc.append("code_list", tmp);
	   return doc;
   }
	
	public static HashMap<String, Object> toHashMap(Object json){
		JSONObject js=JSONObject.fromObject(json);
		HashMap<String, Object> map=new HashMap<String, Object>();
		Iterator it = js.keys();
		while (it.hasNext()) {
	           String key = String.valueOf(it.next());  
	           Object value=js.get(key);
	           map.put(key, value);
		}
		return map;
	}
	
}
