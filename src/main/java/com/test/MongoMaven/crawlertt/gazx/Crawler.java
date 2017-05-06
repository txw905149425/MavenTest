package com.test.MongoMaven.crawlertt.gazx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	
		public static void main(String[] args) {
			String url="http://www.cnlist.com/getLiveData.do?ID=202264";
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			try{
				List<HashMap<String, Object>> list=parse(html);
				if(!list.isEmpty()){
					MongoDbUtil mongo=new MongoDbUtil();
					mongo.upsetManyMapByTableName(list, "tt_gazx_zihun");
				}
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public static List<HashMap<String, Object>>  parse(String html){
			if(StringUtil.isEmpty(html)){
				new ArrayList<HashMap<String,Object>>();
			}
			List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
			Object json=IKFunction.jsonFmt(html);
			Object d=IKFunction.keyVal(IKFunction.keyVal(json, "op_info"), "CONTENT");
			Object doc=IKFunction.JsoupDomFormat(d);
			int num=IKFunction.jsoupRowsByDoc(doc, "p");
			HashMap<String, Object > map=null;
			for(int i=0;i<num;i++){
				String text=IKFunction.jsoupTextByRowByDoc(doc, "p", i);
				if(text.contains("-")&&text.contains(":")){
					map=new HashMap<String, Object>();
					String content=IKFunction.jsoupTextByRowByDoc(doc, "p", i+1);
					map.put("id", IKFunction.md5(content));
					map.put("time", text);
					map.put("content", content);
					map.put("class", "资讯");
					map.put("source", "港澳资讯");
					list.add(map);
				}
				
			}
		return list;
		}
}
