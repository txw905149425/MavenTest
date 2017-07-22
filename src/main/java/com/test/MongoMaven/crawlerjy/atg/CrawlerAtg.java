package com.test.MongoMaven.crawlerjy.atg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

//爱投顾炒股大赛交易信息
public class CrawlerAtg {
	
	public static void main(String[] args) {
		String url="http://itougu.jrj.com.cn/match/v7/getDynTradingData.jspa?currentPageNo=1";
		Map<String, String> resultmap=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>());
		String html=resultmap.get("html");
		try{
			if(html.length()>100){
			 MongoDbUtil mongo=new MongoDbUtil();
			 PostData post=new PostData();
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
					String totalrate=IKFunction.keyVal(one, "curProfit").toString();
					String rate="";
					if(totalrate.length()>5){
						rate=totalrate.substring(0, 5);
					}else{
						rate=IKFunction.regexp(totalrate, "(.*?)\\.");
					}
	//				BigDecimal b1 = new BigDecimal(totalrate.toString());
	//				BigDecimal b2 = new BigDecimal("100");
	//				Double d=b1.multiply(b2).doubleValue();
	//				Object fromPosition=IKFunction.keyVal(one, "fromPosition");
	//				Object toPosition=IKFunction.keyVal(one, "toPosition");
					Object type=IKFunction.keyVal(one, "commissionType");
					Object timestr=IKFunction.keyVal(one, "concludeTime");
					String time=IKFunction.timeFormat(timestr.toString());
					result.put("id",IKFunction.md5(name+"[0入1出]"+type+code_name+timestr));
					result.put("tid",name+"[0入1出]"+type+code_name+timestr);
					result.put("describe","收益："+rate+"%");
					result.put("StockCode", code);
					result.put("StockName", code_name);
					result.put("UserName", name);
	//				result.put("fromPosition", fromPosition);
	//				result.put("toPosition", toPosition);
					result.put("closing_cost", price.toString());
					result.put("AddTime", time);
//					result.put("html",one);
					result.put("option",type.toString());
					result.put("website","爱投顾");
//					mongo.upsertMapByTableName(result, "mm_deal_dynamic_all");
					JSONObject mm_data=JSONObject.fromObject(result);
//					System.out.println(mm_data.toString());
//					http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=mm_stock_json
//					http://localhost:8888/import?type=mm_stock_json
				   String su=post.postHtml("http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=mm_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
					if(su.contains("exception")){
						System.err.println("写入数据异常！！！！  < "+su+" >");
					}
					mongo.upsertMapByTableName(result, "mm_deal_dynamic_all");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
}
