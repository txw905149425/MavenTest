package com.test.MongoMaven.crawler1.ttkp;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	//http://www.ourkp.com/tw
	public static void main(String[] args) {
		String url="http://www.ourkp.com/qaapi/getlist/?type=1&lastid=&ctype=1&only=0";
		HashMap<String, String> map1=new HashMap<String, String>();
		map1.put("Cookie", "tid=169875567_1500534660_595; vjuids=-4c11ba0d3.15d5ed5f3d0.0.63c633ac48934; sid=9509545dc2da827409f0bfda75dc791fc74fc90c; vict=AWVRNQBqVmMDbF48AiwCNAc0CmcAb1QyUDBXOVRlA2IDZwAxUmAAOlczBWUBN1RrAzwPNFQyBzZRY1JtWm4BNAFiUWAAMFZjA2xeOAJmAm4HNgpjADBUZFA9VzxUYg%3D%3D; vjlast=1500534666.1500534666.30");
	  try{
		String html=HttpUtil.getHtml(url, map1, "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>100){
			MongoDbUtil mongo=new MongoDbUtil();
			HashMap<String, Object > map=null;
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json, "data");
			int num=IKFunction.rowsArray(data);
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(data, i);
				Object time=IKFunction.keyVal(one, "in_time");
				if(!IKFunction.timeOK(time.toString())){
					continue;
				}
				map=new HashMap<String, Object>();
				Object answer=IKFunction.keyVal(one, "content");
				Object question=IKFunction.keyVal(one, "qcontent");
				Object ed=IKFunction.keyVal(one, "editor");
				String name="";
				if("1".equals(ed.toString())){
					name="突发君";
				}else{
					name="异动君";
				}
				map.put("id",IKFunction.md5(question+""+time));
				map.put("tid",question+""+time);
				map.put("question", question);
				map.put("name", name);
				map.put("answer", answer);
				map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				map.put("time", time);
				map.put("website", "天天看盘");
				mongo.upsertMapByTableName(map, "ww_ask_online_all");
			}
		}
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	}
}
