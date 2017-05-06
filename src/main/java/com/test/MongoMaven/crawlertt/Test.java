package com.test.MongoMaven.crawlertt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Test {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		HashMap<String, Object > records=null;
		String url="http://www.yuncaijing.com/insider/main.html";
		Map<String, String> map=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>());
		String html=map.get("html");
		Document doc=Jsoup.parse(html);
		int num=IKFunction.jsoupRowsByDoc(doc, ".main.pa>ul>li");
		for(int i=0;i<num-2;i++){
		Element block=doc.select(".main.pa>ul>li>.nc-arc-wrap").get(i);	
		Elements stock_list=block.select(".stock-list>a");
		if(stock_list.size()<1){
			continue;
		}
		records=new HashMap<String, Object>();
		String time=doc.select(".main.pa>ul>li>time").get(i).text();
		if(time.length()==5){
			Date d = new Date();  
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");  
		    String dateNowStr = sdf.format(d); 
			time=dateNowStr+" "+time;
		}else if(time.length()==11){
			String[] str=time.split(" ");
			time=str[1]+" "+str[0];
		}
		String stockstr="";
		for(int j=0;j<stock_list.size();j++){
			Element one=stock_list.get(j);
			String stockName=one.attr("title");
			String stockCode=one.attr("data-wscode");
			stockstr=stockstr+stockName+stockCode+" ";
		}
		System.out.println(stockstr.trim());
		String title=IKFunction.jsoupTextByRowByDoc(doc, ".nc-arc-wrap>h4>a", i);
		String content=block.select("p").get(0).text();
		records.put("id", title+time);
		records.put("class", "消息");
		records.put("source", "云财经");
		records.put("title", title);
		records.put("content", content);
		records.put("time", time);
		records.put("related", stockstr);
		mongo.upsertMapByTableName(records, "tt_ycj_xiaoxi");
		
		}
	
	}
}
