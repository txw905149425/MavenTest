package com.test.MongoMaven.crawlertt.sohu;

import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		String url="http://q.stock.sohu.com/baopan";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "gbk", 1 ,new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, "li[t]")){
			Object doc=IKFunction.JsoupDomFormat(html);
			Document d=Jsoup.parse(html);
			int num=IKFunction.jsoupRowsByDoc(doc, "li[t]");
			try{
			for(int i=0;i<num;i++){
				HashMap<String, Object> map=new HashMap<String, Object>();
				String time=IKFunction.getTimeNowByStr("yyyy-MM-dd")+" "+IKFunction.jsoupTextByRowByDoc(doc, "li[t]>div.left_date",i);
				time=IKFunction.timeFormat(time);
				String text=IKFunction.jsoupTextByRowByDoc(doc, "li[t]>div.title",i);
				String newsClass=IKFunction.regexp(text, "【(.*)】");
				String context="";
				if(text.contains("点击此处查看")){
					String code=d.select("li[t]>div.title").get(i).select("a.announce").get(0).attr("code");
					String gid=d.select("li[t]>div.title").get(i).select("a.announce").get(0).attr("ggid");
//					http://q.stock.sohu.com/cn,gg,000982,2517269069.shtml
					String durl="http://q.stock.sohu.com/cn,gg,"+code+","+gid+".shtml";
					String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "gbk", 1 ,new HashMap<String, String>()).get("html");
					if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, "li[t]")){
						Object ddoc=IKFunction.JsoupDomFormat(dhtml);
						context=IKFunction.jsoupTextByRowByDoc(ddoc, ".part>pre", 0);
					}
				}
				if("".equals(context)){
					context=text;
				}
				map.put("id", IKFunction.md5(text+"搜狐"));
				map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				map.put("tid", text);
				map.put("source", "搜狐");
				map.put("title", text);
				map.put("time", time);
				map.put("related", text);
				map.put("content", context);
				map.put("code_list", "");
				map.put("newsClass", newsClass);
				mongo.upsertMapByTableName(map,"tt_sohu");
			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
