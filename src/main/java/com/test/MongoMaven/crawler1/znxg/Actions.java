package com.test.MongoMaven.crawler1.znxg;

import java.util.HashMap;
import java.util.Random;
import net.sf.json.JSONObject;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class Actions implements Runnable{
	private DataUtil util;
	private MongoDbUtil mongo;
	private PostData post;
	public Actions(DataUtil util,MongoDbUtil mongo,PostData post){
		this.util=util;
		this.mongo=mongo;
		this.post=post;
	}
	
	public void run() {
		HashMap<String, String> map1=new HashMap<String, String>();
	     String url=util.getUrl();
	     String code=util.getCode();
	     String name=util.getName();
	     String tmp=name+"("+code+")";
	     String html=HttpUtil.getHtml(url, map1, "utf8", 1,new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>300){
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json,"data");
			Object one =IKFunction.array(data,1);
			Object answer=IKFunction.keyVal(one, "analysisText");
			String time=IKFunction.getTimeNowByStr("yyyy-MM-dd");
			String question=getQuestion(tmp);
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("id", code+time);
			if(!StringUtil.isEmpty(answer.toString())){
				map.put("ifanswer","1");
			}else{
				map.put("ifanswer","0");
			}
			map.put("id",IKFunction.md5(code+answer+"智能选股"));
			map.put("time",time);
			map.put("question",question);
			map.put("name","股灵三");
			map.put("timedel",time);
			map.put("answer",answer);
			map.put("website","智能选股");
			try {
				JSONObject tjson=JSONObject.fromObject(map);
				String su= post.postHtml("http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=ww_stock_json",new HashMap<String, String>(),tjson.toString(), "utf-8", 1);
				if(su.contains("exception")){
					System.err.println("写入数据异常！！！！  < "+su+" >");
				}
				mongo.upsertMapByTableName(map, "ww_ask_online_all");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
//			mongo.upsertMapByTableName(map, "ww_sglc");
		}
	}
	
	public static String getQuestion(String stock){
		String question="";
		Random rd=new Random();
		int num=rd.nextInt(5);
		if(num==0){
			question="老师，帮忙看下"+stock;
		}else if(num==1){
			question="老师，"+stock+"后市如何？";
		}else if(num==2){
			question=stock+"可以进吗？";
		}else if(num==3){
			question=stock+"怎么操作？谢谢!";
		}else if(num==4){
			question=stock+"继续持有吗？谢谢！";
		}
		return question;
	}
	
}
