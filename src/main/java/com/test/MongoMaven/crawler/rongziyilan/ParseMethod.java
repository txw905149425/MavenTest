package com.test.MongoMaven.crawler.rongziyilan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;

public class ParseMethod {

	public static List<HashMap<String,Object>> parseList(String html,String tableName){
//		HashMap<Object,List<HashMap<String,Object>>> resultDbMap=new HashMap<Object,List<HashMap<String,Object>>>();
		List<HashMap<String,Object>> listDbMap=new ArrayList<HashMap<String,Object>>();
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
		int num=IKFunction.jsoupRowsByDoc(doc, "tbody>tr");
		for(int i=0;i<num;i++){
			HashMap<String,Object> dbMap=new HashMap<String, Object>();
			String code=IKFunction.jsoupTextByRowByDoc(doc, "tbody>tr:nth-child("+i+")>td",1);
			String share_name=IKFunction.jsoupTextByRowByDoc(doc, "tbody>tr:nth-child("+i+")>td",2);
			String detailUrl="http://data.10jqka.com.cn"+IKFunction.jsoupListAttrByDoc(doc, "tbody>tr:nth-child("+i+")>td>a","",0)+"order/desc/page/1/ajax/1/";
//			/market/rzrqgg/code/518880/
//			http://data.10jqka.com.cn/market/rzrqgg/code/518880/order/desc/page/1/ajax/1/
			dbMap.put("id",code);
			dbMap.put("share_name",share_name);
			dbMap.put("detailUrl",detailUrl);
			System.out.println(code+" "+share_name+" "+detailUrl);
			listDbMap.add(dbMap);
		}
//		resultDbMap.put(tableName, listDbMap);
		return listDbMap;
	}
	
	public static List<HashMap<String,Object>> parseDetail(String html,String tableName){
//		HashMap<Object,List<HashMap<String,Object>>> resultDbMap=new HashMap<Object,List<HashMap<String,Object>>>();
		List<HashMap<String,Object>> listDbMap=new ArrayList<HashMap<String,Object>>();
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
		int num=IKFunction.jsoupRowsByDoc(doc, "tbody>tr");
		for(int i=0;i<num;i++){
			HashMap<String,Object> dbMap=new HashMap<String, Object>();
			String code=IKFunction.jsoupTextByRowByDoc(doc, "tbody>tr:nth-child("+i+")>td",1);
			String share_name=IKFunction.jsoupTextByRowByDoc(doc, "tbody>tr:nth-child("+i+")>td",2);
			String detailUrl="http://data.10jqka.com.cn"+IKFunction.jsoupListAttrByDoc(doc, "tbody>tr:nth-child("+i+")>td>a","",0)+"order/desc/page/1/ajax/1/";
//			/market/rzrqgg/code/518880/
//			http://data.10jqka.com.cn/market/rzrqgg/code/518880/order/desc/page/1/ajax/1/
			dbMap.put("id",code);
			dbMap.put("share_name",share_name);
			dbMap.put("detailUrl",detailUrl);
			System.out.println(code+" "+share_name+" "+detailUrl);
			listDbMap.add(dbMap);
		}
//		resultDbMap.put(tableName, listDbMap);
		return listDbMap;
	}
	
	
	
	public static int pageCount(String url){
		HashMap<String, String> map=new HashMap<String, String>();
		Map<String, String> resultmap=HttpUtil.getHtml(url, map, "utf8", 1);
		String html=resultmap.get("html");
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
		Object text=IKFunction.jsoupTextByRowByDoc(doc, ".page_info", 0);
		Object num=IKFunction.regexp(text, "/(\\d+)");
		return Integer.parseInt(num.toString());
	}
	
	public static ArrayList<String> makerUrl(String url,int num){
		ArrayList<String> list=new ArrayList<String>();
		for(int i=1;i<=num;i++){
			url="http://data.10jqka.com.cn/market/rzrq/board/ls/field/rzjmr/order/desc/page/"+i+"/ajax/1/";
			list.add(url);
		}
		return list;
	}
	
	
	
}
