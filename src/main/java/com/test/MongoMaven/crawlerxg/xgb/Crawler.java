package com.test.MongoMaven.crawlerxg.xgb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//选股宝app推荐
public class Crawler {
	public static void main(String[] args) throws ClientProtocolException, IOException {
		String url="https://bao.wallstreetcn.com/api/discover/subjects";
		MongoDbUtil mongo=new MongoDbUtil();
		mongo.getShardConn("xg_xgb_stock").deleteMany(Filters.exists("id"));
		try{
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)&&html.length()>200){
				Object json=IKFunction.jsonFmt(html);
				Object list=IKFunction.keyVal(json, "Section4");
				int num=IKFunction.rowsArray(list);
				ArrayList<HashMap<String, Object>> alist=new  ArrayList<HashMap<String,Object>>();
				for(int i=1;i<=num;i++){
					Object one=IKFunction.array(list, i);
					Object dlist=IKFunction.keyVal(one, "FeaturedMsgStocks");
					Object timeObj=IKFunction.keyVal(one, "FeaturedMsgAt");
					String time=IKFunction.timeFormat(timeObj.toString());
					if(!IKFunction.timeOK(time)){
						continue;
					}
					int size=IKFunction.rowsArray(dlist);
					for(int j=1;j<=size;j++){
						HashMap<String, Object> map=new HashMap<String, Object>();
						Object two=IKFunction.array(dlist, j);
						Object stockName=IKFunction.keyVal(two,"Name");
						Object sCode=IKFunction.keyVal(two,"Symbol");
						String stockCode=IKFunction.regexp(sCode, "(\\d+)");
						map.put("stockName", stockName);
						map.put("stockCode", stockCode);
						alist.add(map);
					}
				}
				if(!alist.isEmpty()){
					HashMap<String, Object> dmap=new HashMap<String, Object>();
					dmap.put("id", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					dmap.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					dmap.put("website", "选股宝");
					dmap.put("time", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					dmap.put("title", "选股宝股票");
					dmap.put("type", "1");
					dmap.put("list",alist );
					mongo.upsertMapByTableName(dmap, "xg_xgb_stock");
				}
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
