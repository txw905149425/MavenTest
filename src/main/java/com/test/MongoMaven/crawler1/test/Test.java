package com.test.MongoMaven.crawler1.test;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.PostData;

public class Test {
	public static void main(String[] args) {
		
		String url="http://group.zbmf.com/async/getgrouppage/?type=&page=1";
//		Accept-Encoding: gzip, deflate
//		Cookie: PHPSESSID=7isptfku6hg0551u18nd18hk47; wechat_id=1; business_id=1925
		HashMap<String, String> map1=new HashMap<String, String>();
//		map1.put("Host", "ax.huaxuntg.com");
//		map1.put("Connection", "keep-alive");
//		map1.put("Upgrade-Insecure-Requests", "1");
//		map1.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; HUAWEI M2-801W Build/HUAWEIM2-801W; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.49 MQQBrowser/6.2 TBS/043305 Safari/537.36 MicroMessenger/6.5.10.1080 NetType/WIFI Language/zh_CN");
//		map1.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/wxpic,image/sharpp,*/*;q=0.8");
//		map1.put("Accept-Language", "zh-CN,en-US;q=0.8");
//		map1.put("Cookie", "PHPSESSID=omrf5r0n59trodoftp49v3at75; wechat_id=1; business_id=1925");
//							PHPSESSID=7isptfku6hg0551u18nd18hk47; wechat_id=1; business_id=1925
		String html=HttpUtil.getHtml(url, map1, "utf8", 1, new HashMap<String, String>()).get("html");
		System.out.println(html);
		Object json=IKFunction.jsonFmt(html);
		Object doc=IKFunction.keyVal(json, "html");
		System.out.println(doc);
		
		
	}
}
