package com.test.MongoMaven.crawlerjy.hghd;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.client.ClientProtocolException;

import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

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
		map.put("userid", "1700206");
		map.put("openid", "78646ee7-355e-4f55-897e-0f4cb277b59b");
		map.put("usertoken", "0d43bdac-4e30-45de-bad1-2aa15dd71ef8");
		map.put("User-Agent", "okhttp/3.4.1");
		map.put("Content-Type", "application/json;charset=utf-8");
		map.put("Host", "fundapi.wlstock.com:9002");
		map.put("Connection", "Keep-Alive");
		map.put("X-Session-Token","11475a388eab151a85eaea83425d79d7");
		if(""!=session){
			map.put("X-Session-Token",session);
		}
		String url="http://fundapi.wlstock.com:9002//fund/traderindex";
		List<HashMap<String , Object>> listresult=new ArrayList<HashMap<String , Object>>();
		HashMap<String , Object> result = null;
	try {
				String data="{'sign':'pQQvewCK+r0xR6TMHx5xERstD84=','ver':'5.3.0','oauth_token':'e7d60c4c-e7fd-4113-b5d4-7c05d04e87e7'}";
					String html=post.postHtml(url, map,data, "utf8", 2);
					System.out.println(html);
					if(html.length()>100){
						Object json=IKFunction.jsonFmt(html);
						Object js=IKFunction.keyVal(json, "data");
						int  num=IKFunction.rowsArray(js);
						for(int j=1;j<=num;j++){
							result=new HashMap<String, Object>();
							Object one=IKFunction.array(js,j);
							Object name=IKFunction.keyVal(one, "tradername");
							Object type=IKFunction.keyVal(one, "newtradedtype");
							Object StockCode=IKFunction.keyVal(one, "newtradedstockno");
							Object StockName=IKFunction.keyVal(one, "newtradedstockname");
							Object price=IKFunction.keyVal(one, "tradeprice");
							Object time=IKFunction.keyVal(one, "newtradedtime");
							Object des=IKFunction.keyVal(one, "monthprofitrate");
							BigDecimal b1 = new BigDecimal(des.toString());
							BigDecimal b2 = new BigDecimal("100");
							Double d=b1.multiply(b2).doubleValue();
							String  describe="本月收益："+d+"%";
//							Object earnings=IKFunction.keyVal(trade, "rate");
							if("1".equals(type.toString())){
								//1是买入，其他什么是卖出不知道
								result.put("option", 0);
							}else{
								System.out.println("股市互动卖出用： ["+type+"]表示");
								result.put("option", 1);
							}
							result.put("id",name+" "+type+"[相反]"+StockName+price+" "+time);
							result.put("describe",describe);
							result.put("StockCode", StockCode);
							result.put("StockName", StockName);
							result.put("UserName", name);
							result.put("closing_cost", price);
							result.put("AddTime", time);
							result.put("html",one);
							result.put("website","好股互动");
							mongo.upsertMapByTableName(result, "mm_hghd_deal_dynamic");
						}
					}
//			if(!listresult.isEmpty()){
//				mongo.upsetManyMapByTableName(listresult, "mm_gncg_deal_dynamic");	
//			}
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
