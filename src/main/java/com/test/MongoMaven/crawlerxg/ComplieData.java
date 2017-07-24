package com.test.MongoMaven.crawlerxg;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.client.ClientProtocolException;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class ComplieData {
	static MongoDbUtil mongo=new MongoDbUtil();
	static PostData post=new PostData();
	//循环取title(指标)
	@SuppressWarnings({ "null", "deprecation", "unchecked" })
	public static void main(String[] args) {
		mongo.getShardConn("xg_all_website").deleteMany(Filters.exists("id"));
//		Date date=new Date();
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//		String ttaday=sdf.format(date);
		try{
		insert2Table("xg_gpdt_stock");
		insert2Table("xg_tzyj_stock");
		insert2Table("xg_ypgpt_stock");
		complie2Table("xg_all_website");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void insert2Table(String table) throws ClientProtocolException, IOException{
		MongoCollection<Document> collection=mongo.getShardConn(table);
//		Bson filter = Filters.eq("time", condition);
		try{
		 MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator(); 
//		 if(!cursor.hasNext()){
//			 //如果今天没有数据就获取昨天的数据
//			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//			String t1=condition+" 00:00:00";
//			long taday=IKFunction.getTimestamp(t1);
//			long oneday=24*60*60*1000;
//			long lastday=taday-oneday;
//			Date dtmp = new Date(lastday);
//			String tlastday=sdf.format(dtmp);
//			filter = Filters.eq("time", tlastday);
//			cursor =collection.find(filter).batchSize(10000).noCursorTimeout(true).iterator(); 
//		 }
		 Document doc=null;
		 HashMap<String, Object > records=null;
		 while(cursor.hasNext()){
			 doc=cursor.next();
			 Object title=doc.get("title");
			 Object time=doc.get("time");
			 Object website=doc.get("website");
			 Object list=doc.get("list");
			 int num=IKFunction.rowsArray(list);
			 for(int i=1;i<=num;i++){
				 records=new HashMap<String, Object>();
				 Object one=IKFunction.array(list, i);
				 Object name=IKFunction.keyVal(one, "stockName");
				 if(name.toString().contains("ST")||name.toString().contains("st")){
					 continue;
				 }
				 Object code=IKFunction.keyVal(one, "code");
				 String selecprice=IKFunction.keyVal(one, "selecprice").toString();
				 records.put("id", title+""+code+time+website);
				 records.put("stockName", name);
				 records.put("code", code);
				 records.put("title", title);
				 records.put("selectime", time);
				 records.put("website", website);
				 if(StringUtil.isEmpty(selecprice)){
				    String url="";
					if(code.toString().startsWith("6")){
						url="http://hq.sinajs.cn/list=sh"+code;
					}else{
						url="http://hq.sinajs.cn/list=sz"+code;
					}
					String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
					if(!StringUtil.isEmpty(html)&&html.length()>50){
						HashMap<String, Object > map=IKFunction.parseSina(html);
					    selecprice= map.get("priceE").toString();
					}
				}
				 records.put("selecprice", selecprice);
				 mongo.upsertMapByTableName(records, "xg_all_website"); 
			 } 
		 }
	    cursor.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void complie2Table(String table) throws ClientProtocolException, IOException{
	  try{ 
		MongoCollection<Document> collection=mongo.getShardConn(table);
		 MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator(); 
		 Document doc=null;
		 HashMap<String, Object > records=null;
		 List<HashMap<String, Object >> list=new ArrayList<HashMap<String,Object>>();
		 while(cursor.hasNext()){
			 records=new HashMap<String, Object>();
			 doc=cursor.next();
			Object name= doc.get("stockName");
			Object code= doc.get("code");
			Object title= doc.get("title");
			Object time= doc.get("selectime");
			String price= doc.get("selecprice").toString();
		if(StringUtil.isEmpty(price)||"0.00".equals(price)){
			String url="";
			if(code.toString().startsWith("6")){
				url="http://hq.sinajs.cn/list=sh"+code;
			}else{
				url="http://hq.sinajs.cn/list=sz"+code;
			}
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)&&html.length()>50){
			HashMap<String, Object > map=IKFunction.parseSina(html);
				if(!map.isEmpty()){
					price= map.get("priceE").toString();
				}
			}
//						continue;
		}
		
		if(price.contains(".")){
			String [] pstr=price.split("\\.");
			String ptmp=pstr[1];
			if(ptmp.length()>2){
				ptmp=ptmp.substring(0, 2);
			}else if(ptmp.length()==1){
				ptmp=ptmp+"0";
			}
			price=pstr[0]+"."+ptmp;
			}
			int supportnum=1;
			List<HashMap<String, Object >> list1=new ArrayList<HashMap<String,Object>>();
			HashMap<String, Object > map1=new HashMap<String, Object>();
			map1.put("ss", title);
			int num=-1;
			for(int p=0;p<list.size();p++){
				HashMap<String, Object> map=list.get(p);
				if(map.get("code").toString().equals(code)){
					supportnum=Integer.parseInt(map.get("supportnum").toString())+1;
					list1=(List<HashMap<String, Object >>)map.get("support");
					num=p;
				}
			}
			if(num!=-1){
				 list.remove(num);
			}
			 list1.add(map1);
			 records.put("id", code);
			 records.put("stockName", name);
			 records.put("code", code);
			 records.put("selectime", time);
			 records.put("newprice", "");
			 records.put("selecprice", price);
			 records.put("supportnum", supportnum);
			 records.put("support", list1);
			 list.add(records);
		 }
		   cursor.close();
		   Collections.sort(list, new Comparator<HashMap<String, Object >>() {
		            public int compare(HashMap<String, Object > a, HashMap<String, Object > b) {
		                String  t1 =a.get("supportnum").toString();
		                String t2 = b.get("supportnum").toString();
		                int time=Integer.parseInt(t1);
		                int time1=Integer.parseInt(t2);
//		                return t2.compareTo(t1);
		                return time1-time;
		            }
		        });
//			for(HashMap<String,Object > record:list){
//				record.remove("crawl_time");
//				 JSONObject mm_data=JSONObject.fromObject(record);
//				  String su=post.postHtml("http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=xg_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
//					if(su.contains("exception")){
//						System.out.println(mm_data.toString());
//						System.err.println("写入数据异常！！！！  < "+su+" >");
//					}
//			}
//		   Calendar   cal   =   Calendar.getInstance();
//		   cal.add(Calendar.DATE,   -1);
//		   String yesterday = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime());
//		   MongoCursor<org.bson.Document> cursor1= mongo.getShardConn("xg_stock_last_json").find().filter(Filters.eq("selectime",yesterday)).batchSize(10000).noCursorTimeout(true).iterator();
		   mongo.getShardConn("xg_stock_last_json").deleteMany(Filters.exists("id"));
		   mongo.upsetManyMapByTableName(list, "xg_stock_last_json");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
