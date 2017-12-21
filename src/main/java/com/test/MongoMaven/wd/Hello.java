package com.test.MongoMaven.wd;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.crawler.dfcfWeb.Actions;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Hello {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document>  collection=mongo.getShardConn("xg_all_good_complite");
		Document d=new Document();
		d.put("supportbnum", -1);
		MongoCursor<Document> cursor =collection.find().sort(d).batchSize(10000).noCursorTimeout(true).iterator(); 
		 Document doc=null;
		 DataUtil util=null;
		 while(cursor.hasNext()){
			 doc=cursor.next();
			 util=new DataUtil();
			 Object code=doc.get("id");
			 Object name=doc.get("name");
			 Object supportbnum=doc.get("name");
			 String url="http://guba.eastmoney.com/list,"+code+",f.html";
			 util.setCode(code.toString());
			 util.setName(name.toString());
			 util.setUrl(url);
//			 executor.execute(new Actions(util,mongo)); 
		 }
		
	}
}
