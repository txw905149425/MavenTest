package com.test.MongoMaven.crawler1.wcx;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		String url="http://www.weicaixun.com/pubapi2/ask_plaza?token=d72d1e8aaf5abb1ace8482362635a7ab&limit=10";
	try{
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>100){
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json, "data");
			MongoDbUtil mongo=new MongoDbUtil();
			int num=IKFunction.rowsArray(data);
			HashMap<String, Object > map=null;
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(data, i);
				Object id=IKFunction.keyVal(one, "ask_id");
				Object name=IKFunction.keyVal(one, "ask_target_nick");
				String durl="http://www.weicaixun.com/priapi2/ask_unlock?ask_id="+id+"&token=d72d1e8aaf5abb1ace8482362635a7ab";
				String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
				if(!StringUtil.isEmpty(dhtml)&&dhtml.length()>100){
					Object djson=IKFunction.jsonFmt(dhtml);
					Object ddata=IKFunction.keyVal(djson, "data");
					Object question=IKFunction.keyVal(ddata, "ask_question");
					Object answer=IKFunction.keyVal(ddata, "ask_replied");
					Object time=IKFunction.keyVal(ddata, "ask_replied_date");
					map=new HashMap<String, Object>();
					map.put("id",IKFunction.md5(question+""+time));
					map.put("tid",question+""+time);
					map.put("question", question);
					map.put("name", name);
					map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					map.put("answer", answer);
					map.put("time", time);
					map.put("website", "微财讯");
					mongo.upsertMapByTableName(map, "ww_ask_online_all");
				}
			}
		}
	  }catch(Exception e){
		  e.printStackTrace();
	  }
		
	}
}
