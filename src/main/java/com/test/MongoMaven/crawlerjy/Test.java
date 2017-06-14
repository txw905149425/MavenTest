package com.test.MongoMaven.crawlerjy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.PostData;

public class Test {
	
	//https://gupiao.nicaifu.com/app/game/trading
		public static void main(String[] args) {
		    PostData post=new PostData();
			String url="https://gupiao.nicaifu.com/api/stock_router/post";//data[lastId]:6478419
			String data="path=/stock/game/list_dealrecord&data[marker]=game_long&reqtoken=9f2561da1f8602889887df1860ca76ab59e59baf1c6396733d1da1cc4758f0c9";
			try {
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("Cookie", "UM_distinctid=15c3481705ab2-0b592f5382a094-38385702-1fa400-15c3481705b199; __GUID__=95660457014955299141530842493161; gr_user_id=e320c452-3917-42de-810b-def20f86fdd5; NCFTCK=oqg8tg6qpkeq1ftx3kjrc7tit1oz7lqg; CNZZDATA1258733443=1238623131-1495528309-%7C1495615253");
				map.put("Accept-Encoding","gzip, deflate, br");
				map.put("Accept-Language","zh-CN,zh;q=0.8");
				map.put("Connection","keep-alive");
				map.put("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
				map.put("Host", "gupiao.nicaifu.com");
				map.put("Origin", "https://gupiao.nicaifu.com");
				map.put("Referer", "https://gupiao.nicaifu.com/app/game/trading");
				map.put("X-Requested-With","XMLHttpRequest");
				String html=post.postHtml(url,map, data, "utf8", 1);
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
