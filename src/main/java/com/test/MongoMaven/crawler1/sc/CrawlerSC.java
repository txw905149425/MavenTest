package com.test.MongoMaven.crawler1.sc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//全球市场直播http://gmv.cjzg.cn/Mv/faq_list  和看财经一起合并了
//数据量比较少 更新较慢  抓取频率低  5分钟左右一次
public class CrawlerSC {
	
	public static void main(String[] args) {
//		String url="http://gmv.cjzg.cn/Mv/get_more.html";
		String url="https://kcj.ebdsp.com/kcj/questionlist.do?in=%7B%22user%22:%7B%22uid%22:%22175py%22%7D,%22old_currPage%22:1,%22type%22:%22hasanswer%22%7D";
		HashMap< String, String> map=new HashMap<String, String>();
		try {
			 MongoDbUtil mongo=new MongoDbUtil();
				String html=HttpUtil.getHtml(url, map,"utf8", 1, new HashMap<String, String>()).get("html");
				if(!StringUtil.isEmpty(html)&&html.length()>200){
					List<HashMap<String, Object>> listMap=parseList(html);
					if(!listMap.isEmpty()){
						mongo.upsetManyMapByTableName(listMap, "ww_ask_online_all");
					}
				}
				
		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	public static List<HashMap<String, Object>> parseList(String html){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
		Object json=IKFunction.jsonFmt(html);
		Object data=IKFunction.keyVal(json, "data");
		int num=IKFunction.rowsArray(data);
		HashMap<String, Object> map=null;
		for(int i=1;i<=num;i++){
			Object one=IKFunction.array(data, i);
			Object ask=IKFunction.keyVal(one, "ask");
			Object article=IKFunction.keyVal(ask, "article");
			Object timeObj=IKFunction.keyVal(article, "createtime");
			String time=IKFunction.timeFormat(timeObj.toString());
			if(!IKFunction.timeOK(time)){
				continue;
			}
			String  question=IKFunction.keyVal(article, "content").toString();
			Object  stock=IKFunction.keyVal(one, "stock");
			String  sname=IKFunction.keyVal(stock, "name").toString();
			if(!question.contains(sname)){
				question="["+sname+"]"+question;
			}
			Object  ans=IKFunction.keyVal(one, "answer");
			Object answ=IKFunction.array(ans, 1);
			Object  answe=IKFunction.keyVal(answ, "essay");
			Object  answe1=IKFunction.keyVal(answe, "article");
			String  answer=IKFunction.keyVal(answe1, "content").toString();
			Object  design=IKFunction.keyVal(one, "design");
			Object des=IKFunction.array(design, 1);
			Object  tuser=IKFunction.keyVal(des, "tuser");
			Object  name=IKFunction.keyVal(tuser, "nickname");
			map=new HashMap<String, Object>();
			if(!StringUtil.isEmpty(answer)){
				map.put("ifanswer","1");
			}else{
				map.put("ifanswer","0");
			}
			map.put("id",IKFunction.md5(question+answer));
			map.put("tid",question+time);
			map.put("question", question);
			map.put("name", name);
			map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
			map.put("answer", answer);
			map.put("time", time);
			map.put("website", "看财经");
			list.add(map);
		}
		return list;
	}
	
	
	
}
