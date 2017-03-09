package com.test.MongoMaven.uitil;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TestHttpClient implements Runnable{
	 private HashMap map=null;
	 private String  url=null;
	public void run() {
		// TODO Auto-generated method stub
		Map< String, String> result=HttpUtil.getHtml(url, map, "", 1);
		String html=result.get("html");
		Document doc=Jsoup.parse(html);
		doc.select("title");
		System.out.println(result.get("setCookie"));
		
	}
	  public TestHttpClient(String url,HashMap<String,String> map){
		  //初始化赋值
		  this.map=map;
		  this.url=url;
	  }
	
}
