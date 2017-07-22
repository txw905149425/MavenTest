package com.test.MongoMaven.crawler1.agp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mongodb.Mongo;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
//  https://www.5igupiao.com/AllLiveMsg/master_rank
	public static void main(String[] args) {
		String url="https://www.5igupiao.com/index.php?s=/Api/Live/search&order_type=follow_nums&u_id=undefined&number=20";
	try {
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>100){
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json, "datalist");
			int num=IKFunction.rowsArray(data);
			List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
			HashMap<String, Object > map=null;
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(data, i);
				Object  uid=IKFunction.keyVal(one, "id");
				Object  name=IKFunction.keyVal(one, "title");
				String durl="https://www.5igupiao.com/api/live.php?act=qa_all_a&id="+uid;
				String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
//				System.out.println(dhtml);
				if(!StringUtil.isEmpty(dhtml)&&dhtml.length()>50){
					Object djson=IKFunction.jsonFmt(dhtml);
					Object ddata=IKFunction.keyVal(djson, "qa_list");
					int dnum=IKFunction.rowsArray(ddata);
					for(int j=1;j<=dnum;j++){
						Object done=IKFunction.array(ddata, j);
						Object  dtime=IKFunction.keyVal(done, "a_time");
						String time="";
						if(dtime!=null){
							time=IKFunction.timeFormat(dtime.toString());
						}
						if("".equals(time)||!IKFunction.timeOK(time)){
							continue;
						}
						map=new HashMap<String, Object>();
					    Object question=IKFunction.keyVal(done,"question");
					    Object answer=IKFunction.keyVal(done, "answer");
					    map.put("id",IKFunction.md5(question+time));
						map.put("tid",question+time);
						map.put("question", question);
						map.put("name", name);
						map.put("answer", answer);
						map.put("time", time);
						map.put("website", "爱股票");
						list.add(map);
					}
				}
			}
			if(!list.isEmpty()){
				MongoDbUtil mongo=new MongoDbUtil();
				mongo.upsetManyMapByTableName(list, "ww_ask_online_all");
			}
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}
	
}
