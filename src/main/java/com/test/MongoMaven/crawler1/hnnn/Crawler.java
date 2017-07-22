package com.test.MongoMaven.crawler1.hnnn;

import java.math.BigDecimal;
import java.util.HashMap;

import org.jsoup.helper.StringUtil;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Crawler {
	public static void main(String[] args) {
	 String url="http://advisor.0606.com.cn/api/weblive/room/hot?access_token=&limit=20&type=0&page=1";
//	 http://advisor.0606.com.cn/api/weblive/rooms/a080de0cbad36ab607f5ee82?access_token=
//	 http://advisor.0606.com.cn/api/weblive/messages/43a58de05457647be46cf5ee?ref_id=248a83e959ea8df5ee3d605d&limit=20
	try{
	 String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
	 if(!StringUtil.isBlank(html)&&html.length()>200){
		 	MongoDbUtil mongo=new MongoDbUtil();
		 	Object json=IKFunction.jsonFmt(html);
		 	Object data=IKFunction.keyVal(json, "data");
		 	int num=IKFunction.rowsArray(data);
		 	HashMap<String, Object > map=null;
		 	for(int i=1;i<=num;i++){
		 		Object one=IKFunction.array(data,i);
		 		Object uid=IKFunction.keyVal(one, "_id");
		 		Object tmp=IKFunction.keyVal(one, "advisor");
		 		String name=IKFunction.keyVal(tmp, "name").toString();
		 		String turl="http://advisor.0606.com.cn/api/weblive/rooms/"+uid+"?access_token=";
		 		 String thtml=HttpUtil.getHtml(turl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		 		 if(!StringUtil.isBlank(thtml)&&thtml.length()>100){
		 			Object tjson=IKFunction.jsonFmt(thtml);
				 	Object tdata=IKFunction.keyVal(tjson, "data");
				 	Object uuid=IKFunction.keyVal(tdata, "chat_channel_id");
				 	String durl="http://advisor.0606.com.cn/api/weblive/messages/"+uid+"?ref_id="+uuid+"&limit=20";
				 	String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
				 	 if(!StringUtil.isBlank(dhtml)&&dhtml.length()>100){
				 		 Object djson=IKFunction.jsonFmt(dhtml);
				 		 Object ddata=IKFunction.keyVal(djson, "data");
				 		 Object listData=IKFunction.keyVal(ddata, "messages");	 
				 		 int size=IKFunction.rowsArray(listData);
				 		 for(int j=1;j<=size;j++){
				 			 Object two=IKFunction.array(listData,j);
				 			 String timeObj=IKFunction.keyVal(two,"create_time").toString();
				 			 if(timeObj.contains(".")){
				 				 double d=Double.parseDouble(timeObj);
				 				  BigDecimal bd=new BigDecimal(d);
				 				 timeObj= bd.longValue()+"";
				 			 }
				 			 String time=IKFunction.timeFormat(timeObj);
				 			 if(!IKFunction.timeOK(time)){
				 				 continue;
				 			 }
				 			 Object  from=IKFunction.keyVal(two, "from");
				 			 Object uname=IKFunction.keyVal(from, "name");
				 			 if(!name.equals(uname)){
				 				continue; 
				 			 }
				 			 map=new HashMap<String, Object>();
				 			 Object body=IKFunction.keyVal(two, "body");
				 			 String content=IKFunction.keyVal(body, "content").toString();
				 			 String question="";
				 			 String answer="";
				 			 if(!content.startsWith(">@")){
				 				continue;
				 			 }
				 			 if(content.contains("<span")){
				 					question=content.replaceAll("<span.*?>","JCJ").split("JCJ")[0];
				 					answer=content.replaceAll("<span.*?>","JCJ").split("JCJ")[1];
				 				 }else if(content.contains("\n")){
				 					question=content.split("\n")[0];
				 					answer=content.split("\n")[1];
				 				 }
				 			 if(question.contains(">@")){
				 				 question=question.split(":")[1];
				 			 }
				 			 if("".equals(question)){
				 				 continue;
				 			 }
				 			map.put("id",IKFunction.md5(question+time));
							map.put("tid",question+time);
							map.put("question", question);
							map.put("name", name);
							map.put("answer", answer);
							map.put("time", time);
							map.put("website", "海纳牛牛");
							mongo.upsertMapByTableName(map, "ww_ask_online_all");
				 		 }
				 	 }
		 		 }
		 	}
    	}
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	}
}
