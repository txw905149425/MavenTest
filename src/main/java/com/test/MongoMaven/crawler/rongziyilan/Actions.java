package com.test.MongoMaven.crawler.rongziyilan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.test.MongoMaven.db.MyCollection;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Actions implements Runnable{
	private String url;
	public Actions(String url){
		this.url=url;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		MyCollection conn=new MyCollection();
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("", "");
		Map<String, String> resultmap=HttpUtil.getHtml(url, map, "utf8", 1);
		String html=resultmap.get("html");
		List<HashMap<String,Object>> resultDbMapList=ParseMethod.parseList(html,"ths_margin_data");
		
		try {
			MongoDatabase db=conn.getMongoDataBase();
			MongoCollection<Document> collection=db.getCollection("ths_margin_data");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 MongoDbUtil mongo=new MongoDbUtil();
		 for(HashMap<String,Object> detailMap:resultDbMapList){  
			 String detailUrl=detailMap.get("detailUrl").toString();
	     } 
		
		
	}

}
