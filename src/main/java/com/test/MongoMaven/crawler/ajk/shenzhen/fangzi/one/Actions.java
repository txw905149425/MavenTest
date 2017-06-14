package com.test.MongoMaven.crawler.ajk.shenzhen.fangzi.one;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;
import com.test.MongoMaven.uitil.HttpUtil;
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
					if(durl==null||StringUtil.isEmpty(durl.toString())){
						continue;
					}
					String xml=HttpUtil.getHtml(durl.toString(), new HashMap<String, String>(), "utf8", 1,applyProxy()).get("html");
						if(!StringUtil.isEmpty(xml)&&ParseMethod.htmlFilter(xml, ".wrapper>div>h3")){
							 HashMap<String , Object> records= ParseMethod.parseDetail(xml);
							 records.put("url", durl);
							 records.put("uid",uid);
							 mongo.upsertMapByTableName(records, "ajk_shenzhen_house_information");
							 HashMap<String, Object > map=new HashMap<String, Object>();
								map.put("id", url);
								map.put("crawl_all", "1");
								mongo.upsertMapByTableName(map, "ajk_shenzhen_list_url");
							 System.out.println("0.0");
						 }else if(ParseMethod.htmlFilter(html, ".info>p")){
							 org.jsoup.nodes.Document dd=Jsoup.parse(html);
							 String text=dd.select(".info>p").get(0).text();
							 System.err.println(text);
							 for(int i=0;i<10;i++){
								 String html1=HttpUtil.getHtml(durl.toString(), new HashMap<String, String>(), "utf8", 1,applyProxy()).get("html");
								if(!StringUtil.isEmpty(html1)&&ParseMethod.htmlFilter(html1, ".wrapper>div>h3")){
									 HashMap<String , Object> records= ParseMethod.parseDetail(html);
									 records.put("url", durl);
									 records.put("uid",uid);
									 mongo.upsertMapByTableName(records, "ajk_shenzhen_house_information");
									 HashMap<String, Object > map=new HashMap<String, Object>();
									 map.put("id", url);
									 map.put("crawl_all", "1");
									 mongo.upsertMapByTableName(map, "ajk_shenzhen_list_url");
									 System.err.println("0.0");
									 break;
								 }
							 }
							 
						 }
				
				}
				
			}else if(htmlFilter(html, ".info>p")){
				System.out.println(url);
				 org.jsoup.nodes.Document dd=Jsoup.parse(html);
				 String text=dd.select(".info>p").get(0).text();
				 System.err.println(text);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	
	public static HashMap<String, String> applyProxy(){
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("ip", "proxy.abuyun.com");
		map.put("port", "9020");
		map.put("user", "HD7WZ57850C8X24D");
		map.put("pwd", "71C9C56D5EA523E8");
		map.put("need", "need");
		return map;	
	}
	
//	public static HashMap<String, String> applyProxy(){
//		 List<HashMap<String, String>> list=new ArrayList<HashMap<String,String>>();
//		String url="http://svip.kuaidaili.com/api/getproxy/?orderid=939576819704167&num=50&b_pcchrome=1&b_pcie=1&b_pcff=1&protocol=2&method=2&an_an=1&an_ha=1&format=json&sep=1";
//		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
//		Object json=IKFunction.jsonFmt(html);
//		Object proxylist=IKFunction.keyVal(IKFunction.keyVal(json, "data"),"proxy_list");
//		Object test=IKFunction.array(IKFunction.arrayFmt(proxylist),1);
//		HashMap<String, String> map=new HashMap<String, String>();
//		String[] one=test.toString().split(":");
//		String ip=one[0];
//		String port=one[1];
//		map.put("ip",ip);
//		map.put("port",port);
//		list.add(map);
//		return map;	
//	}
	
	
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
	public static void main(String[] args) {
		applyProxy();
	}
		
}