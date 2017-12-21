package com.test.MongoMaven.crawler1.ygds;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

//优股大师 --问答
public class Crawler {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		String url="http://app.cctvup.cn:8080/cctv/AppInterface/GetGoodAnswerByQue?UserID=&pagenum=1";
		String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>60){
			Object doc=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(doc, "data");
			int num=IKFunction.rowsArray(data);
			HashMap<String, Object> map=null;
		try {
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(data,i);
				Object question=IKFunction.keyVal(one,"QuestionContent");
				Object time=IKFunction.keyVal(one,"RegTime");
				if(!IKFunction.timeOK(time.toString())){
					continue;
				}
				Object name=IKFunction.keyVal(one,"TrueName");
				String answer=IKFunction.keyVal(one,"AnswerDesc").toString();
				map=new HashMap<String, Object>();
				if(!StringUtil.isEmpty(answer)){
					map.put("ifanswer","1");
				}else{
					map.put("ifanswer","0");
				}
				map.put("id",IKFunction.md5(question+answer));
				map.put("tid",question+""+time);
				map.put("question", question);
				map.put("name", name);
				map.put("answer", answer);
				map.put("time", time);
				map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				map.put("website", "优股大师");
				mongo.upsertMapByTableName(map, "ww_ask_online_all");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		}
	}
}
