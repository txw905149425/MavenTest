package com.test.MongoMaven.crawlertt.zxg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//自选股  一天固定只给4个   //一天一次
public class Crawler {
	
	public static void main(String[] args) {
		String url="http://61.135.157.158/ifzq.gtimg.cn/appstock/app/invest/get?limit=5&start=0&r=0.2611330155138889&publish=1";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>200){
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json, "data");
			Object arry=IKFunction.keyVal(data, "invest");
			Object one=IKFunction.array(arry, 1);
			Object year=IKFunction.keyVal(one, "date");
			Object inverst=IKFunction.keyVal(one, "invest");
			int num=IKFunction.rowsArray(inverst);
			MongoDbUtil mongo=new MongoDbUtil();
			PostData post=new PostData();
			List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
			for(int i=1;i<=num;i++){
				Object records=IKFunction.array(inverst, i);
				Object title=IKFunction.keyVal(records, "title");
				Object industry=IKFunction.keyVal(IKFunction.array(IKFunction.keyVal(records, "relate_bankuai"),1),"name");
				Object time=year+" "+IKFunction.keyVal(records, "create_time");
				String tt=IKFunction.timeFormat(time.toString());
				Object abs=IKFunction.keyVal(records, "reason");
				Object id=IKFunction.keyVal(records, "id");
				Object relate_stocks=IKFunction.keyVal(records, "relate_stocks");
				int size=IKFunction.rowsArray(relate_stocks);
				List<HashMap<String, Object >> list1=new ArrayList<HashMap<String,Object>>();
				String clist="";
				for(int j=1;j<=size;j++){
					HashMap<String,Object> map1=new HashMap<String, Object>();
					Object stock=IKFunction.array(relate_stocks, j);
					String code=IKFunction.keyVal(stock,"code").toString();
					code=code.substring(2, 8);
					Object name=IKFunction.keyVal(stock,"name");
					clist=clist+code+" ";
					map1.put("code", code);
					map1.put("name", name);
					list1.add(map1);
				}
				String durl="http://61.135.157.158/ifzq.gtimg.cn/appstock/app/invest/getById?date="+year+"&id="+id+"&&_callback=";
				HashMap<String, Object> map=parse(durl);
				if(map.isEmpty()){
					continue;
				}
				map.put("tid",title+""+time);
				map.put("title",title);
				map.put("industry",industry);//产业
//				map.put("abs",abs);
				map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				map.put("newsClass", "题材");
				map.put("source", "自选股");
				map.put("time", tt);
				map.put("related", clist.trim());
				map.put("code_list", list1);
				list.add(map);
			}
			try {
				if(!list.isEmpty()){
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			map.put("id",IKFunction.md5(content));
		}
		return map;
	
	
	}
}
