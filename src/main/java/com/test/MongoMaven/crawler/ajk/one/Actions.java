package com.test.MongoMaven.crawler.ajk.one;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Actions implements Runnable{
	private DataUtil util;
	
	public Actions(DataUtil util){
		this.util=util;
	}
	public void run() {
		// TODO Auto-generated method stub
		
		String url=util.getUrl();
		String uid=util.getCode();
		try {
			Map<String, String> resultmap=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1,applyProxy());
			String html=resultmap.get("html");
			if(!StringUtil.isEmpty(html)&&htmlFilter(html,"p.title>span")){
				MongoDbUtil mongo=new MongoDbUtil();
				List<HashMap<String, Object >>  list=parseList(html,uid);
//				mongo.upsetManyMapByTableName(list, "ajk_detail_url");
				for(HashMap<String, Object > listmap:list){
					//抓取详情页
					Object durl=listmap.get("id");
					if(durl!=null&&!IKFunction.isEmptyString(durl.toString())){
						resultmap=HttpUtil.getHtml(durl.toString(), new HashMap<String, String>(), "utf8", 1,applyProxy());
						if(resultmap.isEmpty()){
							 
						 }else{
							 html=resultmap.get("html");
							if(!StringUtil.isEmpty(html)&&ParseMethod.htmlFilter(html, ".wrapper>div>h3")){
								 HashMap<String , Object> records= ParseMethod.parseDetail(html);
								 records.put("url", durl);
								 records.put("uid",uid);
								 mongo.upsertMapByTableName(records, "ajk_house_information");
								 HashMap<String, Object > map=new HashMap<String, Object>();
									map.put("id", url);
									map.put("crawl_all", "1");
									mongo.upsertMapByTableName(map, "ajk_list_url");
								 System.out.println("0.0");
							 }else if(ParseMethod.htmlFilter(html, ".info>p")){
								 org.jsoup.nodes.Document dd=Jsoup.parse(html);
								 String text=dd.select(".info>p").get(0).text();
								 System.err.println(text);
							 }else {
								 System.err.println(html);
							 }
					 } 
					}
				}
				
			}else if(htmlFilter(html, ".info>p")){
				System.out.println(url);
				 org.jsoup.nodes.Document dd=Jsoup.parse(html);
				 String text=dd.select(".info>p").get(0).text();
				 System.err.println(text);
			 }else{
				 System.err.println(html);
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	
	public static HashMap<String, String> applyProxy(){
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("ip", "42.51.22.87");
		map.put("port", "37860");
		map.put("user", "jcj");
		map.put("pwd", "jcj2017666");
		map.put("need", "need");
		return map;	
	}
	
	public static List<HashMap<String, Object >> parseList(String html,String uid){
		List<HashMap<String, Object > > list=new ArrayList<HashMap<String,Object>>();
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
		Elements es=doc.select("p.title>span");
		int num=es.size();
		for(int i=0;i<num;i++){
			HashMap<String, Object > map=new HashMap<String, Object >();
			String url=IKFunction.jsoupListAttrByDoc(doc, "p.title>span", "href", i);
			map.put("id",url);
			map.put("uid",uid);
			list.add(map);
		}
		return list;
	}
	
	public static boolean htmlFilter(String html,String css){
		boolean flag=false;
		if(IKFunction.isEmptyString(html)){
			return false;
		}
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
		Elements es = doc.select(css);
		if (es.size() > 0) {		
			flag=true;
		}
		return flag;
	}
		
}