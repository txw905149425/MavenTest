package com.test.MongoMaven.selenium;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

//import com.gaodig.bigcrawler.parseAuthCode.client.ParseImage;
import com.test.MongoMaven.uitil.PageUrl;
//import com.gaodig.bigcrawler.geetest.UsedProxyer;
public class MyAjaxClientUtils {
/*
public static void wait4AjaxLoad(Integer ajaxSleepInterval) {
		if (ajaxSleepInterval != 0) {
			try {
				Thread t = Thread.currentThread();
				t.sleep(ajaxSleepInterval);
			} catch (Exception ex) {
			}
		}
	}*/
	public static String clickSearchKeywordByAuthCode(WebDriver driver,String url,String searchValue,String searchInputBoxCS,String searchButtonCS,String authCodeSelector,String authCodeInputBoxCS,String authCodeSubmitBubttonCS ,PageUrl curpageUrl) {
		 driver.get(url);
		 
		 try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		 searchValue = "甘肃大学";
//		 searchInputBoxCS = "#text_query";
//		 searchButtonCS = "#input_img";
//		 authCodeSelector = "#code_";
//		 authCodeInputBoxCS = "#text_code";
//		 authCodeSubmitBubttonCS = "#btn_search";
		Actions action = new Actions(driver);
//		WebElement searchInputBox = driver.findElement(By.ByCssSelector.cssSelector(searchInputBoxCS));// gansu
		WebElement searchInputBox =	waitforLoad(searchInputBoxCS,driver,0);
		action.sendKeys(searchInputBox, searchValue).perform();
		WebElement searchButton = driver.findElement(By.ByCssSelector.cssSelector(searchButtonCS));// #text_query//guangdong
		try{
		action.click(searchButton).perform();
		}catch(Exception ex){
			 clickByJS(driver,searchButton);
		}
		TakesScreenshot ts = (TakesScreenshot) driver;
		byte[] a = ts.getScreenshotAs(OutputType.BYTES);
		ByteArrayInputStream ina = new ByteArrayInputStream(a);
		BufferedImage imagea = null;
		try {
			imagea = ImageIO.read(ina);
		} catch (IOException e) {
			e.printStackTrace();
		}
		WebElement authcode = driver.findElement(By.ByCssSelector.cssSelector(authCodeSelector));
		int startX = authcode.getLocation().x;
		int startY = authcode.getLocation().y;
		imagea = imagea.getSubimage(startX, startY, authcode.getRect().width, authcode.getRect().height);
		WebElement authCodeInput = driver.findElement(By.ByCssSelector.cssSelector(authCodeInputBoxCS));
		WebElement submit_button = driver.findElement(By.ByCssSelector.cssSelector(authCodeSubmitBubttonCS));
		try{
			action.click(submit_button).perform();
		}catch (Exception es){
			 clickByJS(driver,searchButton);
		}
		Set<String> windows = driver.getWindowHandles();
		String cur_windows = driver.getWindowHandle();
		for(String s:windows){
			if(s.equalsIgnoreCase(cur_windows)){
				continue;
			}else{
				driver.switchTo().window(s);
				break;
			}
		}
		String html =	driver.getPageSource();
		curpageUrl.curPageUrl = driver.getCurrentUrl();
		return html;
	}
	
	public static String SeachKeyWordByClick(WebDriver driver,String url,String searchValue,String searchInputBoxCS,String searchButtonCS,PageUrl curPageUrl){
		driver.get(url);		
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
			searchButton = driver.findElement(By.ByCssSelector.cssSelector(searchButtonCS));//#text_query//guangdong
			try{
				action.click(searchButton).perform();
			}catch(Exception ex){
				clickByJS(driver,searchButton);
			}
		}
	
		Set<String> windows = driver.getWindowHandles();
		String cur_windows = driver.getWindowHandle();
		for(String s:windows){
			if(s.equalsIgnoreCase(cur_windows)){
				continue;
			}else{
				driver.switchTo().window(s);
				break;
			}
		}
		String html =	driver.getPageSource();
		curPageUrl.curPageUrl = driver.getCurrentUrl();
		return html;
	}
		public static WebElement findElementByXpath(WebDriver driver,String selector){
			selector = selector.substring(5);
			WebElement searchButton = driver.findElement(By.ByXPath.xpath(selector));
			return searchButton;
		}
	   public static void clickByJS(WebDriver driver,	WebElement searchButton ){
		   JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].click();", searchButton);
	   }
	   
	public static WebElement waitforLoad(final String url, WebDriver driver, final int row) {
		if (!url.startsWith("http")) {
			try {
				new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver d) {
						if (url.startsWith("XPATH")) {
							String url_tmp = url.substring(5);
							return d.findElement(By.ByXPath.xpath(url_tmp)).isDisplayed();
						} else {
							List<WebElement> list_WebElement = d.findElements(By.ByCssSelector.cssSelector(url));
							return list_WebElement.get(row).isDisplayed();
						}
					}
				});
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		WebElement clickButton = null;
		List<WebElement> list_WebElement = new ArrayList<WebElement>();
		if (url.startsWith("XPATH")) {
			String url_tmp = url.substring(5);
			clickButton = driver.findElement(By.ByXPath.xpath(url_tmp));
		} else {
			list_WebElement = driver.findElements(By.ByCssSelector.cssSelector(url));
			if (list_WebElement.size() > 0) {
				clickButton = list_WebElement.get(row);
			}
		}
		return clickButton;
	}
}
