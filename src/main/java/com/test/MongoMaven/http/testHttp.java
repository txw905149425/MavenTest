package com.test.MongoMaven.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.test.MongoMaven.uitil.HttpUtil;

public class testHttp {
	
		public static void main(String[] args) {
			Map<String, String> resultMap=null;
			HttpUtil ht=new HttpUtil();
			String url="http://blog.cnstock.com/ShowBlogger.aspx?__VIEWSTATE=%2FwEPDwUKMTc4NTU4OTg0Mw8WBB4GaXNiZXN0BQE1Hgx1c2VyX2NsYXNzaWRlFgICAw9kFgICAQ8PFgIeC1JlY29yZGNvdW50AtAPZGRk0csybRUnt60IYlZHWRFpBufw0cI%3D&__VIEWSTATEGENERATOR=BFBE5DDB&__EVENTTARGET=pagerQuestion&__EVENTARGUMENT=4&pagerQuestion_input=1";
			HashMap<String, String>  map =new HashMap<String, String>();
//			map.put("Cookie", "emstat_bc_emcount=24103958402492106440; st_pvi=13078228188975; HAList=a-sz-002525-%u80DC%u666F%u5C71%u6CB3; em_hq_fls=old; ADVS=34e5f695bc8a28; ASL=17233,rroio,b7d68760b7d6876fb7d68769858231d9af0d3dfd; st_si=46461112729587; emstat_ss_emcount=6_1488983760_227369416; ADVC=34402c9556f3e0");
			try {
				resultMap=ht.getHtml(url, map, "", 1,new HashMap<String, String>());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String cookie=resultMap.get("setCookie").split("path=")[0].trim();
			System.out.println(resultMap.get("setCookie"));
			String html=resultMap.get("html");
//			Document doc=Jsoup.parse(html);
//			String tmp=doc.select("input[id=__VIEWSTATE]").get(0).attr("value");
//			String tmp1=doc.select("input[id=__VIEWSTATEGENERATOR]").get(0).attr("value");
			System.out.println(html);
//			System.out.println(tmp1);
			
//			ArrayList<NameValuePair> list=new ArrayList<NameValuePair>();
//			list.add(new BasicNameValuePair("__VIEWSTATE",tmp));
//			list.add(new BasicNameValuePair("__VIEWSTATE",tmp));
//			list.add(new BasicNameValuePair("__EVENTTARGET","pagerQuestion"));
//			list.add(new BasicNameValuePair("__EVENTARGUMENT","2"));
//			list.add(new BasicNameValuePair("pagerQuestion_input","1"));
//			map.put("Cookie", "__FTabcjffgh=2016-8-25-17-7-35; __NRUabcjffgh=1472116055431; ASP.NET_SessionId=y3zadv45hqmzsd551ks13i55; __RECabcjffgh=1; __RTabcjffgh=2017-3-8-16-0-57; CNZZDATA1261332431=870437109-1488956017-https%253A%252F%252Fwww.baidu.com%252F%7C1488959483; Hm_lvt_5f1ddd842219521824ad49f82d8a712c=1488958039,1488960058; Hm_lpvt_5f1ddd842219521824ad49f82d8a712c=1488964724");
//			map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//			map.put("Accept-Language", "zh-CN,zh;q=0.8");
//			map.put("Cache-Control", "max-age=0");
//			map.put("Content-Type", "application/x-www-form-urlencoded");
//			map.put("Cache-Control", "max-age=0");
//			map.put("Host", "blog.cnstock.com");
//			map.put("Origin", "http://blog.cnstock.com");
//			map.put("Referer", "http://blog.cnstock.com/ShowBlogger.aspx");
//			map.put("Upgrade-Insecure-Requests", "1");
//			try {
//				html=ht.postHtml(url, map, list, 1000, 1);
//			} catch (ClientProtocolException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			System.out.println(html);
			
			
//			Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
//					Accept-Encoding:gzip, deflate
//					Accept-Language:zh-CN,zh;q=0.8
//					Cache-Control:max-age=0
//					Connection:keep-alive
//					Content-Length:246
//					Content-Type:application/x-www-form-urlencoded
//					Cookie:__FTabcjffgh=2016-8-25-17-7-35; __NRUabcjffgh=1472116055431; ASP.NET_SessionId=y3zadv45hqmzsd551ks13i55; __RECabcjffgh=1; __RTabcjffgh=2017-3-8-16-0-57; CNZZDATA1261332431=870437109-1488956017-https%253A%252F%252Fwww.baidu.com%252F%7C1488959483; Hm_lvt_5f1ddd842219521824ad49f82d8a712c=1488958039,1488960058; Hm_lpvt_5f1ddd842219521824ad49f82d8a712c=1488964724
//					Host:blog.cnstock.com
//					Origin:http://blog.cnstock.com
//					Referer:http://blog.cnstock.com/ShowBlogger.aspx
//					Upgrade-Insecure-Requests:1
//					User-Agent:Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36
			
			
		}
		
}
