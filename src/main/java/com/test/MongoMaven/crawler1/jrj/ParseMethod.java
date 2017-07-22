package com.test.MongoMaven.crawler1.jrj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
			map=new HashMap<String, Object>();
			Element e=li.get(i);
			String question=e.select(".q-ques-item-q>a").get(0).text();
			String timeObject=e.select(".time.fr").get(0).text();
			String time=IKFunction.timeFormat(timeObject);
			if(!IKFunction.timeOK(time)){
				continue;
			}
			String answer=e.select(".q-ques-item-a.mt20.middle").get(0).text();
			if(!StringUtil.isEmpty(answer.toString())){
				map.put("ifanswer","1");
			}else{
				map.put("ifanswer","0");
			}
			String name=e.select(".name.fl").get(0).text();
			map.put("id", IKFunction.md5(question+answer));
			map.put("tid", question+timeObject);
			map.put("question", question);
			map.put("time", time);
			map.put("answer", answer);
			map.put("name", name);
			map.put("website", "爱投顾");
			list.add(map);
		}
		return list;
	}
}
