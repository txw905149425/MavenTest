package com.test.MongoMaven.crawler1.sglc;

import java.util.HashMap;
import java.util.Random;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler1 {
	public static void main(String[] args) {
		String url="http://napi.shagualicai.cn/public_room/getrecomstocklist.shtml?page=1";
		String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "utf8", 1,new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>200){
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json, "data");
			int num=IKFunction.rowsArray(data);
			MongoDbUtil mongo=new MongoDbUtil();
			HashMap<String, Object > map=null;
			try{
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(data,i);
				String stock=IKFunction.keyVal(one, "stock").toString();
				String timeObj=IKFunction.keyVal(one, "ctime").toString();
				String time=IKFunction.timeFormat(timeObj);
				if(!IKFunction.timeOK(time)){
					continue;
				}
				String question=getQuestion(stock);
				Object answer=IKFunction.keyVal(one, "recominfo");
				Object name=IKFunction.keyVal(one, "room_name");
				map=new HashMap<String, Object>();
				if(!StringUtil.isEmpty(answer.toString())){
					map.put("ifanswer","1");
				}else{
					map.put("ifanswer","0");
				}
				map.put("id",IKFunction.md5(answer+time));
				map.put("question", question);
				map.put("name", name);
				map.put("answer", answer);
				map.put("time", time);
				map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				map.put("website", "傻瓜理财炒股");
				mongo.upsertMapByTableName(map, "ww_ask_online_all");
//				mongo.upsertMapByTableName(map, "ww_sglc");
			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	public static String getQuestion(String stock){
		String question="";
		Random rd=new Random();
		int num=rd.nextInt(5);
		if(num==0){
			question="老师，帮忙看下"+stock;
		}else if(num==1){
			question="老师，"+stock+"后市如何？";
		}else if(num==2){
			question=stock+"可以进吗？";
		}else if(num==3){
			question=stock+"怎么操作？谢谢!";
		}else if(num==4){
			question=stock+"继续持有吗？谢谢！";
		}
		return question;
	}
}
