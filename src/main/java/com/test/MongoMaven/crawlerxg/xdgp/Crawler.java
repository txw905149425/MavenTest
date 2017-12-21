package com.test.MongoMaven.crawlerxg.xdgp;

import java.util.ArrayList;
import java.util.HashMap;

import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		mongo.getShardConn("xg_xdgp_stock").deleteMany(Filters.exists("id"));
		String url="http://a.xdstock.com/app-server/RP0103A0101BLogic.do";
		HashMap<String, String> map=new HashMap<String, String>();
		PostData post=new PostData();
		String json="{'appVersion':'3.1.3','osApi':'23','osModel':'HUAWEI M2-801W','osRelease':'6.0','osType':'3','token':'20171012102556'}";
		String durl="http://a.xdstock.com/app-server/RP0103B0101BLogic.do";
		try{
			String html=post.postHtml(url, map, json, "utf8", 1);
//			System.out.println(html);
			if(!StringUtil.isEmpty(html)){
				Object js=IKFunction.jsonFmt(html);
				Object model=IKFunction.keyVal(js, "modelBasicInfos");
				int num=IKFunction.rowsArray(model);
				for(int i=1;i<=num;i++){
					Object one=IKFunction.array(model, i);
					Object id=IKFunction.keyVal(one, "modelNo");
					Object title=IKFunction.keyVal(one, "fullName");
					ArrayList<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
 					String json1="{'loadedCount':0,'modelNo':'"+id+"','appVersion':'3.1.3','osApi':'23','osModel':'HUAWEI M2-801W','osRelease':'6.0','osType':'3','token':'20171012102335'}";
					String dhtml=post.postHtml(durl, map, json1, "utf8", 1);
					if(!StringUtil.isEmpty(html)&&html.length()>200){
						Object djson=IKFunction.jsonFmt(dhtml);
						Object dlist=IKFunction.keyVal(djson, "nowStkList");
						int size=IKFunction.rowsArray(dlist);
						for(int j=1;j<=size;j++){
							HashMap<String, Object> map1=new HashMap<String, Object>();
							Object two=IKFunction.array(dlist,j);
							Object stockName=IKFunction.keyVal(two, "stkName");
							Object scode=IKFunction.keyVal(two, "stkCode");
							String stockCode=IKFunction.regexp(scode, "(\\d+)");
							map1.put("stockName", stockName);
							map1.put("stockCode", stockCode);
							list.add(map1);
						}
						if(!list.isEmpty()){
							HashMap<String, Object> dmap=new HashMap<String, Object>();
							dmap.put("id", IKFunction.getTimeNowByStr("yyyy-MM-dd")+title);
							dmap.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
							dmap.put("website", "迅动股票");
							dmap.put("time", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
							dmap.put("title", title);
							dmap.put("type", "1");
							dmap.put("list",list);
							mongo.upsertMapByTableName(dmap, "xg_xdgp_stock");
						}
						
					}
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}
