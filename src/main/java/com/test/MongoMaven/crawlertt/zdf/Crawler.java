package com.test.MongoMaven.crawlertt.zdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	
	public static void main(String[] args) {
		String url="http://api.matidata.com/news/stocks?app_key=a100001&api_token=c317cee0eb58f03f7379742c4f46009e&p=1&r=20";//个股新闻
		url="http://api.matidata.com/news/comments";//股民热评
		String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		try{
			List<HashMap<String, Object>> list=parse(html);
			if(!list.isEmpty()){
				MongoDbUtil mongo=new MongoDbUtil();
				mongo.upsetManyMapByTableName(list, "tt_zdf_xinwen");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static List<HashMap<String, Object >> parse(String html){
		if(StringUtil.isEmpty(html)){
			return new ArrayList<HashMap<String,Object>>();
		}
		Object json=IKFunction.jsonFmt(html);
		Object data=IKFunction.keyVal(json, "data");
		Object arr=IKFunction.keyVal(data, "info");
		int num=IKFunction.rowsArray(arr);
		HashMap<String, Object > map=null;
		List<HashMap<String, Object >> list=new ArrayList<HashMap<String,Object>>();
		for(int i=1;i<=num;i++){
			map=new HashMap<String, Object>();
			Object one=IKFunction.array(arr, i);
			Object time=IKFunction.keyVal(one, "published");
			Object title=IKFunction.keyVal(one, "title");
			Object content=IKFunction.keyVal(one, "fragment");
			List<HashMap<String, Object >> list1=new ArrayList<HashMap<String,Object>>();
			HashMap<String, Object > map1=new HashMap<String, Object>();
			Object name=IKFunction.keyVal(one, "companyname");
			Object code=IKFunction.keyVal(one, "companycode");
			map1.put("code", code);
			map1.put("name", name);
			list1.add(map1);
			map.put("id",title);
			map.put("title",title);
			map.put("class", "新闻");
			map.put("source", "涨跌福");
			map.put("time", time);
			map.put("related", list1);
			map.put("content", content);
			list.add(map);
		}
		return list;
	}
}
