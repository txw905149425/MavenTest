package com.test.MongoMaven.wd;

import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

import net.sf.json.JSONArray;

public class select {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document> collction=mongo.getShardConn("lzx_viewpoint");
		String timedel = IKFunction.getTimeNowByStr("yyyy-MM-dd");
		Bson filter=Filters.exists("old_flag",false);
		MongoCursor<Document> cursor = collction.find(new org.bson.Document("timedel",timedel)).filter(filter).batchSize(10000).iterator();
		MongoCollection<Document> collction1=mongo.getShardConn("lzx_stockmodule");
		MongoCursor<Document> cursor1 = collction1.find().iterator();
		MongoCollection<Document> collction2=mongo.getShardConn("lzx_stockmoduleinfo");
		ArrayList<String> savelist = new ArrayList<String>();
		ArrayList<String> idlist=new ArrayList<String>();
		while(cursor.hasNext()){
			Document doc=cursor.next();
			String id=doc.get("id").toString();
			Object contentlist=doc.get("contentlist");
			JSONArray js=JSONArray.fromObject(contentlist);
			int num = IKFunction.rowsArray(contentlist);
			for (int i = 1; i < num; i++) {
				 Object one1=js.get(i);
				 Object json=IKFunction.jsonFmt(one1);
				 String cont=IKFunction.keyVal(json, "cont").toString();
				 if(cont.length()>50){
					 savelist.add(cont);
				 }
			}
			idlist.add(id);
		}
		MongoCursor<Document> dp=collction2.find(new Document("id","大盘")).iterator();
		ArrayList<String> saveDP=null;
		Document docdp=null;
		if(dp.hasNext()){
			docdp=dp.next();
		}
		if(docdp==null){
			saveDP=new ArrayList<String>();
		}else{
			Object alist=docdp.get("stockinfo");
			JSONArray js=JSONArray.fromObject(alist);
			saveDP=toArrayList(js);
		}
		while(cursor1.hasNext()){
			Document doc1=cursor1.next();
			String str=doc1.get("stockmodul").toString();
			MongoCursor<Document> tt=collction2.find(new Document("id",str)).iterator();
			Document doc2=null;
			if(tt.hasNext()){
				doc2=tt.next();
			}
			ArrayList<String> save=null;
			
			if(doc2==null){
				save=new ArrayList<String>();
			}else{
				Object alist=doc2.get("stockinfo");
				JSONArray js=JSONArray.fromObject(alist);
				save=toArrayList(js);
			}
			for (int a = 0; a < savelist.size(); a++) {
				int n=0;
	 			String text = savelist.get(a);
				if (text.contains(str)) {
					save.add(text);
				}
				if(text.contains("大盘")){
					n++;
				}else if(text.contains("指数")){
					n++;
				}else if(text.contains("沪深")){
					n++;
				}
				if(n>1){
				  saveDP.add(text);
				}
			}
			ArrayList<String> list=repetitionList(save);
			HashMap<String, Object> map4 = new HashMap<String, Object>();
			map4.put("id", str);
			map4.put("stockinfo", list);
			try {
				mongo.upsertMapByTableName(map4, "lzx_stockmoduleinfo");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!saveDP.isEmpty()){
			ArrayList<String> list=repetitionList(saveDP);
			HashMap<String, Object> map4 = new HashMap<String, Object>();
			map4.put("id", "大盘");
			map4.put("stockinfo", list);
			try {
				mongo.upsertMapByTableName(map4, "lzx_stockmoduleinfo");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//更新数据到库里
		BasicDBObject searchQuery = new BasicDBObject(); // 插入文档更新的条件
		BasicDBObject updateDocument = new BasicDBObject();
		UpdateOptions options = new UpdateOptions().upsert(true);
		for(String id:idlist){
			searchQuery.append("id", id);
			Document records=new Document();
			records.append("old_flag", "1");
 			updateDocument.append("$set", records);
			collction.updateOne(searchQuery, updateDocument,options);
		}
		
	}
	
	public static ArrayList<String> repetitionList(ArrayList<String> save){
		if(save.isEmpty()){
			return  new ArrayList<String>();
		}
		ArrayList<String> list=new ArrayList<String>();
		HashMap<String, String> tmp=new HashMap<String, String>();
		for(String str:save){
			tmp.put(str, "1");
		}
		for (Map.Entry<String, String> entry : tmp.entrySet()) {
			list.add(entry.getKey())  ;
		}
		return list;
	}
	public static ArrayList<String> toArrayList(JSONArray js){
		if(js.isEmpty()){
			return new ArrayList<String>();
		}
		int num=js.size();
		ArrayList<String> list=new ArrayList<String>();
		for(int i=0;i<num;i++){
			String str=js.get(i).toString();
			list.add(str);
		}
		return list;
	}
	
}
