package com.test.MongoMaven.crawler1.wcx;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		String url="http://www.weicaixun.com/pubapi2/ask_plaza?token=77aaa03c31608359af171f56c716b625&limit=10";
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
				String durl="http://www.weicaixun.com/priapi2/ask_unlock?ask_id="+id+"&token=f39d4e2f9c0a2fdf2c347348f3629e0c";
				HashMap<String, String> maph=new HashMap<String, String>();
				maph.put("Cookie", "token=77aaa03c31608359af171f56c716b625; is_mobile=1; my_client=android; SERVERID=176e61338ce1df07af34c0f626f5a887|1503454741|1503454560");
				maph.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0.1; MI 5s Build/MXB48T; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/55.0.2883.91 Mobile Safari/537.36");
				maph.put("Referer", "http://www.weicaixun.com/ask/plaza?myclient=android&token=77aaa03c31608359af171f56c716b625&myversion=4.7");
				String dhtml=HttpUtil.getHtml(durl, maph, "utf8", 1, new HashMap<String, String>()).get("html");
				if(!StringUtil.isEmpty(dhtml)&&dhtml.length()>100){
					Object djson=IKFunction.jsonFmt(dhtml);
					Object ddata=IKFunction.keyVal(djson, "data");
					Object question=IKFunction.keyVal(ddata, "ask_question");
					Object answer=IKFunction.keyVal(ddata, "ask_replied");
					Object time=IKFunction.keyVal(ddata, "ask_replied_date");
					map=new HashMap<String, Object>();
					if(!StringUtil.isEmpty(answer.toString())){
						map.put("ifanswer", "1");
					}else{
						map.put("ifanswer", "0");
					}
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
