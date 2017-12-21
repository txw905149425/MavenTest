package com.test.MongoMaven.crawler.thsApp;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;

public class Test {
	public static void main(String[] args) {
		String url="http://t.10jqka.com.cn/api.php?method=group.getLatestPost&limit=100&page=0&pid=0&return=json&allowHtml=0&uid=384369689&code=600000";
		HashMap<String, String> map=new HashMap<String, String>();
		 map.put("User-Agent","platform=gphone&version=G037.08.216.1.32");
	     map.put("Host","t.10jqka.com.cn");
	     map.put("If-Modified-Since","29 Mar 2017 07:44:28 UTC");
	     String html=HttpUtil.getHtml(url, map, "utf8", 1,new HashMap<String, String>()).get("html");
	     System.out.println(html);
		
		
	}
}
