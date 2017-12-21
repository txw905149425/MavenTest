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
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.test.MongoMaven.uitil.PageUrl;

public class Test {
	public static void main(String[] args) throws Exception {

//        //设置必要参数
//            DesiredCapabilities dcaps = new DesiredCapabilities();
//            //ssl证书支持
//            dcaps.setCapability("acceptSslCerts", true);
//            //截屏支持
//            dcaps.setCapability("takesScreenshot", true);
//            //css搜索支持
//            dcaps.setCapability("cssSelectorsEnabled", true);
//            //js支持
//            dcaps.setJavascriptEnabled(true);
//            //驱动支持
//            dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,"D:/phantomjs/phantomjs-2.1.1-windows/bin");
//            //创建无界面浏览器对象
//            PhantomJSDriver driver = new PhantomJSDriver(dcaps);
			WebDriver driver =  null;
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
			if (driver != null) {
				driver.quit();
				driver = null;
			}
			driver = new PhantomJSDriver(caps);
			driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
			driver.manage().timeouts().setScriptTimeout(60, TimeUnit.SECONDS);
			driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
    try {
           // 让浏览器访问空间主页
            String url="https://www.baidu.com/";
            driver.get(url);
   		 
   		 try {
   			Thread.sleep(1000);
   		} catch (InterruptedException e1) {
   			// TODO Auto-generated catch block
   			e1.printStackTrace();
   		}
   		String searchValue = "甘肃大学";
   		String searchInputBoxCS = "#kw";
   		String searchButtonCS = "#su";
   		String authCodeSelector = "#code_";
   		String authCodeInputBoxCS = "#text_code";
   		String authCodeSubmitBubttonCS = "#btn_search";
   		Actions action = new Actions(driver);
   		WebElement searchInputBox = driver.findElement(By.ByCssSelector.cssSelector(searchInputBoxCS));// gansu
//   		WebElement searchInputBox =	waitforLoad(searchInputBoxCS,driver,0);
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
   		PageUrl curpageurl=new PageUrl();
   		curpageurl.curPageUrl = driver.getCurrentUrl();
   		System.out.println(html);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally{
                //关闭并退出浏览器
                driver.close();
                driver.quit();
            }
    
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
	
	public static void clickByJS(WebDriver driver,	WebElement searchButton ){
		   JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].click();", searchButton);
	   }
	
}
