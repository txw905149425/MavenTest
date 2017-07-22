package com.test.MongoMaven.crawler1.nzw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.Jsoup;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;


//牛仔网
public class Crawler {
	
	public static void main(String[] args) {
		String url="http://live.9666.cn/getBroadcastListByAZ/";
		MongoDbUtil mongo=new MongoDbUtil();
		PostData post=new PostData();
		String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		Object doc=IKFunction.JsoupDomFormat(html);
		int num=IKFunction.jsoupRowsByDoc(doc, ".black.f18.js-cbga");
		try{
			for(int i=0;i<num;i++){
	//			HashMap<String,Object> map=new HashMap<String, Object>(); 
	//			String name=IKFunction.jsoupTextByRowByDoc(doc, ".black.f18.js-cbga",i);	
				String durl=IKFunction.jsoupListAttrByDoc(doc, ".black.f18.js-cbga","href",i);
				String dhtml=HttpUtil.getHtml(durl,new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
				String rid=IKFunction.regexp(dhtml, "\"rid\":(\\d+)");
				if(!"".equals(rid)){
					String lurl="http://liven.9666.info/others.php?rid=335893";//+rid;
					 String lhtml=HttpUtil.getHtml(lurl, new HashMap<String, String>(), "utf8",1, new HashMap<String, String>()).get("html");
					 List<HashMap<String, Object>> list= parse(lhtml);
					 if(!list.isEmpty()){
						 mongo.upsetManyMapByTableName(list, "ww_ask_online_all");
//						 for(HashMap<String, Object> one:list){
//							String ttmp=JSONObject.fromObject(one).toString();
//							 String su= post.postHtml("http://localhost:8888/import?type=ww_stock_json",new HashMap<String, String>(),ttmp, "utf-8", 1);
//								if(su.contains("exception")){
//									System.err.println("写入数据异常！！！！  < "+su+" >");
//								}
//						 }
				
					 }
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
			 
		 
	}
	
	public static List<HashMap<String, Object>> parse(String html){
		List<HashMap<String, Object>> listmap=new ArrayList<HashMap<String,Object>>();
		Object json=IKFunction.jsonFmt(html);
		Object list=IKFunction.keyVal(json, "scrips");
		int num=IKFunction.rowsArray(list);
		if(num==0){
			return listmap;
		}
		HashMap<String, Object> map=null;
		for(int i=1;i<=num;i++){
			map=new HashMap<String, Object>();
			Object one=IKFunction.array(list, i);
			Object stockName=IKFunction.keyVal(one, "stockName");
			Object stockCode=IKFunction.keyVal(one, "stockCode");
			Object objtime=IKFunction.keyVal(one, "at");
			String time=IKFunction.timeFormat(objtime.toString());
			if(!IKFunction.timeOK(time)){
				continue;
			}
			Object que=IKFunction.keyVal(one, "qc");
			String quest="";
			if(que.toString().contains("<span")){
				org.jsoup.nodes.Document obj=Jsoup.parse(que.toString());
				obj.select("span").remove();
				quest=obj.select("body").text();
			}else{
				quest=que.toString();
			}
			String question=stockName+"("+stockCode+")"+quest;
			Object name=IKFunction.keyVal(one, "ann");
			Object answer=IKFunction.keyVal(one, "ac");
			if(answer.toString().contains("<span")){
				org.jsoup.nodes.Document obj=Jsoup.parse(answer.toString());
				obj.select("span").remove();
				answer=obj.select("body").text();
			}
			if(!StringUtil.isEmpty(answer.toString())){
				map.put("ifanswer","1");
			}else{
				map.put("ifanswer","0");
			}
			map.put("id",IKFunction.md5(question+answer));
			map.put("tid",question+objtime);
			map.put("time",time);
			map.put("question",question);
			map.put("name",name);
			map.put("answer",answer);
			map.put("website","牛仔网");
			listmap.add(map);
		}
		return listmap;
	}
	
	
}
