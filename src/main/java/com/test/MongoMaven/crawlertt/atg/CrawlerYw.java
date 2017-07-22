package com.test.MongoMaven.crawlertt.atg;

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

public class CrawlerYw {
	public static void main(String[] args) {
		//http://finance.jrj.com.cn/yaowen/
		MongoDbUtil mongo=new MongoDbUtil();
		PostData post=new PostData();
//		List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
		MongoCollection<org.bson.Document> collection=mongo.getShardConn("stock_code");
		String url="http://finance.jrj.com.cn/yaowen/";
	try{
		String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "UTF8",	1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, "dl>.jrj-clear")){
			Document doc=Jsoup.parse(html);
			Elements node=doc.select("dl>.jrj-clear");
			int num=node.size();
			for(int i=0;i<num;i++){
				Elements tab=node.get(i).select("p>a[class=red]");
				if(tab.size()>0){
					String durl=node.get(i).select("strong>b>a").get(0).attr("href");
					HashMap<String,Object> map=parseDetail(durl);
					if(map.isEmpty()){
						continue;
					}
					List<HashMap<String, Object>> list1=new ArrayList<HashMap<String,Object>>();
					String related="";
					for(int j=0;j<tab.size();j++){
						HashMap<String, Object > map1=new HashMap<String, Object>();
						String name=tab.get(j).text();
						MongoCursor<org.bson.Document> d=collection.find().filter(Filters.eq("name",name)).batchSize(10000).noCursorTimeout(true).iterator();
						if(!d.hasNext()){
							continue;
						}
						Object code=d.next().get("id");
						if("".equals(code.toString())){
							continue;
						}
						related+=code+" ";
						map1.put("name", name);
						map1.put("code", code);
						list1.add(map1);
					}
					String title=node.get(i).select("strong>b>a").get(0).text();
					String time=IKFunction.getTimeNowByStr("yyyy-MM-dd")+" "+node.get(i).select(".timeleft").get(0).text();	
					map.put("id", title+"金融界");
					map.put("tid", title+""+time);
					map.put("title", title);
					map.put("newsClass", "要闻");
					map.put("source", "金融界");
					map.put("code_list", list1);
					map.put("related",related.trim());
					map.put("time", time);
					map.put("durl", durl);
					map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					JSONObject mm_data=JSONObject.fromObject(map);
				    String su=post.postHtml("http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=tt_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
					if(su.contains("exception")){
						System.out.println(mm_data.toString());
						System.err.println("写入数据异常！！！！  < "+su+" >");
					}
					mongo.upsertMapByTableName(map, "tt_json_all");
				}
			}
//			if(!list.isEmpty()){
//				mongo.upsetManyMapByTableName(list,"tt_json_all");
//			}
		}
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
