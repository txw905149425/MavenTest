package com.test.MongoMaven.crawlerxg.kz;

import java.util.Date;
import java.util.HashMap;

import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

//快涨--价值选股
public class Crawler {
	public static void main(String[] args) {
		String day=IKFunction.getTimeNowByStr("yyyy-MM-dd");
		MongoDbUtil mongo=new MongoDbUtil();
		mongo.getShardConn("xg_kz_stock").deleteMany(Filters.exists("id"));
		String url="http://analyst.stockalert.cn/analysts?page=1&latest=1";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>300){
			Object doc=IKFunction.jsonFmt(html);
			Object jlist=IKFunction.keyVal(doc, "payload");
			int num=IKFunction.rowsArray(jlist);
			for(int i=1;i<=num;i++){
				Object one =IKFunction.array(jlist, i);
				Object name=IKFunction.keyVal(one, "name");
				Object winRate=IKFunction.keyVal(one, "winRate");
				String rate=IKFunction.regexp(winRate,"(\\d+)");
				Object json=IKFunction.keyVal(one, "latestReport");
				String reportDate=IKFunction.keyVal(json, "reportDate").toString();
				String time=reportDate.substring(0,10);
				int dsize=IKFunction.comlitTimeReturnDay(time,day);
				if(dsize<2){
					Object sName=IKFunction.keyVal(json,"sName");
					Object sCode=IKFunction.keyVal(json, "sCode");
					Object priceSelect=IKFunction.keyVal(json, "fPriceRC");
					Object priceTarget=IKFunction.keyVal(json, "fTargetPriceL");
					HashMap<String, Object> resultMap=new HashMap<String, Object>();
					 resultMap.put("id", time+name+sCode);
					 resultMap.put("title","价值选股");
					 resultMap.put("time",time);
					 resultMap.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					 resultMap.put("type","1");
					 resultMap.put("rate",rate);
					 resultMap.put("name",name);
					 resultMap.put("stockName", sName);
					 resultMap.put("stockCode", sCode);
					 resultMap.put("priceSelect",priceSelect);
					 resultMap.put("priceTarget",priceTarget);
					 resultMap.put("website","快涨");
					 try {
						mongo.upsertMapByTableName(resultMap, "xg_kz_stock");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			}
		}
	}
}
