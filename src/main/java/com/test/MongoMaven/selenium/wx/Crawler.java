package com.test.MongoMaven.selenium.wx;

import java.util.ArrayList;
import java.util.HashMap;

import org.openqa.selenium.WebDriver;

import com.test.MongoMaven.selenium.SeleniumUtil;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PageUrl;

public class Crawler {
	public static void main(String[] args) {
		String firefox="";
		for(String arg:args){
			if(arg.startsWith("flag=")){
				firefox=arg.substring(5);
			}
		 }
		MongoDbUtil mongo=new MongoDbUtil();
		String url="http://weixin.sogou.com/";
		ArrayList<String> listkey=FileUtil.readFileReturn("wx_gzh");
		try {
			for(String key:listkey){
				WebDriver driver=null;
				driver=SearchForWx.createFireFox(driver,firefox);
				System.out.println(key);
				HashMap<String, Object> dmap=SearchForWx.SeachKeyWordForGzh(driver, url, key);
				if(!dmap.isEmpty()){
					mongo.upsertMapByTableName(dmap, "gd_weixin");	
				}
				waitTime(6000);
				driver.close();
				driver.quit();
			}
//			driver.close();
//			driver.quit();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	 public static void waitTime(int time) {
		  try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }	

}
