package com.test.MongoMaven.zhibiao.zbjcj;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

//每天更新今天推荐的股票的涨跌幅
public class UpdateTodayStockRose {
	public static void main(String[] args) {
		   MongoDbUtil mongo=new MongoDbUtil();
		   MongoCollection<Document> collectiondele=mongo.getShardConn("xg_stock_last_json");
		   MongoCursor<Document> cursor =collectiondele.find().batchSize(10000).noCursorTimeout(true).iterator();
		   DecimalFormat df=new DecimalFormat("0.00");
		   while(cursor.hasNext()){
			   Document doc=cursor.next();
			   Object code=doc.get("code");
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
					String shou= map.get("priceE").toString();
					String now=map.get("priceNow").toString();
					String fudu=df.format((Float.parseFloat(now)-Float.parseFloat(shou))*100/Float.parseFloat(shou));
					doc.remove("_id");
					doc.append("rose", fudu);
					mongo.upsertDocByTableName(doc, "xg_stock_last_json_all");
				}
				}
		   }
	}
}
