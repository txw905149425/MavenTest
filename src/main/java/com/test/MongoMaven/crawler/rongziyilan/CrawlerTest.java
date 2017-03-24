package com.test.MongoMaven.crawler.rongziyilan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.jsoup.Jsoup;

import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;

import clojure.main;


/***
 * 融资融券历史数据
 * */
public class CrawlerTest {
	static int threadNum=5;
	public static void main(String[] args) {
		  ExecutorService executor = Executors.newFixedThreadPool(threadNum);
		  String url="http://data.10jqka.com.cn/market/rzrq/board/ls/field/rzjmr/order/desc/page/1/ajax/1/";
			 int pageCount=pageCount(url);
			 System.out.println(pageCount);
			 ArrayList<String> listUrls=makerUrl(url,pageCount);
			 for(String listUrl:listUrls){
				 executor.execute(new Actions(listUrl)); 
			 }
		  executor.shutdown();
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
