package com.test.MongoMaven.crawlerjy.xrz;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	
	public static void main(String[] args) {
		String url="http://app.icaikee.com/xrz-app-web/portfolio/list.json?userId=127f1cb3-1f34-408e-b7bb-47245d592391&sys=19&version=3.9.5&device=HM%2BNOTE%2B1LTE_Xiaomi&page=Tabbar2Fragment&clientId=866401022288545&clientName=android&type=2";
		Map<String, String> map=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new  HashMap<String, String>());
		String html=map.get("html");
	try{
		if(!StringUtil.isEmpty(html)&&html.length()>100){
			MongoDbUtil mongo=new MongoDbUtil();
			PostData post=new PostData();
			Object json=IKFunction.jsonFmt(html);
			Object list=IKFunction.keyVal(json, "portfolios");
			int num=IKFunction.rowsArray(list);
			HashMap<String, Object > result=null;
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(list, i);
				Object id=IKFunction.keyVal(one, "id");
				String durl="http://app.icaikee.com/xrz-app-web/portfolio/detail.json?page=zu_he_xiang_qing&id="+id+"&personal=false&search=false&userId=127f1cb3-1f34-408e-b7bb-47245d592391&sys=19&version=3.9.5&device=HM%2BNOTE%2B1LTE_Xiaomi&clientId=866401022288545&clientName=android";
				map=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new  HashMap<String, String>());
				html=map.get("html");
				if(html.length()<100){
					continue;
				}
				Object json1=IKFunction.jsonFmt(html);
				Object name=IKFunction.keyVal(json1, "ownerName");
				Object timestr=IKFunction.keyVal(json1, "updateTime");
				String time=IKFunction.timeFormat(timestr.toString());
				if(!IKFunction.timeOK(time)){
					continue;
				}
				Object des=IKFunction.keyVal(json1, "percentage");
				if(StringUtil.isEmpty(des.toString())){
					continue;
				}
				BigDecimal b1 = new BigDecimal(des.toString());
				BigDecimal b2 = new BigDecimal("100");
				Double d=b1.multiply(b2).doubleValue();
				String  describe="本月收益："+d+"%";
				Object adjustment=IKFunction.keyVal(json1, "adjustment");
				Object stockAdjust=IKFunction.keyVal(adjustment, "stockAdjust");
				int size=IKFunction.rowsArray(stockAdjust);
				for(int j=1;j<=size;j++){
					Object two=IKFunction.array(stockAdjust, j);
					if(two.toString().contains("维持")){
						continue;
					}
					result=new HashMap<String, Object >();
					Object StockCode=IKFunction.keyVal(two, "code");
					String code=IKFunction.regexp(StockCode, "(\\d+)");
					Object StockName=IKFunction.keyVal(two, "name");
					Object type=IKFunction.keyVal(two, "operate");
					Object lastWeight=IKFunction.keyVal(two, "lastWeight");
					Object toWeight=IKFunction.keyVal(two, "toWeight");
					Object price=IKFunction.keyVal(two, "dealPrice");
					if("买入".equals(type.toString())){
						//1是买入，其他什么事卖出不知道
						result.put("option", "0");
					}else if("卖出".equals(type.toString())){
						result.put("option", "1");
					}
					result.put("id",IKFunction.md5(name+" "+type+" "+StockName+price+" "+timestr));
					result.put("tid",name+" "+type+" "+StockName+price+" "+timestr);
					result.put("describe",describe);
					result.put("StockCode", code);
					result.put("StockName", StockName);
					result.put("UserName", name);
					result.put("closing_cost", price);
					result.put("AddTime", time);
//					result.put("html",one);
					result.put("fromPosition", lastWeight);
					result.put("toPosition", toWeight);
					result.put("website","仙人掌股票");
//					result.remove("html");
					JSONObject mm_data=JSONObject.fromObject(result);
//					http://wisefinance.chinanorth.cloudapp.chinacloudapi.cn/wf/import?type=mm_stock_json
//					http://localhost:8888/import?type=mm_stock_json
				   String su=post.postHtml("http://wisefinance.chinanorth.cloudapp.chinacloudapi.cn/wf/import?type=mm_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
					if(su.contains("exception")){
//						System.out.println(mm_data.toString());
						System.err.println("写入数据异常！！！！  < "+su+" >");
					}
					mongo.upsertMapByTableName(result, "mm_deal_dynamic_all");
				}
			}
		}
	  }catch(Exception e){
		  e.printStackTrace();
	  }
		
		
	}
	
}
