package com.test.MongoMaven.crawler;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class CrawlerPrice {
		public static void main(String[] args) {
			MongoDbUtil mongo=new MongoDbUtil();
			 MongoCollection<Document>  collection=mongo.getShardConn("stock_code");
			 MongoCursor<Document> cursor =collection.find()/*.filter(filter)*/.batchSize(10000).noCursorTimeout(true).iterator(); 
			 Document doc=null;
			 int num=1;
			 String tmp="";
	  try {
			 while(cursor.hasNext()){
				 doc=cursor.next();
				 String code=doc.get("id").toString();
				 if(code.startsWith("6")){
					 code="sh"+code;
				 }else{
					 code="sz"+code;
				 }
				 if(num%54==0){
					 String name=doc.get("name").toString();
					 String url="http://hq.sinajs.cn/list="+tmp;
					 tmp="";
					 Map<String, String> map=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 15000, new HashMap<String, String>());
					 String html=map.get("html");
					 List<HashMap<String, Object>> result=parse(html);
					  if(!result.isEmpty()){
							mongo.upsetManyMapByTableName(result, "stock_online_information");
							System.out.println("some One");
					  }
				 }else{
					 tmp=tmp+code+",";
				 }
				 num++;
			 }
			 cursor.close();
			 if(tmp!=""){
				 String url="http://hq.sinajs.cn/list="+tmp;
				 Map<String, String> map=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 15000, new HashMap<String, String>());
				 String html=map.get("html");
				 List<HashMap<String, Object>> result=parse(html);
				  if(!result.isEmpty()){
						mongo.upsetManyMapByTableName(result, "stock_online_information");
						System.out.println("last One");
				  } 
			 }
			 
			
			 
	  } catch (Exception e) {
			// TODO Auto-generated catch block
		  
			e.printStackTrace();
		}
		   
	}
		
		public static List<HashMap<String, Object>> parse(String html){
//			 var hq_str_sh600000="浦发银行,15.210,15.210,15.160,15.220,15.130,15.160,15.170,12607509,191225527.000,9830,15.160,63300,15.150,186800,15.140,223400,15.130,108700,15.120,125100,15.170,161920,15.180,29200,15.190,148963,15.200,189415,15.210,2017-05-02,15:00:00,00";
//			浦发银行,15.160,15.160,15.140,15.160,15.130,15.140,15.150,173380,2625508.000,29420,15.140,7700,15.130,88800,15.120,51900,15.110,69900,15.100,5200,15.150,44160,15.160,1700,15.170,1400,15.180,24500,15.190,2017-05-03,09:33:20,00
			if(StringUtil.isEmpty(html)||html.length()<100){
				return new ArrayList<HashMap<String,Object>>();
			}
			List<HashMap<String, Object>> listmap=new ArrayList<HashMap<String,Object>>();
			HashMap<String, Object> map=null;
			String[] one=html.split(";");
			for(int i=0;i<one.length-1;i++){
				String onestr=one[i];
				String code=onestr.split("=")[0].replace("var hq_str_", "");
				if(onestr.length()<50){
					System.err.println(code);
					continue;
				}
				map=new HashMap<String, Object>();
				String str=onestr.split("=")[1];
				 String tmp=str.replace("\"", "");
				 String[] list=tmp.split(",");
//					 0：”大秦铁路”，股票名字；
//					 1：”27.55″，今日开盘价；
//					 2：”27.25″，昨日收盘价；
//					 3：”26.91″，当前价格；
//					 4：”27.55″，今日最高价；
//					 5：”26.20″，今日最低价；
//					 6：”26.91″，竞买价，即“买一”报价；
//					 7：”26.92″，竞卖价，即“卖一”报价；
//					 8：”22114263″，成交的股票数，由于股票交易以一百股为基本单位，所以在使用时，通常把该值除以一百；
//					 9：”589824680″，成交金额，单位为“元”，为了一目了然，通常以“万元”为成交金额的单位，所以通常把该值除以一万；
//					 10：”4695″，“买一”申请4695股，即47手；
//					 11：”26.91″，“买一”报价；
//					 12：”57590″，“买二”
//					 13：”26.90″，“买二”
//					 14：”14700″，“买三”
//					 15：”26.89″，“买三”
//					 16：”14300″，“买四”
//					 17：”26.88″，“买四”
//					 18：”15100″，“买五”
//					 19：”26.87″，“买五”
//					 20：”3100″，“卖一”申报3100股，即31手；
//					 21：”26.92″，“卖一”报价
//					 (22, 23), (24, 25), (26,27), (28, 29)分别为“卖二”至“卖四的情况”
//					 30：”2008-01-11″，日期；
//					 31：”15:05:32″，时间；
					 String name=list[0];
					 map.put("name", name);
					 String priceB=list[1];
					 map.put("priceB", priceB);
					 String priceE=list[2];
					 map.put("priceE", priceE);
					 String priceNow=list[3];
					 map.put("priceNow", priceNow);
					 String priceH=list[4];
					 map.put("priceH", priceH);
					 String priceL=list[5];
					 map.put("priceL", priceL);
					 String buy1=list[6];
					 map.put("buy1", buy1);
					 String sale1=list[7];
					 map.put("sale1", sale1);
					 String tradeNum=list[8];
					 map.put("tradeNum", tradeNum);
					 String tradePrice=list[9];
					 map.put("tradePrice", tradePrice);
					 String buy1Nums=list[10];
					 map.put("buy1Nums", buy1Nums);
					 String buy2Nums=list[12];
					 map.put("buy2Nums", buy2Nums);
					 String buy2=list[13];
					 map.put("buy2", buy2);
					 String buy3Nums=list[14];
					 map.put("buy3Nums", buy3Nums);
					 String buy3=list[15];
					 map.put("buy3", buy3);
					 String buy4Nums=list[16];
					 map.put("buy4Nums", buy4Nums);
					 String buy4=list[17];
					 map.put("buy4", buy4);
					 String buy5Nums=list[18];
					 map.put("buy5Nums", buy5Nums);
					 String buy5=list[19];
					 map.put("buy5", buy5); 
					 String sale1Nums=list[20];
					 map.put("sale1Nums", sale1Nums);
					 String sale2Nums=list[22];
					 map.put("sale2Nums", sale2Nums);
					 String sale2=list[23];
					 map.put("sale2", sale2);
					 String sale3Nums=list[24];
					 map.put("sale3Nums", sale3Nums);
					 String sale3=list[25];
					 map.put("sale3", sale3);
					 String sale4Nums=list[26];
					 map.put("sale4Nums", sale4Nums);
					 String sale4=list[27];
					 map.put("sale4", sale4);
					 String sale5Nums=list[28];
					 map.put("sale5Nums", sale5Nums);
					 String sale5=list[29];
					 map.put("sale5", sale5);
					 String year=list[30];
					 map.put("year", year);
					 String time=list[31];
					 map.put("time", time);
					 map.put("id", code+year+time);
					 listmap.add(map);
			}
			
				 return listmap;
		}
		
		
}
