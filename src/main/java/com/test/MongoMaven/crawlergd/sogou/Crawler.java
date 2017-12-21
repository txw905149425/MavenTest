package com.test.MongoMaven.crawlergd.sogou;

import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
	try{	       
			HashMap<String, String> map1=new HashMap<String, String>();
			map1.put("Accept", "application/json, text/javascript, */*; q=0.01");
			map1.put("Cookie", "SUV=1503918688903464; pgv_pvi=5592766464; ABTEST=0|1503918676|v1; weixinIndexVisited=1; CXID=C250BBC6E5E39E2B272388BD8431B5FF; ad=eZllllllll2BIyWqlllllVufGL7lllllrZYppkllll9lllllROxlw@@@@@@@@@@@; SUID=1A0606AF3120910A0000000059A3FA51; SNUID=986F01A50B0F524741921E630B0F8A29; IPLOC=CN4301; ld=tZllllllll2BsSG4lllllVud9ZtlllllrZHO6Zllll9lllll9llll5@@@@@@@@@@; JSESSIONID=aaafB20nluw4I_1dwAr5v; sct=73");
			map1.put("Host", "weixin.sogou.com");
			String today=IKFunction.getTimeNowByStr("yyyy-MM-dd");
			
			String[] codelist={"国科微300672","安阳钢铁600569","凌钢股份600231"};
		for(int xx=0;xx<codelist.length;xx++){
			String tmp=codelist[xx];
			tmp=IKFunction.charEncode(tmp,"utf8");
			map1.put("Referer",  "http://weixin.sogou.com/weixin?usip=&query="+tmp+"&ft=&tsn=1&et=&interation=&type=2&wxid=&ie=utf8");
			String url="http://weixin.sogou.com/weixin?usip=&query="+tmp+"&ft="+today+"&tsn=5&et="+today+"&interation=&type=2&wxid=&page=1&ie=utf8";
//			System.out.println(url);
			String html=HttpUtil.getHtml(url, map1, "utf8", 1, new HashMap<String, String>()).get("html");
//			System.out.println(html);
			int page=getPage(html);
			if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, ".news-list>li")){
					for(int p=1;p<=page;p++){
						url="http://weixin.sogou.com/weixin?usip=&query="+tmp+"&ft=&tsn=1&et=&interation=&type=2&wxid=&ie=utf8&page="+p;
						String lhtml=HttpUtil.getHtml(url, map1, "utf8", 1, new HashMap<String, String>()).get("html");
						ArrayList<HashMap<String, Object>> list=parseList(lhtml,url);
						if(!list.isEmpty()){
							mongo.upsetManyMapByTableName(list, "lzx_viewpoint");
						}
					}
			}else{
				System.err.println("屏蔽");
			}
		}
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	}
	public static int getPage(String html){
		Object doc=IKFunction.JsoupDomFormat(html);
		int page=1;
	  if(IKFunction.htmlFilter(html,"div.mun")){
		String text=IKFunction.jsoupTextByRowByDoc(doc, "div.mun", 0);
		page=Integer.parseInt(IKFunction.regexp(text, "(\\d+)"));
		if(page%10==0){
			page=page/10;
		}else{
			page=page/10+1;
		}
	  }
	  if(page>10){
		  page=10;
	  }
		return page;
	}
	
	public static ArrayList<HashMap<String, Object>> parseList(String html,String url){
		Object doc=IKFunction.JsoupDomFormat(html);
		int num=IKFunction.jsoupRowsByDoc(doc, ".news-list>li");
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		for(int i=0;i<num;i++){
			String timestr=IKFunction.jsoupListAttrByDoc(doc, ".news-list>li>div.txt-box>div.s-p","t",i);
			String time=IKFunction.timeFormat(timestr);
			if(!IKFunction.timeOK(time)){
				continue;
			}
			String title=IKFunction.jsoupTextByRowByDoc(doc, ".news-list>li>div.txt-box>h3>a", i);
			String durl=IKFunction.jsoupListAttrByDoc(doc, ".news-list>li>div.txt-box>h3>a","href",i);
			String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(dhtml)&&IKFunction.htmlFilter(dhtml, "#js_content")){
				HashMap<String, Object> map=new HashMap<String, Object>();
				Document ddoc=Jsoup.parse(dhtml);
				String name=ddoc.select("#post-user").text();
				Elements pagenode=ddoc.select("#js_content>p");
				String contxml =ddoc.select("#js_content").get(0).outerHtml();
				int num1 = pagenode.size();
				ArrayList<HashMap<String, Object>> contList = new ArrayList<HashMap<String,Object>>();
				for(int c = 0;c<num1;c++){
					String  txt = pagenode.select("p").get(c).text();
					if (!StringUtil.isEmpty(txt)) {
						HashMap<String, Object> map21 = new HashMap<String, Object>();
						map21.put("cont", txt);
						contList.add(map21);
						}
				}
				map.put("id", IKFunction.md5(title + name));
				map.put("source", "搜狗微信");
				map.put("name", name);
				map.put("contentlist", contList);
				map.put("url", durl);
				map.put("contenthtml", contxml);
				map.put("title", title);
				map.put("time", time);
				map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				list.add(map);
			}
		}
		return list;
	}
	
	
}
