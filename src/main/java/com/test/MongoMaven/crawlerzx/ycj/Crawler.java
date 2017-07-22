package com.test.MongoMaven.crawlerzx.ycj;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

//7*24小时直播 ：10分钟左右执行一次
public class Crawler {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		HashMap<String, Object > records=null;
		String url="http://www.yuncaijing.com/insider/page_1.html";
		try{
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			Document doc=Jsoup.parse(html);
			int num=IKFunction.jsoupRowsByDoc(doc, "div.tab-panel.active>ul.news-ul>li");
			for(int i=0;i<num;i++){
				Element block=doc.select("div.tab-panel.active>ul.news-ul>li").get(i);
				String time=block.select(".time>time").get(0).text();
				String tt=IKFunction.timeFormat(time);
				if(!IKFunction.timeOK(tt)){
					continue;
				}
				records=new HashMap<String, Object>();
				String title=block.select(".nc-arc-wrap>p>a").get(0).text().trim();
				String content=block.select(".nc-arc-wrap>p>span").get(0).text();
				records.put("id", IKFunction.md5(title+"云财经"));
				records.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
				records.put("newsClass", "消息");
				records.put("source", "云财经");
				records.put("title", title);
				records.put("content", content);
				records.put("time", tt);
//				mongo.upsertMapByTableName(records, "tt_json_all");
				mongo.upsertMapByTableName(records, "tt_zx");
			}
		}catch(Exception e){
					e.printStackTrace();
		}
	}
}
