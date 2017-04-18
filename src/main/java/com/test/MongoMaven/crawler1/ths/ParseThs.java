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
			String time=timeFormat(timeStr);
//			System.out.println(i+"    "+question+"  < "+answer+" >  "+name+"   "+time+"    "+timeStr);
			map.put("id",question+time);
			map.put("question", question);
			map.put("name", name);
			map.put("answer", answer);
			map.put("time", time);
			list.add(map);
		}
		return list;
	}
	
	public static String timeFormat(String str){
		if(StringUtil.isEmpty(str)){
			return "";
		}
		
		if(str.contains("今天")){
			 Date d = new Date();  
		     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		     String dateNowStr = sdf.format(d); 
			 str=dateNowStr+" "+str.replace("今天", "");
		}else if(str.contains("分钟前")){
			String tmp=str.replace("分钟前", "");
			int num=Integer.parseInt(tmp);
			long numMill=num*60*1000;
			Long s =System.currentTimeMillis()-numMill;
			Date date=new Date(s);
		     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
		     String dateNowStr = sdf.format(date); 
			str=dateNowStr;
		}else if(str.contains("月")&&str.contains("日")){
			 Calendar now = Calendar.getInstance();  
		      int year=now.get(Calendar.YEAR); 
			  str=year+"-"+str.replace("月", "-").replace("日","");
		}else{
			Calendar now = Calendar.getInstance();  
		    int year=now.get(Calendar.YEAR); 
			str=year+"-"+str;
//			System.err.println("*******====>  时间转换出现新情况："+str);
//			return "1";
		}
//		System.out.println(str);
		return  str;
		
	}
	
	
	
	public static List<HashMap<String, Object>> parseList2(String html){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
		Document doc=Jsoup.parse(html);
		Elements block=doc.select(".info-rolling.rem12>a");
		int num=block.size();
		Element lists=null;
		HashMap<String, Object> map=null;
		Map<String, String> resultMap=null;
		for(int i=0;i<num;i++){
			map=new HashMap<String, Object>();
			lists=block.get(i);
			String url="http:"+lists.attr("href");
			resultMap=HttpUtil.getHtml(url, new HashMap<String,String>(), "utf8", 1);
			html=resultMap.get("html");
			map=parseDetail(html);
			list.add(map);
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
		String time=timeFormat(timeStr);
		map.put("id",question+time);
		map.put("question", question);
		map.put("name", name);
		map.put("answer", answer);
		map.put("time", time);
		return map;
	}
	
}
