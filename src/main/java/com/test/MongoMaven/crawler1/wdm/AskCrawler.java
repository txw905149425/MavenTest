package com.test.MongoMaven.crawler1.wdm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//问董秘
public class AskCrawler {

	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		PostData post=new PostData();
		String url="http://rs.p5w.net/interaction/getNewR.shtml";
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("Accept", "application/json, text/javascript, */*; q=0.01");
		map.put("Accept-Encoding", "gzip, deflate");
		map.put("Accept-Language","zh-CN,zh;q=0.8");
		map.put("Content-Type","application/x-www-form-urlencoded");
		map.put("Cookie","SHRIOSESSIONID=bad6d313-572a-49b2-8f35-c7379a9c5b03; Hm_lvt_ed9dac8a2b525df95dc69c97bbcda470=1497508339,1498110849; Hm_lpvt_ed9dac8a2b525df95dc69c97bbcda470=1498110849; JSESSIONID=16B3EAE605FDFBC14AC8D55E076E6320");
		map.put("Host", "rs.p5w.net");
		map.put("Origin", "http://rs.p5w.net");
		map.put("Referer","http://rs.p5w.net/index/company/showQuestionPage.shtml?stationId=&query=1");
		map.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.101 Safari/537.36");
		map.put("X-Requested-With", "XMLHttpRequest");
	  try {
		for(int i=0;i<3;i++){
		    	String tmp="query=0&isPagination=1&page="+i+"&rows=10";
				String html=post.postHtml(url, map, tmp, "utf8", 1);
				if(!StringUtil.isEmpty(html)&&html.length()>500){
					List<HashMap<String, Object>> list=parse(html);
					if(!list.isEmpty()){
//						mongo.upsetManyMapByTableName(list,"ww_wdm_ask_shares");
						mongo.upsetManyMapByTableName(list,"ww_ask_online_all");
					}
				}
		}
	  }catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	  }	 

	}
	public static List<HashMap<String, Object>> parse(String html){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
		Object json=IKFunction.jsonFmt(html);
		Object data=IKFunction.keyVal(json, "rows");
		int num=IKFunction.rowsArray(data);
		HashMap<String, Object > map=null;
		for(int i=1;i<=num;i++){
			map=new HashMap<String, Object>();
			Object one=IKFunction.array(data, i);
			Object cdata=IKFunction.keyVal(one,"companyBaseInfo");
			Object stockcode=IKFunction.keyVal(cdata,"companyCode");
			Object stockname=IKFunction.keyVal(cdata,"companyShortname");
			Object question=stockname+"("+stockcode+")"+IKFunction.keyVal(one, "content");
			Object adata=IKFunction.keyVal(one, "replyList");
			Object ans=IKFunction.array(adata, 1);
			Object answer=IKFunction.keyVal(ans, "replyContent");
			Object time=IKFunction.keyVal(ans, "replyerTimeStr");
			if(!IKFunction.timeOK(time.toString())){
				continue;
			}
			if(!StringUtil.isEmpty(answer.toString())){
				map.put("ifanswer","1");
			}else{
				map.put("ifanswer","0");
			}
			Object name=stockname+"董秘";
			map.put("id",IKFunction.md5(question+""+answer));
			map.put("tid",question+""+time);
			map.put("question", question);
			map.put("name", name);
			map.put("answer", answer);
			map.put("time", time);
			map.put("website", "全景网");
			list.add(map);
			
		}
		return list;
	}

}
