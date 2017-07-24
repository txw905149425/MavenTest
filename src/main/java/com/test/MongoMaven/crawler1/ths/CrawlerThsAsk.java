package com.test.MongoMaven.crawler1.ths;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//同花顺问股   跟新速度快，抓取频率也要高快，抓取速度1分钟一次
public class CrawlerThsAsk {
	
	public static void main(String[] args) {
		String url="http://t.10jqka.com.cn/m/askIndex.html";
	try{
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1,new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html,".hot-ask-answer.bgfff.hide")&&IKFunction.htmlFilter(html,".info-rolling.rem12>a")){
			List<HashMap<String, Object>> listMap=ParseThs.parseList(html);
			 MongoDbUtil mongo=new MongoDbUtil();
			 if(!listMap.isEmpty()){
			   mongo.upsetManyMapByTableName(listMap, "ww_ask_online_all");
			 }
			 listMap.clear();
			 listMap=ParseThs.parseList2(html);
			 if(!listMap.isEmpty()){
			    mongo.upsetManyMapByTableName(listMap, "ww_ask_online_all");
			 }	
		}
	  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	  }
	}
}
