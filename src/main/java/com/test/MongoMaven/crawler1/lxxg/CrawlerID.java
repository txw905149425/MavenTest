package com.test.MongoMaven.crawler1.lxxg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

public class CrawlerID {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		PostData post=new PostData();
		HashMap<String , String> map=new HashMap<String, String>();
		map.put("User-Agent", "android-async-http/1.4.3 (http://loopj.com/android-async-http)");
		map.put("Content-Type", "application/x-www-form-urlencoded");
		map.put("Host", "app.55188.com");
	try {
		String url="http://app.55188.com/v330/live/recommendlist";
		String data="version=3.3.3.0&total=20&offset=90&type=talk&loantoken=";
//		             version=3.3.3.0&total=20&offset=20&type=comment&loantoken=
//		              version=3.3.3.0&total=20&offset=20&type=pv&loantoken=
		String html=post.postHtml(url, map,data,"utf8", 2);
//		System.out.println(html);
		Object json=IKFunction.jsonFmt(html);
		Object datas=IKFunction.keyVal(json, "data");
		Object list=IKFunction.keyVal(datas, "list");
		int num=IKFunction.rowsArray(list);
		HashMap<String , Object> result = null;
		for(int i=1;i<=num;i++){
			result=new HashMap<String, Object>();
			Object one=IKFunction.array(list, i);
			Object id=IKFunction.keyVal(one, "anchorid");
			Object name=IKFunction.keyVal(one, "title");
			result.put("id",id);
			result.put("name",name);
			mongo.upsertMapByTableName(result, "ww_lxxg_genius_id");
			System.out.println(i);
		}
		System.out.println("........");
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
