package com.test.MongoMaven.crawler1.hexun;

import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		String url="http://zhibo.hexun.com/";
	try{
		String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "UTF8",	1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, ".wdbox")){
			Document doc=Jsoup.parse(html);
			Elements node=doc.select(".wdbox");
			int num=node.size();
			HashMap<String , Object > map3=null;
		   for(int i = 0;i<num;i++){
			   String question = node.get(i).select(".awdtit").get(0).text();
			   String durl ="http://zb.hexun.com"+node.get(i).select(".awdtit").get(0).attr("href");
			   if(durl.length()==19){
				   continue;
			   }
			   String dhtml=HttpUtil.getHtml(durl,new HashMap<String, String>(), "UTF8",1, new HashMap<String, String>()).get("html");
			   if(!StringUtil.isEmpty(dhtml)&&IKFunction.htmlFilter(dhtml, ".asktxt")){
				   Document doc1=Jsoup.parse(dhtml);
				   String answer=doc1.select(".asktxt").get(0).text();
				   String name=doc1.select(".aconr.fl>div.titinf>a").get(0).text();
				   String time=doc1.select("div.titinf>.c-999").get(0).text();
				   time=IKFunction.timeFormat(time);
				   if("".equals(time)||!IKFunction.timeOK(time)){
					   continue;
				   }
				   map3=new HashMap<String, Object>();
				   String ifanswer = "1";
				   String timedel = IKFunction.getTimeNowByStr("yyyy-MM-dd");
				   if(StringUtil.isEmpty(answer)){
					   ifanswer = "0";
				   }
				    map3.put("id", IKFunction.md5(question+answer));
					map3.put("website","和讯网");
					map3.put("name",name);
					map3.put("question",question);
					map3.put("answer",answer);
					map3.put("time",time);
					map3.put("ifanswer",ifanswer);
					map3.put("timedel",timedel);
					mongo.upsertMapByTableName(map3,"ww_ask_online_all");
			   }
			   
		   }
			
		}
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	
	}
}
