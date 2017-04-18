package com.test.MongoMaven.crawler.ajk.xiaoqu;

import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class AjuCrawl {
	public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document>  collection=mongo.getShardConn("ajk_zhuhai_community_name");
		 Bson filter = Filters.exists("url", true);
		 Bson filter1 = Filters.exists("crawl", false);
		 MongoCursor<Document> cursor =collection.find(filter).filter(filter1).batchSize(10000).noCursorTimeout(true).iterator(); 
		 try{
			 Map<String, String> resultMap=null;
//			 int i=0;
			 while(cursor.hasNext()){
				 Document doc=cursor.next();
				 Object code=doc.get("id");//小区名
	//			 String str=IKFunction.charEncode(code, "utf8");
	//			 String url="http://zh.zu.anjuke.com/fangyuan/p1/?t=1&comm_exist=on&kw="+str+"&cw="+str;
				 Object  url=doc.get("url");
				 System.out.println(url);
				 if(url==null||StringUtil.isEmpty(url.toString())){
					 continue;
				 }
				 resultMap=HttpUtil.getHtml(url.toString(), new HashMap<String, String>(), "utf8", 1);
				 String html=resultMap.get("html");
				 if(htmlFilter(html, ".comm-list.clearfix>dl>dd")){
					 HashMap<String, Object> records= parse(html);
					 records.put("id", url);
					 records.put("community_name", code);
					 mongo.upsertMapByTableName(records, "ajk_zhuhai_community_information");
					 Document d=new Document();
					 d.append("id", code);
					 d.append("crawl", 1);
					 mongo.upsertDocByTableName(d, "ajk_zhuhai_community_name");
				 }else if(htmlFilter(html, ".info>p")){
					 org.jsoup.nodes.Document dd=Jsoup.parse(html);
					 String text=dd.select(".info>p").get(0).text();
					 System.err.println(text);
				 }else {
					 System.err.println(html);
					 Document d=new Document();
					 d.append("id", code);
					 d.append("crawl", 2);
					 mongo.upsertDocByTableName(d, "ajk_zhuhai_community_name");
				 }
				 Thread.sleep(10000);
			 }
		 }catch (Exception e){
			 e.printStackTrace();
		 }
	  cursor.close();
	}
	
	 public static  HashMap<String, Object>  parse(String html){
			org.jsoup.nodes.Document doc=Jsoup.parse(html);
			Elements es=doc.select(".comm-list.clearfix>dl>dd"); 
//			int num=es.size();
			HashMap<String, Object> jsonMap=new HashMap<String,Object>();
				String name=es.get(0).text();//小区名
//				System.out.println("小区名："+name);
				jsonMap.put("name", name);
				String block=es.get(1).text();//所在版块
				
//				System.out.println("所在版块："+block);
				jsonMap.put("block", block);
				String addr=es.get(2).text();//地址
				jsonMap.put("addr", addr);
				
//				System.out.println("地址："+addr);
				String  developers=es.get(3).text();//开发商
//				System.out.println("开发商："+developers);
				
				jsonMap.put("developers", developers);
				String wuye_name=es.get(4).text();//物业公司
				
//				System.out.println("物业公司："+wuye_name);
				jsonMap.put("wuye_name", wuye_name);
				String wuye_type=es.get(5).text();//物业类型
				
//				System.out.println("物业类型："+wuye_type);
				jsonMap.put("wuye_type", wuye_type);
				String wuye_cost=es.get(6).text();//物业费用	
				
//				System.out.println("物业费用："+wuye_cost);
				
				jsonMap.put("wuye_cost", wuye_cost);
				String total_area=es.get(7).text();//总建面
				
//				System.out.println("总建面："+total_area);
				
				jsonMap.put("total_area", total_area);
				String total_num=es.get(8).text();//总户数
				
//				System.out.println("总户数："+total_num);
				
				jsonMap.put("total_num", total_num);
				String build_year=es.get(9).text();//建造年代
				
//				System.out.println("建造年代："+build_year);
				
				jsonMap.put("build_year", build_year);
				String rongjilv=es.get(10).text();//容积率

//				System.out.println("容积率："+rongjilv);
				
				jsonMap.put("rongjilv", rongjilv);
				String chuzulv=es.get(11).text();//出租率
				
//				System.out.println("出租率："+chuzulv);
				
				jsonMap.put("chuzulv", chuzulv);
				String park_num=es.get(12).text();//停车位
				
//				System.out.println("停车位："+park_num);
				
				jsonMap.put("park_num", park_num);
				String lvhualv=es.get(13).text();//绿化率
//				System.out.println("绿化率："+lvhualv);
				jsonMap.put("lvhualv", lvhualv);
				String price=doc.select(".comm-avg-price").get(0).text();
				jsonMap.put("price", price);
//				System.out.println(price);
				Elements dd=doc.select(".comm-mark.clearfix>a");
				String descibe="";
				for(int i=0;i<dd.size();i++){
					if(i==(dd.size()-1)){
						descibe+=dd.get(i).text();
					}else{
					descibe+=dd.get(i).text()+"、";
					}
				}
//				System.out.println(descibe);
				jsonMap.put("descibe", descibe);
				String abs=doc.select(".desc-cont").get(0).text();
				jsonMap.put("abs", abs);
			return jsonMap;
			
	 }
	
	 public static boolean htmlFilter(String html,String css){
			boolean flag=false;
			org.jsoup.nodes.Document doc=Jsoup.parse(html);
			Elements es = doc.select(css);
			if (es.size() > 0) {		
				flag=true;
			}
			return flag;
		}
	
}
