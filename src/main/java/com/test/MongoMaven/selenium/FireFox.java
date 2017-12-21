package com.test.MongoMaven.selenium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PageUrl;
import com.test.MongoMaven.uitil.StringUtil;


public class FireFox {
	
	public static void main(String[] args) {
		String firefox="";
		for(String arg:args){
			if(arg.startsWith("flag=")){
				firefox=arg.substring(5);
			}
		 }
		MongoDbUtil mongo=new MongoDbUtil();
		String url="http://weixin.sogou.com/";
		String search="#query";
		String button=".swz";
		PageUrl purl=new PageUrl();
		ArrayList<String> listkey=FileUtil.readFileReturn("keyword.txt");
		try {
			for(String key:listkey){
				WebDriver driver=null;
				driver=SeleniumUtil.createFireFox(driver,firefox);
				System.out.println(key);
				ArrayList<HashMap<String, Object>> list=SeleniumUtil.SeachKeyWordByClick(driver, url, key, search, button, purl);
				if(!list.isEmpty()){
					mongo.upsetManyMapByTableName(list, "gd_weixin");	
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
	
	
//	public static void test(){
//	        System.setProperty("webdriver.chrome.driver","C:/Program Files (x86)/Google/Chrome/Application/chrome.exe");
//	        WebDriver webDriver = new ChromeDriver();
//	        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
//	        webDriver.manage().timeouts().setScriptTimeout(18, TimeUnit.SECONDS);
//	        webDriver.manage().timeouts().implicitlyWait(18,  TimeUnit.SECONDS);
//	        webDriver.manage().window().maximize();
//	        webDriver.get("http://www.baidu.com");
//	        waitTime(2000);
//	        WebElement kw = webDriver.findElement(By.id("kw"));
//	        kw.sendKeys("暗算");
//	        WebElement su = webDriver.findElement(By.id("su"));
//	        su.click();
//	        //webDriver.close();
//	        System.out.println("Hello World!");
//	}
	
	public static ArrayList<HashMap<String, Object>> parseList(String html,String key){
		Object doc=IKFunction.JsoupDomFormat(html);
		int num=IKFunction.jsoupRowsByDoc(doc, ".news-list>li");
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		for(int i=0;i<num;i++){
			String timestr=IKFunction.jsoupListAttrByDoc(doc, ".news-list>li>div.txt-box>div.s-p","t",i);
			String time=IKFunction.timeFormat(timestr);
			if(!IKFunction.timeOK(time)){
				continue;
			}
			String title=IKFunction.jsoupTextByRowByDoc(doc, ".news-list>li>div.txt-box>h3>a", i);
			String durl=IKFunction.jsoupListAttrByDoc(doc, ".news-list>li>div.txt-box>h3>a","href",i);
			String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(dhtml)&&IKFunction.htmlFilter(dhtml, "#js_content")){
				HashMap<String, Object> map=new HashMap<String, Object>();
				Document ddoc=Jsoup.parse(dhtml);
				String name=ddoc.select("#post-user").text();
				Elements pagenode=ddoc.select("#js_content");
				String contxml =ddoc.select("#js_content").get(0).outerHtml();
				Elements p=pagenode.select("p");
				int num1 = p.size();
				ArrayList<HashMap<String, Object>> contList = new ArrayList<HashMap<String,Object>>();
				HashMap<String, Object> mapt = new HashMap<String, Object>();
				mapt.put("cont", title);
				contList.add(mapt);
				for(int c = 0;c<num1;c++){
					String  txt = p.get(c).text();
					if (!StringUtil.isEmpty(txt)) {
						HashMap<String, Object> map21 = new HashMap<String, Object>();
						map21.put("cont", txt);
						contList.add(map21);
						}
				}
				map.put("id", IKFunction.md5(title + name));
				map.put("source", "搜狗微信");
				map.put("name", name);
				map.put("contentlist", contList);
				map.put("url", durl);
				map.put("contenthtml", contxml);
				map.put("title", title);
				map.put("keyword", key);
				map.put("time", time);
				map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				list.add(map);
				waitTime(3000);
			}
		}
		return list;
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
