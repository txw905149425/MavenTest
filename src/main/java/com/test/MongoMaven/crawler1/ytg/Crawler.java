package com.test.MongoMaven.crawler1.ytg;

import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {

	public static void main(String[] args) {
		String url="http://ask.yueniuwang.com/new/all/?pn=1";
	 try{
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html,".content>div.item")){
			MongoDbUtil mongo=new MongoDbUtil();
			HashMap<String, Object > map=null;
			Document dd=Jsoup.parse(html);
			Elements el=dd.select(".content>div.item");
			int num=el.size();
			for(int i=0;i<num;i++){
				String timeObj=el.get(i).select(".time").get(0).text();
				String time=timeObj.split("\\.")[0];
				if(!IKFunction.timeOK(time)){
					continue;
				}
				map=new HashMap<String, Object>();
				Element e=el.get(i).select("div.question>a").get(0);
				e.select("span").remove();
				String question=e.text();
				String answer=el.get(i).select(".fl.content-wrap>p").get(0).text();
				String name=el.get(i).select(".user-name").get(0).text();
				map.put("id",IKFunction.md5(question+time));
				map.put("tid",question+time);
				if(!StringUtil.isEmpty(answer.toString())){
					map.put("ifanswer", "1");
				}else{
					map.put("ifanswer", "0");
				}
				map.put("question", question);
				map.put("name", name);
				map.put("answer", answer);
				map.put("time", time);
				map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				map.put("website", "约投顾");
				mongo.upsertMapByTableName(map, "ww_ask_online_all");
			}
		}
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	}
}