package com.test.MongoMaven.crawler1.sc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

//全球市场直播http://gmv.cjzg.cn/Mv/faq_list
public class CrawlerSC {
	
	public static void main(String[] args) {
		String url="http://gmv.cjzg.cn/Mv/get_more.html";
		HashMap< String, String> map=new HashMap<String, String>();
		ArrayList<NameValuePair> list=new ArrayList<NameValuePair>();
		String html=null;
		try {
			 MongoDbUtil mongo=new MongoDbUtil();
			for(int i=0;i<5;i++){
				list.add(new BasicNameValuePair("loadingnum", i+""));
				html = HttpUtil.postHtml(url, map, list, 1000, 1);
				List<HashMap<String, Object>> listMap=parseList(html);
				mongo.upsetManyMapByTableName(listMap, "ww_global_online_shares");
//				System.out.println(html);
			}
		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 System.out.println("...........");
	}
	
	public static List<HashMap<String, Object>> parseList(String html){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
		Object json=IKFunction.jsonFmt(html);
		Object str=IKFunction.keyVal(json, "data");
		Document doc=Jsoup.parse(str.toString());
		Elements lists=doc.select("li");
		int num=doc.select("li").size();
		HashMap<String, Object> map=null;
		for(int i=0;i<num;i++){
			map=new HashMap<String, Object>();
			String  html_str=lists.get(i).html();
			String question=doc.select(".wangyou-txt").get(i).text();
			String name=doc.select(".zhuanjia-title").get(i).text();
			String tmp=doc.select(".zhuanjia-txt").get(i).text();
			String answer=IKFunction.regexp(tmp, "(.*)\\[");
			String time=IKFunction.regexp(tmp, "\\[(.*)\\]");
			map.put("id",question+time);
			map.put("question", question);
			map.put("name", name);
			map.put("answer", answer);
			map.put("time", time);
			map.put("html", html_str);
			list.add(map);
//			System.out.println(question+"  "+name+"   "+answer+"  "+time);
		}
		return list;
	}
	
	
	
}
