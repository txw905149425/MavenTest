package com.test.MongoMaven.crawler1.tgb;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		String url="https://api.taoguba.com.cn/free/zhihu/queryAllZhiHu?userID=2258655&sign=4afb7829e4be0045960683e0a289e848&pageNo=1&sign_type=MD5&version=5.35&queryType=bang";
	  try{
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>200){
			MongoDbUtil mongo=new MongoDbUtil();
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json, "dto");
			int num=IKFunction.rowsArray(data);
			HashMap<String, Object > map=null;
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(data,i);
				Object timeObj=IKFunction.keyVal(one, "answerTime");
				String time=IKFunction.timeFormat(timeObj.toString());
				if(!IKFunction.timeOK(time)){
					continue;
				}
				map=new HashMap<String, Object>();
				Object name=IKFunction.keyVal(one, "answerUserName");
				Object question=IKFunction.keyVal(one, "content");
				map.put("id",IKFunction.md5(question+time));
				map.put("tid",question+time);
				map.put("question", question);
				map.put("name", name);
				map.put("ifanswer", "0");
				map.put("answer", "");
				map.put("time", time);
				map.put("website", "淘股吧");
				mongo.upsertMapByTableName(map, "ww_ask_online_all");
			}
			
		}
	  }catch(Exception e){
		e.printStackTrace();  
	  }
	}
	  
}

