package com.test.MongoMaven.crawler1.gpb;

import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		String url="http://www.gupiaobang.com/live/discovery";
		try{		
			HashMap<String, String> map1=new HashMap<String, String>();
			map1.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			map1.put("Accept-Encoding", "gzip, deflate");
			map1.put("Accept-Language", "zh-CN,zh;q=0.8");
			map1.put("Cache-Control", "max-age=0");
			map1.put("Connection", "keep-alive");
			map1.put("Cookie", "PHPSESSID=1n4746ta8c51q54qq5511dgt92; _pc_identity=ee32c8b3c0ab6b2d2d4af7ec6008e1dd2c2a769c2d1e1ede5c66bb10c0945795a%3A2%3A%7Bi%3A0%3Bs%3A12%3A%22_pc_identity%22%3Bi%3A1%3Bs%3A81%3A%22%5B550113%2C%22_ICPqezhb8Xe5v1oA4lkth6eJwDhecob%22%2C604800%2C%22frontend%5C%5Cmodels%5C%5Car%5C%5CARUser%22%5D%22%3B%7D");
			map1.put("Host", "www.gupiaobang.com");
			map1.put("Referer", "http://www.gupiaobang.com/user?t=sign");
			map1.put("Upgrade-Insecure-Requests", "1");
			map1.put("ser-AgentU", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.104 Safari/537.36");
    		String html2=PostData.postHtml(url, map1, "", "utf8", 1);
    	 if(!StringUtil.isEmpty(html2)&&IKFunction.htmlFilter(html2, ".c_teacherLRWar")){
    		Document doc=Jsoup.parse(html2);
			Elements urlnode=doc.select(".c_teacherLRWar");
			HashMap<String , Object > map3=null;
		    int num = urlnode.size();
		    for(int i = 0;i<num;i++){
		    	 String liveid = urlnode.get(i).select("div").attr("data-liveid");
		    	 String adviserid = urlnode.get(i).select("div").attr("data-adviserid");
		    	 String jumpurl = "http://www.gupiaobang.com/live?adviserId="+adviserid+"&liveId="+liveid;
		    	 String html4=PostData.postHtml(jumpurl, map1, "", "utf8", 1);
		    	 if(!StringUtil.isEmpty(html4)&&IKFunction.htmlFilter(html4, "div.right_jiaoliu")){
		    		 Document doc1=Jsoup.parse(html4);
			    	 Elements node1=doc1.select("div.right_jiaoliu");
			    	 Elements questionnode = node1.select(".right_jiaoliu>.kou");
			    	 int num1 = node1.size();
			    	for(int j = 0;j<num1;j++){
			    		Elements namenode =node1.get(j).select(".skey");
			    		if(namenode.size()==0){
			    			continue;
			    		}
			    		String time = questionnode.get(j).select("p").get(0).text();
			    		if(!IKFunction.timeOK(time)){
			    			continue;
			    		}
			    		String answer = questionnode.get(j).select("p").get(1).text();
			    		if(answer.length()>30){
			    			continue;
			    		}
			    		String name = questionnode.get(j).select("h3").text();
			    		String ifanswer ="1";
			    		String question =  namenode.get(0).select("span").text();
			    		if(StringUtil.isEmpty(question)||question.length()>40||question.length()<8){
				    		continue;
				    	}
			    		map3=new HashMap<String, Object>();
			    		String timedel = IKFunction.getTimeNowByStr("yyyy-MM-dd");
				    	name = name.replace(" ：", "");
				    	map3.put("id", IKFunction.md5(question+time));
						map3.put("website","股票邦");
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
    	 }
		}catch(Exception e){
			  e.printStackTrace();
		  }
	}
}
