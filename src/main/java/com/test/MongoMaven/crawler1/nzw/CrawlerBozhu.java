package com.test.MongoMaven.crawler1.nzw;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class CrawlerBozhu {
  
	public static void main(String[] args) {
		String url="http://live.9666.cn/getBroadcastListByAZ/";
		MongoDbUtil mongo=new MongoDbUtil();
		String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		Object doc=IKFunction.JsoupDomFormat(html);
		int num=IKFunction.jsoupRowsByDoc(doc, ".black.f18.js-cbga");
		for(int i=0;i<num;i++){
			HashMap<String,Object> map=new HashMap<String, Object>(); 
			String name=IKFunction.jsoupTextByRowByDoc(doc, ".black.f18.js-cbga",i);	
			String durl=IKFunction.jsoupListAttrByDoc(doc, ".black.f18.js-cbga","href",i);
			String dhtml=HttpUtil.getHtml(durl,new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			String rid=IKFunction.regexp(dhtml, "\"rid\":(\\d+)");
			if(!"".equals(rid)){
				map.put("rid",rid);
			}
			map.put("id", durl);
			map.put("name", name);
			mongo.upsertMapByTableName(map, "ww_nzw_bozhu");
		}
		
		
		
	}
	
	
}
