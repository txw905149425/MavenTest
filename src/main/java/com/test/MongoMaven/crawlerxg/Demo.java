package com.test.MongoMaven.crawlerxg;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

public class Demo {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document> collection=mongo.getShardConn("xg_tzyj_stock");
		 MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator(); 
		 Document doc=null;
		 HashMap<String, Object > records=null;
		 while(cursor.hasNext()){
			 doc=cursor.next();
			 Object title=doc.get("title");
			 Object time=doc.get("time");
			 Object list=doc.get("list");
			 int num=IKFunction.rowsArray(list);
			 for(int i=1;i<=num;i++){
				 records=new HashMap<String, Object>();
				 Object one=IKFunction.array(list, i);
				 Object name=IKFunction.keyVal(one, "stockName");
				 Object code=IKFunction.keyVal(one, "code");
				 Object price= IKFunction.keyVal(one, "priceNow");
				 records.put("id", title+""+name+time);
				 records.put("stockName", name);
				 records.put("code", code);
				 records.put("title", title);
				 records.put("selectime", time);
				 records.put("selecprice", price);
				 mongo.upsertMapByTableName(records, "xg_gpdt_stock_test"); 
			 }
		 }
	   cursor.close();
		
		
	}
}
