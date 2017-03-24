package com.test.MongoMaven.crawler.tonghuashun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;
import org.jsoup.Jsoup;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.test.MongoMaven.db.MyCollection;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
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
		HashMap<Object,List<HashMap<String,Object>>> resultDbMap=parseHtml(html,"ths_margin_data");
		MongoCollection<Document> collection=null;
		try {
			MongoDatabase db=conn.getMongoDataBase();
			collection=db.getCollection("ths_margin_data");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MongoDbUtil mongo=new MongoDbUtil();
	 for(Entry<Object,List<HashMap<String,Object>>> entry:resultDbMap.entrySet()){  
		 List<HashMap<String,Object>> recordList= entry.getValue();  
		 String tableName=entry.getKey().toString();
		 try {
			mongo.insertManyDoc(recordList, collection, tableName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    } 
		
		
	}

	
	public HashMap<Object,List<HashMap<String,Object>>> parseHtml(String html,String tableName){
		HashMap<Object,List<HashMap<String,Object>>> resultDbMap=new HashMap<Object,List<HashMap<String,Object>>>();
		List<HashMap<String,Object>> listDbMap=new ArrayList<HashMap<String,Object>>();
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
		int num=IKFunction.jsoupRowsByDoc(doc, ".m-table.J-ajax-table>tbody>tr");
		for(int i=0;i<num;i++){
			HashMap<String,Object> dbMap=new HashMap<String, Object>();
			String time=IKFunction.jsoupTextByRowByDoc(doc, ".m-table.J-ajax-table>tbody>tr>td:nth-child(1)",i);
			String text=IKFunction.jsoupTextByRowByDoc(doc, ".m-table.J-ajax-table>tbody>tr",i);
			dbMap.put("id",time);
//			System.out.println(text);
			int size=text.split(" ").length;
			for(int j=0;j<size;j++){
				String name="";
				if(j<=3){
					name="本日融资余额(亿元)>";
				}else if(j<=6){
					name="本日融资买入额(亿元)>";
				}else if(j<=9){
					name="本日融券余量余额(亿元)>";
				}else{
					name="本日融资融券余额(亿元)>";
				}
				if(j==0){
					name="交易日期";
				}else{
//					System.out.println(IKFunction.jsoupTextByRowByDoc(doc, "tr.row2>.th-col",j-1)+"      "+(j-1));
					name+=IKFunction.jsoupTextByRowByDoc(doc, "tr.row2>.th-col",j-1);
				}
				dbMap.put(name, text.split(" ")[j]);
//				System.out.println(name+">"+text.split(" ")[j]);
			}
			listDbMap.add(dbMap);
		}
		resultDbMap.put(tableName, listDbMap);
		return resultDbMap;
	}
	

	

}
