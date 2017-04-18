package com.test.MongoMaven.crawler1.coach;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import net.sf.json.JSONArray;

import com.mongodb.client.MongoCollection;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

//股市教练-看直播
public class CrawlerCoach {

	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		MongoCollection<org.bson.Document> collection=mongo.getShardConn("ww_stock_coach_ask_online");	
		String url="http://t.10jqka.com.cn/api.php?method=newcircle.getLives&mask=&brokerName=&sort=";
		Map<String , String> result=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1);
		String html=result.get("html");
		try{
			if(filter(html)){
				List<String> list=parseList(html);
				for(String str:list){
					String durl="http://t.10jqka.com.cn/api.php?method=newcircle.getCircleLiveList&allowHtml=1&sort=down&master=0&limit=100&fid="+str;
//					System.out.println(durl);
					result.clear();
					result=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1);
					 html=result.get("html");
					 if(filter(html)){
						 List<HashMap<String, Object>> records=parseDetail(html);
						 mongo.upsetManyMapByCollection(records, collection, "ww_stock_coach_ask_online");
					 }
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	
	public static boolean filter(String html){
		if(StringUtil.isEmpty(html)){
			return false;
		}
		int num=html.length();
		if(num>300){
			return true;
		}
		return false;
	}
	
	public static List<String> parseList(String html){
		List<String> list =new ArrayList<String>();
		Object json=IKFunction.jsonFmt(html);
		Object block=IKFunction.keyVal(json, "result");
		Object array=IKFunction.keyVal(block, "list");
		int num=IKFunction.rowsArray(array);
		for(int i=1;i<=num;i++){
			Object tmp=IKFunction.array(array, i);
			Object id=IKFunction.keyVal(tmp, "id");
			list.add(id.toString());
		}
		return list;
	}
	
	public static List<HashMap<String, Object>> parseDetail(String html){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
		Object json=IKFunction.jsonFmt(html);
		Object block=IKFunction.keyVal(json, "result");
		Object array=IKFunction.keyVal(block, "lives");
		int num=IKFunction.rowsArray(array);
		HashMap<String, Object > map=null;
		for(int i=1;i<=num;i++){
			Object tmp=IKFunction.array(array, i);
			Object content=IKFunction.keyVal(tmp, "content");
			if(content.toString().contains("@")&&content.toString().contains("---")){
				map=new HashMap<String, Object>();
				Document doc=Jsoup.parse(content.toString());
				doc.select("span").remove();
				String str=doc.select("body").get(0).text();
//				System.out.println(str);
				str=str.replace("-", "");
				if(str.contains("回复：")){
//					System.out.println(str);
					String[] s=str.split("回复：");
					if(s.length<2){
						continue;
					}
					String question=s[0];
					String answer=s[1];
					Object time=timeFormat(IKFunction.keyVal(tmp, "time"));
					Object name=IKFunction.keyVal(tmp, "nickname");
					map.put("id",question+time);
					map.put("question", question);
					map.put("name", name);
					map.put("answer", answer);
					map.put("time", time);
					map.put("html", tmp.toString());
					list.add(map);
				}
				else{
					System.out.println(str);
//					System.exit(1);
				}
			}
		}
		return list;
		
	}
	
	public static  String timeFormat(Object ctime){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
		String tmp=ctime.toString()+"000";
		long mu=Long.parseLong(tmp);
		Date date=new Date(mu);
		String time = sdf.format(date);
		return time;
	}

}
