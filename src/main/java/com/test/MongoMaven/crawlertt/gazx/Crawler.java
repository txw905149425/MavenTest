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
			url="http://vip1.gaotime.com/info-weixin/getNewestWXAbstractByType.dhtml?abtype=186610";
		try{
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)){
				List<HashMap<String, Object>> list=parse(html);
				if(!list.isEmpty()){
					MongoDbUtil mongo=new MongoDbUtil();
					mongo.upsetManyMapByTableName(list, "tt_gazx_zihun");
				}
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
			Object doc=IKFunction.JsoupDomFormat(html);
			int num=IKFunction.jsoupRowsByDoc(doc, "p");
			HashMap<String, Object > map=null;
			for(int i=0;i<num;i++){
				String text=IKFunction.jsoupTextByRowByDoc(doc, "p", i);
				if(text.contains("-")&&text.contains(":")){
					map=new HashMap<String, Object>();
					String content="";
					for(int j=(i+1);j<num;j++){
						String tmp=IKFunction.jsoupTextByRowByDoc(doc, "p", j);
						if(tmp.contains("-")&&tmp.contains(":")){
						break;
						}
						content=content+"。"+tmp;
					}
					
					map.put("id", text);
					map.put("time", text);
					map.put("content", content);
					map.put("newsClass", "资讯");
					map.put("source", "港澳资讯");
					list.add(map);
				}
				
			}
		return list;
		}
}
