package com.test.MongoMaven.crawler1.jrj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;


//金融界 -问股   （爱投顾） 更新较快，抓取频率要高  1-2分钟左右抓一次
public class CrawlerJrj {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
	    PostData post=new PostData();
		String url="http://itougu.jrj.com.cn/ques/na.shtml";
		HashMap<String , String> map =new HashMap<String, String>();
		 Map<String, String> resultMap=null;
	try {
		 for(int i=1;i<5;i++){
			 if(i!=1){
				url="http://itougu.jrj.com.cn/ques/na_"+i+".shtml";
			 }
			 resultMap=HttpUtil.getHtml(url, map, "utf8", 1,new HashMap<String, String>());
			 String html=resultMap.get("html");
			 if(StringUtil.isEmpty(html)){
				 continue;
			 }
			 List<HashMap<String, Object>> list= ParseMethod.parseList(html);
			 if(!list.isEmpty()){
				mongo.upsetManyMapByTableName(list, "ww_ask_online_all");
			 }
		 }
	  } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	  }
	}
}
