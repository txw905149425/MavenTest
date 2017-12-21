package com.test.MongoMaven.crawler1.wgp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.helper.StringUtil;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
//问股票 ,阿牛智投
public class Crawler {
	//
    public static void main(String[] args) {
    	String url="http://wengu.aniu.com/index_hotAnswer.shtml?type=0&pno=1";
    	try{
    	String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
    	if(!StringUtil.isBlank(html)&&html.length()>200){
    		List<HashMap<String, Object>> list=parseList(html);
    		if(!list.isEmpty()){
    			MongoDbUtil mongo=new MongoDbUtil();
    			mongo.upsetManyMapByTableName(list, "ww_ask_online_all");
    		}
    	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static List<HashMap<String, Object>> parseList(String html){
    	Object json=IKFunction.jsonFmt(html);
    	Object data=IKFunction.keyVal(json, "data");
    	Object content=IKFunction.keyVal(data, "content");
    	int num=IKFunction.rowsArray(content);
    	List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
    	HashMap<String, Object> map=null;
    	for(int i=1;i<=num;i++){
    		Object one=IKFunction.array(content, i);
    		Object time=IKFunction.keyVal(one , "time");
    		if(!IKFunction.timeOK(time.toString())){
    			continue;
    		}
    		map=new HashMap<String, Object>();
    		Object question=IKFunction.keyVal(one, "content");
    		Object name=IKFunction.keyVal(one, "guestNickname");
		    map.put("id", IKFunction.md5(question+"问股票"));
            map.put("tid", question+""+time);
			map.put("name", name);
			map.put("time", time);
			map.put("question", question);
			map.put("answer", "");
			map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
			map.put("ifanswer","0");
			map.put("website", "问股票");
			list.add(map);
    	}
    	return list;
    }
    
}
