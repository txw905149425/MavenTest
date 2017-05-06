package com.test.MongoMaven.crawlertt.atg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.runners.ParentRunner;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		 Date date=new Date();
	     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	     String dd = sdf.format(date); 
		String url="http://stock.jrj.com.cn/share/news/itougu/zhangting/"+dd+".js";
		//http://stock.jrj.com.cn/share/news/itougu/qingbao/2017-05-05.js
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		try{
			List<HashMap<String, Object>> list=parseList(html);
			if(!list.isEmpty()){
				MongoDbUtil mongo=new MongoDbUtil();
				mongo.upsetManyMapByTableName(list, "tt_atg_zixun");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
		
	}
	public  static List<HashMap<String, Object>> parseList(String html){
		if(StringUtil.isEmpty(html)){
			return new ArrayList<HashMap<String, Object>>();
		}
		Object json=IKFunction.jsonFmt(html);
		Object data=IKFunction.keyVal(json, "newsinfo");
		int num=IKFunction.rowsArray(data);
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
			for(int i=1;i<=num;i++){
				Object one =IKFunction.array(data, i);
				Object durl=IKFunction.keyVal(one, "infourl");
				HashMap<String,Object> map=parseDetail(durl.toString());
				if(map.isEmpty()){
					continue;
				}
				Object title=IKFunction.keyVal(one, "title");
				Object time=IKFunction.keyVal(one, "makedate");
				Object abs=IKFunction.keyVal(one, "content");
				Object name=IKFunction.keyVal(one, "stockname");
				Object code=IKFunction.keyVal(one, "stockcode");
				List<HashMap<String, Object>> list1=new ArrayList<HashMap<String,Object>>();
				if(name.toString().contains(",")){
					String[] tmp1=name.toString().split(",");
					String[] tmp2=code.toString().split(",");
					for(int j=0;j<tmp1.length;j++){
						HashMap<String, Object > map1=new HashMap<String, Object>();
						String sname=tmp1[j];
						String scode=tmp2[j];
						map1.put("name", sname);
						map1.put("code", scode);
						list1.add(map1);
					}
				}else{
					HashMap<String, Object > map1=new HashMap<String, Object>();
					map1.put("name", name);
					map1.put("code", code);
					list1.add(map1);
				}
				map.put("id", title);
				map.put("class", "资讯");
				map.put("source", "爱投顾");
				map.put("related", list1);
				map.put("abs", abs);
				map.put("time", time);
				map.put("durl", durl);
				list.add(map);
		}
		return list;
	}
	
	public static HashMap<String, Object> parseDetail(String url){
		if(StringUtil.isEmpty(url)){
			return new HashMap<String, Object>();
		}
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		HashMap<String, Object> map=new HashMap<String, Object>();
		if(IKFunction.htmlFilter(html, ".tbox")){
			Object doc=IKFunction.JsoupDomFormat(html);
			String content=IKFunction.jsoupTextByRowByDoc(doc, ".tbox", 0);
			map.put("content", content);
		}
		return map;
	}
	
	
}
