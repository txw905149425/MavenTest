package com.test.MongoMaven.crawler1.ffwg;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	
	public static void main(String[] args) {
		String url="https://api.fafachina.com/fafa/Questions?questionSort=answerTime&proId=0&genreId=0&page=1&pageSize=10";
	  try{
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>200){
			MongoDbUtil mongo=new MongoDbUtil();
			Object data=IKFunction.arrayFmt(html);
			int num=IKFunction.rowsArray(data);
			HashMap<String, Object> map=null;
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(data, i);
				String time=IKFunction.keyVal(one, "replyTime").toString();
				if(!IKFunction.timeOK(time)){
					continue;
				}
				map=new HashMap<String, Object>();
				Object name=IKFunction.keyVal(one, "lectureNickName");
				Object question=IKFunction.keyVal(one, "content");
				Object answer=IKFunction.keyVal(one, "answer");
				if(!StringUtil.isEmpty(answer.toString())){
					map.put("ifanswer","1");
				}else{
					map.put("ifanswer","0");
				}
				map.put("answer", answer);
				map.put("id", IKFunction.md5(question+""+time));
				map.put("tid",question+""+time);
				map.put("question", question);
				map.put("time", time);
				map.put("name", name);
				map.put("website", "发发问股");
				mongo.upsertMapByTableName(map, "ww_ask_online_all");
			}
		}
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	  
	}
	
}
