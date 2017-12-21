package com.test.MongoMaven.crawler.update;

import java.util.HashMap;
import java.util.Map;

import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Actions implements Runnable{
	private DataUtil util;
	private MongoDbUtil mongo;
	public Actions(DataUtil util,MongoDbUtil mongo){
		this.util=util;
		this.mongo=mongo;
	}
	
	public void run() {
		HashMap<String, String> map=new HashMap<String, String>();
		 map.put("User-Agent","platform=gphone&version=G037.08.216.1.32");
	     map.put("Host","t.10jqka.com.cn");
	     map.put("If-Modified-Since","29 Mar 2017 07:44:28 UTC");
	     String url=util.getUrl();
	     String code=util.getCode();
	     String html=HttpUtil.getHtml(url, map, "utf8", 1,new HashMap<String, String>()).get("html");
	     System.out.println("1  "+html);
	    try{
		if(!StringUtil.isEmpty(html)){
				Object js=IKFunction.jsonFmt(html);
				Object result=IKFunction.keyVal(js, "result");
				String errorMsg=IKFunction.keyVal(js, "errorMsg").toString();
				if(result.toString().length()>50){
					HashMap<String, Object> map1=new HashMap<String, Object>();
					Object list=IKFunction.keyVal(result, "postlist");
					Object one=IKFunction.keyVal(list, "1");
					Object obj=IKFunction.keyVal(one, "forumObj");
					Object name=IKFunction.keyVal(obj, "name");
					map1.put("id", code);
					map1.put("name", name);
					mongo.upsertMapByTableName(map1, "stock_code");
				}else if("没有帖子".equals(errorMsg)){
					System.out.println(errorMsg);
				}
		
		}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
		
	}

}
