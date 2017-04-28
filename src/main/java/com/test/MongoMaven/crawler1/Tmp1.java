package com.test.MongoMaven.crawler1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.bson.Document;

import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

public class Tmp1 {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		PostData post=new PostData();
		HashMap<String , String> map=new HashMap<String, String>();
		map.put("User-Agent", "android-async-http/1.4.3 (http://loopj.com/android-async-http)");
		map.put("Content-Type", "application/x-www-form-urlencoded");
		map.put("Host", "app.55188.com");
		List<HashMap<String , Object>> listresult=new ArrayList<HashMap<String , Object>>();
		HashMap<String , Object> result = null;
	try {
		String url="http://app.55188.com/live/live/asklist";
		String data="anchorid=472&version=3.3.3.0&pagesize=20&offset=0&loantoken=";
		String html=post.postHtml(url, map,data,"utf8", 2);
		System.out.println(html);
		
	} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
