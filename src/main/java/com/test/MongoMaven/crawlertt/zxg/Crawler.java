package com.test.MongoMaven.crawlertt.zxg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	
	public static void main(String[] args) {
		String url="http://61.135.157.158/ifzq.gtimg.cn/appstock/app/invest/get?limit=5&start=0&r=0.2611330155138889&publish=1&&_callback=jsonp_1493969889779_91057";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)){
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json, "data");
			Object arry=IKFunction.keyVal(data, "invest");
			Object one=IKFunction.array(arry, 1);
			Object year=IKFunction.keyVal(one, "date");
			Object inverst=IKFunction.keyVal(one, "invest");
			int num=IKFunction.rowsArray(inverst);
			List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
			for(int i=1;i<=num;i++){
				Object records=IKFunction.array(inverst, i);
				Object title=IKFunction.keyVal(records, "title");
				Object industry=IKFunction.keyVal(IKFunction.array(IKFunction.keyVal(records, "relate_bankuai"),1),"name");
				Object time=year+" "+IKFunction.keyVal(records, "create_time");
				Object abs=IKFunction.keyVal(records, "reason");
				Object id=IKFunction.keyVal(records, "id");
				Object relate_stocks=IKFunction.keyVal(records, "relate_stocks");
				int size=IKFunction.rowsArray(relate_stocks);
				List<HashMap<String, Object >> list1=new ArrayList<HashMap<String,Object>>();
				for(int j=1;j<=size;j++){
					HashMap<String,Object> map1=new HashMap<String, Object>();
					Object stock=IKFunction.array(relate_stocks, j);
					Object code=IKFunction.keyVal(stock,"code");
					Object name=IKFunction.keyVal(stock,"name");
					map1.put("code", code);
					map1.put("name", name);
					list1.add(map1);
				}
				String durl="http://61.135.157.158/ifzq.gtimg.cn/appstock/app/invest/getById?date="+year+"&id="+id+"&&_callback=";
				HashMap<String, Object> map=parse(durl);
				if(map.isEmpty()){
					continue;
				}
				map.put("id",title+""+time);
				map.put("title",title);
				map.put("industry",industry);
				map.put("abs",abs);
				map.put("class", "题材");
				map.put("source", "自选股");
				map.put("time", time);
				map.put("related", list1);
				list.add(map);
			}
			
			if(!list.isEmpty()){
				MongoDbUtil mongo=new MongoDbUtil();
				try {
					mongo.upsetManyMapByTableName(list, "tt_zxg_ticai");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
	public static HashMap<String, Object> parse(String url){
		if(StringUtil.isEmpty(url)){
			return new HashMap<String, Object>();
		}
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		HashMap<String, Object> map=new HashMap<String, Object>();
		if(html.length()>200){
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json, "data");
			Object one=IKFunction.array(data, 1);
			Object content=IKFunction.keyVal(one, "content");
			map.put("content", content);
		}
		return map;
	
	
	}
}
