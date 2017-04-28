package com.test.MongoMaven.crawler1.dzh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;


public class CrawlerDzh {
	
	public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
			try {
				for(int i=0;i<31;i++){
					String url="https://htg.yundzh.com/data/showindex_"+i+".json?49718476";
					Map<String, String> resultMap=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1,new HashMap<String, String>());
					String html=resultMap.get("html");
//					System.out.println(html);
					List<HashMap<String, Object>> listMap=parseList(html);
					mongo.upsetManyMapByTableName(listMap, "ww_dzh_ask_shares");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.out.println(".......");
	}
	
	public static List<HashMap<String, Object>> parseList(String html){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
		Object json=IKFunction.jsonFmt(html);
		Object array1=IKFunction.keyVal(json, "msg");
		int num=IKFunction.rowsArray(array1);
		HashMap<String, Object> map=null;
		for(int i=1;i<=num;i++){
			map=new HashMap<String, Object>();
			Object js=IKFunction.array(array1,i);
			Object question=IKFunction.keyVal(js, "t");
			Object qq=IKFunction.JsoupDomFormat(question);
			String que=IKFunction.jsoupTextByRowByDoc(qq, "body", 0);
//			System.out.println(que);
			Object name=IKFunction.keyVal(js, "tgnm");
//			System.out.println(name);
			Object time=IKFunction.keyVal(js, "d");
//			System.out.println(time);
			Object answer_array=IKFunction.keyVal(js, "comment");
			Object answer_json=IKFunction.array(answer_array,1);
			Object answer=IKFunction.keyVal(answer_json, "t");
//			System.out.println(answer);
			map.put("id",que+time);
			map.put("question", que);
			map.put("name", name);
			map.put("answer", answer);
			map.put("time", time);
			map.put("html", js.toString());
			list.add(map);
		}
		return list;
		
	}
	
}
