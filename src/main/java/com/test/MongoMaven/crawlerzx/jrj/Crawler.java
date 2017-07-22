package com.test.MongoMaven.crawlerzx.jrj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		String url="http://finance.jrj.com.cn/yaowen/";
	try{
		String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "UTF8",	1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, "dl>.jrj-clear")){
			Document doc=Jsoup.parse(html);
			Elements node=doc.select("dl>.jrj-clear");
			int num=node.size();
			for(int i=0;i<num;i++){
					String durl=node.get(i).select("strong>b>a").get(0).attr("href");
					String title="【"+node.get(i).select("strong>b>a").get(0).text().trim()+"】";
					HashMap<String,Object> map=new HashMap<String, Object>();//parseDetail(durl);
//					if(map.isEmpty()){
//						continue;
//					}
					String content=node.get(i).select(".trbox>p").get(0).text();
					String time=node.get(i).select(".timeleft").get(0).text();
					time=IKFunction.timeFormat(time);
					if(!IKFunction.timeOK(time)){
						continue;
					}
					map.put("id", IKFunction.md5(title.trim()+"金融界"));
					map.put("newsClass", "要闻");
					map.put("source", "金融界");
					map.put("content", content);
					map.put("title", title.trim());
					map.put("time", time);
					map.put("durl", durl);
					map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
//					JSONObject mm_data=JSONObject.fromObject(map);
//				    String su=post.postHtml("http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=tt_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
//					if(su.contains("exception")){
//						System.out.println(mm_data.toString());
//						System.err.println("写入数据异常！！！！  < "+su+" >");
//					}
//					mongo.upsertMapByTableName(map, "tt_json_all");
					mongo.upsertMapByTableName(map, "tt_zx");
				}
			}
//			if(!list.isEmpty()){
//				mongo.upsetManyMapByTableName(list,"tt_json_all");
//			}
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	}
	
	public static HashMap<String, Object> parseDetail(String url){
		if(StringUtil.isEmpty(url)){
			return new HashMap<String, Object>();
		}
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		HashMap<String, Object> map=new HashMap<String, Object>();
		if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, ".texttit_m1")){
			Object doc=IKFunction.JsoupDomFormat(html);
			String content=IKFunction.jsoupTextByRowByDoc(doc, ".texttit_m1", 0);
			map.put("content", content);
		}
		return map;
	}
	
	
}
