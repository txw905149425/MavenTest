package com.test.MongoMaven.crawler.ajk.xiaoqu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

//新浪理财师
public class Crawler {
	 public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
			
			HashMap<String , String> map =new HashMap<String, String>();
			 Map<String, String> resultMap=null;
			 for(int i=1;i<=24;i++){
				 String url="http://www.anjuke.com/zh/cm-zu/p"+i+"/";;
				 resultMap=HttpUtil.getHtml(url, map, "utf8", 1);
				 String html=resultMap.get("html");
				 List<HashMap<String, Object>> list=parseList(html);
				 try {
					mongo.upsetManyMapByTableName(list, "ajk_zhuhai_community_name");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 System.out.println(".....................");
	}
	 
	 public static  List<HashMap<String, Object>>  parse(String html){
			List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> map=null;
			Document doc=Jsoup.parse(html);
			Elements block=doc.select(".li-info>h3>a");
			int num=block.size();
			for(int i=0;i<num;i++){
				map=new HashMap<String, Object>();
				Element e=block.get(i);
				String name=e.text().trim();
				String url="http://zh.anjuke.com"+e.attr("href").trim();
				String uid=IKFunction.regexp(url, "(\\d+)");
				map.put("id", name);
				map.put("url", url);
				map.put("uid", uid);
				list.add(map);
			}
			return list;
			
	 }
	 
	 public static  List<HashMap<String, Object>>  parseList(String html){
			List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> map=null;
			Document doc=Jsoup.parse(html);
			Element block=doc.select("ul.P3").get(0);
			Elements a=block.select("em>a");
			int num=a.size();
			for(int i=0;i<num;i++){
				map=new HashMap<String, Object>();
				Element e=a.get(i);
				String name=e.text().trim();
				String tmp=e.attr("href").trim();
				String uid=IKFunction.regexp(tmp, "(\\d+)");
				String url="http://zh.anjuke.com/community/view/"+uid;
				map.put("id", name);
				map.put("url", url);
				map.put("uid", uid);
				list.add(map);
			}
			return list;
			
	 }
	 
}
