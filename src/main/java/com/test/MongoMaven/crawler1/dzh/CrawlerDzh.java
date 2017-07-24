package com.test.MongoMaven.crawler1.dzh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//大智慧  抓取频率 普通2分钟一次
public class CrawlerDzh {
	
	public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
		 PostData post=new PostData();
			try {
				for(int i=0;i<31;i++){
					String url="https://htg.yundzh.com/data/showindex_"+i+".json?49718476";
					Map<String, String> resultMap=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1,new HashMap<String, String>());
					String html=resultMap.get("html");
					List<HashMap<String, Object>> listMap=parseList(html);
					if(!listMap.isEmpty()){
						mongo.upsetManyMapByTableName(listMap, "ww_ask_online_all");
//						 for(HashMap<String, Object> one:listMap){
//							 one.remove("json_str");
//							 String ttmp=JSONObject.fromObject(one).toString();
//							 String su= post.postHtml("http://localhost:8888/import?type=ww_stock_json",new HashMap<String, String>(),ttmp, "utf-8", 1);
//							if(su.contains("exception")){
//								System.err.println("写入数据异常！！！！  < "+su+" >");
//							}
//						 }
						
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static List<HashMap<String, Object>> parseList(String html){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
		Object json=IKFunction.jsonFmt(html);
		Object array1=IKFunction.keyVal(json, "msg");
		int num=IKFunction.rowsArray(array1);
		HashMap<String, Object> map=null;
		for(int i=1;i<=num;i++){
			map=new HashMap<String, Object>();
			Object js=IKFunction.array(array1,i);
			String time=IKFunction.keyVal(js, "d").toString();
			if(!IKFunction.timeOK(time)){
				continue;
			}
			Object question=IKFunction.keyVal(js, "t");
			Object qq=IKFunction.JsoupDomFormat(question);
			String que=IKFunction.jsoupTextByRowByDoc(qq, "body", 0);
			Object name=IKFunction.keyVal(js, "tgnm");
			Object answer_array=IKFunction.keyVal(js, "comment");
			Object answer_json=IKFunction.array(answer_array,1);
			Object answer=IKFunction.keyVal(answer_json, "t");
			String ans=answer.toString();
			if(ans.contains("<a")){
				Object doc=IKFunction.JsoupDomFormat(ans);
				 ans=IKFunction.jsoupTextByRowByDoc(doc, "body", 0);
			}
//			<a href='@min=SH600185' class='stockmin'>格力地产</a>可以继续持有。<a href='@min=SH601668' class='stockmin'>中国建筑</a>看半年线支撑，破了要控制仓位
			if(!StringUtil.isEmpty(ans)){
				map.put("ifanswer","1");
			}else{
				map.put("ifanswer","0");
			}
			map.put("id",IKFunction.md5(question+ans));
			map.put("tid",que+time);
			map.put("question", que);
			map.put("name", name);
			map.put("answer", ans);
			map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
			map.put("time", time);
			map.put("website", "大智慧");
//			map.put("json_str", js.toString());
			list.add(map);
		}
		return list;
		
	}
	
}
