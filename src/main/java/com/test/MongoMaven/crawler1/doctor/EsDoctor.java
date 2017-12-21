package com.test.MongoMaven.crawler1.doctor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class EsDoctor {
	static String[] listStr={"爱股票","股市教练","大智慧","发发问股","呱呱财经","股金在线","股票跟投网","股票赢家","股先生","和讯网","红顾问","海纳牛牛","海淘王炒股票","爱投顾","看财经","理想选股","牛仔网","全球市场直播","新浪","淘股吧","同花顺","天天看盘","投资脉搏","微财讯","全景网","问股票","优品股票通","一起牛","约投顾","资本魔方","自选股","股票邦","老钱庄","股市导师","傻瓜理财炒股","东方财富","智能选股"};
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		String time=IKFunction.getTimeNowByStr("yyyy-MM-dd");
		 MongoCollection<Document>  collection=mongo.getShardConn("ww_test");
		 int num=listStr.length;
		 for(int i=0;i<num;i++){
			 String web=listStr[i];
			 BasicDBObject doc = new BasicDBObject();
			 doc.append("timedel", time);
			 doc.append("website",web);
			 MongoCursor<Document> cursor =collection.find(doc).batchSize(100).noCursorTimeout(false).iterator();
			 if(!cursor.hasNext()){
				 cursor=null;
				 System.out.println("网站：  "+web+"    当天("+time+")内无数据");
				 doc.append("timedel", getYestime());
				 cursor =collection.find(doc).batchSize(100).noCursorTimeout(false).iterator();
				 if(!cursor.hasNext()){
				  System.err.println("网站：  "+web+"   昨日("+getYestime()+")内无数据！！！！！！"); 
				 }
			 }
			 cursor=null;
		 }
	}
	
	public static String getYestime(){
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE,-1);
		Date time=cal.getTime();
		return new SimpleDateFormat("yyyy-MM-dd").format(time);
		
	}
}
