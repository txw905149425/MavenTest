package com.test.MongoMaven.crawlerjy.ngw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

//牛股王 牛人动态
public class CrawlerNiuRen {
	
	public static void main(String[] args) {
		String url="https://swww.niuguwang.com/tr/2016/history.ashx?s=xiaomi&version=3.7.0&packtype=1";
	try {
			Map<String, String> result=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1,new HashMap<String, String>());
			String html=result.get("html");
			List<HashMap<String, Object>> listMap=parseList(html);
			 MongoDbUtil mongo=new MongoDbUtil();
			mongo.upsetManyMapByTableName(listMap, "mm_ngw_genius");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<HashMap<String, Object>> parseList(String html){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
		Object json=IKFunction.jsonFmt(html);
		Object block=IKFunction.keyVal(json, "tradeData");
		int num=IKFunction.rowsArray(block);
		HashMap<String, Object> map=null;
		for(int i=1;i<=num;i++){
			map=new HashMap<String, Object>();
			Object tmp=IKFunction.array(block, i);
			Object id=IKFunction.keyVal(tmp, "UserID");
			Object name=IKFunction.keyVal(tmp, "UserName");
			Object MonthYield=IKFunction.keyVal(tmp, "MonthYield");
			Object WinRatio=IKFunction.keyVal(tmp, "WinRatio");
			String  describe="月均收益："+MonthYield.toString()+"；胜率："+WinRatio.toString();
			map.put("id", id);
			map.put("name", name);
			map.put("describe", describe);
			list.add(map);
		}
		return list;
	}
	
}
