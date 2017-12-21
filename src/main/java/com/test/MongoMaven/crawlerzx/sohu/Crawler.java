package com.test.MongoMaven.crawlerzx.sohu;

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
			for(int i=0;i<num;i++){
				HashMap<String, Object> map=new HashMap<String, Object>();
				String time=IKFunction.getTimeNowByStr("yyyy-MM-dd")+" "+IKFunction.jsoupTextByRowByDoc(doc, "li[t]>div.left_date",i);
				time=IKFunction.timeFormat(time);
				if(!IKFunction.timeOK(time)){
					continue;
				}
				String text=IKFunction.jsoupTextByRowByDoc(doc, "li[t]>div.title",i);
				String newsClass=IKFunction.regexp(text, "【(.*)】");
				if(text.contains("点击此处查看")){
					continue;
				}
				map.put("id", IKFunction.md5(text+"搜狐"));
				map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				map.put("source", "搜狐");
				map.put("time", time);
				map.put("content", text);
				map.put("newsClass", newsClass);
				try {
					mongo.upsertMapByTableName(map,"tt_zx");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
}
