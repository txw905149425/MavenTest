package com.test.MongoMaven.crawlerxg.test;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.PostData;

public class Test {
	public static void main(String[] args) throws ClientProtocolException, IOException {
		String url="http://msapiprod.williamoneil.cn/MSLocal/ASHARES/portfolio/frontPageDigest";
		HashMap<String, String> map=new HashMap<String, String>();
//		map.put("Accept", "application/json");
//		map.put("X-Requested-With", "XMLHttpRequest");
		map.put("Content-Type", "application/x-www-form-urlencoded");
//		map.put("Host", "m.jihegupiao.com");
//		map.put("User-Agent", "okhttp/3.8.1");
//		map.put("u_version", "2.6.2");
		PostData post=new PostData();
		String json="accessKey=188b4383-b6ef-4f30-a443-49d6769597e1&lang=zh_CN&freq=Daily";
		String html=post.postHtml(url, map, json, "utf8", 1);
		System.out.println(html);
		
		
	}
}
