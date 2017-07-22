package com.test.MongoMaven.crawler1.qfw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;
//启富网
public class Crawler {
	public static void main(String[] args) {
			String url="http://www.qifu66.com/other/stockcenter";
		try{
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8",1,new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, "div.stock_center_o")){
				Object doc=IKFunction.JsoupDomFormat(html);
				int num=IKFunction.jsoupRowsByDoc(doc, "div.stock_center_o");
				List<HashMap<String, Object>> listmap=new ArrayList<HashMap<String,Object>>();
				HashMap<String, Object> map=null;
				for(int i=0;i<num;i++){
					map=new HashMap<String, Object>();
					String timeObj=IKFunction.jsoupTextByRowByDoc(doc, ".stock_center_p3>span", i);
					String time=IKFunction.timeFormat(timeObj);
					if(!IKFunction.timeOK(time)){
						continue;
					}
					String abs=IKFunction.jsoupTextByRowByDoc(doc, "div.stock_center_o>#stock_p0",i);
					String question=IKFunction.jsoupTextByRowByDoc(doc, "div.stock_center_o>#stock_p1", i)+"（描述："+abs+")";
					String answer=IKFunction.jsoupTextByRowByDoc(doc, "div.stock_center_o>.stock_center_p2", i);
					String name=IKFunction.jsoupTextByRowByDoc(doc, ".stock_center_p3>font", i);
					if(!StringUtil.isEmpty(answer.toString())){
						map.put("ifanswer","1");
					}else{
						map.put("ifanswer","0");
					}
					map.put("id",IKFunction.md5(question+answer));
					map.put("tid",question+timeObj);
					map.put("time",time);
					map.put("question",question);
					map.put("name",name);
					map.put("answer",answer);
					map.put("website","启富网");
					listmap.add(map);
				}
				if(!listmap.isEmpty()){
				    MongoDbUtil mongo=new MongoDbUtil();
				    mongo.upsetManyMapByTableName(listmap, "ww_ask_online_all");
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
			
	}
}
