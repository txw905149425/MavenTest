package com.test.MongoMaven.crawlertt.gp360;

import java.util.HashMap;

import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

public class Crawler {
	public static void main(String[] args) {
		PostData post=new PostData();
		MongoDbUtil mongo=new MongoDbUtil();
		String url="https://stockapp.nicaifu.com/stock/information/get_list";//data[lastId]:6478419
		String data="data=bqORRoonURxa+LQYq+Z5ITWGMzXQOlxHZClJluZdQ0avpk74RRavlD3lQEUcGkwRaMwMkQroLWvypSHIU+IpOg8GqXr8eVDPrkxTiSFQGpPCe21IggVrEZbnQkaKBNVfqjAJws/rGToVMVUBGYzGZa8jJq4TTke4G4Luiaqf4FSXwsvqAz6llpvFUdpfz002gsF0h++fc5DYAk1U/+5tBW8Ga549UTpNvp5uoRg7RPoegWdAcjTfQLaeqeeaNVRSIWD+YQ2qjK+4SldWeHPd25Xx/zh220zy2W6ypafuIpQqUfG8OtYCs9SvlF4vkvtKdJWS6waH1BFFapXuqYLL6/rJW9AXgbNhP4C0JYxh59shaYB6MMaExduGPAb77wtuDAW4T6cCH7JsV82SBcbyc2fWI4Hq3A16CyOANzzfSJwDwj32Qe7uyU8MEvJfCv5UMaZY4gqamXE";
		try {
//			for(int i=0;i<=50;i++){
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("User-Agent", "model/HM NOTE 1LTE version/android4.4.4 okhttp/3.1.2");
				map.put("Accept-Encoding","gzip");
				map.put("Content-Type","application/x-www-form-urlencoded");
				map.put("Host", "gupiao.nicaifu.com");
				map.put("Connection", "Keep-Alive");
				String html=post.postHtml(url,map, data, "utf8", 2);
				System.out.println(html);
//				data=bqORRoonURxa+LQYq+Z5ITWGMzXQOlxHZClJluZdQ0avpk74RRavlD3lQEUcGkwRaMwMkQroLWvypSHIU+IpOg8GqXr8eVDPrkxTiSFQGpPCe21IggVrEZbnQkaKBNVfqjAJws/rGToVMVUBGYzGZa8jJq4TTke4G4Luiaqf4FSXwsvqAz6llpvFUdpfz002gsF0h++fc5DYAk1U/+5tBW8Ga549UTpNvp5uoRg7RPoegWdAcjTfQLaeqeeaNVRSIWD+YQ2qjK+4SldWeHPd25Xx/zh220zy2W6ypafuIpQqUfG8OtYCs9SvlF4vkvtKdJWS6waH1BFFapXuqYLL6/rJW9AXgbNhP4C0JYxh59shaYB6MMaExduGPAb77wtuDAW4T6cCH7JsV82SBcbyc2fWI4Hq3A16CyOANzzfSJwDwj32Qe7uyU8MEvJfCv5UMaZY4gqamXE
//			    data=bqORRoonURxa+LQYq+Z5ITWGMzXQOlxHZClJluZdQ0avpk74RRavlD3lQEUcGkwRaMwMkQroLWvypSHIU+IpOg8GqXr8eVDPrkxTiSFQGpPCe21IggVrEZbnQkaKBNVfqjAJws/rGToVMVUBGYzGZa8jJq4TTke4G4Luiaqf4FSXwsvqAz6llpvFUdpfz002gsF0h++fc5CqhMMWtNmrljeP1xhgxBEm21ng2ZSVpR45N9IP/HCvJg2u9jnxwhu8BjRSmGjRT4G7raLua6gUaTvLi57MPGaN4dk+Dw2VwG8LWMq38ywb47J2oswZdD7tdDlskd8kJfZpwJBjbnFFCCZa14P7umC4PqZsog3X1R9fc37PGT2oGStfzxj+4RcKUvuwmSaLXxI6wAuan5dMvjFkscrlPHWzFbZSuLgG54AWf9a2Q3kX/pjp8+BadqJiObrbf0y0thQ
				
				//			}
		}catch(Exception e){
			e.printStackTrace();
		}	
		
	}
}
