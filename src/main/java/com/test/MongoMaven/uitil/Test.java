package com.test.MongoMaven.uitil;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Test {
   public static void main(String[] args) {
	   //Executors是ThreadPoolExecutor的工厂构造方法
       ExecutorService executor = Executors.newFixedThreadPool(10);
       String[] str=new String[10];
       HashMap<String , String> map=new HashMap<String, String>();
	  for(int i=0;i<10;i++){
		//submit有返回值，而execute没有返回值，有返回值方便Exception的处理
		  String url="http://www.gsxt.gov.cn/index.html";
		  Future res = executor.submit(new TestHttpClient(url,map));
		  
	  }
	  executor.shutdown();
   }
}
