package com.test.MongoMaven.crawlertt.ycj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;
//云财经-市场热点
public class Crawler {
	static MongoDbUtil mongo=new MongoDbUtil();
	public static void main(String[] args) {
		String url="http://www.yuncaijing.com/markethot/hot_news.html";
		Map<String, String> map=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>());
		String html=map.get("html");
		try{
			List<HashMap<String, Object>> list=parse(html);
			if(!list.isEmpty()){
				MongoDbUtil mongo=new MongoDbUtil();
				PostData post=new PostData();
				mongo.upsetManyMapByTableName(list, "tt_json_all");
				for(HashMap<String, Object> result:list){
					result.remove("crawl_time");
					JSONObject mm_data=JSONObject.fromObject(result);
				   String su=post.postHtml("http://wisefinance.chinaeast.cloudapp.chinacloudapi.cn:8000/wf/import?type=tt_stock_json_test",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
					if(su.contains("exception")){
						System.out.println(mm_data.toString());
						System.err.println("写入数据异常！！！！  < "+su+" >");
					}
				}
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static  List<HashMap<String, Object>> parse(String html){
		if(StringUtil.isEmpty(html)){
			return new ArrayList<HashMap<String,Object>>(); 
		}
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
		MongoCollection<org.bson.Document> collention=mongo.getShardConn("stock_code");
		Document doc=Jsoup.parse(html);
		int num=IKFunction.jsoupRowsByDoc(doc, ".content1");
		for(int i=0;i<num;i++){
			Element block=doc.select(".content1").get(i);	
			Element a=block.select(".content1>h3>a").get(0);
			String industry=a.text();  //行业
			String time=block.select(".content1>h3>span").get(0).text().replace(" 发现", "");
			String ttime=IKFunction.timeFormat(time); 
			String durl="http://www.yuncaijing.com"+block.select(".content1>a").get(0).attr("href");
			HashMap<String, Object > records=parseDetail(durl);
			String title=block.select(".content1>a").get(0).text();
			Element one=doc.select(".content2").get(i);	
			List<HashMap<String, Object>> list1=new ArrayList<HashMap<String,Object>>();
			Elements codelist=one.select("div[data-wstrcode]");
			String code_list="";
			for(int j=0;j<codelist.size();j++){
				String code1=codelist.get(j).attr("data-wstrcode");
				Object name1=collention.find(Filters.eq("id", code1)).first().get("name");
				HashMap<String, Object> map=new HashMap<String, Object>();
				code_list=code_list+code1+" ";
				map.put("name", name1);
				map.put("code", code1);
				list1.add(map);
			}
			records.put("id", title+time);
			records.put("industry",industry);
			records.put("newsClass", "消息");
			records.put("source", "云财经");
			records.put("title", title);
			records.put("time", ttime);
			records.put("related", code_list.trim());
			records.put("code_list", list1);
			list.add(records);
		
		}
		
		return list;
	}
	
	public static HashMap<String, Object> parseDetail(String url){
		if(StringUtil.isEmpty(url)){
			return new HashMap<String, Object>();
		}
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		HashMap<String, Object> map=new HashMap<String, Object>();
		if(IKFunction.htmlFilter(html, "#news-content")){
			Object doc=IKFunction.JsoupDomFormat(html);
			String content=IKFunction.jsoupTextByRowByDoc(doc, "#news-content", 0);
			map.put("content", content);
		}
		return map;
	}
	
}
