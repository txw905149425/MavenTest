package com.test.MongoMaven.crawlerxg.yxg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class CrawlerGood {
	
	public static void main(String[] args) {
		String day=IKFunction.getDateByIndexDay(-1, "yyyy-MM-dd");	
		MongoDbUtil mongo=new MongoDbUtil();
		mongo.getShardConn("xg_yxg1_stock").deleteMany(Filters.exists("id"));
		for(int i=1;i<=3;i++){
			String url="https://data.estockapp.com/esFront/getStrategyHighStockHistoryList?strategyId="+i+"&pageNumber=1&pageSize=20&v=4.3";
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)){
				Object json=IKFunction.jsonFmt(html);
				Object result=IKFunction.keyVal(json, "result");
				Object list=IKFunction.keyVal(result, "strategyHighStockList");
				int num=IKFunction.rowsArray(list);
				ArrayList<HashMap<String, Object>> list1=new ArrayList<HashMap<String,Object>>();
				for(int j=1;j<=num;j++){
					Object one=IKFunction.array(list, j);
					String date=IKFunction.keyVal(one,"date").toString();
					int dsize=IKFunction.comlitTimeReturnDay(date,day);
					if(dsize<2){
						HashMap<String, Object> map=new HashMap<String, Object>();
						Object stockId=IKFunction.keyVal(one,"stockId");
						String stockCode=IKFunction.regexp(stockId, "(\\d+)");
						Object stockName=IKFunction.keyVal(one, "stockName");
						map.put("stockName", stockName);
						map.put("stockCode", stockCode);
						list1.add(map);
					}
				}
				if(!list1.isEmpty()){
					HashMap<String, Object> dmap=new HashMap<String, Object>();
					String id="";
					String title="";
					if(i==1){
						id=day+"黑科技选股";
						title="黑科技选股";
					}else if(i==2){
						id=day+"热点追击";
						title="热点追击";
					}else if(i==3){
						id=day+"精准抄底王";
						title="精准抄底王";
					}
					dmap.put("id", id);
					dmap.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					dmap.put("website", "易选股");
					dmap.put("time", day);
					dmap.put("title", title);
					dmap.put("type", "1");
					dmap.put("list",list1 );
					try {
						mongo.upsertMapByTableName(dmap, "xg_yxg1_stock");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
			
		}
	}
	
	
	
}
