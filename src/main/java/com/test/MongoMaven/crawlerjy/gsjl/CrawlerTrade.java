package com.test.MongoMaven.crawlerjy.gsjl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.crawler.dfcfWeb.ParseMethod;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class CrawlerTrade {
	
	public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document>  collection=mongo.getShardConn("stock_code");
//		 Bson filter = Filters.exists("name", true);
		 Bson filter1 = Filters.exists("gsjl_crawl", false);
		 MongoCursor<Document> cursor =collection.find(filter1).batchSize(10000).noCursorTimeout(true).iterator(); 
		 Document doc=null;
		 List<HashMap<String , Object>> listresult=new ArrayList<HashMap<String , Object>>();
		 HashMap<String , Object> result = null;
		try{
			 while(cursor.hasNext()){
				 doc=cursor.next();
				 Object code=doc.get("id");
				 Object code_name=doc.get("name");
				 String url="http://t.10jqka.com.cn/api.php?method=newtrace.getTacticTrend&code="+code+"&type=0&rid=0";
				 Map<String, String> resultmap=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1,new HashMap<String, String>());
				 String  html=resultmap.get("html");
						if(!StringUtil.isEmpty(html)&&html.length()>100){
						 Object json=IKFunction.jsonFmt(html);
						 Object list=IKFunction.keyVal(json, "result");
						 int num=IKFunction.rowsArray(list);
						 for(int i=1;i<=num;i++){
							 result=new HashMap<String, Object>();
							 Object one=IKFunction.array(list,i);
							 Object name=IKFunction.keyVal(one, "strategyname");
							 Object totalrate=IKFunction.keyVal(IKFunction.array(IKFunction.keyVal(one, "totalrate"),2),"text");
							 Object date=IKFunction.array(IKFunction.keyVal(one, "list"),1);
							 String  mm=IKFunction.keyVal(date, "date")+" "+IKFunction.keyVal(date, "time");
							 String time=IKFunction.timeFormat(mm);
							 Object info=IKFunction.keyVal(date, "info");
							 Object price=IKFunction.keyVal(IKFunction.array(info,1),"text").toString().replace("以", "").replace("元","");
							 Object type=IKFunction.keyVal(IKFunction.array(info,2),"text");
							 Object nums=IKFunction.keyVal(IKFunction.array(info,3),"text").toString().replace("股","");
							 	if("买入".equals(type.toString())){
									result.put("option", 0);
								}else if("卖出".equals(type.toString())){
									result.put("option", 1);
								}
							 	result.put("id",name+" "+type+code_name+price+" "+time);
								result.put("describe","总收益："+totalrate);
								result.put("quantity",nums);
								result.put("StockCode", code);
								result.put("StockName", code_name);
								result.put("UserName", name);
								result.put("closing_cost", price);
								result.put("AddTime", time);
								result.put("html",one);
								result.put("website","股市教练");
								mongo.upsertMapByTableName(result, "mm_gsjl_deal_dynamic");
								listresult.add(result);
						 }
						 Document doc1=new Document();
						 doc1.put("id",code);
						 doc1.put("gsjl_crawl", "1");
						 mongo.upsertDocByTableName(doc1, "stock_code");
						 System.out.println(code_name);
					}else{
						Object json=IKFunction.jsonFmt(html);
						Object mes=IKFunction.keyVal(json, "errorMsg");
						if("无数据".equals(mes.toString())){
							System.out.println(mes);
							Document doc1=new Document();
							 doc1.put("id",code);
							 doc1.put("gsjl_crawl", "无数据");
							 mongo.upsertDocByTableName(doc1, "stock_code");
						}
					}
			 }
			 System.out.println("---");
//			 if(!listresult.isEmpty()){
//					mongo.upsetManyMapByTableName(listresult, "mm_gsjl_deal_dynamic");	
//				}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
