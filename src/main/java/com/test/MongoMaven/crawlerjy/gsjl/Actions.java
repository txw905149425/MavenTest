package com.test.MongoMaven.crawlerjy.gsjl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class Actions implements Runnable{
	private DataUtil util;
	private MongoDbUtil mongo;
	public Actions(DataUtil util,MongoDbUtil mongo){
		this.util=util;
		this.mongo=mongo;
	}
	
		public void run() {
			     String url=util.getUrl();
			     String code=util.getCode();
			     String name=util.getName();
			     Map<String, String> resultmap=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1,new HashMap<String, String>());
				String html=resultmap.get("html");
			try{
				if(!StringUtil.isEmpty(html)&&html.length()>100){
					 PostData post=new PostData();
					 Object json=IKFunction.jsonFmt(html);
					 Object list=IKFunction.keyVal(json, "result");
					 int num=IKFunction.rowsArray(list);
					 for(int i=1;i<=num;i++){
						 HashMap<String, Object> result=new HashMap<String, Object>();
						 Object one=IKFunction.array(list,i);
						 Object date=IKFunction.array(IKFunction.keyVal(one, "list"),1);
						 String  mm=IKFunction.keyVal(date, "date")+" "+IKFunction.keyVal(date, "time");
						 String time=IKFunction.timeFormat(mm);
						 if(!IKFunction.timeOK(time)){
							 break;
						 }
						 Object uname=IKFunction.keyVal(one, "strategyname");
						 Object totalrate=IKFunction.keyVal(IKFunction.array(IKFunction.keyVal(one, "totalrate"),2),"text");
						 String rate="";
						 if(totalrate.toString().length()>5){
							 rate=totalrate.toString().substring(0,5);
						 }else{
							 rate=IKFunction.regexp(totalrate, "(.*?)\\.");
						 }
						 Object info=IKFunction.keyVal(date, "info");
						 String price=IKFunction.keyVal(IKFunction.array(info,1),"text").toString().replace("以", "").replace("元","");
						 if(StringUtil.isEmpty(price)){
							 continue;
						 }
						 Object type=IKFunction.keyVal(IKFunction.array(info,2),"text");
						 Object nums=IKFunction.keyVal(IKFunction.array(info,3),"text").toString().replace("股","");
						 	if("买入".equals(type.toString())){
								result.put("option", "0");
							}else if("卖出".equals(type.toString())){
								result.put("option", "1");
							}
						 	result.put("tid",name+" "+mm+type+uname);
						 	result.put("id",IKFunction.md5(name+" "+mm+type+uname));
							result.put("describe","总收益："+rate+"%");
							result.put("quantity",nums);
							result.put("StockCode", code);
							result.put("StockName", name);
							result.put("UserName", uname);
							result.put("closing_cost", price);
							result.put("AddTime", time);
//							result.put("html",one);
							result.put("website","股市教练");
//							result.remove("html");
							JSONObject mm_data=JSONObject.fromObject(result);
//							http://wisefinance.chinanorth.cloudapp.chinacloudapi.cn/wf/import?type=mm_stock_json
//							http://localhost:8888/import?type=mm_stock_json
						   String su=post.postHtml("http://wisefinance.chinanorth.cloudapp.chinacloudapi.cn/wf/import?type=mm_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
							if(su.contains("exception")){
								System.err.println("写入数据异常！！！！  < "+su+" >");
							}
							mongo.upsertMapByTableName(result, "mm_deal_dynamic_all");
					 }
				}
			}catch(Exception es){
				es.printStackTrace();
			}
				
				
	    }

}
