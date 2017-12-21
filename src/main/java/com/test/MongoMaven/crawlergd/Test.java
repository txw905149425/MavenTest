package com.test.MongoMaven.crawlergd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Test {
	public static void main(String[] args) throws FileNotFoundException {
		MongoDbUtil mongo=new MongoDbUtil();
		PrintWriter pw=new PrintWriter(new File("e:/test.txt"));
		ArrayList<String> list=FileUtil.readFileReturn("e:/ans.txt");
		 MongoCollection<Document>  collection=mongo.getShardConn("stock_code");
		 Bson filter = Filters.exists("id", true);
		 MongoCursor<Document> cursor =collection.find(filter).batchSize(10000).noCursorTimeout(true).iterator(); 
		 ArrayList<String> list1=new ArrayList<String>();
		 while(cursor.hasNext()){
			 Document doc=cursor.next();
			 String name=doc.get("name").toString();
			 if(StringUtil.isEmpty(name)){
				 continue;
			 }
			 list1.add(name);
		 }
			 for(String str:list){
				 str=str.replaceAll("\"", "").trim();
				 if(StringUtil.isEmpty(str)||str.length()<2){
					continue;
				 }
			   for(String name:list1){
				 if(StringUtil.isEmpty(name)){
					 continue;
				 }
				 if(str.contains(name)){
					 if(name.contains("*")){
						 name=name.replace("*", "");
					 }
					 str=str.replaceAll(name, "");
				 }
			 }
			 pw.println(str);
		 }
		 pw.close();
		 cursor.close();
		
	}
}
