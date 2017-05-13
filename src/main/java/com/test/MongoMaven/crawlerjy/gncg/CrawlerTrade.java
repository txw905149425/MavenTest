package com.test.MongoMaven.crawlerjy.gncg;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.client.ClientProtocolException;

import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

//公牛炒股交易数据
public class CrawlerTrade {

	public static void main(String[] args) {
		String session="";
		for(String arg:args){
			if(arg.startsWith("session=")){
				session=arg.substring(8);
			}
		}
		MongoDbUtil mongo=new MongoDbUtil();
		PostData post=new PostData();
		HashMap<String , String> map=new HashMap<String, String>();
		map.put("Content-Type", "application/x-www-form-urlencoded");
		map.put("Host", "api.gongniuchaogu.com");
		map.put("Connection", "Keep-Alive");
		map.put("X-Session-Token","c52b45e62112dce57fe2984ca513ab6d");
		if(""!=session){
			map.put("X-Session-Token",session);
		}
		String url="https://api.gongniuchaogu.com/api/trade/list";
		List<HashMap<String , Object>> listresult=new ArrayList<HashMap<String , Object>>();
		HashMap<String , Object> result = null;
	try {
				String data="t=1493177400&token=eb717c98c40d236dbf054c1e8875387eace3021a&date=0&id=0&machine=HM+NOTE+1LTE&number=20&os=4.4.4&platform=2&protocolVersion=2.0&qudao=1000001&resolution=720*1280&type=niuren&uuid=866401022288545&version=2.4.4";
					String html=post.postHtml(url, map,data, "utf8", 2);
					System.out.println(html);
					if(html.length()>100){
						Object json=IKFunction.jsonFmt(html);
						Object js=IKFunction.keyVal(json, "data");
						Object list=IKFunction.keyVal(js, "lists");
						Object tmp=IKFunction.array(list,1);
						Object alist=IKFunction.keyVal(tmp, "lists");
						int  num=IKFunction.rowsArray(alist);
						for(int j=1;j<=num;j++){
							result=new HashMap<String, Object>();
							Object one=IKFunction.array(alist,j);
							Object trade=IKFunction.keyVal(one, "trade");
							Object type=IKFunction.keyVal(trade, "type");
							Object stock=IKFunction.keyVal(trade, "stock");
							Object StockCode=IKFunction.keyVal(stock, "code");
							Object StockName=IKFunction.keyVal(stock, "name");
							Object price=IKFunction.keyVal(trade, "price");
							Object timeStr=IKFunction.keyVal(trade, "date");
							String time=IKFunction.timeFormat(timeStr.toString());
							Object nums=IKFunction.keyVal(trade, "num");
							Object earnings=IKFunction.keyVal(trade, "rate");
							if("buy".equals(type.toString())){
								result.put("option", 0);
							}else if("sell".equals(type.toString())){
								result.put("option", 1);
							}
							Object member=IKFunction.keyVal(one, "member");
							Object name=IKFunction.keyVal(member, "name");
							BigDecimal b2 = new BigDecimal(IKFunction.keyVal(member, "totalYieldRate").toString());
							BigDecimal b4 = new BigDecimal(IKFunction.keyVal(trade, "rate").toString());
							BigDecimal b3 = new BigDecimal("100");
							Double dd=b2.multiply(b3).doubleValue();
							String ddt=IKFunction.regexp(dd, "(.*?)\\.");
							String  describe="总收益率:"+ddt+"%";
							result.put("id",name+" "+type+StockName+price+" "+time);
							result.put("earnings",b4.multiply(b3).doubleValue()+"%");
							result.put("describe",describe);
							result.put("quantity",nums);
							result.put("StockCode", StockCode);
							result.put("StockName", StockName);
							result.put("UserName", name);
							result.put("closing_cost", price);
							result.put("AddTime", time);
							result.put("html",one);
							result.put("website","公牛炒股");
							listresult.add(result);
						}
					}
			if(!listresult.isEmpty()){
				mongo.upsetManyMapByTableName(listresult, "mm_deal_dynamic_all");	
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
