package com.test.MongoMaven.crawlertt.dfcf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class CrawlerNews {
	static MongoDbUtil mongo=new MongoDbUtil();
	static PostData post=new PostData();
	public static void main(String[] args) {
		 MongoCollection<Document>  collection=mongo.getShardConn("stock_code");
		 Bson filter = Filters.exists("name", true);
//		 Bson filter = Filters.eq("id", "600187");
		 MongoCursor<Document> cursor =collection.find().filter(filter).batchSize(10000).noCursorTimeout(true).iterator();
	  try{
		 while(cursor.hasNext()){
			 Document doc=cursor.next();
			 String code=doc.get("id").toString();
			 String name=doc.get("name").toString();
			 String url="http://so.eastmoney.com/Search.ashx?qw="+code+"&qt=1&sf=0&st=1&cpn=1&pn=10&f=1&p=0";
			 String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			 if(!StringUtil.isEmpty(html)&&html.length()>200){
//				 System.out.println(html);
				 parseList(html, code, name);
			 }
		 }
		 cursor.close();
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	}
	
	public static void parseList(String html,String code,String name){
		Object json=IKFunction.jsonFmt(html);
	  try{
		Object data=IKFunction.keyVal(json,"DataResult");
		int num=IKFunction.rowsArray(data);
		List<HashMap<String, Object>> list1=new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> map1=new HashMap<String, Object>();
		map1.put("name", name);
		map1.put("code", code);
		list1.add(map1);
		for(int i=1;i<=num;i++){
			Object one=IKFunction.array(data, i);
			String timeObj=IKFunction.keyVal(one, "ShowTime").toString();
			String time=timeF(timeObj);
			if(!IKFunction.timeOK(time)){
				break;
			}
			Object title=IKFunction.keyVal(one, "Title");
			String url=IKFunction.keyVal(one, "Url").toString();
			 String dhtml=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			 if(!StringUtil.isEmpty(dhtml)&&IKFunction.htmlFilter(dhtml, "#ContentBody")){
				HashMap<String, Object > map= parseDetail(dhtml);
				map.put("id", IKFunction.md5(title+"东方财富"+code));
				map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				map.put("tid", title);
				map.put("newsClass", "新闻");
				map.put("source", "东方财富");
				map.put("title", title);
				map.put("time", time);
				map.put("related", code);
				map.put("code_list", list1);
				JSONObject mm_data=JSONObject.fromObject(map);
			    String su=post.postHtml("http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=tt_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
				if(su.contains("exception")){
					System.out.println(mm_data.toString());
					System.err.println("写入数据异常！！！！  < "+su+" >");
				}
				mongo.upsertMapByTableName(map, "tt_json_all");
//				mongo.upsertMapByTableName(map, "tt_dfcf");
			 }
			
		}
	  }catch(Exception e){e.printStackTrace();}
		
	}
	
	public static String timeF(String tt){
//		20170629073812
		if(tt.length()!=14){
			return "";
		}
		String year=tt.substring(0, 4);
		String mon=tt.substring(4, 6);
		String day=tt.substring(6, 8);
		String hour=tt.substring(8, 10);
		String min=tt.substring(10, 12);
		String sec=tt.substring(12, 14);
		String last=year+"-"+mon+"-"+day+" "+hour+":"+min+":"+sec;
	   return last;
	}
	
	public static HashMap<String, Object > parseDetail(String html){
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
		HashMap<String, Object > map=new HashMap<String, Object>();
		Elements con=doc.select("#ContentBody");
		con.select("table").remove();
		String content=con.get(0).text();
		map.put("content", content);
		return map;
	}

	
}
