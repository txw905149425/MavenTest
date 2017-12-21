package com.test.MongoMaven.crawler1.gsds;

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
		MongoDbUtil mongo=new MongoDbUtil();
		String url="http://www.gushidaoshi.com/castlist/";
	try{
		String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "UTF8",	1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, ".liv>span>a")){
			Document doc=Jsoup.parse(html);
			Elements node=doc.select(".liv>span>a");
			int num = node.size();
			HashMap<String, Object> map=null;
			for(int i = 0;i<num ;i++){
				String teacherurl = node.get(i).attr("href");
				String name = node.get(i).text();
				String html1 = HttpUtil.getHtml(teacherurl,new HashMap<String, String>(), "UTF8",	1, new HashMap<String, String>()).get("html");
				if(StringUtil.isEmpty(html1)||!IKFunction.htmlFilter(html1, "#scripDiv>dl>dd")){
					continue;
				}
				Document doc1=Jsoup.parse(html1);
				Elements node1=doc1.select("#scripDiv>dl>dd");
				int num1 = node1.size();
				for(int j = 0;j<num1;j++){
					Element p1 = node1.get(j).select("p").get(0);
					String time=p1.select("span.cGray.f12").get(0).text();
					Elements el=p1.select("span.fr.f12");
					String tmp="";
					if(el.size()>0){
						tmp=el.get(0).text().replace("相关个股：","");
					}
					String question = node1.get(j).select("p").get(1).text();
					String answer = node1.get(j).select("p").get(2).text();
					String ifanswer = "1";
					String[] questionarray=question.split("：");
					String[] answerarray = answer.split("：");
					if(!StringUtil.isEmpty(tmp)){
						question = "["+tmp+"]"+questionarray[1];
					}else{
						question=questionarray[1];
					}
					answer = answerarray[1];
					time = IKFunction.timeFormat(time);
					if(!IKFunction.timeOK(time)){
		    			continue;
		    		}
					String timedel = IKFunction.getTimeNowByStr("yyyy-MM-dd");
					if(answer == ""){
						ifanswer ="0"; 
					}
					map = new HashMap<String, Object>();
					map.put("id", IKFunction.md5(question+answer));
					map.put("website","股市导师");
			        map.put("question", question);
			        map.put("answer", answer);
			        map.put("ifanswer", ifanswer);
			        map.put("time", time);
			        map.put("name", name);	
			        map.put("timedel", timedel);
			        mongo.upsertMapByTableName(map, "ww_ask_online_all");
				}	
			}
			}
	  }catch(Exception e){
		  e.printStackTrace();
	  }
		
		
	}

}
