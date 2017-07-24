package com.test.MongoMaven.crawler1.ths;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.StringUtil;

public class ParseThs {
	public static List<HashMap<String, Object>> parseList(String html){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
		Document doc=Jsoup.parse(html);
		Elements block=doc.select(".hot-ask-answer.bgfff.hide");
		int num=block.size();
		Element lists=null;
		HashMap<String, Object> map=null;
		for(int i=0;i<num;i++){
			map=new HashMap<String, Object>();
			lists=block.get(i);
			String question=lists.select(".rem14.wdwrap.hot-ask-content.c666").get(0).text();
			String name=lists.select(".rem10.lh30.hot-user-info.c999").get(0).text();
			String answer=lists.select(".hot-answer.rem14.wdwrap").get(0).text();
			String timeStr=lists.select(".hot-time.fl").get(0).text();
			String time=IKFunction.timeFormat(timeStr);
			if(!IKFunction.timeOK(time)){
				continue;
			}
			if(!StringUtil.isEmpty(answer)){
				map.put("ifanswer","1");
			}else{
				map.put("ifanswer","0");
			}
			map.put("id",IKFunction.md5(question+answer));
			map.put("tid",question+answer);
			map.put("question", question);
			map.put("name", name);
			map.put("answer", answer);
			map.put("website", "同花顺");
			map.put("time", time);
			list.add(map);
		}
		return list;
	}
	

	
	
	
	public static List<HashMap<String, Object>> parseList2(String html){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
		Document doc=Jsoup.parse(html);
		Elements block=doc.select(".info-rolling.rem12>a");
		int num=block.size();
		Element lists=null;
		for(int i=0;i<num;i++){
			lists=block.get(i);
			String url="http:"+lists.attr("href");
			String htm=HttpUtil.getHtml(url, new HashMap<String,String>(), "utf8", 1,new HashMap<String, String>()).get("html");
			HashMap<String, Object> map=parseDetail(htm);
			if(!map.isEmpty()){
				list.add(map);
			}
		}
		return list;
	}
	
	public static HashMap<String, Object> parseDetail(String html){
		HashMap<String, Object> map=new HashMap<String, Object>();
		Document doc=Jsoup.parse(html);
		String question=doc.select(".ask-question.rem16.wdwrap").get(0).text();
		String name=doc.select(".bo-user-nickname").get(0).text();
		String answer=doc.select(".answer-question.rem16.c666.wdwrap").get(0).text();
		String timeStr=doc.select(".answer-time").get(0).text();
		String askname=doc.select("div.ask-user").get(0).text();
		
		String time=IKFunction.timeFormat(timeStr);
		if(IKFunction.timeOK(time)){
			if(!StringUtil.isEmpty(answer)&&!answer.contains("帮你详细讲解")&&!answer.contains("这里不能截图")){
				map.put("ifanswer","1");
			}else{
				answer="";
				map.put("ifanswer","0");
			}
			map.put("id",IKFunction.md5(question+answer));
			map.put("tid",askname+question+name);
			map.put("question", question);
			map.put("name", name);
			map.put("answer", answer);
			map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
			map.put("time", time);
			map.put("website", "同花顺");
		}
		return map;
	}
}
