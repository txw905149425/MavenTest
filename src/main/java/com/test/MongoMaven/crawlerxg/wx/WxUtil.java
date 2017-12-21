package com.test.MongoMaven.crawlerxg.wx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.StringUtil;

public class WxUtil {

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
				if (!txt.trim().equals("")&&txt!=null) {
					HashMap<String, Object> map21 = new HashMap<String, Object>();
					map21.put("cont", txt);
					contList.add(map21);
				}
			}
			map.put("id", title+name+time);
			map.put("name", name);
			map.put("contentlist", contList);
//			map.put("url", durl);
			map.put("contenthtml", contxml);
			map.put("title", title);
			map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
		}
		return map;
	}
	
	
	public static ArrayList<HashMap<String, Object>> parseWeixinGZH(String html,HashMap<String, String> mapd){
		ArrayList<HashMap<String, Object>> ulist=new ArrayList<HashMap<String, Object>>();
		if(StringUtil.isEmpty(html)){
			return ulist;
		}
//		System.out.println(html);
		if(html.contains("msgList = ")){
			String tmp=html.split("msgList = ")[1];
			String json=tmp.split("if\\(")[0];
			json=json.replaceAll("&quot;", "\"");
//			System.out.println(json);
			Object js=IKFunction.jsonFmt(json);
//			System.out.println(js);
			Object list=IKFunction.keyVal(js, "list");
			String name="";
			int num=IKFunction.rowsArray(list);
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(list, i);
				Object djson=IKFunction.keyVal(one, "app_msg_ext_info");
				Object tjson=IKFunction.keyVal(one, "comm_msg_info");
				Object timeobj=IKFunction.keyVal(tjson, "datetime");
				String time=IKFunction.timeFormat(timeobj.toString());
				if(!tmp(time.toString())){
					continue;
				}
				if(djson.toString().equals("")){
					if(name.equals("")){
						continue;
					}
					String cont=IKFunction.keyVal(tjson, "content").toString();
					String tcode=IKFunction.regexp(cont,"(\\d{6,})");
					if(tcode.length()!=6){
						continue;
					}
					HashMap<String, Object> map=new HashMap<String, Object>();
					ArrayList<HashMap<String, Object>> contList = new ArrayList<HashMap<String,Object>>();
					HashMap<String, Object> map21 = new HashMap<String, Object>();
					map21.put("cont", cont);
					contList.add(map21);
					map.put("id", name+time);
					map.put("name", name);
					map.put("contentlist", contList);
					map.put("time", time);
					map.put("contenthtml", "");
					map.put("title", "no title");
					map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					ulist.add(map);
				}else{
					Object multi_app_msg_item_list=IKFunction.keyVal(djson, "multi_app_msg_item_list");
					String url="";
					String title=IKFunction.keyVal(djson, "title").toString();
					if(!StringUtil.isEmpty(title)){
						if(isTarge(title)){
							url=IKFunction.keyVal(djson, "content_url").toString();
							if(!StringUtil.isEmpty(url)){
//								/s?timestamp=1509954677&amp;src=3&amp;ver=1&amp;signature=7NYtmRhYIu1bLdEjaTMpsn9wzj2q4XM8DVokbft6z*UjNKCRajmjLxtgMPLyYQh5r8QKEVgL1ULdonkJP*SVZRAMMyc2aZBvb0Rpxt5V7Y*kwcu0oG1XUOsBlcs890HEGt*yHzZLD57mEds6ZgdUb3X9kN88dChp3aCAMFAi3V8=
								url=url.replaceAll("amp;", "");
//								url=url.split("__biz=")[1];
								String durl="https://mp.weixin.qq.com"+url;
								String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, mapd).get("html");
								HashMap<String, Object> map=parseDetail(dhtml);
								name=map.get("name").toString();
								map.put("time",time);
								map.put("url",durl);
								ulist.add(map);
							}
						}
					}
					if(multi_app_msg_item_list.toString().length()>20){
						int size=IKFunction.rowsArray(multi_app_msg_item_list);
						for(int j=1;j<=size;j++){
							Object two=IKFunction.array(multi_app_msg_item_list, j);
							 title=IKFunction.keyVal(two, "title").toString();
							if(isTarge(title)){
								url=IKFunction.keyVal(two, "content_url").toString();
								if(!StringUtil.isEmpty(url)){
									url=url.replaceAll("amp;", "");
//									url=url.split("__biz=")[1];
									String durl="https://mp.weixin.qq.com"+url;
//									String durl="https://mp.weixin.qq.com/s?__biz="+url;
									String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, mapd).get("html");
									HashMap<String, Object> map=parseDetail(dhtml);
									name=map.get("name").toString();
									map.put("time",time);
									map.put("url",durl);
									ulist.add(map);
								}	
							}
							
						}
					}
		
				}
							
			}
		}
		return ulist;
	}
	
	public static boolean isTarge(String title){
		boolean flag=false;
		String snum=IKFunction.regexp(title, "(\\d+)");
		if(snum.length()==6){
			flag=true;
			return flag;
		}
		String[] str={"明日股票","领取牛股","股票精选","附股","推荐","股票池","牛股分享","股票分享","好股分享","核心参考","股票行情","股票分析","今日牛股","牛股精选","金股分享","一只","一支","一股","个股分享","交易机会","荐股","T+0","潜力两股"};
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
