package com.test.MongoMaven.crawlerjy.ypgpt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.json.JSONObject;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;


/***
 * 优品股票通
 * */
public class CrawlerJY {
	
	public static void main(String[] args) {
			 MongoDbUtil mongo=new MongoDbUtil();
			PostData post=new PostData();
			 MongoCollection<Document>  collection=mongo.getShardConn("mm_ypgpt_name");
//			 BasicDBObject query=new BasicDBObject();
//			 query.put("id", 1);
//			 query.put("describe", 1);
			 MongoCursor<Document> cursor =collection.find()/*.filter(filter)*/.batchSize(10000).noCursorTimeout(true).iterator(); 
			 Document doc=null;
		try{
			 while(cursor.hasNext()){
				 doc=cursor.next();
				 Object id=doc.get("id");
				 Object name=doc.get("name");
//				 System.out.println(name);
				 String url1="http://cgdsm.upchina.com/center/1/"+id;
				 String html1=HttpUtil.getHtml(url1, new HashMap<String, String>(), "utf8", 1,new HashMap<String, String>()).get("html");
				 String describe=IKFunction.jsoupTextByRowByDoc(IKFunction.JsoupDomFormat(html1),".profits>span",2);
				 String url="http://cgdsm.upchina.com/userCenter/userBargainHis/"+id;
				 String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1,new HashMap<String, String>()).get("html");
				 List<HashMap<String, Object>> list=parse(html,describe,name.toString());
				 if(!list.isEmpty()){
					 for(HashMap<String, Object> result:list){
//							result.remove("html");
							result.remove("quality");
							result.remove("crawl_time");
							JSONObject mm_data=JSONObject.fromObject(result);
						   String su=post.postHtml("http://wisefinance.chinaeast.cloudapp.chinacloudapi.cn:8000/wf/import?type=mm_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
							if(su.contains("exception")){
								System.out.println(mm_data.toString());
								System.err.println("写入数据异常！！！！  < "+su+" >");
							}
					 }
					 mongo.upsetManyMapByTableName(list, "mm_ypgpt_deal_dynamic");
				 }
			 }
		   cursor.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static List<HashMap<String, Object>> parse(String html,String describe,String name){
		if(StringUtil.isEmpty(html)){
			return new ArrayList<HashMap<String, Object>>();
		}
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map=null;
		int num=IKFunction.rowsArray(html);
		for(int i=1;i<=num;i++){
			map=new HashMap<String, Object>();
			Object js=IKFunction.array(html,i);
			Object timeObj=IKFunction.keyVal(js, "trddate");
			if(!timOK(timeObj.toString())){
				continue;
			}
			Object closing_cost=IKFunction.keyVal(js, "price");
			Object option=IKFunction.keyVal(js, "trdid");
			Object stockName=IKFunction.keyVal(js, "secuname");
			Object stockCode=IKFunction.keyVal(js, "secucode");
			Object nums=IKFunction.keyVal(js, "matchedqty");
			Object cost=IKFunction.keyVal(js, "matchedamt");
			map.put("id",IKFunction.md5(timeObj+""+option+stockName));
			map.put("tid",timeObj+""+option+stockName);
			map.put("AddTime", timeObj);
			map.put("closing_cost", closing_cost);
			map.put("StockName", stockName);
			map.put("StockCode", stockCode);
			map.put("website", "优品股票通");
			map.put("quality", nums);
			map.put("quality_cost", cost);
			map.put("option", option);
			map.put("UserName", name);
			map.put("describe", "总收益："+describe);
			list.add(map);
		}
		
		return list;
	}

	public static boolean timOK(String timestr){
		boolean flag=false;
		 Date date=new Date();
	     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	     String dateNowStr = sdf.format(date)+" 00:00:00"; 
	     SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	     try {
			long timeNow=sdf1.parse(dateNowStr).getTime();
			long time=sdf1.parse(timestr).getTime();
			if(time>timeNow){
				flag=true;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
	
}
