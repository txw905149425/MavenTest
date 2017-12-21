package com.test.MongoMaven.crawlergd.znxg;

import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		 MongoCollection<Document>  collection=mongo.getShardConn("stock_code");
		 Bson filter = Filters.exists("name", true);
		 MongoCursor<Document> cursor =collection.find().filter(filter).batchSize(10000).noCursorTimeout(true).iterator(); 
		 try{
		 while(cursor.hasNext()){
			 Document doc=cursor.next();
			 Object code=doc.get("id");
			 Object sname=doc.get("name");
			 String url="http://robot.rxhui.com/robot/semantic//semantic-api-service/api/qa?question="+code;
				String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
				if(!StringUtil.isEmpty(html)&&html.length()>200){
					Object json=IKFunction.jsonFmt(html);
					Object data=IKFunction.keyVal(json, "data");
					Object ddata=IKFunction.keyVal(data,"stockReportResult");
					int num=IKFunction.rowsArray(ddata);
					for(int i=1;i<=num;i++){
						Object one=IKFunction.array(ddata, i);
						Object timeObj=IKFunction.keyVal(one, "createTime");
						String time=IKFunction.timeFormat(timeObj.toString());
						if(!IKFunction.timeOK(time)){
							continue;
						}
						Object name=IKFunction.keyVal(one, "author");
						Object source=IKFunction.keyVal(one, "organization");
						Object tcont=IKFunction.keyVal(one, "analyseResults");
						ArrayList<HashMap<String, Object>> contList = new ArrayList<HashMap<String,Object>>();
						String  tcont1=IKFunction.keyVal(tcont, code+"估值评级").toString();
						String text=code.toString();
						if(!StringUtil.isEmpty(tcont1)){
							text=text+tcont1;
							HashMap<String, Object> map1=new HashMap<String, Object>();
							map1.put("cont",code+tcont1);
							contList.add(map1);
						}
						String  tcont2=IKFunction.keyVal(tcont, code+"事件对公司的影响").toString();
						if(!StringUtil.isEmpty(tcont2)){
							text=text+tcont2;
							HashMap<String, Object> map1=new HashMap<String, Object>();
							map1.put("cont",code+tcont2);
							contList.add(map1);
						}
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("id", IKFunction.md5(code +time+name));
						map.put("source", source);
						map.put("name", name);
						map.put("contentlist", contList);
						map.put("url", url);
						map.put("contenthtml", text);
						map.put("title", code+"估值评级");
						map.put("time", time);
						map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
						mongo.upsertMapByTableName(map, "lzx_viewpoint");
					}
				}
		 }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 cursor.close();
		 System.err.println("*******[over]*****");
	}
}
