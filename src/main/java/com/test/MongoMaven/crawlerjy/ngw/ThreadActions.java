package com.test.MongoMaven.crawlerjy.ngw;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;


public class ThreadActions implements Runnable{
	private DataUtil util;
	private MongoDbUtil mongo;
	public  ThreadActions(DataUtil util,MongoDbUtil mongo){
		this.util=util;
		this.mongo=mongo;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		String url=util.getUrl();
		String describe=util.getDescribe();
		try{
			Map<String, String> map=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1,new HashMap<String, String>());
			String html=map.get("html");
			if(html.length()>300){
				PostData post=new PostData();
				List<HashMap<String, Object>> recordList=parseList(html,describe);
				if(!recordList.isEmpty()){
					for(HashMap<String, Object> result:recordList){
						result.remove("html");
						result.remove("quantity");
						result.remove("AccountID");
						result.remove("crawl_time");
						JSONObject mm_data=JSONObject.fromObject(result);
//						http://wisefinance.chinanorth.cloudapp.chinacloudapi.cn/wf/import?type=mm_stock_json
//						http://localhost:8888/import?type=mm_stock_json
					   String su=post.postHtml("http://wisefinance.chinanorth.cloudapp.chinacloudapi.cn/wf/import?type=mm_stock_json",new HashMap<String, String>(), mm_data.toString(), "utf-8", 1);
						if(su.contains("exception")){
							System.out.println(mm_data.toString());
							System.err.println("写入数据异常！！！！  < "+su+" >");
						}
					}
					mongo.upsetManyMapByTableName(recordList, "mm_deal_dynamic_all");
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public static List<HashMap<String, Object>> parseList(String html,String describe){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
		Object json=IKFunction.jsonFmt(html);
		Object data=IKFunction.keyVal(json, "data");
		int num=IKFunction.rowsArray(data);
//		HashMap<String, Object> map=null;
		for(int i=1;i<=num;i++){
//			map=new HashMap<String, Object>();
			Object tmp=IKFunction.array(data, i);
			Object timeStr=IKFunction.keyVal(tmp, "addTime");
			String time=IKFunction.timeFormat(timeStr.toString());
			if(!IKFunction.timeOK(time)){
				continue;
			}
			Object code=IKFunction.keyVal(tmp, "stockCode");
			Object stock=IKFunction.keyVal(tmp, "stockName");
			Object name=IKFunction.keyVal(tmp, "userName");
			Object uid=IKFunction.keyVal(tmp, "userID");
			Object block=IKFunction.array(IKFunction.keyVal(tmp, "contentFormat"),1);
			String str=IKFunction.keyVal(block, "content").toString();
			HashMap<String, Object> map=parseTmp(str);
			map.put("id",IKFunction.md5(name+""+timeStr+stock+code));
			map.put("tid",name+""+timeStr+stock+code);
			map.put("describe",describe);
			map.put("StockCode", code);
			map.put("StockName", stock);
			map.put("UserName", name);
			map.put("AccountID", uid.toString());
			map.put("AddTime", time);
//			map.put("html",tmp);
			map.put("website","牛股王");
			list.add(map);
		}
		return list;
	}
	
	public static HashMap<String,Object> parseTmp(String str){
//		"04-11 14:48 买入东土科技(300353) 成交价17.51元,仓位占比26.67%
		HashMap<String,Object> map=new HashMap<String,Object>();
		if(StringUtil.isEmpty(str)){
			return map;
		}
		if(str.contains("买入")){
			map.put("option", "0");
		}else if(str.contains("卖出")){
			map.put("option", "1");
		}
		String closing_cost=IKFunction.regexp(str, "成交价(.*?)元,");
		String proportion=IKFunction.regexp(str, "元,(.*)");
		map.put("closing_cost",closing_cost);
		map.put("quantity",proportion);
		return map;
		
	}
	
	public static void main(String[] args) {
		Date date=new Date();
	     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
	     String str=sdf.format(date);
	     System.out.println(str);
	}
}
