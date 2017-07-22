package com.test.MongoMaven.crawlertt.qjw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;



public class Crawler {
	
	public static void main(String[] args) {
	  try {
		  MongoDbUtil mongo=new MongoDbUtil();
		  PostData post=new PostData();
		  for(int i=1;i<=3;i++){
		   String url="";
			if(i==1){
			   url="http://www.p5w.net/stock/news/gsxw/";  //全景网 股票公司信息
			}else if(i==2){
				 url="http://www.p5w.net/stock/gpyb/hyfx/";//全景网股票  行业信息
			}else {
				url="http://www.p5w.net/stock/gpyb/ggjj/";//全景网 股票 个股研究
			}
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)&&html.length()>200){	
			List<HashMap<String, Object>> list=parse(html);
				if(!list.isEmpty()){
					mongo.upsetManyMapByTableName(list, "tt_json_all");
					for(HashMap<String, Object> result:list){
						result.remove("crawl_time");
						JSONObject mm_data=JSONObject.fromObject(result);
					   String su=post.postHtml("http://localhost:8888/import?type=tt_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
						if(su.contains("exception")){
							System.out.println(mm_data.toString());
							System.err.println("写入数据异常！！！！  < "+su+" >");
						}
					}
				}
			}	
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
					map.put("tid", IKFunction.md5(title));
					map.put("newsClass", "新闻");
					map.put("source", "全景网");
//					map.put("related", "");
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
