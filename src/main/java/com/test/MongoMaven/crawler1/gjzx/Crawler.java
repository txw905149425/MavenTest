package com.test.MongoMaven.crawler1.gjzx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//股金在线
public class Crawler {
	
	public static void main(String[] args) {
		String taday=IKFunction.getTimeNowByStr("yyyy-MM-dd");
		String url="http://gjzx.cnstock.com/stock_online/ask_form/"+taday+"/all/1";
		MongoDbUtil mongo=new MongoDbUtil();
		 PostData post=new PostData();
		try{
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8",1, new HashMap<String, String>()).get("html");
			if(html.length()>300){
				Document d=Jsoup.parse(html);
				int size=d.select("ul>li").size()-1;
				List<HashMap<String, Object>> list=parse(html);
				if(!list.isEmpty()){
					mongo.upsetManyMapByTableName(list, "ww_ask_online_all");
					 for(HashMap<String, Object> one:list){
							String ttmp=JSONObject.fromObject(one).toString();
							 String su= post.postHtml("http://localhost:8888/import?type=ww_stock_json",new HashMap<String, String>(),ttmp, "utf-8", 1);
								if(su.contains("exception")){
									System.err.println("写入数据异常！！！！  < "+su+" >");
								}
					 }
				}
				if(size>1){
					for(int i=2;i<=size;i++){
						url="http://gjzx.cnstock.com/stock_online/ask_form/"+taday+"/all/"+i;
					     html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8",1, new HashMap<String, String>()).get("html");
					     List<HashMap<String, Object>> list1=parse(html);
						if(!list1.isEmpty()){
							mongo.upsetManyMapByTableName(list1, "ww_ask_online_all");
							 for(HashMap<String, Object> one:list1){
									String ttmp=JSONObject.fromObject(one).toString();
									 String su= post.postHtml("http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=ww1_stock_json",new HashMap<String, String>(),ttmp, "utf-8", 1);
										if(su.contains("exception")){
											System.err.println("写入数据异常！！！！  < "+su+" >");
										}
							 }
						}
					}
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public static List<HashMap<String, Object>> parse(String html){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
		Object doc=IKFunction.JsoupDomFormat(html);
		Document d=Jsoup.parse(html);
		int num=IKFunction.jsoupRowsByDoc(doc, ".qs-list>dl>dt");
		HashMap<String, Object> map=null;
		for(int i=0;i<num;i++){
			Element es=d.select(".qs-list>dl").get(i);			
			Elements dlist=es.select("dd");
			if(dlist.size()<1){
				continue;
			}
			String question =IKFunction.jsoupTextByRowByDoc(doc, ".qs-list>dl>dt", i);
			question=question.split("：")[1];
			map=new HashMap<String, Object>();
			String answer="";
			String name="";
			for(int j=0;j<dlist.size();j++){
				answer=answer+dlist.get(j).text()+";";
				answer=answer.replaceAll("答", "");
				name=name+dlist.get(j).select("a").text()+";";
			}
			if(!StringUtil.isEmpty(answer)){
				map.put("ifanswer","1");
			}else{
				map.put("ifanswer","0");
			}
			name=name.substring(0, name.length()-1);
			String time=IKFunction.getTimeNowByStr("yyyy-MM-dd");
			map.put("id", question+time);
			map.put("question", question);
			map.put("time", time);
			map.put("answer", answer);
			map.put("name", name);
			map.put("website", "股金在线");
			list.add(map);
		}
		return list;
	}
	
}
