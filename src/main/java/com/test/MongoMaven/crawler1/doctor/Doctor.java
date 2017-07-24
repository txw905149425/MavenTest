package com.test.MongoMaven.crawler1.doctor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.storm.shade.org.yaml.snakeyaml.constructor.SafeConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Doctor {
	static String[] listStr={"爱股票","股市教练","大智慧","发发问股","呱呱财经","股金在线","股票跟投网","股票赢家","股先生","和讯网","红顾问","海纳牛牛","海淘王炒股票","爱投顾","看财经","理想选股","牛仔网","全球市场直播","新浪","淘股吧","同花顺","天天看盘","投资脉搏","微财讯","全景网","问股票","优品股票通","一起牛","约投顾","资本魔方","自选股"};
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		String time=IKFunction.getTimeNowByStr("yyyy-MM-dd");
		 MongoCollection<Document>  collection=mongo.getShardConn("ww_ask_online_all");
		 Bson filter = Filters.eq("timedel",time);
		 int num=listStr.length;
		 for(int i=0;i<num;i++){
			 String web=listStr[i];
			 Bson filter1 = Filters.eq("website", web);
			 MongoCursor<Document> cursor =collection.find().filter(filter).filter(filter1).batchSize(10000).noCursorTimeout(true).iterator();
			 if(!cursor.hasNext()){
				 System.out.println("网站：  "+web+" 当天("+time+")内无数据");
				 filter=Filters.eq("timedel",getYestime());//获取昨日的数据
				 cursor =collection.find().filter(filter).filter(filter1).batchSize(10000).noCursorTimeout(true).iterator();
				 if(!cursor.hasNext()){
				  System.err.println("网站：  "+web+" 昨日("+getYestime()+")内无数据！！！！！！"); 
				 }
			 }
		 }
	}
	
	public static String getYestime(){
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE,-1);
		Date time=cal.getTime();
		return new SimpleDateFormat("yyyy-MM-dd").format(time);
		
	}
}
