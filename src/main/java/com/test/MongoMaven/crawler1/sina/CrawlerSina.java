package com.test.MongoMaven.crawler1.sina;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.storm.shade.org.eclipse.jetty.util.UrlEncoded;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;

//新浪理财师
public class CrawlerSina {
	 public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
		 Date d = new Date();  
	     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	     String dateNowStr = sdf.format(d); 
	     String urltmp =IKFunction.charEncode(dateNowStr,"utf8");
	 	 String url="http://licaishi.sina.com.cn/api/askList?page=null&ind_id=1&is_p=null&u_time="+urltmp+"&__t="+d.getTime();  
		 HashMap<String, String> map=new HashMap<String, String>();
		 for(int i=0;i<4;i++){
			 Map<String, String> resultMap= HttpUtil.getHtml(url, map, "utf8", 1,new HashMap<String, String>());
			 String html=resultMap.get("html");
			 List<HashMap<String, Object>> list= ParthMethod.parseList(html);
			 try {
				mongo.upsetManyMapByTableName(list, "sina_financial_planner");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 Object obj=list.get(list.size()-1).get("time");
			 urltmp=IKFunction.charEncode(obj,"utf8");
			 url="http://licaishi.sina.com.cn/api/askList?page=null&ind_id=1&is_p=null&u_time="+urltmp+"&__t="+d.getTime();  
//			 System.out.println(obj);
		 }
		 System.out.println(".......................");
	}
}
