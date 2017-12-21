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
					Object tid=IKFunction.keyVal(one,"advisor_id");
					String durl="http://www.hongguwen.com/newhome//adviser/getQuestion?advisor_id="+tid+"&page=1";
					String dhtml=HttpUtil.getHtml(durl,new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
					if(!StringUtil.isEmpty(dhtml)&&dhtml.length()>100){
						Object djson=IKFunction.jsonFmt(dhtml);
						Object ddata=IKFunction.keyVal(djson, "data");
						Object dlist=IKFunction.keyVal(ddata, "list");
						int size=IKFunction.rowsArray(dlist);
						for(int j=1;j<=size;j++){
							Object done=IKFunction.array(dlist,j);
							Object dtimeObj=IKFunction.keyVal(done,"answer_time");
							String dtime=IKFunction.timeFormat(dtimeObj.toString());
							if(!IKFunction.timeOK(dtime)){
								continue;
							}
							Object que=IKFunction.keyVal(done, "content");
							Object answer=IKFunction.keyVal(done, "answer");
							Object stock_name=IKFunction.keyVal(done, "stock_name");
							Object stock_code=IKFunction.keyVal(done, "stock_code");
							String question=stock_name+"("+stock_code+")"+que;
							Object name=IKFunction.keyVal(one,"teacher_name");
							map=new HashMap<String, Object>();
							if(!StringUtil.isEmpty(answer.toString())){
								map.put("ifanswer", "1");
							}else{
								map.put("ifanswer", "0");
							}
							map.put("id",IKFunction.md5(question+answer));
							map.put("tid",question+dtime);
							map.put("question", question);
							map.put("name", name);
							map.put("answer", answer);
							map.put("time", time);
							map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
							map.put("website", "红顾问");
							mongo.upsertMapByTableName(map, "ww_ask_online_all");
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}
