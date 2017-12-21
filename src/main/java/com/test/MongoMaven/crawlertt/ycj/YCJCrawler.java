package com.test.MongoMaven.crawlertt.ycj;

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

import com.test.MongoMaven.uitil.Constants;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;

//7*24小时直播 ：10分钟左右执行一次
public class YCJCrawler {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		PostData post=new PostData();
		HashMap<String, Object > records=null;
		for(int p=1;p<=10;p++){
		String url="http://www.yuncaijing.com/insider/page_"+p+".html";
				try{
					String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
					Document doc=Jsoup.parse(html);
					int num=IKFunction.jsoupRowsByDoc(doc, "div.tab-panel.active>ul.news-ul>li");
					for(int i=0;i<num;i++){
						Element block=doc.select("div.tab-panel.active>ul.news-ul>li").get(i);	
						Elements stock_list=block.select("a.stock-gray");
						if(stock_list.size()<1){
							continue;
						}
						records=new HashMap<String, Object>();
						String time=block.select(".time>time").get(0).text();
						if(time.length()==5){
							Date d = new Date();  
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
						    String dateNowStr = sdf.format(d); 
							time=dateNowStr+" "+time;
						}
						String tt=IKFunction.timeFormat(time);
						String stockstr="";
						List<HashMap<String, Object>> list1=new ArrayList<HashMap<String,Object>>();
						for(int j=0;j<stock_list.size();j++){
							Element one=stock_list.get(j);
							HashMap<String, Object> map=new HashMap<String, Object>();
							String stockName=one.attr("title");
							String stockCode=one.attr("data-wscode");
							stockstr=stockstr+stockCode+" ";
							map.put("name", stockName);
							map.put("code", stockCode);
							list1.add(map);
						}
						String title=block.select(".nc-arc-wrap>p>a").get(0).text();
						String content=block.select(".nc-arc-wrap>p>span").get(0).text();
						records.put("id", IKFunction.md5(title+"云财经"));
						records.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
						records.put("tid", title);
						records.put("newsClass", "消息");
						records.put("source", "云财经");
						records.put("title", title);
						records.put("content", content);
						records.put("time", tt);
						records.put("related", stockstr.trim());
						records.put("code_list", list1);
						JSONObject mm_data=JSONObject.fromObject(records);
					    String su=post.postHtml(Constants.ES_URI+"type=tt_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
						if(su.contains("exception")){
							System.out.println(mm_data.toString());
							System.err.println("写入数据异常！！！！  < "+su+" >");
						}
						mongo.upsertMapByTableName(records, "tt_json_all");
					}
				}catch(Exception e){
					e.printStackTrace();
				}
		}
	}
}
