package com.test.MongoMaven.crawler1.htwcg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	
	public static void main(String[] args) {
		String cookie="";
		for(String arg:args){
			if(arg.startsWith("flag=")){
				cookie=arg.substring(5);
			}
		 }
		String url="http://ax.huaxuntg.com/index.php/Index/index?business_id=1925&wechat_id=1";
		HashMap<String, String> map1=new HashMap<String, String>();
		map1.put("Host", "ax.huaxuntg.com");
		map1.put("Connection", "keep-alive");
		map1.put("Upgrade-Insecure-Requests", "1");
		map1.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; HUAWEI M2-801W Build/HUAWEIM2-801W; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.49 MQQBrowser/6.2 TBS/043305 Safari/537.36 MicroMessenger/6.5.10.1080 NetType/WIFI Language/zh_CN");
		map1.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/wxpic,image/sharpp,*/*;q=0.8");
		map1.put("Accept-Language", "zh-CN,en-US;q=0.8");
		map1.put("Cookie", cookie);//PHPSESSID=omrf5r0n59trodoftp49v3at75; wechat_id=1; business_id=1925
	try {
			String html=HttpUtil.getHtml(url, map1, "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, "ul.list>li")){
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
		Object doc=IKFunction.JsoupDomFormat(html);
		int num=IKFunction.jsoupRowsByDoc(doc, "ul.list>li");
		HashMap<String, Object> records=null;
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();//无答案的问题
		for(int i=0;i<num;i++){
			String time=IKFunction.jsoupTextByRowByDoc(doc, "div.autherHour", i);
			time=IKFunction.timeFormat(time);
			if(!IKFunction.timeOK(time)){
				continue;
			}
			records=new HashMap<String, Object>();
			String question=IKFunction.jsoupTextByRowByDoc(doc, "div.userask", i).replace("问", "");
			String name=IKFunction.jsoupTextByRowByDoc(doc, "div.auther", i);
			String answer=IKFunction.jsoupTextByRowByDoc(doc, "span.answername-b", i);
			records.put("id", IKFunction.md5(question+time));
            records.put("tid", question+time);
			records.put("name", name);
			records.put("time", time);
			records.put("question", question);
			records.put("answer", answer);
			records.put("website", "海淘王炒股票");
			list.add(records);
		}
		return list;
	}
	
	
}
