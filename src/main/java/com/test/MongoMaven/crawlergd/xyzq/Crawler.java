package com.test.MongoMaven.crawlergd.xyzq;

import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<Document> collection=mongo.getShardConn("stock_code");
		String time=IKFunction.getTimeNowByStr("yyyy-MM-dd");
		MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator();
		try{
		while(cursor.hasNext()){
			Document doc=cursor.next();
			Object id=doc.get("id");
			Object stockName=doc.get("name");
			String url="http://gs.bolanjr.com/gusou04/Home/Xyzqapp/index.html&bg=2&stockcode="+id;
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, "li.index_five_content")){
				Object doc1=IKFunction.JsoupDomFormat(html);
				int num=IKFunction.jsoupRowsByDoc(doc1, "li.index_five_content");
				ArrayList<HashMap<String, Object>> contList = new ArrayList<HashMap<String,Object>>();
				String content="";
				for(int i=0;i<num;i++){
					HashMap<String, Object> map1=new HashMap<String, Object>();
					String lable=IKFunction.jsoupTextByRowByDoc(doc1, "div.index_five_main", i);
					String text=IKFunction.jsoupTextByRowByDoc(doc1, "li.index_five_content", i);
					content=content+lable+text;
					map1.put("cont", lable+text);
					contList.add(map1);
				}
				HashMap<String, Object> map=new HashMap<String, Object>();
				map.put("id", id+time);
				map.put("stockName", stockName);
				map.put("stockCode", id);
				map.put("source", "兴业证券优理宝");
				map.put("contentlist", contList);
				map.put("url", url);
				map.put("content", content);
				map.put("time", time);
				mongo.upsertMapByTableName(map, "gd_test");
			}
		 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cursor.close();
	}
	
}
