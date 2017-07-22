package com.test.MongoMaven.crawlerzx.wgp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	
	public static void main(String[] args) {
		String url="http://zjt.aniu.tv/index_slivelists_eid_1573625.shtml";
		try {
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html,"li.content")){
				List<HashMap<String, Object>> list=parse(html);
				if(!list.isEmpty()){
					MongoDbUtil mongo=new MongoDbUtil();
					mongo.upsetManyMapByTableName(list, "tt_zx");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static List<HashMap<String, Object>> parse(String html){
		Object doc=IKFunction.JsoupDomFormat(html);
		int num=IKFunction.jsoupRowsByDoc(doc, "li.content");
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> map=null;
		for(int i=1;i<=num;i++){
			String timeObj=IKFunction.jsoupTextByRowByDoc(doc, "li.content>span", i);
			String time=IKFunction.timeFormat(timeObj);
			if(IKFunction.timeOK(time)&&timeOk(time)){
				map=new HashMap<String, Object>();
				String content=IKFunction.jsoupTextByRowByDoc(doc,"li.content>div.txt",i);
				if(!content.contains("【")){
					continue;
				}
				String title=IKFunction.regexp(content, "(【.*?】)");
				content=content.replaceAll("【.*?】", "");
				map.put("id", IKFunction.md5(content+"阿牛直播"));
				map.put("newsClass", "快讯");
				map.put("title", title.trim());
				map.put("source", "阿牛直播");
				map.put("content", content);
				map.put("time", time);
				map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				list.add(map);
			}
		}
		return list;
	}
	
	public static boolean timeOk(String str){
	 boolean flag=true;
	try{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		long time=sdf.parse(str).getTime();
		long now=System.currentTimeMillis()+1000;
		if(time>now){
			flag=false;
		}
	  }catch(Exception e){
		  flag=false;
	  }
	return flag;
	}
}
