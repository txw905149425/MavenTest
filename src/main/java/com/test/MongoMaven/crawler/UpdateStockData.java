package com.test.MongoMaven.crawler;

import java.util.HashMap;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class UpdateStockData {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document>  collection=mongo.getShardConn("stock_code");
//		 Bson filter = Filters.exists("name", false);
		 Bson filter = Filters.eq("name", "");
		 MongoCursor<Document> cursor =collection.find().filter(filter).batchSize(10000).noCursorTimeout(true).iterator();
		 while(cursor.hasNext()){
			 Document doc=cursor.next();
			 String code=doc.get("id").toString();
			 String tmp="";
			 if(code.startsWith("6")){
				 tmp="sh"+code;
			 }else{
				 tmp="sz"+code;
			 }
			 String url="http://hq.sinajs.cn/list="+tmp;
			 String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			 if(!StringUtil.isEmpty(html)&&html.length()>50){
//				 System.out.println(html);
				 HashMap<String, Object> map=IKFunction.parseSina(html);
				 Object name=map.get("name");
				 doc.put("name", name);
				 mongo.upsertDocByTableName(doc, "stock_code");
			 }
		 }
		 System.out.println("..................");
	}

}
