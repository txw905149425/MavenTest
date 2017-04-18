package com.test.MongoMaven.crawler.dzhApp;

import java.util.HashMap;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class ParseMethod {
	
	/**
	 * 该方法只适应于同花顺论股的解析
	 * 存储json源码+源码的部分描述,参数html在传递之前，要做非空以及数据准确性判断！！！！
	 *1. 抽取部分json数据（code,stock_name）作为对整个json的描述
	 *2. 返回一个HashMap<String,Object>
	 * */
	public static HashMap<String,Object> parseAllJson(String html){
//		List<HashMap<String,Object>> listDbMap=new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> dbMap=new HashMap<String, Object>();
		Object json=IKFunction.jsonFmt(html);
		Object tmp=IKFunction.keyVal(json, "result");
		Object tmp1=IKFunction.keyVal(tmp, "postlist");
		Object tmp2=IKFunction.keyVal(tmp1, "1");
		Object tmp3=IKFunction.keyVal(tmp2, "forumObj");
		Object code=IKFunction.keyVal(tmp3, "code");
		Object name=IKFunction.keyVal(tmp3, "name");
		Object id=IKFunction.md5(html);
		dbMap.put("id",id);
		dbMap.put("code", code);
		dbMap.put("name", name);
		dbMap.put("html", json);
//		listDbMap.add(dbMap);
		return dbMap;
	}
	
	public static HashMap<String,Object> parseJson(String html){
//		List<HashMap<String,Object>> listDbMap=new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> dbMap=new HashMap<String, Object>();
		Object json=IKFunction.jsonFmt(html);
		Object tmp=IKFunction.keyVal(json, "result");
		Object postlist=IKFunction.keyVal(tmp, "postlist");
		Object tmp2=IKFunction.keyVal(postlist, "1");
		Object tmp3=IKFunction.keyVal(tmp2, "forumObj");
		Object code=IKFunction.keyVal(tmp3, "code");
		Object name=IKFunction.keyVal(tmp3, "name");
		dbMap.put("id", code);
		dbMap.put("name", name);
		dbMap.put("result", tmp);
		return dbMap;
	}
	
	
	
	/**
	 * 0:正常
	 * 14：该论股堂暂时不支持讨论
	 * 8888：其他情况
	 * */
	public static boolean htmlFilter(String html,String css){
		boolean flag=false;
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
		Elements es = doc.select(css);
		if (es.size() > 0) {		
			flag=true;
		}
		return flag;
	}
	
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		String html=IKFunction.read("json.txt");
		HashMap<String,Object> map=parseJson(html);
		map.put("id", "333333");
		mongo.upsertMapByTableName(map, "test");
//		System.out.println(parseJson(html).toString());
	}
	
	
}
