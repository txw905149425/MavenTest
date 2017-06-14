package com.test.MongoMaven.crawlertt.atg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class CrawlerJG {
	
		public static void main(String[] args) {
			
			String url="http://itougu.jrj.com.cn/rstock/recstock.shtml";
//			http://itougu.jrj.com.cn/rstock/recstock-2.shtml
			PostData post=new PostData();
			MongoDbUtil mongo=new MongoDbUtil();
			try {
			 for(int i=1;i<=20;i++){
				 if(i!=1){
					 url="http://itougu.jrj.com.cn/rstock/recstock-"+i+".shtml";
				 }
				String html=HttpUtil.getHtml(url, new HashMap<String, String>(),"utf8", 1,new HashMap<String, String>()).get("html");	
				if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html,".vp-list-item.mt20")){
					List<HashMap<String, Object>> list=parse(html);
					if(!list.isEmpty()){
//						mongo.upsetManyMapByTableName(list, "tt_atg_jiangu");
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
		
		public static List<HashMap<String, Object>> parse(String html){
			Object doc=IKFunction.JsoupDomFormat(html);
			int num=IKFunction.jsoupRowsByDoc(doc,".vp-list-item.mt20");
			List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
			HashMap<String, Object> map=null;
			for(int i=0;i<num;i++){
				map=new HashMap<String, Object>();
				String title=IKFunction.jsoupTextByRowByDoc(doc, ".tit.middle>a",i);	//股票
				String stockName=IKFunction.regexp(title, "鉴股：(.*)\\(");
				String stockCode=IKFunction.regexp(title, "(\\d+)");
				String type=IKFunction.jsoupListAttrByDoc(doc, ".tit.middle>i","class",i); //涨跌
				String name=IKFunction.jsoupTextByRowByDoc(doc, "a.name",i);//投顾名
				String timestr=IKFunction.jsoupTextByRowByDoc(doc, "span.time",i);//时间
				String time=IKFunction.timeFormat(timestr);
				String reason=IKFunction.jsoupTextByRowByDoc(doc, ".desc",i);//理由
				map.put("id", title+timestr);
				map.put("title", title);
				map.put("stockName", stockName);
				map.put("stockCode", stockCode);
				map.put("type", type);
				map.put("newsClass", "鉴股");
				map.put("name", name);
				map.put("time", time);
				map.put("reason", reason);
				list.add(map);
			}
			return list;
		}
}
