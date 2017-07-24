package com.test.MongoMaven.crawler1.gpgt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//更新频率较低 3分钟  股票跟投
public class Crawler {
	public static void main(String[] args) {
		//链接从APP上获取
		String url="http://app.gentou.com.cn/servlet/json;jsessionid=?is_new=1&num_per_page=40&record_type=1&from_type=4&groupid=1&funcNo=407413";
		try {
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			if(html.length()>200){
				List<HashMap<String, Object >> list=parse(html);
				if(!list.isEmpty()){
					MongoDbUtil mongo=new MongoDbUtil();
					mongo.upsetManyMapByTableName(list, "ww_ask_online_all");
					PostData post=new PostData();
//					for(HashMap<String, Object> one:list){
//						String ttmp=JSONObject.fromObject(one).toString();
//						 String su= post.postHtml("http://localhost:8888/import?type=ww_stock_json",new HashMap<String, String>(),ttmp, "utf-8", 1);
//							if(su.contains("exception")){
//								System.err.println("写入数据异常！！！！  < "+su+" >");
//							}
//				     }
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<HashMap<String, Object >> parse(String html){
		Object json=IKFunction.jsonFmt(html);
		Object data=IKFunction.keyVal(json, "data2");
		int num=IKFunction.rowsArray(data);
		List<HashMap<String, Object >> list= new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> map=null;
		for(int i=1;i<=num;i++){
			Object one=IKFunction.array(data, i);
			Object timeObj=IKFunction.keyVal(one, "timestamp");
			String time=IKFunction.timeFormat(timeObj.toString());
			if(!IKFunction.timeOK(time)){
				continue;
			}
			String content=IKFunction.keyVal(one, "content").toString();
			if(!content.contains("回复了你")){
				continue;
			}
			map=new HashMap<String, Object>();
			Object msg=IKFunction.keyVal(content, "ext");
			Object text=IKFunction.keyVal(msg, "reply_text");
			Object question=IKFunction.keyVal(text, "msg_string");
			Object answer=IKFunction.keyVal(text, "send_text");
			Object name=IKFunction.keyVal(one, "from_user_name");
			if(!StringUtil.isEmpty(answer.toString())){
				map.put("ifanswer","1");
			}else{
				map.put("ifanswer","0");
			}
			map.put("id",IKFunction.md5(question+""+answer));
			map.put("tid",timeObj+""+answer);
			map.put("question",question);
			map.put("answer",answer);
			map.put("name",name);
			map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
			map.put("time",time);
			map.put("website","股票跟投网");
			list.add(map);
		}
		return list;
	}
	
}
