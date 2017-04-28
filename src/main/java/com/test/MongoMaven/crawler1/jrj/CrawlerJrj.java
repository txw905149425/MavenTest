package com.test.MongoMaven.crawler1.jrj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;


//金融界 -问股
public class CrawlerJrj {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		String url="http://itougu.jrj.com.cn/ques/na.shtml";
		HashMap<String , String> map =new HashMap<String, String>();
		 Map<String, String> resultMap=null;
		 for(int i=1;i<11;i++){
			 if(i!=1){
				url="http://itougu.jrj.com.cn/ques/na_"+i+".shtml";
			}
			 resultMap=HttpUtil.getHtml(url, map, "utf8", 1,new HashMap<String, String>());
			 String html=resultMap.get("html");
			 List<HashMap<String, Object>> list= ParseMethod.parseList(html);
			 try {
				mongo.upsetManyMapByTableName(list, "tzj_ask_shares");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 System.out.println(".....................");
	}
}
