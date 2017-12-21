package com.test.MongoMaven.selenium;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.TemporaryFilesystem;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.apache.commons.io.FileUtils;

import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.PageUrl;
import com.test.MongoMaven.uitil.StringUtil;

public class SeleniumUtil {
	static int s=1;
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
//			System.setProperty("webdriver.firefox.bin","D:/Program Files (x86)/Mozilla Firefox/firefox.exe");//C:/Program Files (x86)/Mozilla Firefox/firefox.exe
//			FirefoxProfile firefoxProfile = new FirefoxProfile();
//			C:/Users/lenovo/AppData/Roaming/Mozilla/Firefox/Profiles/rdjptgqr.default
//			C:/Users/jcj/AppData/Roaming/Mozilla/Firefox/Profiles/e82gk26s.default  	//微信专用
//			C:/Users/jcj/AppData/Roaming/Mozilla/Firefox/Profiles/yifrey8u.default		//fh	
//			firefoxProfile.setPreference("network.proxy.share_proxy_settings",true);
//			firefoxProfile.setPreference("network.proxy.type",1);
//			firefoxProfile.setPreference("network.proxy.http","46.26.216.69");
//			firefoxProfile.setPreference("network.proxy.http_port",55012);
//			firefoxProfile.setPreference("network.proxy.no_proxies_on","localhost");
			defaultDriver = new FirefoxDriver(firefoxProfile);
			defaultDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			defaultDriver.manage().timeouts().setScriptTimeout(18, TimeUnit.SECONDS);
			defaultDriver.manage().timeouts().implicitlyWait(18,  TimeUnit.SECONDS);
			s++;
			//defaultDriver.manage().window().setSize(new Dimension(1124, 1024));
			//isDriverCached = false;
			//driver.manage().window().setSize(new Dimension(1124, 2024));
			return defaultDriver;
	}

	public static WebDriver creatPhantom(WebDriver driver){
		if (driver!= null){
			return driver;
		}
		Capabilities caps = new DesiredCapabilities();
		((DesiredCapabilities) caps).setJavascriptEnabled(true);
		((DesiredCapabilities) caps).setCapability("loadImages", false);
		((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
		((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "D:/phantomjs/phantomjs-2.1.1-windows/bin/phantomjs.exe");
		((DesiredCapabilities) caps).setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
		// 具体参数设置可参考http://phantomjs.org/api/command-line.html
		ArrayList<String> cliArgsCap = new ArrayList<String>();
		cliArgsCap.add("--disk-cache=true");
		cliArgsCap.add("--load-images=false");
		cliArgsCap.add("--ignore-ssl-errors=true");
		// cliArgsCap.add("--ssl-protocol=any");
		cliArgsCap.add("--cookies-file=test.cookies");
		((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
		driver = new PhantomJSDriver(caps);
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(60, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
//		driver.manage().window().setSize(new Dimension(600, 700));
		return driver;
	
	}
	
	
	public static ArrayList<HashMap<String, Object>> SeachKeyWordByClick(WebDriver driver,String url,String searchValue,String searchInputBoxCS,String searchButtonCS,PageUrl curPageUrl) throws IOException{
		driver.get(url);	
		waitTime(1500);
		ArrayList<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
		Actions action = new Actions(driver);
		WebElement searchInputBox = null;
		if(searchInputBoxCS.startsWith("XPATH")){
			searchInputBox = findElementByXpath(driver,searchInputBoxCS);
		}else{
			searchInputBox = driver.findElement(By.ByCssSelector.cssSelector(searchInputBoxCS));//gansu
		}
		action.sendKeys(searchInputBox, searchValue).perform();
		WebElement searchButton  = null;
		if(searchButtonCS.startsWith("ENTER")){
			action.sendKeys(Keys.ENTER).perform();
		}else if(searchButtonCS.startsWith("XPATH")){
			 searchButton = findElementByXpath(driver,searchButtonCS);
				try{
					action.click(searchButton).perform();
				}catch(Exception ex){
					clickByJS(driver,searchButton);
				}
		}else{
			driver.findElement(By.ByCssSelector.cssSelector(searchButtonCS)).click();//#text_query//guangdong
		}
		waitTime(2000);
//		File screenshot12 = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
//		FileUtils.copyFile(screenshot12, new File("D:test12.jpg"));
		if(url.contains("http://weixin.sogou.com")){
			searchButton =driver.findElement(By.ByCssSelector.cssSelector("#tool_show>a"));
			try{
				clickByJS(driver,searchButton);
			}catch(Exception ex){
				action.click(searchButton).perform();
			}
			waitTime(500);
			searchButton =driver.findElement(By.ByCssSelector.cssSelector("#time"));
			try{
				clickByJS(driver,searchButton);
			}catch(Exception ex){
				action.click(searchButton).perform();
			}
			waitTime(500);
			searchButton =driver.findElement(By.ByCssSelector.cssSelector("#time_enter"));
			try{
				clickByJS(driver,searchButton);
			}catch(Exception ex){
				action.click(searchButton).perform();
			}
			waitTime(500);
		String html =driver.getPageSource();
		int page=getPage(html);
		try{
			for(int i=1;i<page;i++){
				int size=getHtmlSize(html);
				System.out.println(i+" ===> "+size);
				String handler = driver.getWindowHandle();
				for(int j=1;j<=size;j++ ){
					waitTime(3000);
					Actions action1 = new Actions(driver);
					String dcss="ul.news-list>li:nth-child("+j+")>div.txt-box>h3>a";
					WebElement  button=null;
					try{
					 button =driver.findElement(By.ByCssSelector.cssSelector(dcss));
					 action1.click(button).perform();
					}catch(NoSuchElementException es){
						continue;
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
					String dhtml =	driver.getPageSource();
					String durl =	driver.getCurrentUrl();
					HashMap<String, Object> map=parseDetail(dhtml, durl);
					if(!map.isEmpty()){
						map.put("key", searchValue);	
						list.add(map);
					}
					waitTime(2000);
					driver.close();
					driver.switchTo().window(handler);
//					String cur_windows1 = driver.getWindowHandle();
//					Set<String> windows1 = driver.getWindowHandles();
//					for(String s:windows1){
//						if(s.equalsIgnoreCase(cur_windows1)){
//							continue;
//						}else{
//							driver.switchTo().window(s);
//							break;
//						}
//					}
				}
				waitTime(1000);
				searchButton =driver.findElement(By.ByCssSelector.cssSelector("#sogou_next"));
				action.click(searchButton).perform();
				waitTime(5000);
				html =	driver.getPageSource();
			}
		}catch(Exception e){
			e.printStackTrace();
//			return list;
		}
	}
		return list;
	}
	
	
	
	
	
	public static int getPage(String html){
		Object doc=IKFunction.JsoupDomFormat(html);
		int page=1;
	  if(IKFunction.htmlFilter(html,"div.mun")){
		String text=IKFunction.jsoupTextByRowByDoc(doc, "div.mun", 0);
		page=Integer.parseInt(IKFunction.regexp(text, "(\\d+)"));
		if(page%10==0){
			page=page/10;
		}else{
			page=page/10+1;
		}
	  }
	  if(page>6){
		  page=6;
	  }
		return page;
	}
	
	public static int getHtmlSize(String html){
		Object doc=IKFunction.JsoupDomFormat(html);
		int size=1;
	  if(IKFunction.htmlFilter(html,".news-list>li>div.txt-box>h3>a")){
		  size=IKFunction.jsoupRowsByDoc(doc, ".news-list>li>div.txt-box>h3>a");
	  }
		  return size;
	}
	
	
	 public static void clickByJS(WebDriver driver,	WebElement searchButton ){
		   JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].click();", searchButton);
	   }
	
	 public static WebElement findElementByXpath(WebDriver driver,String selector){
			selector = selector.substring(5);
			WebElement searchButton = driver.findElement(By.ByXPath.xpath(selector));
			return searchButton;
		}
	 
	  public static void waitTime(int time) {
		  try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	  
	  public static HashMap<String, Object> parseDetail(String dhtml,String url){
		  HashMap<String, Object> map=new HashMap<String, Object>();
		  if(!StringUtil.isEmpty(dhtml)&&IKFunction.htmlFilter(dhtml, "#js_content")){
				Document ddoc=Jsoup.parse(dhtml);
				String title=ddoc.select("#activity-name").text();
				String time=ddoc.select("#post-date").text();
				String name=ddoc.select("#post-user").text();
				Elements pagenode=ddoc.select("#js_content>*");
				String contxml =ddoc.select("#js_content").get(0).outerHtml();
				int num1 = pagenode.size();
				ArrayList<HashMap<String, Object>> contList = new ArrayList<HashMap<String,Object>>();
				HashMap<String, Object> mapt = new HashMap<String, Object>();
				mapt.put("cont", title);
				contList.add(mapt);
				for(int c = 0;c<num1;c++){
					String  txt = pagenode.get(c).text();
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


