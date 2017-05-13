package com.test.MongoMaven.crawler.dfcfWeb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.StringUtil;

public class ParseMethod {
	
	static String reg="(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})";
	/**
	 * 该方法只适应于东方财富股吧
	 * 参数html在传递之前，要做非空以及数据准确性判断！！！！
	 *1. 里面不抽内容只抽详情页的链接(逻辑会简单很多，但是爬虫性能会下降，优化方向抽取评论数，判断是否大于0，然后在抓详情页，性能会高很多，代码会复杂)
	 *2. 返回一个List<HashMap<String,Object>>
	 * @throws Exception 
	 * */
	
	/*
	 * 抽内容,抽详情页的链接,爬虫性能上升，抽取评论数，判断是否大于0，然后在抓详情页，性能会高很多，代码会复杂)
	 * */
	public static List<HashMap<String,Object>> parseList2(String html) throws Exception{
		HashMap<String, String> map1=new HashMap<String, String>();
		Map<String, String> resultmap=null;
		List<HashMap<String,Object>> listDbMap=new ArrayList<HashMap<String,Object>>();
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
		int num=IKFunction.jsoupRowsByDoc(doc, ".l3>a");
		for(int i=1;i<num;i++){
			Element es=doc.select("span.l3").get(i);
			Elements ttt= es.select("span>em");
			if(ttt.size()>0){
				continue;
			}
			Calendar now = Calendar.getInstance();  
		    int year=now.get(Calendar.YEAR); 
		    Random r = new Random();
			 int second= r.nextInt(60);
			 String se="";
			 if(Integer.toString(second).length()<2){
				 se="0"+second;
			 }else{
				 se=""+second;
			 }
			String utime=year+"-"+IKFunction.jsoupTextByRowByDoc(doc, "span.l5", i+1)+":"+se;
			if(!IKFunction.timeOK(utime)){
				break;
			}
				//抓取评论详情页的内容
				String tmp=IKFunction.jsoupListAttrByDoc(doc, ".l3>a","href",i);
				if(!StringUtil.isEmpty(tmp)){
					String url;
					if(tmp.startsWith("http")){
						continue;
					}else if(tmp.startsWith("/")){
						url="http://guba.eastmoney.com"+tmp;
					}else{
						url="http://guba.eastmoney.com/"+tmp;
					}
					resultmap=HttpUtil.getHtml(url, map1, "utf8", 1,new HashMap<String, String>());
					html=resultmap.get("html");
					if(htmlFilter(html,"#zwconttbt")){
						HashMap<String,Object> jsonMap=ParseMethod.parseDetail2(html);//flist
						listDbMap.add(jsonMap);
					}
				}
		}
//		oneMapRecord.put("list", listDbMap);
		return listDbMap;
	}
	
	public static void main(String[] args) {
		String utime="发表于 2017-04-18 13:41:28 东方财富电脑版";
		utime=IKFunction.regexp(utime,reg);
		System.out.println(utime);
	}
	
	public static HashMap<String,Object> parseDetail2(String html){
		HashMap<String,Object> records=new HashMap<String, Object>();	 //带主评论的整条评论
		List<HashMap<String,Object>> commentList=new ArrayList<HashMap<String,Object>>();  //跟评论列表
		List<Long> timeList=new ArrayList<Long>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
	try{
		String ucontent=doc.select("#zwconttbt").get(0).text().trim();
		String uname=doc.select("#zwconttbn>strong").get(0).text().trim();
		String utime=doc.select(".zwfbtime").get(0).text().trim();
		utime=IKFunction.regexp(utime, reg);
		long ti=sdf.parse(utime).getTime();
		timeList.add(ti);
		//跟评论
		Elements es=doc.select(".zwlitxt");
		int size=es.size();
		if(size>0){
			HashMap<String,Object> dbMap=null;
			for(int i=0;i<size;i++){
				dbMap=new HashMap<String, Object>();
				Element e=es.get(i);
				int tmp=e.select("div.zwlitalkbox").size();
				if(tmp>0){
					e.select("div.zwlitalkbox").remove();
				}
				String userName=e.select("div.zwlianame").get(0).text().trim();
				String timeStr=e.select("div.zwlitime").get(0).text().replace("发表于 ", "").trim();
	//			String time=IKFunction.regexp(timeStr,reg);
				String  comment=e.select("div.zwlitext.stockcodec").get(0).text().trim();
				dbMap.put("name", userName);
				dbMap.put("time", timeStr);
				dbMap.put("content", comment);
				long ti1=sdf.parse(timeStr).getTime();
				timeList.add(ti1);
				commentList.add(dbMap);
			}
		}
		Collections.sort(timeList);
		records.put("lastCommentTime", sdf.format(new Date(timeList.get(timeList.size()-1))));
		records.put("ucontent", ucontent);
		records.put("uname", uname);
		records.put("utime", utime);
		records.put("website", "东方财富");
		if(!commentList.isEmpty()){
			records.put("flist", commentList);
		}
	}catch(Exception e){
		e.printStackTrace();
	}
		return records;
	}
	
	/**
	 *html判断是否是正常内容
	 * */
	public static boolean htmlFilter(String html,String css){
		boolean flag=false;
		if(StringUtil.isEmpty(html)){
			return flag;
		}
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
		Elements es = doc.select(css);
		if (es.size() > 0) {		
			flag=true;
		}
		return flag;
	}
	
	
	
	public static boolean timeFilter(String time){
		boolean flag=false;
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		 try {
			 Date d=sdf.parse(time);
			String str=sdf1.format(new Date())+" 00:00:00";
			 Date dt=sdf.parse(str);
			 if(d.getTime()>dt.getTime()){
				 return true;
			 }
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
	
}
