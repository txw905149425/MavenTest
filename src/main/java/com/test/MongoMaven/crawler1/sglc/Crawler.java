package com.test.MongoMaven.crawler1.sglc;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		String url="http://napi.shagualicai.cn/public_qa/gethotasklist.shtml?page=1";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>200){
			MongoDbUtil mongo=new MongoDbUtil();
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json, "data");
			int num=IKFunction.rowsArray(data);
			HashMap<String, Object > map=null;
			try{
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(data, i);
				Object question=IKFunction.keyVal(one, "ask_content");
				Object timeObj=IKFunction.keyVal(one, "ask_ctime");
				String time=IKFunction.timeFormat(timeObj.toString());
				if(!IKFunction.timeOK(time)){
					continue;
				}
				Object answer_list=IKFunction.keyVal(one, "answer_list");
				int num1=IKFunction.rowsArray(answer_list);
				String answer="";
				String name="";
				for(int j=1;j<=num1;j++){
					Object two=IKFunction.array(answer_list, j);
					String tmp=IKFunction.keyVal(two, "answer_content").toString();
					String tname=IKFunction.keyVal(two, "nickName").toString();
					answer=answer+tmp+"    ";
					name=name+tname+"    ";
				}
				map=new HashMap<String, Object>();
				if(!StringUtil.isEmpty(answer)){
					map.put("ifanswer","1");
				}else{
					map.put("ifanswer","0");
				}
				map.put("id",IKFunction.md5(question+time));
				map.put("question", question);
				map.put("name", name.trim());
				map.put("answer", answer.trim());
				map.put("time", time);
				map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				map.put("website", "傻瓜理财炒股");
				mongo.upsertMapByTableName(map, "ww_ask_online_all");
			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
