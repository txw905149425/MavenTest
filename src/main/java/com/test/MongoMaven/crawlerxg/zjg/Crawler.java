package com.test.MongoMaven.crawlerxg.zjg;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

//抓金股 -推荐
public class Crawler {
	public static void main(String[] args) {
//		String url="http://www.i-quanta.com/api/xmGetMediumPeriodOpportunityCumReturnsByType?type=1&uid=";
		//http://www.i-quanta.com/api/xmGetDayCumReturnsList
		String d=IKFunction.getWeekOfDate(new Date());
		if(d.equals("星期六")||d.equals("星期天")){
			System.exit(1);
		}
		MongoDbUtil mongo=new MongoDbUtil();
		mongo.getShardConn("xg_zjg_stock").deleteMany(Filters.exists("id"));
		String day=IKFunction.getTimeNowByStr("yyyyMMdd");
		for(int i=1;i<=3;i++){
				String name="";
				String url="http://www.i-quanta.com/api/xmGetMediumPeriodOpportunityCumReturnsByType?type="+i+"&uid=";
				ArrayList<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
				if(i==1){
					name="底部酝酿";
					continue;
				}else if(i==2){
					name="启动拉升";
					url="http://www.i-quanta.com/api/xmGetMediumPeriodOpportunityCumReturnsByType?type=2&uid=c01f8df1-70c2-47e5-bc7b-98a013ab1354";
					String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
					if(!StringUtil.isEmpty(html)&&html.length()>200){
						Object doc=IKFunction.jsonFmt(html);
						Object json=IKFunction.keyVal(doc,"infos");
						Object slist=IKFunction.keyVal(json, "stock_list");
						int num=IKFunction.rowsArray(slist);
						for(int j=1;j<=num;j++){
							Object one=IKFunction.array(slist, j);
							Object stockName=IKFunction.keyVal(one,"stock_name");
							Object stockCode=IKFunction.keyVal(one,"stock_code");
							HashMap<String, Object> map=new HashMap<String, Object>();
							 map.put("stockName", stockName);
							 map.put("stockCode", stockCode);
							 list.add(map);
						}
					}
				}else if(i==3){
					name="每天3只股";
					url="http://www.i-quanta.com//api/xmGetStockReturnsRange?uid=c01f8df1-70c2-47e5-bc7b-98a013ab1354&startDate="+day;
					String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
					if(!StringUtil.isEmpty(html)&&html.length()>200){
						Object doc=IKFunction.jsonFmt(html);
						Object json=IKFunction.keyVal(doc,"infos");
						Object djson=IKFunction.array(json, 1);
						Object slist=IKFunction.keyVal(djson, "trace");
						int num=IKFunction.rowsArray(slist);
						for(int j=1;j<=num;j++){
							Object one=IKFunction.array(slist, j);
							Object stockName=IKFunction.keyVal(one,"stock_name");
							Object stockCode=IKFunction.keyVal(one,"stock_code");
							HashMap<String, Object> map=new HashMap<String, Object>();
							 map.put("stockName", stockName);
							 map.put("stockCode", stockCode);
							 list.add(map);
						}
					}
				}
				if(!list.isEmpty()){
					String time=IKFunction.getTimeNowByStr("yyyy-MM-dd");
					 HashMap<String, Object> resultMap=new HashMap<String, Object>();
					 resultMap.put("id", time+name);
					 resultMap.put("title",name);
					 resultMap.put("type","1");
					 resultMap.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					 resultMap.put("time",time);
					 resultMap.put("list",list);
					 resultMap.put("website","抓金股");
					 try {
						mongo.upsertMapByTableName(resultMap, "xg_zjg_stock");
//						if(name.equals("底部酝酿")){
//							mongo.upsertMapByTableName(resultMap, "app_xg_other_stock");
//						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 }
			}
		}
}
