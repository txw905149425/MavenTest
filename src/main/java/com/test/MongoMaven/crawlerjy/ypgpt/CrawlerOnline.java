package com.test.MongoMaven.crawlerjy.ypgpt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//优品股票通  5.15 本季度炒股大赛已结束...//比赛开始
public class CrawlerOnline {
		public static void main(String[] args) {
			String url="http://cgdsm.upchina.com/rank/master/dynamic";
		try{
			String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1,new HashMap<String, String>()).get("html");
			if(!StringUtil.isEmpty(html)&&html.length()>200){
			List<HashMap<String, Object>> listMap=parseList(html);
			   if(!listMap.isEmpty()){
			    MongoDbUtil mongo=new MongoDbUtil();
			    PostData post=new PostData();
		    	for(HashMap<String, Object> one:listMap){
					String ttmp=JSONObject.fromObject(one).toString();
//					http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=mm_stock_json
//					http://localhost:8888/import?type=mm_stock_json
					 String su= post.postHtml("http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=mm_stock_json",new HashMap<String, String>(),ttmp, "utf-8", 1);
						if(su.contains("exception")){
							System.err.println("写入数据异常！！！！  < "+su+" >");
						}
				}
		    	mongo.upsetManyMapByTableName(listMap, "mm_deal_dynamic_all");
			   }
			
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
		
		public static List<HashMap<String, Object>> parseList(String html){
			if(StringUtil.isEmpty(html)){
				return new ArrayList<HashMap<String, Object>>();
			}
			List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();

			int num=IKFunction.rowsArray(html);
			HashMap<String, Object> map=null;
			HashMap<String, Object> userMap=new HashMap<String, Object>();
			for(int i=1;i<=num;i++){
				map=new HashMap<String, Object>();
				Object js=IKFunction.array(html,i);
				String timestr=IKFunction.keyVal(js, "tringData").toString();
				if(timestr.contains("/")){
					timestr=timestr.replace("/", "-");
				}
				String time=IKFunction.timeFormat(timestr);
				if(!IKFunction.timeOK(time)){
					continue;
				}
//				System.out.println(time);
				String id=IKFunction.keyVal(js, "urCode").toString();
				String describe="";
				if(!userMap.containsKey(id)){
					String url1="http://cgdsm.upchina.com/center/1/"+id;
					String html1=HttpUtil.getHtml(url1, new HashMap<String, String>(), "utf8", 1,new HashMap<String, String>()).get("html");
					describe=IKFunction.jsoupTextByRowByDoc(IKFunction.JsoupDomFormat(html1),".profits>span",2);
					userMap.put(id, describe);
				}else{
					describe=userMap.get(id).toString();
				}
				
				Object uname=IKFunction.keyVal(js, "urNickName");
				String option=IKFunction.keyVal(js, "sType").toString();
				if("2".equals(option)){
					option="1";
				}else if("1".equals(option)){
					option="0";
				}
				Object closing_cost=IKFunction.keyVal(js, "sPrice");
				Object stockName=IKFunction.keyVal(js, "sName");
				String stockCode=IKFunction.keyVal(js, "sCode").toString();
				String code=stockCode.substring(4);
				map.put("id",IKFunction.md5(timestr+""+option+stockName));
				map.put("tid",timestr+""+option+stockName);
				map.put("AddTime", time);
				map.put("closing_cost", closing_cost);
				map.put("StockName", stockName);
				map.put("website", "优品股票通");
				map.put("StockCode", code);
				map.put("option", option);
				map.put("UserName", uname);
				map.put("describe", "胜率："+describe);
				list.add(map);
			}
			return list;
			
		}
}
