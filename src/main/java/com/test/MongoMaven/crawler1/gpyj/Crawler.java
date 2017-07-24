package com.test.MongoMaven.crawler1.gpyj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//"股票赢家"
public class Crawler {
	
	public static void main(String[] args) {
		long timenow=System.currentTimeMillis();
		long timeago=timenow-100*60*1000;
		String url="http://api.upbaa.com/upbaa/service.jsp?op=MobileQueryChatMsgIncludeSelf&p1=%7B%22ifClear%22%3A1%2C%22targetId%22%3A-52%2C%22remindMaxTimestamp%22%3A"+timeago+"%2C%22maxTimestamp%22%3A"+timeago+"%2C%22userId%22%3A794590%7D&p2="+timenow+"%3AD8416376F15E8F2D854BA9DFE21ECD6F00000001495698531636";
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("User-Agent", "okhttp/3.2.0");
	try {
		String html=HttpUtil.getHtml(url, map, "utf8", 1, new HashMap<String, String>()).get("html");
		if(html.contains("returnCode")&&html.length()>300){
			List<HashMap<String, Object>> list=parse(html);
			if(!list.isEmpty()){
				MongoDbUtil mongo=new MongoDbUtil();
				mongo.upsetManyMapByTableName(list, "ww_ask_online_all");
			}
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	}
	
	public static List<HashMap<String, Object>> parse(String html){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
		Object json=IKFunction.keyVal(html, "returnCode");
		Object data=IKFunction.keyVal(json, "backupMessages");
		int num=IKFunction.rowsArray(data);
		HashMap<String, Object> map=null;
		for(int i=1;i<=num;i++){
			map=new HashMap<String, Object>();
			Object one=IKFunction.array(data, i);
			String uname=IKFunction.keyVal(one, "senderName").toString();
			if(!"股票精灵".equals(uname)){
				continue;
			}
			String content=IKFunction.keyVal(one, "content").toString().trim();
			if(!content.contains("~")){
				continue;
			}
			String time=IKFunction.keyVal(one,"sendTime").toString();
			if(!IKFunction.timeOK(time)){
				continue;
			}
			String[] str=content.split("~");
			if(str.length!=3){
				continue;
			}
			String question=str[0];
			String answer=str[2];
			if(!StringUtil.isEmpty(answer)){
				map.put("ifanswer","1");
			}else{
				map.put("ifanswer","0");
			}
			map.put("id", IKFunction.md5(question+answer));
			map.put("tid", question+time);
			map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
			map.put("question", question);
			map.put("time", time);
			map.put("answer",answer);
			map.put("name",uname);
			map.put("website","股票赢家");
			list.add(map);
		}
		return list;
	}
	
	
}
