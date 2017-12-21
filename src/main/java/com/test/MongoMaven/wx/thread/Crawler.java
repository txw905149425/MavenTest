package com.test.MongoMaven.wx.thread;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
/***
 * 微信公众号数据抓取
 * */
public class Crawler {
	public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
		 ExecutorService executor = Executors.newFixedThreadPool(3);///home/jcj/crawler/wx/
		 ArrayList<String> list=FileUtil.readFileReturn("wx_gzh1");
		 DataUtil util=null;
		 for(String tmp:list){
				String key=tmp.split("=")[0];
//					String nname=tmp.split("=")[1];
//					if(nname.equals("每晚牛股短线一只")){
//						System.out.println("www");
//					}
				String rtmp=IKFunction.charEncode(key.trim(),"utf8");
				String url="http://weixin.sogou.com/weixin?query="+rtmp+"&_sug_type_=&s_from=input&_sug_=n&type=1&page=1&ie=utf8";
				util=new DataUtil();
				util.setUrl(url);
				executor.execute(new Actions(util,mongo));
		 }
		 executor.shutdown();
	}
	
	
}
