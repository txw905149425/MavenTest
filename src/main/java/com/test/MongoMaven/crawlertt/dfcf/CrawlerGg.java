package com.test.MongoMaven.crawlertt.dfcf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.nodes.Element;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.Constants;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class CrawlerGg {
	static MongoDbUtil mongo=new MongoDbUtil();
	static PostData post=new PostData();
	public static void main(String[] args) {
		 MongoCollection<Document>  collection=mongo.getShardConn("stock_code");
		 Bson filter = Filters.exists("name", true);
//		 Bson filter = Filters.eq("id", "300145");
		 MongoCursor<Document> cursor =collection.find().filter(filter).batchSize(10000).noCursorTimeout(true).iterator();
	  try{
		 while(cursor.hasNext()){
			 Document doc=cursor.next();
			 String code=doc.get("id").toString();
			 String name=doc.get("name").toString();
			 String url="http://data.eastmoney.com/notices/getdata.ashx?CodeType=1&PageIndex=1&PageSize=50&StockCode="+code;
			 String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			 if(!StringUtil.isEmpty(html)&&html.length()>200){
//				 System.out.println(html);
				 parseList(html,code,name);
			 }
		 }
		 cursor.close();
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	}
	
	public static void parseList(String html,String code,String name){
		Object json=IKFunction.jsonFmt(html);
		Object data=IKFunction.keyVal(json,"data");
		int num=IKFunction.rowsArray(data);
	 try{
		 List<HashMap<String, Object>> list1=new ArrayList<HashMap<String,Object>>();
			HashMap<String, Object> map1=new HashMap<String, Object>();
			map1.put("name", name);
			map1.put("code", code);
			list1.add(map1);
		for(int i=1;i<=num;i++){
			Object one=IKFunction.array(data, i);
			String timeObj=IKFunction.keyVal(one, "EUTIME").toString();
			String time=timeObj.replace("+08:00", "").replace("T", " ");
			if(!IKFunction.timeOK(time)){
				break;
			}
			Object title=IKFunction.keyVal(one, "NOTICETITLE");
			Object str=IKFunction.keyVal(one, "INFOCODE");
			String url="http://data.eastmoney.com/notices/detail/"+code+"/"+str+",gz.html";
			String dhtml=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			 if(!StringUtil.isEmpty(dhtml)&&IKFunction.htmlFilter(dhtml, ".detail-body>div")){
				 HashMap<String, Object > map= parseDetail(dhtml);
				map.put("id", IKFunction.md5(title+"东方财富"+code));
				map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				map.put("tid", title);
				map.put("newsClass", "公告");
				map.put("source", "东方财富");
				map.put("title", title);
				map.put("time", time);
				map.put("related", code);
				map.put("code_list", list1);
				JSONObject mm_data=JSONObject.fromObject(map);
			    String su=post.postHtml(Constants.ES_URI+"type=tt_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
				if(su.contains("exception")){
					System.out.println(mm_data.toString());
					System.err.println("写入数据异常！！！！  < "+su+" >");
				}
				mongo.upsertMapByTableName(map, "tt_json_all");
//				mongo.upsertMapByTableName(map, "tt_dfcf");
			 }
		}
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	} 
	
	public static HashMap<String, Object > parseDetail(String html){
		Object doc=IKFunction.JsoupDomFormat(html);
		HashMap<String, Object > map=new HashMap<String, Object>();
		String content=IKFunction.jsoupTextByRowByDoc(doc, ".detail-body>div", 0);
		map.put("content", content);
		return map;
	}
	
}
