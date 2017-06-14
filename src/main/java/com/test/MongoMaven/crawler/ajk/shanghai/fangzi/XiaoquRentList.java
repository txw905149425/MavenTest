package com.test.MongoMaven.crawler.ajk.shanghai.fangzi;

import java.util.HashMap;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class XiaoquRentList {
	public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document>  collection=mongo.getShardConn("ajk_shanghai_community_information");
		 Bson filter = Filters.exists("listed", false);
		 MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator(); 
		 HashMap<String,Object> rec=null;
		 while(cursor.hasNext()){
			 Document doc=cursor.next();
			 Object uid=doc.get("uid");
//			 Object name=doc.get("community_name");
			 Object rentNum=doc.get("rentNum");
			 if(rentNum==null||"0".equals(rentNum.toString())||"".equals(rentNum.toString())){
				 continue;
			 }
			 String num=rentNum.toString();
		     int rent=Integer.parseInt(num);
		     int page=0;
		     if(rent%20==0){
		    	 page=rent/20;
		     }else{
		    	 page=rent/20+1;
		     }
//		     System.out.println(page);
		     for(int i=1;i<=page;i++){
		    	rec =new HashMap<String, Object>();
		    	String url="http://shanghai.anjuke.com/community/props/rent/"+uid+"/p"+i;
		    	rec.put("id", url) ;
		    	rec.put("uid", uid) ;
		    	mongo.upsertMapByTableName(rec, "ajk_shanghai_list_url");
		     }
		     doc.append("listed", "1");
			 mongo.upsertDocByTableName(doc, "ajk_shanghai_community_information");
//			
		 }
	  cursor.close();
	}
}
