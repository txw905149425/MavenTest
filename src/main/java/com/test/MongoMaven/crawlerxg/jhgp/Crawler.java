package com.test.MongoMaven.crawlerxg.jhgp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//几何股票--找策略
public class Crawler {
	public static void main(String[] args) {
		String day=IKFunction.getTimeNowByStr("yyyy-MM-dd");
		MongoDbUtil mongo=new MongoDbUtil();
		mongo.getShardConn("xg_jhgp_stock").deleteMany(Filters.exists("id"));
		Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
		String url="https://m.jihegupiao.com/jihe/app/strategy/measure/list";
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("idfa", "ffffffff-afdc-86c9-96fd-12d80033c587");
		map.put("typeCode", "2");
		map.put("Content-Type", "application/x-www-form-urlencoded");
		map.put("Host", "m.jihegupiao.com");
		map.put("User-Agent", "okhttp/3.8.1");
		map.put("u_version", "2.6.2");
		PostData post=new PostData();
		String json="type=RISE";
		try {
			String html = post.postHtml(url, map, json, "utf8", 1);
//			System.out.println(html);
			if(!StringUtil.isEmpty(html)&&html.length()>60){
				Object doc=IKFunction.jsonFmt(html);
				Object data=IKFunction.keyVal(doc, "data");
				int num=IKFunction.rowsArray(data);
				for(int i=1;i<=num;i++){
					Object one=IKFunction.array(data,i);
					Object id=IKFunction.keyVal(one,"strategyId");
					Object name=IKFunction.keyVal(one,"strategyName");
					String durl="https://m.jihegupiao.com/jihe/app/strategy/measure/getTodayMsg";
					json="strategyId="+id;
					String dhtml=post.postHtml(durl, map, json, "utf8", 1);
					String time="";
					ArrayList<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
					if(!StringUtil.isEmpty(dhtml)&&dhtml.length()>60){
						Object ddoc=IKFunction.jsonFmt(dhtml);
						Object ddata=IKFunction.keyVal(ddoc, "data");
						Object djson=IKFunction.keyVal(ddata, "currentRecommended");
						String titleDate=IKFunction.keyVal(djson, "titleDate").toString();
						if(titleDate.equals("")||titleDate.equals("null")||titleDate==null){
							continue;
						}
						time=year+"-"+titleDate.replace("入选","");
					    int dsize=IKFunction.comlitTimeReturnDay(time,day);
					    if(dsize<2){
							Object dlist=IKFunction.keyVal(djson, "stockInfoList");
							int dnum=IKFunction.rowsArray(dlist);
							for(int j=1;j<=dnum;j++){
								Object two=IKFunction.array(dlist, j);
								Object stockName=IKFunction.keyVal(two,"stockName");
								Object stockCode=IKFunction.keyVal(two,"stockCode");
								String code=IKFunction.regexp(stockCode, "(\\d+)");
								HashMap<String, Object> map1=new HashMap<String, Object>();
								map1.put("stockName", stockName);
								map1.put("stockCode", code);
								list.add(map1);
							}	
						}
					}
					 if(!list.isEmpty()){
						 HashMap<String, Object> resultMap=new HashMap<String, Object>();
						 resultMap.put("id", time+name);
						 resultMap.put("title",name);
						 resultMap.put("type","1");
						 resultMap.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
						 resultMap.put("time",time);
						 resultMap.put("list",list);
						 resultMap.put("website","几何股票");
					 try {
						 mongo.upsertMapByTableName(resultMap, "xg_jhgp_stock");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					 }
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
