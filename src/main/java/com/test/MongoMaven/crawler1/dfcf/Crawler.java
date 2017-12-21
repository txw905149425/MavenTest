package com.test.MongoMaven.crawler1.dfcf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		String url="https://emqah5.eastmoney.com/api/qa/GetQAHotList";
		String str="SortType=0&PageNo=1&PageSize=30&LastId=0";
		HashMap<String, String> map1=new HashMap<String, String>();
		map1.put("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
		try {
			String html=PostData.postHtml(url, map1, str, "utf8", 1);
			if(!StringUtil.isEmpty(html)&&html.length()>200){
				Object json=IKFunction.jsonFmt(html);
				Object js=IKFunction.keyVal(json, "Result");
				Object data=IKFunction.keyVal(js, "QAHotList");
				int num=IKFunction.rowsArray(data);
				MongoDbUtil mongo=new MongoDbUtil();
				HashMap<String, Object > map=null;
				for(int i=1;i<=num;i++){
					Object one=IKFunction.array(data, i);	
					Object qust=IKFunction.keyVal(one, "QuestionUser");
					Object qusti=IKFunction.keyVal(qust, "Question");
					Object question=IKFunction.keyVal(qusti, "Summary");
					Object timeObj=IKFunction.keyVal(qusti,"ModifyDate");
					Object timeObj1=IKFunction.keyVal(qusti,"CreateDate");
					String time="";
					if(timeObj.toString().contains(".")){
						 time=timeObj.toString().split("\\.")[0];
					}
					if(!IKFunction.timeOK(time)){
						continue;
					}
					Object answ=IKFunction.keyVal(one, "AnswerUser");
					Object answe=IKFunction.keyVal(answ, "Answer");
					Object answer=IKFunction.keyVal(answe, "Summary");
					Object User=IKFunction.keyVal(answ, "User");
					Object name=IKFunction.keyVal(User, "UserName");
					map=new HashMap<String, Object>();
					if(!StringUtil.isEmpty(answer.toString())){
						map.put("ifanswer", "1");
					}else{
						map.put("ifanswer", "0");
					}
					map.put("id",IKFunction.md5(question+""+timeObj1));
					map.put("tid",question+time);
					map.put("question", question);
					map.put("name", name);
					map.put("answer", answer);
					map.put("time", time);
					map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					map.put("website", "东方财富");
					mongo.upsertMapByTableName(map, "ww_ask_online_all");
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
