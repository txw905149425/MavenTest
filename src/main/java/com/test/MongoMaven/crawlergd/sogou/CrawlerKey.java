package com.test.MongoMaven.crawlergd.sogou;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class CrawlerKey {


	public static void main(String[] args) throws FileNotFoundException {
		MongoDbUtil mongo=new MongoDbUtil();
	try{	       
			HashMap<String, String> map1=new HashMap<String, String>();
//			map1.put("need", "true");
//			map1.put("ip", "http-dyn.abuyun.com");
//			map1.put("port", "9020");
//			map1.put("user", "H1799393Q8VD2D1D");
//			map1.put("pwd", "089AB8F589925559");
			ArrayList<String> list=FileUtil.readFileReturn("/home/jcj/crawler/wx/wx_gzh1");
			MongoCollection<org.bson.Document>  coll=mongo.getShardConn("gd_weixin_by_gzh");
			BasicDBObject doc5 = new BasicDBObject();
	        doc5.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
			for(String tmp:list){
				String key=tmp.split("=")[0];
				String nname=tmp.split("=")[1];
				System.out.println(tmp.trim());
				doc5.put("name", nname.trim());
				MongoCursor<org.bson.Document> cursor =coll.find(doc5).batchSize(10000).noCursorTimeout(true).iterator();
				if(cursor.hasNext()){
					System.err.println("库里已经有了");
					continue;
				}
				String rtmp=IKFunction.charEncode(key.trim(),"utf8");
				String url="http://weixin.sogou.com/weixin?query="+rtmp+"&_sug_type_=&s_from=input&_sug_=n&type=1&page=1&ie=utf8";
				String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, map1).get("html");
//				System.out.println(html);
				String durl=getDurl(html, tmp);
				if(durl.length()>10){
					String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, map1).get("html");
					ArrayList<String> ulist=parseWeixinGZH(dhtml);
					if(!ulist.isEmpty()){
						for(String ddurl:ulist){
							if(ddurl.length()>24){
								String ddhtml=HttpUtil.getHtml(ddurl, new HashMap<String, String>(), "utf8", 1, map1).get("html");
								HashMap<String, Object> dmap=parseDetail(ddhtml);
			//					System.out.println(ddurl);
								dmap.put("url", ddurl);
								mongo.upsertMapByTableName(dmap,"gd_weixin_by_gzh");
							}
						}
					}
				}
			}
		  }catch(Exception e){
			  e.printStackTrace();
		  }
	}
	
	public static String getDurl(String html,String key){
		if(StringUtil.isEmpty(html)){
			return "";
		}
		Object doc=IKFunction.JsoupDomFormat(html);
		int num=IKFunction.jsoupRowsByDoc(doc, ".tit>a");
		String durl="";
		for(int i=0;i<num;i++){
			durl=IKFunction.jsoupListAttrByDoc(doc, ".tit>a","href",i);	
			break;
		}
		return durl;
	}
	
	public static ArrayList<String> parseWeixinGZH(String html){
		ArrayList<String> ulist=new ArrayList<String>();
		if(StringUtil.isEmpty(html)){
			return ulist;
		}
		if(html.contains("msgList = ")){
			String tmp=html.split("msgList = ")[1];
			String json=tmp.split("seajs.use")[0];
			Object js=IKFunction.jsonFmt(json);
//			System.out.println(js);
			Object list=IKFunction.keyVal(js, "list");
			int num=IKFunction.rowsArray(list);
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(list, i);
				Object tjson=IKFunction.keyVal(one, "comm_msg_info");
				Object time=IKFunction.keyVal(tjson, "datetime");
				if(!tmp(time.toString())){
					continue;
				}
//				if(!IKFunction.timeOK(time.toString())){
//					continue;
//				}
				Object djson=IKFunction.keyVal(one, "app_msg_ext_info");
				String title=IKFunction.keyVal(djson, "title").toString();
				if(isTarge(title)){
					String url=IKFunction.keyVal(djson, "content_url").toString();
					url=url.replaceAll("amp;", "");	
					String durl="https://mp.weixin.qq.com"+url;
					ulist.add(durl);	
				}
				
			}
			
			
			 
		}
		return ulist;
	}
	
	public static HashMap<String, Object> parseDetail(String html){
		HashMap<String, Object> map=new HashMap<String, Object>();
		if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, "#js_content")){
			Document ddoc=Jsoup.parse(html);
			String name=ddoc.select("#post-user").text();
			Elements pagenode=ddoc.select("#js_content>*");
			String title=ddoc.select("#activity-name").text();
			String time=ddoc.select("#post-date").text();
			String contxml =ddoc.select("#js_content").get(0).outerHtml();
			int num1 = pagenode.size();
			ArrayList<HashMap<String, Object>> contList = new ArrayList<HashMap<String,Object>>();
			for(int c = 0;c<num1;c++){
				String  txt = pagenode.get(c).text();
				if (!StringUtil.isEmpty(txt)) {
					HashMap<String, Object> map21 = new HashMap<String, Object>();
					map21.put("cont", txt);
					contList.add(map21);
				}
			}
			map.put("id", IKFunction.md5(title + name));
			map.put("source", "搜狗微信");
			map.put("name", name);
			map.put("type", "2");
			map.put("contentlist", contList);
//			map.put("url", durl);
			map.put("contenthtml", contxml);
			map.put("title", title);
			map.put("time", time);
			map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
		}
		return map;
	}
	
	public static boolean isTarge(String title){
		boolean flag=false;
		String snum=IKFunction.regexp(title, "(\\d+)");
		if(snum.length()==6){
			flag=true;
			return flag;
		}
		String[] str={"股票精选","附股","推荐","股票池","牛股分享"};
		for(int i=0;i<str.length;i++){
			String key=str[i];
			if(title.contains(key)){
				flag=true;
				break;
			}	
		}
		return flag;
	}
	
	public static boolean tmp(String t){
		boolean flag=false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
         long time=Long.valueOf(t+"000");
         long now=System.currentTimeMillis();
         long h=(now-time)/(1000 * 60 * 60);
         if(h<24){
        	flag=true; 
         }
         return flag;
	}
}
