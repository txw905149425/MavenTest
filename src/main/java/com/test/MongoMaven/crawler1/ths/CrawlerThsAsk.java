package com.test.MongoMaven.crawler1.ths;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;

//同花顺问股
public class CrawlerThsAsk {
	
	public static void main(String[] args) {
		String url="http://t.10jqka.com.cn/m/askIndex.html";
		HashMap<String, String> map=new HashMap<String, String>();
		Map<String, String> resultMap=HttpUtil.getHtml(url, map, "utf8", 1,new HashMap<String, String>());
		String html=resultMap.get("html");
		List<HashMap<String, Object>> listMap=ParseThs.parseList(html);
		 MongoDbUtil mongo=new MongoDbUtil();
		 try {
			mongo.upsetManyMapByTableName(listMap, "ths_ask_shares");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 System.out.println("...........");
		 listMap.clear();
		 listMap=ParseThs.parseList2(html);
		 try {
				mongo.upsetManyMapByTableName(listMap, "ths_ask_shares");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 System.out.println("...........");
		 
	}
}
