package com.test.MongoMaven.crawlerxg.yxg;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

//易选股——技术形态——买入指标     （买入指标异动，盘中实时更新）


//https://data.estockapp.com/esFront/getStrategyHighDetailAPP?strategyId=3&v=4.3   智能推送
public class Crawler {
	 public static void main(String[] args) {
		 String d=IKFunction.getWeekOfDate(new Date());
			String day="";
			if(d.equals("星期六")||d.equals("星期天")){
				System.exit(1);
			}else if(d.equals("星期一")){
				day=IKFunction.getDateByIndexDay(-3, "yyyy-MM-dd");	
			}else{
				day=IKFunction.getDateByIndexDay(-1, "yyyy-MM-dd");	
			}
		 MongoDbUtil mongo=new  MongoDbUtil();
		 mongo.getShardConn("xg_yxg_stock").deleteMany(Filters.exists("id"));
		 String url="https://data.estockapp.com/esFront/stockRulesBuy?v=4.2";
		 String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		 if(!StringUtil.isEmpty(html)&&html.length()>100){
			 Object json=IKFunction.jsonFmt(html);
			 Object result=IKFunction.keyVal(json, "result");
			 Object jlist=IKFunction.keyVal(result, "allHitRulesList");
			 int num=IKFunction.rowsArray(jlist);
			 for(int i=1;i<=num;i++){
				 Object one=IKFunction.array(jlist, i);
				 String time= IKFunction.keyVal(one, "quoteDate").toString();
				 if(!time.equals(day)){
					 continue;
				 }
				 Object rulesName=IKFunction.keyVal(one, "rulesName");
				 Object rulesId= IKFunction.keyVal(one, "rulesId");
				 String durl="https://data.estockapp.com/stock/getHitRulesStockListAPP?pageNo=1&pageSize=20&rulesId="+rulesId+"&v=4.2";
				 String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
				 ArrayList<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
				 if(!StringUtil.isEmpty(dhtml)&&dhtml.length()>100){
					 Object djson=IKFunction.jsonFmt(dhtml);
					 Object dresult=IKFunction.keyVal(djson, "result");
					 Object dlist=IKFunction.keyVal(dresult, "hitStockList");
					 int size=IKFunction.rowsArray(dlist);
					 for(int j=1;j<=size;j++){
						 Object two=IKFunction.array(dlist, j);
						 Object stockName=IKFunction.keyVal(two,"stockName");
						 Object stockCode=IKFunction.keyVal(two,"stockId");
						 String code=IKFunction.regexp(stockCode, "(\\d+)");
						 HashMap<String, Object> map=new HashMap<String, Object>();
						 map.put("stockName", stockName);
						 map.put("stockCode", code);
						 list.add(map);
					 }
				 }
				 if(!list.isEmpty()){
					 HashMap<String, Object> resultMap=new HashMap<String, Object>();
					 resultMap.put("id", time+rulesName);
					 resultMap.put("title",rulesName);
					 resultMap.put("type","0");
					 resultMap.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					 resultMap.put("time",time);
					 resultMap.put("list",list);
					 resultMap.put("website","易选股");
				 try {
					 mongo.upsertMapByTableName(resultMap, "xg_yxg_stock");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 }
			 }
			 
		 }
		 
	 }
}
