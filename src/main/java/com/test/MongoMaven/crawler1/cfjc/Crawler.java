package com.test.MongoMaven.crawler1.cfjc;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		String url="https://h5tg.cfzq.com/servlet/json?funcNo=1106524&curPage=1&numPerPage=30&stockCode=&isRecommend=&stockName=";
		try{
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>200){
			Object json=IKFunction.jsonFmt(html);
			Object da=IKFunction.keyVal(json, "DataSet");
			Object dat=IKFunction.array(da, 1);
			Object data=IKFunction.keyVal(dat, "data");
			int num=IKFunction.rowsArray(data);
			HashMap<String, Object > map=null;
			MongoDbUtil mongo=new MongoDbUtil();
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(data, i);
				Object timeObj=IKFunction.keyVal(one,"create_time");
				String time=IKFunction.timeFormat(timeObj.toString());
				if(!IKFunction.timeOK(time)){
					continue;
				}
				Object id=IKFunction.keyVal(one, "ques_id");
				String durl="https://h5tg.cfzq.com/servlet/json?funcNo=1106522&ques_id="+id;
				String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
				if(!StringUtil.isEmpty(dhtml)&&dhtml.length()>200){
					Object djson=IKFunction.jsonFmt(dhtml);
					Object results=IKFunction.keyVal(djson, "results");
					Object result=IKFunction.array(results, 1);
					Object scode=IKFunction.keyVal(result,"stock_code");
					Object sname=IKFunction.keyVal(result,"stock_name");
					Object ques=IKFunction.keyVal(result,"ques_content");
					String question=sname+"("+scode+")"+ques;
					Object time1=IKFunction.keyVal(result, "create_time");
					String answer=IKFunction.keyVal(result, "answer_content").toString();
					if(answer.contains("（根据监管以及政策要求，只能对签约投顾的客户才能出具买卖操作的建议）")){
						answer=answer.replace("（根据监管以及政策要求，只能对签约投顾的客户才能出具买卖操作的建议）", "");
					}else if(answer.contains("(根据监管以及政策要求，只能对签约投顾的客户才能出具买卖操作的建议)")){
						answer=answer.replace("(根据监管以及政策要求，只能对签约投顾的客户才能出具买卖操作的建议)", "");
					}
					Object name=IKFunction.keyVal(result, "invest_name");
					map=new HashMap<String, Object>();
					if(!StringUtil.isEmpty(answer)){
						map.put("ifanswer", "1");
					}else{
						map.put("ifanswer", "0");
					}
				    map.put("id",IKFunction.md5(question+time1));
					map.put("tid",question+time1);
					map.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					map.put("question", question);
					map.put("name", name);
					map.put("answer", answer);
					map.put("time", time1);
					map.put("website", "财富聚财");
					mongo.upsertMapByTableName(map, "ww_ask_online_all");
				}
			}
		}
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	}
		
}
