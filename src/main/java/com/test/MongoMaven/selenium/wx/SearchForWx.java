package com.test.MongoMaven.selenium.wx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;

import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.StringUtil;


public class SearchForWx {

	static int s=2;
	public static WebDriver createFireFox(WebDriver defaultDriver,String firefox) {
		if (defaultDriver!= null){
			return defaultDriver;
		}
		FirefoxProfile firefoxProfile=null;
		System.setProperty("webdriver.firefox.bin","C:/Program Files (x86)/Mozilla Firefox/firefox.exe");//C:/Program Files (x86)/Mozilla Firefox/firefox.exe
		if(s%2==0){
//			firefox="C:/Users/lenovo/AppData/Roaming/Mozilla/Firefox/Profiles/rdjptgqr.default";
			firefoxProfile = new FirefoxProfile(new File(firefox));
		}else{
			firefoxProfile = new FirefoxProfile();
		}
			defaultDriver = new FirefoxDriver(firefoxProfile);
			defaultDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			defaultDriver.manage().timeouts().setScriptTimeout(18, TimeUnit.SECONDS);
			defaultDriver.manage().timeouts().implicitlyWait(18,  TimeUnit.SECONDS);
			s++;
			return defaultDriver;
	}


	
	public static HashMap<String, Object> SeachKeyWordForGzh(WebDriver driver,String url,String searchValue) throws IOException{
		driver.get(url);	
		waitTime(1500);
		Actions action = new Actions(driver);
		WebElement searchInputBox = null;
		searchInputBox = driver.findElement(By.ByCssSelector.cssSelector("#query"));//gansu
		action.sendKeys(searchInputBox, searchValue).perform();
		driver.findElement(By.ByCssSelector.cssSelector(".swz2")).click();//#text_query//guangdong
		waitTime(2000);
		Actions action1 = new Actions(driver);
		String dcss="ul.news-list2>li:nth-child(1)>div>div.img-box>a";
		WebElement  button=null;
		try{
		 button =driver.findElement(By.ByCssSelector.cssSelector(dcss));
		 action1.click(button).perform();
		}catch(Exception e){
			clickByJS(driver,button);
		}
		
		String cur_windows = driver.getWindowHandle();
		Set<String> windows = driver.getWindowHandles();
		for(String s:windows){
			if(s.equalsIgnoreCase(cur_windows)){
				continue;
			}else{
				driver.switchTo().window(s);
				break;
			}
		}
		waitTime(2000);
		dcss="div.weui_msg_card:nth-child(1)>div>div>div.weui_media_bd>.weui_media_title";
		try{
			 button =driver.findElement(By.ByCssSelector.cssSelector(dcss));
			 action1.click(button).perform();
			}catch(Exception e){
				clickByJS(driver,button);
			}
//		System.out.println(driver.getPageSource());
//		System.out.println(driver.getCurrentUrl());
		String html=driver.getPageSource();
		String durl=driver.getCurrentUrl();
		HashMap<String, Object> map=parseDetail(html, durl);
		return map;
	}
	
	
	 public static void waitTime(int time) {
		  try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	 
	 public static void clickByJS(WebDriver driver,	WebElement searchButton ){
		   JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].click();", searchButton);
	   }
	 
	 public static HashMap<String, Object> parseDetail(String html,String url){
			HashMap<String, Object> map=new HashMap<String, Object>();
			if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, "#js_content")){
				Document ddoc=Jsoup.parse(html);
				String name=ddoc.select("#post-user").text();
				Elements pagenode=ddoc.select("#js_content>p");
				String title=ddoc.select("#activity-name").text();
				String time=ddoc.select("#post-date").text();
				String contxml =ddoc.select("#js_content").get(0).outerHtml();
				int num1 = pagenode.size();
				ArrayList<HashMap<String, Object>> contList = new ArrayList<HashMap<String,Object>>();
				for(int c = 0;c<num1;c++){
					String  txt = pagenode.select("p").get(c).text();
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
				map.put("url", url);
				map.put("contenthtml", contxml);
				map.put("title", title);
				map.put("time", time);
				map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
			}
			return map;
		}

	 
}
