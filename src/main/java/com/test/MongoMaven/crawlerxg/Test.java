package com.test.MongoMaven.crawlerxg;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import com.test.MongoMaven.uitil.PostData;

public class Test {
	public static void main(String[] args) {
		PostData post=new PostData();
		String url="http://shiye.gesoiner.com:8080/shiye/threeInter/stockPickInfoThreeInter.dhtml";
		HashMap<String , String> map=new HashMap<String, String>();
		map.put("Content-Type", "application/x-www-form-urlencoded");
		map.put("Host", "shiye.gesoiner.com:8080");
		map.put("Connection", "Keep-Alive");
		map.put("Cookie","JSESSIONID=7D89E39E1E286E4366DCE28BE969121E; SERVERID=d1eae3c7b2d8661f13da3e37a085d8d2|1493365050|1493364585");
		map.put("Cookie2","$Version=1");
		String data="condition=%7B%22cond_id%22%3A%22ad_vol_001%22%7D&cookie=&devType=PT_MIHM+NOTE+1LTE&orderBy=&pt=2&requestDate=2017-04-27&sortField=&user=obadhtyhkhfrm3wi43ocaskbglm&userId=obadhtyhkhfrm3wi43ocaskbglm&ver=V4.2.0&sign=1ca23acd9f5f3ae9ec7595991d0917da";
		try {
			String html=post.postHtml(url, map,data, "gbk", 2);
			System.out.println(html);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
