package com.test.MongoMaven.crawler1.jrj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseMethod {
	
	public static List<HashMap<String, Object>> parseList(String html){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map=null;
		Document doc=Jsoup.parse(html);
		Element block=doc.select("ul.P3").get(0);
		Elements li=block.select("li>em");
		int num=li.size();
		for(int i=0;i<num;i++){
			map=new HashMap<String, Object>();
			Element e=li.get(i);
			String name=e.text().trim();
			map.put("id", name);
			list.add(map);
		}
		return list;
	}
}
