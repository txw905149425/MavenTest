package com.test.MongoMaven.crawler1.lxxg;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.sf.json.JSONObject;

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


//理想选股！！ 数据更新慢     抓取频率较低  5分钟左右一次
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
					Object time=IKFunction.keyVal(one, "replytime");
					String timestr=IKFunction.timeFormat(time.toString());
					if(!IKFunction.timeOK(timestr)){
						continue;
					}
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
					String  answer=IKFunction.keyVal(one, "reply").toString();
					if(!StringUtil.isEmpty(answer)){
						result.put("ifanswer","1");
					}else{
						result.put("ifanswer","0");
					}
					result.put("name",name);
					result.put("id",IKFunction.md5(que+""+answer));
					result.put("tid",que+""+time);
					result.put("question", que);
					result.put("answer", answer);
					result.put("time", timestr);
					result.put("website", "理想选股");
					listresult.add(result);
				}
		 }
		 
		 if(!listresult.isEmpty()){
			 mongo.upsetManyMapByTableName(listresult, "ww_ask_online_all");
//			 for(HashMap<String, Object> one:listresult){
//					String ttmp=JSONObject.fromObject(one).toString();
//					 String su= post.postHtml("http://localhost:8888/import?type=ww_stock_json",new HashMap<String, String>(),ttmp, "utf-8", 1);
//						if(su.contains("exception")){
//							System.err.println("写入数据异常！！！！  < "+su+" >");
//						}
//			     }
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
