package com.test.MongoMaven.crawlerjy.hexun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {

	public static void main(String[] args) {
		//周榜 http://api.match.vip.hexun.com/Competition.aspx?callback=jQuery182022423432115465403_1496819160012&methodname=GetPhaseUserList&pid=112&ptype=1&_=1496819160594
		//月榜 http://api.match.vip.hexun.com/Competition.aspx?callback=jQuery182029811948939809585_1496827128292&methodname=GetPhaseUserList&pid=110&ptype=0&_=1496827128633
//		具体交易链接 http://api.match.vip.hexun.com/UserInformation.aspx?callback=jQuery18201691036083786337_1496824885139&methodname=DealHistory&p=1&_=1496824885883&AccountID=237120
		getZJiaoYi();
	}
	public static void getZJiaoYi(){
		PostData post=new PostData();
		MongoDbUtil mongo=new MongoDbUtil();
	    try{		
	    for(int p=0;p<2;p++){
	    	String url="";
	    	if(p==0){
	    	    url="http://api.match.vip.hexun.com/Competition.aspx?methodname=GetPhaseUserList&pid=110&ptype=0";	
	    	}else{
	    		url="http://api.match.vip.hexun.com/Competition.aspx?methodname=GetPhaseUserList&pid=112&ptype=1";
	    	}
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)&&html.length()>200){
				Object json=IKFunction.jsonFmt(html);
				Object data=IKFunction.keyVal(json, "virtualData");
				int num=IKFunction.rowsArray(data);
				for(int i=1;i<=num;i++){
					Object one =IKFunction.array(data, i);
					String name=IKFunction.keyVal(one, "HexunUserName").toString();
					Object uid=IKFunction.keyVal(one, "AccountID");
					String describe=IKFunction.keyVal(one, "PhaseIncomeRate").toString();
					describe="收益率："+describe+"%";
					String durl="http://api.match.vip.hexun.com/UserInformation.aspx?methodname=DealHistory&p=1&AccountID="+uid;
					String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
					if(!StringUtil.isEmpty(dhtml)&&dhtml.length()>200){
						List<HashMap<String, Object>> list=parseDetail(dhtml,describe,name);
						if(!list.isEmpty()){
//							mongo.upsetManyMapByTableName(list, "mm_hexun_deal_dynamic");
							for(HashMap<String, Object> two:list){
								String ttmp=JSONObject.fromObject(two).toString();
//								http://wisefinance.chinanorth.cloudapp.chinacloudapi.cn/wf/import?type=mm_stock_json
//								http://localhost:8888/import?type=mm_stock_json
								 String su= post.postHtml("http://wisefinance.chinanorth.cloudapp.chinacloudapi.cn/wf/import?type=mm_stock_json",new HashMap<String, String>(),ttmp, "utf-8", 1);
									if(su.contains("exception")){
										System.err.println("写入数据异常！！！！  < "+su+" >");
									}
							}
							mongo.upsetManyMapByTableName(list, "mm_deal_dynamic_all");
						}
					}
				}
			}
	      }
	     }catch(Exception e){
	    	e.printStackTrace(); 
	     }
	}
	
	public static List<HashMap<String, Object>> parseDetail(String html,String describe,String name){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
		Object json=IKFunction.jsonFmt(html);
		Object data=IKFunction.keyVal(json, "virtualData");
		int num=IKFunction.rowsArray(data);
		HashMap<String, Object > map=null;
		for(int i=1;i<=num;i++){
			map=new HashMap<String, Object>();
			Object one =IKFunction.array(data, i);
			Object StockName=IKFunction.keyVal(one, "StockName2");
			Object StockCode=IKFunction.keyVal(one, "StockCode");
			String time=IKFunction.keyVal(one, "HistoryDateTime").toString();
			if(time.contains("/")){
				time=time.replaceAll("/", "-");
			}
			if(!IKFunction.timeOK(time)){
				break;
			}
			String tmp=IKFunction.keyVal(one, "HistoryType").toString();
			String option="";
			if(tmp.contains("买入")){
				option="0";
			}else if(tmp.contains("卖出")){
				option="1";
			}
			Object price=IKFunction.keyVal(one, "ActionPrice");
			map.put("describe", describe);
			map.put("StockName",StockName);
			map.put("StockCode", StockCode);
			map.put("closing_cost", price);
			map.put("AddTime", time);
			map.put("option", option);
			map.put("UserName", name);
			map.put("website", "和讯股票");
			map.put("tid", name+time+StockName+option);
			map.put("id", IKFunction.md5(name+time+StockName+option));
			list.add(map);
		}
		return list;
		
	}
	
	
	
}
