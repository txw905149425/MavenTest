package com.test.MongoMaven.crawler1.jrj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.StringUtil;

public class ParseMethod {
	
	public static List<HashMap<String, Object>> parseList(String html){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map=null;
		Document doc=Jsoup.parse(html);
		Elements li=doc.select(".q-block.q-ques-item");
		int num=li.size();
		for(int i=0;i<num;i++){
			Element e=li.get(i);
			String question=e.select(".q-ques-item-q>a").get(0).text();
			String timeObject=e.select(".time.fr").get(0).text();
			String time=IKFunction.timeFormat(timeObject);
			if(!IKFunction.timeOK(time)){
				continue;
			}
			String durl=e.select(".goflow").get(0).attr("href");
			String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8",1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(dhtml)&&IKFunction.htmlFilter(dhtml, "span.content")){
				map=parseDetail(dhtml);
				String name=e.select(".name.fl").get(0).text();
				map.put("id", IKFunction.md5(question+timeObject));
				map.put("tid", question+timeObject);
				map.put("question", question);
				map.put("time", time);
				map.put("name", name);
				map.put("website", "爱投顾");
				list.add(map);
			}
		}
		return list;
	}
	
	public static HashMap<String, Object> parseDetail(String html){
		HashMap<String, Object> map=new HashMap<String, Object>();
		Object doc=IKFunction.JsoupDomFormat(html);
		String answer=IKFunction.jsoupTextByRowByDoc(doc,"span.content", 0);
	    if(!StringUtil.isEmpty(answer.toString())){
	 		map.put("ifanswer","1");
	 		map.put("answer", answer);
		}else{
			map.put("ifanswer","0");
		}
		return map;
	}
}
