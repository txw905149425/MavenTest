package com.test.MongoMaven.crawlerjy.gpyj;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;

public class Crawler {

	public static void main(String[] args) {
		HashMap<String, String> map=new HashMap<String, String>();
//		POST http://api.upbaa.com/upbaa/service.jsp?op=MobileQueryFriendRequests HTTP/1.1
//			Accept-Encoding: gzip
//			Content-Length: 81
//			Content-Type: application/x-www-form-urlencoded
//			Host: api.upbaa.com
//			Connection: Keep-Alive
//			Expect: 100-continue
//			Cookie: JSESSIONID=6CA2F2680CF3E9D8408465BFC7BA6CBB; SERVER_ID=8703f1ed-222384c9
//			Cookie2: $Version=1
		map.put("Content-Type","application/x-www-form-urlencoded");
		map.put("Host","Keep-Alive");
		map.put("Cookie","JSESSIONID=6CA2F2680CF3E9D8408465BFC7BA6CBB; SERVER_ID=8703f1ed-222384c9");
		map.put("Cookie2","$Version=1");
		String url="http://api.upbaa.com/upbaa/service.jsp?op=MobileQueryFriendRequests";
		String html=HttpUtil.getHtml(url,map, "utf8", 1, new HashMap<String, String>()).get("html");
		Object json=IKFunction.jsonFmt(html);
		Object data=IKFunction.keyVal(json, "returnCode");
		System.out.println(data);
	}
	
}
