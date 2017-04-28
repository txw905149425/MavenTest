package com.test.MongoMaven.crawler.sina;

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
	static String reg="(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d+)";
	

	
	/**
	 *html判断是否是正常内容
	 * */
	public static boolean htmlFilter(String html,String css){
		boolean flag=false;
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
		Elements es = doc.select(css);
		if (es.size() > 0) {		
			flag=true;
		}
		return flag;
	}
	

	
	
	
	/*
	 * 抽内容,抽详情页的链接,爬虫性能上升，抽取评论数，判断是否大于0，然后在抓详情页，性能会高很多，代码会复杂)
	 * */
	public static List<HashMap<String,Object>> parseList2(String html) throws Exception{
		HashMap<String, String> map1=new HashMap<String, String>();
//		HashMap<String,Object> oneMapRecord=new HashMap<String,Object>();
		Map<String, String> resultmap=null;
		List<HashMap<String,Object>> listDbMap=new ArrayList<HashMap<String,Object>>();
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
		Elements es=doc.select(".table_content>table>tbody>tr");
		int num=es.size();
		for(int i=1;i<num;i++){
			Element block=es.get(i);
			Elements ttt=block.select(".fred.f14");
			if(ttt.size()>0){
				continue;
			}
			String utime=block.select("td:nth-child(5)").get(0).text().trim();
			utime=timeFormat(utime);
			if("1".equals(utime)){
				break;
			}
			String tmp=block.select("td:nth-child(3)>a").get(0).attr("href").trim();
			if(!StringUtil.isEmpty(tmp)){
				String url;
				if(tmp.startsWith("http")){
					continue;
				}else if(tmp.startsWith("/")){
					url="http://guba.sina.com.cn"+tmp;
				}else{
					url="http://guba.sina.com.cn/"+tmp;
				}
				resultmap=HttpUtil.getHtml(url, map1, "gbk", 1,new HashMap<String, String>());
				html=resultmap.get("html");
				if(htmlFilter(html,".ilt_tit")){
					HashMap<String,Object> jsonMap=ParseMethod.parseDetail2(html);//flist
					listDbMap.add(jsonMap);
				}
			}
		}
		return listDbMap;
	}
	
	
	public static HashMap<String,Object> parseDetail2(String html){
		HashMap<String,Object> records=new HashMap<String, Object>();	 //带主评论的整条评论
		List<HashMap<String,Object>> commentList=new ArrayList<HashMap<String,Object>>();  //跟评论列表
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Long> timeList=new ArrayList<Long>();
		org.jsoup.nodes.Document doc=Jsoup.parse(html);
		//抓取主评论
		try{
			String ucontent=doc.select(".ilt_tit").get(0).text();
			String uname=doc.select(".ilt_name").get(0).text();
			String utime=doc.select(".fl_left.iltp_time").get(0).text();
			 utime=timeFormat(utime);
			 long ti=sdf.parse(utime).getTime();
			timeList.add(ti);
			//抓取跟评论
			Elements es=doc.select(".repost_list>div.item_list");
			int size=es.size();
			if(size>0){
				HashMap<String,Object> dbMap=null;
				for(int i=0;i<size;i++){
					dbMap=new HashMap<String, Object>();
					Element e=es.get(i);
					String userName=e.select(".ilt_name").get(0).text().trim();
					String timeStr=e.select(".fl_left>span").get(0).text().trim();
					String time=timeFormat(timeStr);
					long ti1=sdf.parse(time).getTime();
					timeList.add(ti1);
					String  comment=e.select(".ilt_p").get(0).text().trim();
					dbMap.put("name", userName);
					dbMap.put("time", time);
					dbMap.put("content", comment);
					commentList.add(dbMap);
				}
			}
			Collections.sort(timeList);
			records.put("lastCommentTime", sdf.format(new Date(timeList.get(timeList.size()-1))));
			records.put("ucontent", ucontent);
			records.put("uname", uname);
			records.put("utime", utime);
			records.put("website", "新浪");
			if(!commentList.isEmpty()){
				records.put("flist", commentList);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return records;
	}
	
	public static String timeFormat(String str){
		if(StringUtil.isEmpty(str)){
			return "";
		}
		if(str.contains("今天")){
			 Date d = new Date();  
			 Random r = new Random();
			 int second= r.nextInt(60);
			 String se="";
			 if(Integer.toString(second).length()<2){
				 se="0"+second;
			 }else{
				 se=""+second;
			 }
		     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		     String dateNowStr = sdf.format(d); 
			 str=dateNowStr+" "+str.replace("今天", "")+":"+se;
		}else if(str.contains("分钟前")){
			String tmp=str.replace("分钟前", "");
			int num=Integer.parseInt(tmp);
			long numMill=num*60*1000;
			Long s =System.currentTimeMillis()-numMill;
			Date date=new Date(s);
		     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		     String dateNowStr = sdf.format(date); 
			str=dateNowStr;
		}/*else if(str.contains("月")&&str.contains("日")){
			 Calendar now = Calendar.getInstance();  
		      int year=now.get(Calendar.YEAR); 
			  str=year+"-"+str.replace("月", "-").replace("日","");
		}else if(str.length()==5){
			 Calendar now = Calendar.getInstance();  
		      int year=now.get(Calendar.YEAR); 
			 str=year+"-"+str;
		}*/else{
//			System.err.println("*******====>  时间转换出现新情况："+str);
			return "1";
		}
		return  str;
		
	}
	
	public static void main(String[] args) {
//		int[] arr={1,2,1,4,6,3,2};
//		int num=arr.length;
//		for(int i=0;i<num-1;i++){
//			for(int j=i;j<num;j++){
//				if(arr[i]>arr[j]){
//					int tmp=arr[i];
//					arr[i]=arr[j];
//					arr[j]=tmp;
//				}
//			}
//		}
//		for(int i:arr){
//			System.out.println(i);
//		}
		String ss="所谓的老师说股市理论头头是道，可是炒股亏大的，想用骗新";
		
		
		
	}
	
}
