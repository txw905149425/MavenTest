package com.test.MongoMaven.crawler1.lqz;

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
		String url="http://www.lqz.cn/bozhu-list.html";
		try{
			HashMap<String, String> map1=new HashMap<String, String>();
			map1.put("Accept", "application/json, text/javascript, */*; q=0.01");
			map1.put("Accept-Encoding", "gzip, deflate");
			map1.put("Accept-Language", "zh-CN,zh;q=0.8");
			map1.put("Connection", "keep-alive");
			map1.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			map1.put("Cookie", "liveset_fontsize=1; liveset_voicereminder=1; liveset_bgsound=0; PHPSESSID=6kf0mfqslupf9aidls6j3puje3; onlineipd=175.6.6.26; Hm_lvt_6854f4340ae7bc15b3939573073d744d=1500276259,1500287129; Hm_lpvt_6854f4340ae7bc15b3939573073d744d=1500287134");
			map1.put("Host", "www.lqz.cn");
			map1.put("Origin", "http://www.lqz.cn");
			map1.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.104 Safari/537.36");
			map1.put("X-Requested-With", "XMLHttpRequest");
			String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "UTF8",	1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, ".bo_foucs>a")){
				Document doc=Jsoup.parse(html);
				HashMap<String, Object> map=null;
				Elements node=doc.select(".bo_foucs>a");
				Elements links = node.select("a[target]");
				Elements node2 = doc.select(".bo_name>a");
				int num=links.size();
				for(int i=0;i<num;i++){
					String uuu = links.get(i).select("a").attr("href");
					String name = node2.get(i).select("a").text();
					String html1=HttpUtil.getHtml(uuu,new HashMap<String, String>(), "UTF8",	1, new HashMap<String, String>()).get("html");
					if(!StringUtil.isEmpty(html1)&&IKFunction.htmlFilter(html1, "#qdBtn>input")){
						Document doc1=Jsoup.parse(html1);
						Elements node1=doc1.select("#qdBtn>input");
						String uuu1 = node1.get(0).select("input").attr("onclick");
						uuu1 = uuu1.replace("supportPro(", "");
						uuu1 = uuu1.replace(")", "");
						if(StringUtil.isEmpty(uuu1)||!uuu1.contains(",")){
							continue;
						}
						String[] strarray=uuu1.split(","); 
						// 时间戳
					    long time3  =System.currentTimeMillis() / 1000-50000;
	                    String parm = "reply_id=0&t="+time3+"&tid="+strarray[1]+"&rid="+strarray[0];
						String url1="http://www.lqz.cn/index.php?m=room&a=postlist";
						String html2=PostData.postHtml(url1, map1, parm, "gbk", 1);
						Object json=IKFunction.jsonFmt(html2);      
						Object listData=IKFunction.keyVal(json, "list");
						int size1=IKFunction.rowsArray(listData);
						for (int j = 1; j <=size1; j++) {
						Object listarray=IKFunction.array(listData, j);
						String username=IKFunction.keyVal(listarray, "username").toString();
						String content=IKFunction.keyVal(listarray, "content").toString();
						String time1=IKFunction.keyVal(listarray, "time").toString();
						map=new HashMap<String, Object>();
						if(content.contains("【原文】")&&content.contains("【回复】")&&username.contains(name)){
							String str = content;
							str  = str.replace("【原文】", "");
							str  = str.replace("【回复】", "");
							String[] strarray1=str.split("<br>");
							String[] ansarray = strarray1[1].split("：");
							String question = strarray1[0];
							String answer = ansarray[1];
							String ifanswer = "1";
							Object doc3=IKFunction.JsoupDomFormat(answer);
							answer=IKFunction.jsoupTextByRowByDoc(doc3,"body", 0);
							Object doc4=IKFunction.JsoupDomFormat(question);
							question=IKFunction.jsoupTextByRowByDoc(doc4,"body", 0);
						    time1 = IKFunction.timeFormat(time1);
						    if(!IKFunction.timeOK(time1)){
						    	continue;
						    }
							if(answer == null){
								ifanswer = "0";
							}
							String[] strarray2=time1.split(" ");
						    String timedel = strarray2[0];
						    map.put("id", IKFunction.md5(question+time1));
							map.put("website","老钱庄");
							map.put("question",question);
							map.put("answer", answer);
							map.put("name",name );
							map.put("ifanswer", ifanswer);
							map.put("time", time1);			
							map.put("timedel", timedel);
							mongo.upsertMapByTableName(map,"ww_ask_online_all");
						}
					}
					
					}
					

				}
				}
		  }catch(Exception e){
			  e.printStackTrace();
		  }
	}
}
