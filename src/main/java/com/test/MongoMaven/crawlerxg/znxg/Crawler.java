package com.test.MongoMaven.crawlerxg.znxg;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		mongo.getShardConn("xg_znxg_stock").deleteMany(Filters.exists("id"));
		String url="http://info.zq88.cn:9085/smartPick/chooseStockHomePage.do?client=android&version=4.6.0&productId=CP140813001";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>100){
			Object doc=IKFunction.jsonFmt(html);
			Object jlist=IKFunction.keyVal(doc,"strategyInfos");
			int num=IKFunction.rowsArray(jlist);
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(jlist,i);
				String name=IKFunction.keyVal(one,"name").toString();
				Object key=IKFunction.keyVal(one,"route");
				String id=IKFunction.regexp(key, "/(.*)/");
				String durl="https://www.hczq.com/goto_info_zq88_cn//smartPick/chooseStock.do?skipType="+id+"&cp=1&ps=100";;
				String time="";
				if(name.equals("最优技术指标选股")){
					id="OPTIMAL";
					durl="https://www.hczq.com/goto_info_zq88_cn//smartPick/chooseStock.do?skipType="+id+"&cp=1&ps=100";
				}else if(name.equals("研报选股")){
					time=IKFunction.getTimeNowByStr("yyyy-MM-dd HH:mm:ss");
				  durl="https://www.hczq.com/goto_new_analyse_home//nj/orgRecommend/list.do?currentPage=1&pageSize=100&cycle=1";
				}else if(name.equals("游资突击股")){
					  durl="https://www.hczq.com/goto_info_zq88_cn//appEvent/mainDepartmentList.do?type=win&currentPage=1&pageSize=100";
				}else if(name.equals("主力龙虎榜")){
					 continue;
				}else if(name.equals("分析师金股")){
					 continue;
				}else if(name.equals("投顾精选个股")){
					 continue;
				}
//				Object notify=IKFunction.keyVal(one,"notify");
				String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
				ArrayList<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
				if(!StringUtil.isEmpty(dhtml)&&dhtml.length()>100){
					Object ddoc=IKFunction.jsonFmt(dhtml);
					Object timeObj=IKFunction.keyVal(ddoc,"updateTime");
					if(time.equals("")){
						time=IKFunction.timeFormat(timeObj.toString());	
					}
					try {
						if(!IKFunction.judgeTime(time,IKFunction.getTimeNowByStr("yyyy-MM-dd HH:mm:ss"), 24)){//每天计划8:30抓取，就取到前一天收盘之后一个小时刚好17个小时
							continue;
						}
					} catch (Exception e) {
						continue;
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
					Object dlist=IKFunction.keyVal(ddoc, "indicesStockPools");
					int size=IKFunction.rowsArray(dlist);
					for(int j=1;j<=size;j++){
						Object two=IKFunction.array(dlist, j);
						Object stockName=IKFunction.keyVal(two,"stockName");
						Object stockCode=IKFunction.keyVal(two,"stockCode");
						HashMap<String, Object> map1=new HashMap<String, Object>();
						map1.put("stockName", stockName);
						map1.put("stockCode", stockCode);
						list.add(map1);
					}
				}
			 if(!list.isEmpty()){
					 HashMap<String, Object> resultMap=new HashMap<String, Object>();
					 if(time.contains(" ")){
						 time=time.split(" ")[0];
					 }
					 resultMap.put("id", time+name);
					 resultMap.put("title",name);
					 resultMap.put("time",time);
					 resultMap.put("website","智能选股");
					 resultMap.put("list",list);
					 resultMap.put("type","0");
					 resultMap.put("timedel",IKFunction.getTimeNowByStr("yyyy-MM-dd"));
			try {
					 mongo.upsertMapByTableName(resultMap, "xg_znxg_stock");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 }
				
			}
			
			
		}
		
	}
}
