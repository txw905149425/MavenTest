package com.test.MongoMaven.crawlerxg.gpld;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

//股票雷达 --选股策略(镭矿选股)
public class Crawler {
	public static void main(String[] args) {
		String day=IKFunction.getTimeNowByStr("yyyy-MM-dd");
		MongoDbUtil mongo=new MongoDbUtil();
		mongo.getShardConn("xg_gpld_stock").deleteMany(Filters.exists("id"));
		Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
		String url="http://raquant.com/smartra/stralst?fr=raq";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, "ul.str-list.freeUl>a")){
//			http://raquant.com/smartra/show?id=9&uid=undefined
			Object doc=IKFunction.JsoupDomFormat(html);
			int num=IKFunction.jsoupRowsByDoc(doc,"ul.str-list.freeUl>a");
			for(int i=0;i<num;i++){
				String tmp=IKFunction.jsoupListAttrByDoc(doc, "ul.str-list.freeUl>a", "href",i);
				String id=IKFunction.regexp(tmp, "\\((\\d+)\\)");
				String name=IKFunction.jsoupTextByRowByDoc(doc, "span.str-title", i);
				String durl="http://raquant.com/smartra/show?id="+id+"&uid=undefined";
				String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
//				System.out.println(dhtml);
				ArrayList<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
				String time="";
				if(!StringUtil.isEmpty(dhtml)&&IKFunction.htmlFilter(dhtml, "span.stock-name")){
					Object ddoc=IKFunction.JsoupDomFormat(dhtml);
					int dnum=IKFunction.jsoupRowsByDoc(ddoc,"span.stock-name");
					String timeObj=IKFunction.jsoupTextByRowByDoc(ddoc, ".title_span>span", 0);
					time=year+"-"+IKFunction.regexp(timeObj, "(\\d+-\\d+)");
					int dsize=IKFunction.comlitTimeReturnDay(time,day);
					if(dsize<2){
						for(int j=0;j<dnum;j++){
							 String stockName=IKFunction.jsoupTextByRowByDoc(ddoc, "span.stock-name", j);
							 String stockCode=IKFunction.jsoupTextByRowByDoc(ddoc, "span.stock-code", j);
							 HashMap<String, Object> map=new HashMap<String, Object>();
							 map.put("stockName", stockName);
							 map.put("stockCode", stockCode);
							 list.add(map);
							}
					}
				}
				 if(!list.isEmpty()){
					 HashMap<String, Object> resultMap=new HashMap<String, Object>();
					 resultMap.put("id", time+name);
					 resultMap.put("title",name);
					 resultMap.put("type","1");
					 resultMap.put("time",time);
					 resultMap.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					 resultMap.put("list",list);
					 resultMap.put("website","股票雷达");
					 try {
						mongo.upsertMapByTableName(resultMap, "xg_gpld_stock");
						if(name.equals("疯狂十字星")||name.equals("逃顶器")){
							mongo.upsertMapByTableName(resultMap, "app_xg_other_stock");
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 }
				
			}
			
		}
		
		
	}
}
