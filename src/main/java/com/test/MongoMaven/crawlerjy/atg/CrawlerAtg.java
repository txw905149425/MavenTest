package com.test.MongoMaven.crawlerjy.atg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

//爱投顾炒股大赛交易信息
public class CrawlerAtg {
	
	public static void main(String[] args) {
		String url="http://itougu.jrj.com.cn/match/v7/getDynTradingData.jspa?currentPageNo=1";
		Map<String, String> resultmap=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>());
		String html=resultmap.get("html");
		if(html.length()>100){
		 MongoDbUtil mongo=new MongoDbUtil();
		 HashMap<String , Object> result = null;
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json, "data");
			Object list=IKFunction.keyVal(data, "tlist");
			int num= IKFunction.rowsArray(list);
			for(int i=1;i<=num;i++){
				result=new HashMap<String, Object>();
				Object one=IKFunction.array(list, i);
				Object name=IKFunction.keyVal(one, "userName");
				Object code=IKFunction.keyVal(one, "stockCode");
				Object code_name=IKFunction.keyVal(one, "stockName");
				Object price=IKFunction.keyVal(one, "commissionPrice");
				Object totalrate=IKFunction.keyVal(one, "curProfit");
//				BigDecimal b1 = new BigDecimal(totalrate.toString());
//				BigDecimal b2 = new BigDecimal("100");
//				Double d=b1.multiply(b2).doubleValue();
				Object fromPosition=IKFunction.keyVal(one, "fromPosition");
				Object toPosition=IKFunction.keyVal(one, "toPosition");
				Object type=IKFunction.keyVal(one, "commissionType");
				Object timestr=IKFunction.keyVal(one, "concludeTime");
				String time=IKFunction.timeFormat(timestr.toString());
				result.put("id",name+" "+type+"[0入1出]"+code_name+price+" "+time);
				result.put("describe","收益："+totalrate+"%");
				result.put("StockCode", code);
				result.put("StockName", code_name);
				result.put("UserName", name);
				result.put("fromPosition", fromPosition);
				result.put("toPosition", toPosition);
				result.put("closing_cost", price);
				result.put("AddTime", time);
				result.put("html",one);
				result.put("website","爱投顾");
				mongo.upsertMapByTableName(result, "mm_atg_deal_dynamic");
			}
		}
	}
	
}
