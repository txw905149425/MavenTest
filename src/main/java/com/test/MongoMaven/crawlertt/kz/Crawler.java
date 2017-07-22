package com.test.MongoMaven.crawlertt.kz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;


//快涨负面快报
public class Crawler {
	
	public static void main(String[] args) {
		for(int i=1;i<=10;i++){
			String url="http://pdt.api.stockalert.cn/negatives?page="+i+"&perPage=50";
//			http://pdt.api.stockalert.cn/negatives/590c5157ecb53b0a114b9a21
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			try{
				List<HashMap<String, Object>> list=parseList(html);
				if(!list.isEmpty()){
					MongoDbUtil mongo=new MongoDbUtil();
					PostData post=new PostData();
					for(HashMap<String, Object> result:list){
						result.remove("crawl_time");
						JSONObject mm_data=JSONObject.fromObject(result);
//						http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=tt_stock_json
//						http://localhost:8888/import?type=tt_stock_json
					   String su=post.postHtml("http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=tt_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
						if(su.contains("exception")){
							System.out.println(mm_data.toString());
							System.err.println("写入数据异常！！！！  < "+su+" >");
						}
					}
					mongo.upsetManyMapByTableName(list, "tt_json_all");
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	
	}
	public static List<HashMap<String, Object >> parseList(String html){
		if(StringUtil.isEmpty(html)){
			return new ArrayList<HashMap<String,Object>>();
		}
		Object json=IKFunction.jsonFmt(html);
		Object data=IKFunction.keyVal(json, "payload");
		int num=IKFunction.rowsArray(data);
		List<HashMap<String, Object >> list=new ArrayList<HashMap<String,Object>>();
		for(int i=1;i<=num;i++){
			
			Object one=IKFunction.array(data, i);
			Object title=IKFunction.keyVal(one, "title");
			Object url=IKFunction.keyVal(one, "url");
			Object source=IKFunction.keyVal(one, "mediaName");
			Object id=IKFunction.keyVal(one, "_id");
			String durl="http://pdt.api.stockalert.cn/negatives/"+id;
			HashMap<String, Object > map=parseDetail(durl);
			if(map.isEmpty()){
				continue;
			}
			Object time=IKFunction.keyVal(one, "createdAt");
			if(time.toString().contains("T")){
			 String time1=time.toString().replace("T", " ").substring(0,19);
			 map.put("time", time1);
			}
			List<HashMap<String, Object >> list1=new ArrayList<HashMap<String,Object>>();
			HashMap<String, Object > map1=new HashMap<String, Object>();
			Object name=IKFunction.keyVal(one, "sname");
			Object code=IKFunction.keyVal(one, "scode");
			map1.put("code", code);
			map1.put("name", name);
			list1.add(map1);
			map.put("tid",url);
			map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
			map.put("title",title);
			map.put("newsClass", "负面快报");
			map.put("source", source);
			map.put("code_list", list1);
			map.put("related", code);
			list.add(map);
		}
		
		return list;
	}

	public static HashMap<String, Object > parseDetail(String durl){
		if(durl.length()<=39){
			return new HashMap<String, Object>();
		}
		HashMap<String, Object > map=new HashMap<String, Object>();
		String html=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)){
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json, "payload");
			Object one=IKFunction.array(data, 1);
			Object content=IKFunction.keyVal(one, "content");
			Object doc=IKFunction.JsoupDomFormat(content);
			String content1=IKFunction.jsoupTextByRowByDoc(doc,"body", 0);
			map.put("content", content1);
			map.put("id",IKFunction.md5(content1));
		}
		return map;
	}
	
}
