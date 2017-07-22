package com.test.MongoMaven.crawlerxg.tzyj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;


//投资赢家
public class Crawler {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		mongo.getShardConn("xg_tzyj_stock").deleteMany(Filters.exists("id"));
		String url="https://xuangu.hsmdb.com/stockSelection/policy/getPolicys.do?pageSize=20&pageNum=1";
		Map<String, String> map=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>());
		String html=map.get("html");
		List<HashMap<String,Object>> list=parse1(html);
		try {
			mongo.upsetManyMapByTableName(list, "xg_tzyj_stock");
			mongo.upsetManyMapByTableName(list, "xg_stock_json_all");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<HashMap<String , Object >> parse1(String html){
		if(StringUtil.isEmpty(html)&&html.length()>100){
			return new ArrayList<HashMap<String,Object>>();
		}
		List<HashMap<String , Object >> listresult=new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> records=null;
		HashMap<String, Object> stockmap=null;
		Object json=IKFunction.jsonFmt(html);
		Object data=IKFunction.keyVal(json, "data");
		int num=IKFunction.rowsArray(data);
		for(int i=1;i<=num;i++){
			records=new HashMap<String, Object>();
			Object one=IKFunction.array(data, i);
			Object lid=IKFunction.keyVal(one, "strPolicyId");
			Object title=IKFunction.keyVal(one, "strPolicyName");
			Object list=IKFunction.keyVal(one, "list");
			Object describe=IKFunction.keyVal(one, "strPolicyDesc");
			Object tmp=IKFunction.array(list, 1);
			String url="https://xuangu.hsmdb.com/stockSelection/policy/getStocks.do?policyId="+lid+"&pageSize=50&pageNum=1";
//			System.out.println(url);
			Map<String, String> map=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>());
			String xml=map.get("html");
			Object json1=IKFunction.jsonFmt(xml);
			Object data1=IKFunction.keyVal(json1, "data");
			int num1=IKFunction.rowsArray(data1);
			List<HashMap<String , Object >> listmap=new ArrayList<HashMap<String,Object>>();
			for(int j=1;j<=num1;j++){
				stockmap=new HashMap<String, Object>();
				Object two=IKFunction.array(data1, j);
				Object name=IKFunction.keyVal(two, "strStockName");
				Object code=IKFunction.keyVal(two, "strStockCode");
				stockmap.put("stockName", name);
				stockmap.put("selecprice", "");
				stockmap.put("code", code);
				listmap.add(stockmap);
			}
			if(listmap.isEmpty()){
//				System.err.println(url);
				continue;
			}
			if(!StringUtil.isEmpty(tmp.toString())){
				Object timestr=IKFunction.keyVal(tmp,"lUpdateTime");
				if(timestr.toString().length()>8){
					String time=timestr.toString().substring(0,8);
					String t1=time.substring(0,4);
					String t2=time.substring(4,6);
					String t3=time.substring(6,8);
					time=t1+"-"+t2+"-"+t3;
					records.put("time", time);
					records.put("id", time+title);
				}
				
			}
			records.put("url", url);
			records.put("describe", describe);
			records.put("title", title);
			records.put("list", listmap);
			records.put("website", "投资赢家");
			listresult.add(records);
		}
		return listresult;
		
	}
	
	
//	public void parse2(String url){
//		Map<String, String> map=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>());
//		String html=map.get("html");
//		Object json=IKFunction.jsonFmt(html);
//		Object data=IKFunction.keyVal(json, "data");
//		int num=IKFunction.rowsArray(data);
//		for(int i=1;i<=num;i++){
//			Object one=IKFunction.array(data, i);
//			Object name=IKFunction.keyVal(one, "strStockName");
//			Object code=IKFunction.keyVal(one, "strStockCode");
//		}
//		
//		
//	}
	
}
