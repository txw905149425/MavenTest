package com.test.MongoMaven.crawler1.ggcj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		String url="http://qaa.ggcj.com/qaaw/question/list.json?q_per=6&query_dt=&order_by_type=1&page=1";
		MongoDbUtil mongo=new MongoDbUtil();
		HashMap<String, String> map1=new HashMap<String, String>();
		try{
			String html=HttpUtil.getHtml(url, map1, "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)&&html.length()>200){
				List<HashMap<String, Object>> list=parse(html);
				if(!list.isEmpty()){
					mongo.upsetManyMapByTableName(list, "ww_ask_online_all");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static List<HashMap<String, Object>> parse(String html){
		Object json=IKFunction.jsonFmt(html);
		Object js=IKFunction.keyVal(json, "content");
		Object data=IKFunction.keyVal(js, "questionList");
		int num=IKFunction.rowsArray(data);
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
		for(int i=1;i<=num;i++){
			Object one=IKFunction.array(data, i);
//			http://qaa.ggcj.com/qaaw/question/info.json?a_per=3&q_id=15122&query_dt=&page=1
			Object time=IKFunction.keyVal(one, "lastADatetime");
			Object timeobj=IKFunction.keyVal(one, "createdDatetime");
			if(!IKFunction.timeOK(time.toString())){
				continue;
			}
			Object question=IKFunction.keyVal(one,"content");
			Object id=IKFunction.keyVal(one,"id");
			String anum=IKFunction.keyVal(one,"aNum").toString();
			HashMap<String, Object> map=new HashMap<String, Object>();
			if(Integer.parseInt(anum)>0){
				String durl="http://qaa.ggcj.com/qaaw/question/info.json?a_per=3&page=1&q_id="+id;
				String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
				if(!StringUtil.isEmpty(dhtml)&&dhtml.length()>200){
					map=parseDetail(dhtml);
				}
			}
			if(map.isEmpty()){
				map.put("ifanswer","0");
				map.put("answer", "");
			}
			map.put("question", question);
			map.put("id",IKFunction.md5(question+""+timeobj));
			map.put("tid",question+""+timeobj);
			map.put("website", "呱呱财经");
			map.put("time", time);
			list.add(map);
		}
		return list;
	}
	public static  HashMap<String, Object> parseDetail(String html){
		HashMap<String, Object> map=new HashMap<String, Object>();
 		Object json=IKFunction.jsonFmt(html);
		Object js=IKFunction.keyVal(json, "content");
		Object data=IKFunction.keyVal(js, "answers");
		int num=IKFunction.rowsArray(data);
		String answer="";
		String name="";
		for(int i=1;i<=num;i++){
			Object one=IKFunction.array(data, i);
			answer=answer+IKFunction.keyVal(one, "content")+"    ";
			Object  tmp=IKFunction.keyVal(one, "lecturer");
			name=name+IKFunction.keyVal(tmp,"name")+"    ";
		}
		if(!StringUtil.isEmpty(answer)){
			map.put("ifanswer","1");
		}else{
			map.put("ifanswer","0");
		}
		map.put("answer", answer);
		map.put("name", name);
		return map;
	}
}	
