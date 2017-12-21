package com.test.MongoMaven.crawlerjy.dfcf;

import java.util.HashMap;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
	try{
		PostData post=new PostData();
		MongoDbUtil mongo=new MongoDbUtil();
		String turl="";
	for(int t=1;t<=2;t++){
		if(t==1){
			turl="https://contest.securities.eastmoney.com/dsapi/Nuggets/UserNewTran_132_";
		}else{
			turl="https://contest.securities.eastmoney.com/dsapi/Nuggets/UserNewTran_131_";
		}
		for(int p=1;p<=5;p++){
			String url=turl+p+".html";
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)&&html.length()>200){
				Object json=IKFunction.jsonFmt(html);
				Object data=IKFunction.keyVal(json, "data");
				int num=IKFunction.rowsArray(data);
				HashMap<String ,Object> map=null;
				for(int i=1;i<=num;i++){
					Object one=IKFunction.array(data, i);
					String tone=IKFunction.keyVal(one,"tzrq").toString();
					String year=tone.substring(0, 4)+"-"+tone.substring(4, 6)+"-"+tone.substring(6, 8);
					String ttwo=IKFunction.keyVal(one,"tzsj").toString();
					if(ttwo.length()==7){
						ttwo="0"+ttwo;
					}
					String hour=ttwo.substring(0, 2)+":"+ttwo.substring(2, 4)+":"+ttwo.substring(4, 6);
					String time=year+" "+hour;
					if(!IKFunction.timeOK(time)){
						continue;
					}
					Object id=IKFunction.keyVal(one, "zjzh");
					String durl="https://spdsqry.eastmoney.com/rtcs1?type=rtcs_zuhe_detail&zh="+id;
					Object name=IKFunction.keyVal(one, "uidnick");
					Object stockName=IKFunction.keyVal(one, "stkName");
					String stockCode=IKFunction.keyVal(one, "stkMktCode").toString();
					stockCode=IKFunction.regexp(stockCode, "(\\d+)");
					Object price=IKFunction.keyVal(one, "cjjg");
					Object opt=IKFunction.keyVal(one, "mmbz");
					String option="";//1卖出0买入
					if("买入".equals(opt.toString().trim())){
						option="0";
					}else if("卖出".equals(opt.toString().trim())){
						option="1";
					}
					String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
					if(!StringUtil.isEmpty(dhtml)&&dhtml.length()>200){
						Object djson=IKFunction.jsonFmt(dhtml);
						Object ddata=IKFunction.keyVal(djson,"data");
						Object detail=IKFunction.keyVal(ddata,"detail");
						Object rate=IKFunction.keyVal(detail, "rate");
						map=new HashMap<String, Object>();
						map.put("describe", "总收益："+rate+"%");
						map.put("StockName",stockName);
						map.put("StockCode", stockCode);
						map.put("closing_cost", price);
						map.put("AddTime", time);
						map.put("option", option);
						map.put("UserName", name);
						map.put("website", "东方财富");
						map.put("url", durl);
						map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
						map.put("tid", name+time+stockName+option);
						map.put("id", IKFunction.md5(name+time+stockName+option));
						String ttmp=JSONObject.fromObject(map).toString();
//						Constants.ES_URI+type=mm_stock_json
						String su= post.postHtml("http://localhost:8888/import?type=mm_stock_json",new HashMap<String, String>(),ttmp, "utf-8", 1);
						if(su.contains("exception")){
							System.err.println("写入数据异常！！！！  < "+su+" >");
						}
						mongo.upsertMapByTableName(map, "mm_deal_dynamic_all");
//						mongo.upsertMapByTableName(map, "mm_dfcf");
					}
				}
			}
		  }
		}
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	}
}
