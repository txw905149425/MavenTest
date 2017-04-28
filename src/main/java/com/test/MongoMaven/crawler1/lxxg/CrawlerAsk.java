package com.test.MongoMaven.crawler1.lxxg;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.crawler.dfcfWeb.Actions;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class CrawlerAsk {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document>  collection=mongo.getShardConn("ww_lxxg_genius_id");
		 MongoCursor<Document> cursor =collection.find().batchSize(10000).noCursorTimeout(true).iterator(); 
		 Document doc=null;
		 PostData post=new PostData();
		HashMap<String , String> map=new HashMap<String, String>();
		map.put("User-Agent", "android-async-http/1.4.3 (http://loopj.com/android-async-http)");
		map.put("Content-Type", "application/x-www-form-urlencoded");
		map.put("Host", "app.55188.com");
	try {
		List<HashMap<String , Object>> listresult=new ArrayList<HashMap<String , Object>>();
		HashMap<String , Object> result = null;
		 while(cursor.hasNext()){
			 doc=cursor.next();
			 Object id=doc.get("id");
//			 Object name=doc.get("name");
			 String url="http://app.55188.com/live/live/asklist";
				String data="anchorid="+id+"&version=3.3.3.0&pagesize=20&offset=0&loantoken=";
				String html=post.postHtml(url, map,data,"utf8", 2);
				Object json=IKFunction.jsonFmt(html);
				Object datas=IKFunction.keyVal(json, "data");
				Object list=IKFunction.keyVal(datas, "list");
				int num=IKFunction.rowsArray(list);
				for(int i=1;i<=num;i++){
					result=new HashMap<String, Object>();
					Object one=IKFunction.array(list, i);
					Object name=IKFunction.keyVal(one, "anchortitle");
					String  con=IKFunction.keyVal(one, "content").toString();
					String stocktitle=IKFunction.keyVal(one, "stocktitle").toString();
					String  que=null;
					if(stocktitle.contains(" ")){
						String[] str=stocktitle.split(" ");
						if(con.contains(str[0])||con.contains(str[1])){
							que=con;
						}else{
						 que=stocktitle+con;
						}
					}
					if(que==null){
						continue;
					}
					Object answer=IKFunction.keyVal(one, "reply");
					Object time=timeFormat(IKFunction.keyVal(one, "replytime").toString());
					result.put("name",name);
					result.put("id",que+""+time);
					result.put("question", que);
					result.put("answer", answer);
					result.put("time", time);
					result.put("website", "理想选股");
					listresult.add(result);
				}
			 
		 }
		mongo.upsetManyMapByTableName(listresult, "ww_lxxg_ask_shares");
		System.out.println("........");
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
	
	
	
	public static String timeFormat(String str){
		if(StringUtil.isEmpty(str)){
			return "";
		}
		str=str.trim();
		if(str.contains("今天")){
			 Date d = new Date();  
			 Random r = new Random();
			 int second= r.nextInt(60);
			 String se="";
			 if(Integer.toString(second).length()<2){
				 se="0"+second;
			 }else{
				 se=""+second;
			 }
		     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		     String dateNowStr = sdf.format(d); 
			 str=dateNowStr+" "+str.replace("今天", "")+":"+se;
		}else if(str.contains("分钟前")){
			String tmp=str.replace("分钟前", "");
			int num=Integer.parseInt(tmp);
			long numMill=num*60*1000;
			Long s =System.currentTimeMillis()-numMill;
			Date date=new Date(s);
		     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		     String dateNowStr = sdf.format(date); 
			str=dateNowStr;
		}else if(str.contains("月")&&str.contains("日")){
			 Calendar now = Calendar.getInstance();  
		      int year=now.get(Calendar.YEAR); 
			  str=year+"-"+str.replace("月", "-").replace("日","");
		}else if(str.length()==5){
			Date d = new Date();  
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		    String dateNowStr = sdf.format(d); 
			 Random r = new Random();
			 int second= r.nextInt(60);
			 String se="";
			 if(Integer.toString(second).length()<2){
				 se="0"+second;
			 }else{
				 se=""+second;
			 }
			 str=dateNowStr+" "+str+":"+se;
		}else if(str.length()==11){
			 Random r = new Random();
			 int second= r.nextInt(60);
			 String se="";
			 if(Integer.toString(second).length()<2){
				 se="0"+second;
			 }else{
				 se=""+second;
			 }
			 Calendar now = Calendar.getInstance();  
		      int year=now.get(Calendar.YEAR); 
			 str=year+"-"+str+":"+se;
		}else if(str.length()==16){
			 Random r = new Random();
			 int second= r.nextInt(60);
			 String se="";
			 if(Integer.toString(second).length()<2){
				 se="0"+second;
			 }else{
				 se=""+second;
			 }
			 str=str+":"+se;
		}
		else{
			System.err.println("*******====>  时间转换出现新情况："+str);
			return "1";
		}
		return  str;
		
	}
	
}
