package com.test.MongoMaven.crawler1.hgw;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	
	public static void main(String[] args) {
		String url="http://www.hongguwen.com/newhome//index/getQuestionList?page=1&page_num=15";
		try{
			String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)&&html.length()>200){
				MongoDbUtil mongo=new MongoDbUtil();
				Object json=IKFunction.jsonFmt(html);
				Object data=IKFunction.keyVal(json, "data");
				Object list=IKFunction.keyVal(data, "list");
				int num=IKFunction.rowsArray(list);
				HashMap<String, Object > map=null;
				for(int i=1;i<=num;i++){
					Object one=IKFunction.array(list,i);
					Object timeObj=IKFunction.keyVal(one,"answer_time");
					String time=IKFunction.timeFormat(timeObj.toString());
					if(!IKFunction.timeOK(time)){
						continue;
					}
					map=new HashMap<String, Object>();
					Object name=IKFunction.keyVal(one,"teacher_name");
					Object stock_name=IKFunction.keyVal(one, "stock_name");
					Object stock_code=IKFunction.keyVal(one, "stock_code");		
					Object que=IKFunction.keyVal(one, "question");
					String question=stock_name+"("+stock_code+")"+que;
					Object answer=IKFunction.keyVal(one, "answer");
					map.put("id",IKFunction.md5(question+time));
					map.put("tid",question+time);
					map.put("question", question);
					map.put("name", name);
					map.put("answer", answer);
					map.put("time", time);
					map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					map.put("website", "红顾问");
					mongo.upsertMapByTableName(map, "ww_hgw");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}
