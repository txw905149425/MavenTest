package com.test.MongoMaven.crawler1.tzmb;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

//投资脉搏 博主信息
public class Crawler {

	public static void main(String[] args) { 
		MongoDbUtil mongo=new MongoDbUtil();
		try{
		for(int i=1;i<=5;i++){
			String url="http://www.imaibo.net/master?pagelets[]=hotMan&type=last&force_mode=1&t=443967&page="+i;
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			Object doc=IKFunction.JsoupDomFormat(html);
			int num=IKFunction.jsoupRowsByDoc(doc, ".username");
			for(int j=0;j<num;j++){
				HashMap<String, Object > map=new HashMap<String, Object>();
				String href=IKFunction.jsoupListAttrByDoc(doc, ".username", "href", j);
				String uid=IKFunction.regexp(href, "(\\d+)");
				map.put("id", uid);
				map.put("url", href);
				mongo.upsertMapByTableName(map, "ww_tzmb_user");
			}
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
