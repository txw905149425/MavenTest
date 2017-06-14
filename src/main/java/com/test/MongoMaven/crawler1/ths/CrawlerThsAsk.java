package com.test.MongoMaven.crawler1.ths;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

//同花顺问股   跟新速度快，抓取频率也要高快，抓取速度1分钟一次
public class CrawlerThsAsk {
	
	public static void main(String[] args) {
		String url="http://t.10jqka.com.cn/m/askIndex.html";
		HashMap<String, String> map=new HashMap<String, String>();
		Map<String, String> resultMap=HttpUtil.getHtml(url, map, "utf8", 1,new HashMap<String, String>());
		String html=resultMap.get("html");
		List<HashMap<String, Object>> listMap=ParseThs.parseList(html);
		 MongoDbUtil mongo=new MongoDbUtil();
		 PostData post=new PostData();
		 try {
			 if(!listMap.isEmpty()){
			   mongo.upsetManyMapByTableName(listMap, "ww_ask_online_all");
			   for(HashMap<String, Object> one:listMap){
					String ttmp=JSONObject.fromObject(one).toString();
					 String su= post.postHtml("http://localhost:8888/import?type=ww_stock_json",new HashMap<String, String>(),ttmp, "utf-8", 1);
						if(su.contains("exception")){
							System.err.println("写入数据异常！！！！  < "+su+" >");
						}
					}
			 }
			 listMap.clear();
			 listMap=ParseThs.parseList2(html);
			 if(!listMap.isEmpty()){
				 mongo.upsetManyMapByTableName(listMap, "ww_ask_online_all");
				 for(HashMap<String, Object> one:listMap){
						String ttmp=JSONObject.fromObject(one).toString();
						 String su= post.postHtml("http://localhost:8888/import?type=ww_stock_json",new HashMap<String, String>(),ttmp, "utf-8", 1);
							if(su.contains("exception")){
								System.err.println("写入数据异常！！！！  < "+su+" >");
							}
						}
				   
			 }
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
}
