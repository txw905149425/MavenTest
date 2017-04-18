package com.test.MongoMaven.crawler1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import com.test.MongoMaven.uitil.HttpUtil;

public class Test {
	public static void main(String[] args) throws ClientProtocolException, IOException {
		String url="http://proxy.finance.qq.com/group/newstockgroup/Live/getSquareList3?check=0&_appName=android&_dev=HM+NOTE+1LTE&_devId=28ec48936b5a9d2b42feb837340121c6d4a090b2&_mid=28ec48936b5a9d2b42feb837340121c6d4a090b2&_md5mid=7473A582ACF122D5CF8466B2C6B21A5E&_omgid=2dc4062302a6154fc7d804bd8d3d2b5dbc5a001021070b&_omgbizid=a487cdcc46702543c7f8511f43c222dfd58f014021230a&_appver=5.4.1&_ifChId=119&_screenW=720&_screenH=1280&_osVer=4.4.4&_uin=10000&_wxuin=20000&_net=WIFI&__random_suffix=37667";
		url="http://183.57.48.75/group/newstockgroup/GroupChat/getGroupChatMsg?check=0&_appName=android&_dev=HM+NOTE+1LTE&_devId=28ec48936b5a9d2b42feb837340121c6d4a090b2&_mid=28ec48936b5a9d2b42feb837340121c6d4a090b2&_md5mid=7473A582ACF122D5CF8466B2C6B21A5E&_omgid=2dc4062302a6154fc7d804bd8d3d2b5dbc5a001021070b&_omgbizid=a487cdcc46702543c7f8511f43c222dfd58f014021230a&_appver=5.4.1&_ifChId=119&_screenW=720&_screenH=1280&_osVer=4.4.4&_uin=10000&_wxuin=20000&_net=WIFI&__random_suffix=16398";
		HashMap<String, String> map= new HashMap<String, String>();
//		Referer: http://zixuanguapp.finance.qq.com
//			Accept-Encoding: gzip
//			User-Agent: Dalvik/1.6.0 (Linux; U; Android 4.4.4; HM NOTE 1LTE MIUI/V8.1.1.0.KHICNDI)
//			Host: proxy.finance.qq.com
//			Connection: Keep-Alive
//			Content-Type: application/x-www-form-urlencoded
//			Content-Length: 16
		map.put("Referer", "http://zixuanguapp.finance.qq.com");
//		map.put("Accept-Encoding", "gzip");
		map.put("User-Agent", "Dalvik/1.6.0 (Linux; U; Android 4.4.4; HM NOTE 1LTE MIUI/V8.1.1.0.KHICNDI)");
//		map.put("Host", "proxy.finance.qq.com");
		map.put("Content-Type", "application/x-www-form-urlencoded");
		ArrayList<NameValuePair> list=new ArrayList<NameValuePair>();
//		list.add(new BasicNameValuePair("openid", "anonymous"));
//		v=2&begin=-1&groupChatId=201702161445120051804226&limit=30&openid=anonymous
		list.add(new BasicNameValuePair("openid", "anonymous"));
		list.add(new BasicNameValuePair("v", "2"));
		list.add(new BasicNameValuePair("begin", "-1"));
		list.add(new BasicNameValuePair("groupChatId", "201608111502480038472394"));
		list.add(new BasicNameValuePair("limit", "100"));
		String html=HttpUtil.postHtml(url, map, list, 100, 1);
		
//		Map<String, String> result= HttpUtil.getHtml(url, map, "utf8", 1);
//		String html=result.get("html");
		System.out.println(html);
	}
}
