package com.test.MongoMaven.crawlertt.qjw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;



public class Crawler {
	
	public static void main(String[] args) {
		String url="http://www.p5w.net/stock/news/gsxw/";  //全景网 股票公司信息
//		String url="http://www.p5w.net/stock/gpyb/hyfx/";//全景网股票  行业信息
		url="http://www.p5w.net/stock/gpyb/ggjj/";//全景网 股票 个股研究
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		try {
			List<HashMap<String, Object>> list=parse(html);
			if(!list.isEmpty()){
				MongoDbUtil mongo=new MongoDbUtil();
				mongo.upsetManyMapByTableName(list, "tt_qjw_xiaoxi");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static  List<HashMap<String, Object>> parse(String html){
		if(StringUtil.isEmpty(html)){
			return new ArrayList<HashMap<String,Object>>(); 
		}
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
		if(IKFunction.htmlFilter(html, "li.clearfix")){
			Object doc=IKFunction.JsoupDomFormat(html);
			int num=IKFunction.jsoupRowsByDoc(doc, "li.clearfix");
			for(int i=0;i<num;i++){
				String title=IKFunction.jsoupTextByRowByDoc(doc, "li.clearfix>h1", i);
				String durl=IKFunction.jsoupListAttrByDoc(doc, "li.clearfix>h1>a","href",i);
				String abs=IKFunction.jsoupTextByRowByDoc(doc, "li.clearfix>p", i);
				String time=IKFunction.jsoupTextByRowByDoc(doc, ".setinfo3", i);
				time=IKFunction.timeFormat(time);
				HashMap<String, Object> map=parseDetail(durl);
				if(!map.isEmpty()){
					map.put("id", title);
					map.put("class", "新闻");
					map.put("source", "全景网");
					map.put("related", "");
					map.put("abs", abs);
					map.put("time", time);
					map.put("durl", durl);
					list.add(map);
				}
			}
		}
		
		return list;
	}
	
	public static HashMap<String, Object> parseDetail(String url){
		if(StringUtil.isEmpty(url)){
			return new HashMap<String, Object>();
		}
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		HashMap<String, Object> map=new HashMap<String, Object>();
		if(IKFunction.htmlFilter(html, ".TRS_Editor")){
			Object doc=IKFunction.JsoupDomFormat(html);
			String content=IKFunction.jsoupTextByRowByDoc(doc, ".TRS_Editor", 0);
			map.put("content", content);
		}
		return map;
	}
	
}
