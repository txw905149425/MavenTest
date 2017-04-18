package com.test.MongoMaven.uitil;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Demo {
	public static void main(String[] args) throws ClientProtocolException, IOException, ParseException {
		HttpUtil ht=new HttpUtil();
		String url="http://t.10jqka.com.cn/api.php?method=group.getLatestPost&limit=30&page=0&pid=0&return=json&allowHtml=0&uid=384369689&code=600000";
//			   url="http://guba.eastmoney.com/list,000962,f_1.html";
//		url="https://swww.niuguwang.com/tr/2016/history.ashx?s=xiaomi&version=3.7.0&packtype=1";//牛股王牛人选股信息
		
//		url="http://mnews.gw.com.cn//wap/data/ipad/stock/SZ/11/300111/f10/F10_Sjm.json";		//大智慧 问答
//		url="http://guba.sina.com.cn/?s=bar&name=600187";
//		 url="http://guba.sina.com.cn/?s=thread&tid=181776&bid=1111";
//		 url="http://t.10jqka.com.cn/m/askIndex.html";//同花顺问股
//		 url="https://htg.yundzh.com/data/showindex_0.json?49718119";//大智慧 慧问s
//		 url="http://gavinduan.mynetgear.com:8000/wf/search?type=talk_stock_json&terms=_id:600187sina";
		 //https://htg.yundzh.com/data/showindex_1.json?49718123
		HashMap<String, String> map=new HashMap<String, String>();
//		Accept-Encoding: gzip
//		User-Agent: platform=gphone&version=G037.08.216.1.32
//		Host: t.10jqka.com.cn
//		Connection: Keep-Alive
//		If-Modified-Since: Mon, 27 Mar 2017 07:44:28 UTC
        map.put("User-Agent","platform=gphone&version=G037.08.216.1.32");
        map.put("Host","t.10jqka.com.cn");
        map.put("If-Modified-Since","27 Mar 2017 07:44:28 UTC");
        
		Map<String, String> result= ht.getHtml(url, map, "utf8", 1);
		String html=result.get("html");
		System.out.println(html);
//		PrintWriter pw=new PrintWriter(new File("test"));
//		pw.println(html);
//		pw.close();		
		
//		String data="2017-02-15 15:30";
//		SimpleDateFormat   formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm"); 
//		Date date=formatter.parse(data);
//		System.out.println(date.getTime());
	}
}
