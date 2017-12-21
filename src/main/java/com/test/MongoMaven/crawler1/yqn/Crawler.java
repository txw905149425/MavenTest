package com.test.MongoMaven.crawler1.yqn;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		String url="https://www.yiqiniu.com/wenda";
	  try{
		String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, ".seo-questions-list-item.item>dl>dt>a[title]")){
			MongoDbUtil mongo=new MongoDbUtil();
			HashMap<String, Object > map=null;
			Object doc=IKFunction.JsoupDomFormat(html);
			int num=IKFunction.jsoupRowsByDoc(doc,".seo-questions-list-item.item>dl>dt>a[title]");
			for(int i=0;i<num;i++){
				String timeObj=IKFunction.jsoupTextByRowByDoc(doc, ".seo-questions-list-item.item>div>.date", i);
				String time=IKFunction.timeFormat(timeObj);
				if(!IKFunction.timeOK(time)){
					continue;
				}
				String u=IKFunction.jsoupListAttrByDoc(doc, ".seo-questions-list-item.item>dl>dt>a[title]", "href", i);
				if(StringUtil.isEmpty(u)){
					continue;
				}
				String durl="https://www.yiqiniu.com"+u;
				String dhtml=HttpUtil.getHtml(durl,new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
				if(!StringUtil.isEmpty(dhtml)&&IKFunction.htmlFilter(dhtml, ".qn-QA-question>h4")){
					map=new HashMap<String, Object>();
					Object doc1=IKFunction.JsoupDomFormat(dhtml);
					String que=IKFunction.jsoupTextByRowByDoc(doc1, ".qn-QA-question>h4", 0);
					String qu=IKFunction.jsoupTextByRowByDoc(doc1, ".qn-QA-question>.clearfix", 0);
					String question="["+qu+"]"+que;
					String name=IKFunction.jsoupTextByRowByDoc(doc1, ".qn-avatar-info>ul", 0);
					String answer=IKFunction.jsoupTextByRowByDoc(doc1, ".qn-QA-answerCt", 0).replace("答", "");
					if(!StringUtil.isEmpty(answer.toString())){
						map.put("ifanswer", "1");
					}else{
						map.put("ifanswer", "0");
					}
					map.put("id",IKFunction.md5(question+answer));
					map.put("tid",question+time);
					map.put("question", question);
					map.put("name", name);
					map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					map.put("answer", answer);
					map.put("time", time);
					map.put("website", "一起牛");
					mongo.upsertMapByTableName(map, "ww_ask_online_all");
				}
				
			}
		}
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	}
}
