package com.test.MongoMaven.crawler.ajk.shanghai.xiaoqu;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.test.MongoMaven.crawler.ajk.thread.ParseMethod;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Actions implements Runnable{
	private DataUtil util;
	private MongoDbUtil mongo;
	public Actions(DataUtil util,MongoDbUtil mongo){
		this.util=util;
		this.mongo=mongo;
	}
	public void run() {
		// TODO Auto-generated method stub
		String url=util.getUrl();
		String code=util.getCode();
		try{
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1,applyProxy()).get("html");
				 if(htmlFilter(html, ".basic-parms-mod>dd")){
					 HashMap<String, Object> records= parse(html);
					 if(!records.isEmpty()){
						 records.put("id", url);
						 records.put("community_name", code);
						 mongo.upsertMapByTableName(records, "ajk_shanghai_community_information");
						 Document d=new Document();
						 d.append("id", code);
						 d.append("crawl", 1);
						 mongo.upsertDocByTableName(d, "ajk_shanghai_community_name");
						 System.out.println("OoO"); 
					 }
				 }else if(htmlFilter(html, ".info>p")){
					 org.jsoup.nodes.Document dd=Jsoup.parse(html);
					 String text=dd.select(".info>p").get(0).text();
					 System.err.println(text);
//					 System.exit(1);
				 }else {
					 System.err.println(html);
//					 Document d=new Document();
//					 d.append("id", code);
//					 d.append("crawl", 2);
//					 mongo.upsertDocByTableName(d, "ajk_zhuhai_community_name");
				 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
	 }

	
	public static HashMap<String, String> applyProxy(){
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("ip", "proxy.abuyun.com");
		map.put("port", "9020");
		map.put("user", "HT28G37A1W31A32D");
		map.put("pwd", "C7AD181122430DC6");
		map.put("need", "need");
		return map;	
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
	
	 public static  HashMap<String, Object>  parse(String html){
			org.jsoup.nodes.Document doc=Jsoup.parse(html);
			Elements es=doc.select(".basic-parms-mod>dd"); 
			HashMap<String, Object> jsonMap=new HashMap<String,Object>();
			try{
				String wuye_type=es.get(0).text();//物业类型
				 jsonMap.put("wuye_type", wuye_type);
				 String wuye_cost=es.get(1).text();//物业费用	
				 jsonMap.put("wuye_cost", wuye_cost);
				 String total_area=es.get(2).text();//总建面
				 jsonMap.put("total_area", total_area);
				 String total_num=es.get(3).text();//总户数
				 jsonMap.put("total_num", total_num);
				 String build_year=es.get(4).text();//建造年代
				 jsonMap.put("build_year", build_year);
				 String park_num=es.get(5).text();//停车位
				 jsonMap.put("park_num", park_num);
				 String rongjilv=es.get(6).text();//容积率
				 jsonMap.put("rongjilv", rongjilv);
				 String lvhualv=es.get(7).text();//绿化率
				 jsonMap.put("lvhualv", lvhualv);
				 String  developers=es.get(8).text();//开发商
				 jsonMap.put("developers", developers);
				 String wuye_name=es.get(9).text();//物业公司
				 jsonMap.put("wuye_name", wuye_name);
				 String price=doc.select(".average").get(0).text();
				 jsonMap.put("price", price);
			}catch(Exception e){
				System.err.println("解析小区信息异常........");
				e.printStackTrace();
			}
			return jsonMap;
			
	 }
}
