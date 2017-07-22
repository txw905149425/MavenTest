package com.test.MongoMaven.crawlerjy.gp360;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.http.client.ClientProtocolException;

import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//360股票
public class Crawler {

	public static void main(String[] args) {
		PostData post=new PostData();
		MongoDbUtil mongo=new MongoDbUtil();
		String url="https://gupiao.nicaifu.com/api/stock_router/post";//data[lastId]:6478419
		String data="path=/stock/game/list_dealrecord&data[marker]=game_long&reqtoken=9f2561da1f8602889887df1860ca76ab59e59baf1c6396733d1da1cc4758f0c9";
		try {
			for(int i=0;i<=50;i++){
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("Cookie", "UM_distinctid=15c3481705ab2-0b592f5382a094-38385702-1fa400-15c3481705b199; __GUID__=95660457014955299141530842493161; gr_user_id=e320c452-3917-42de-810b-def20f86fdd5; NCFTCK=oqg8tg6qpkeq1ftx3kjrc7tit1oz7lqg; CNZZDATA1258733443=1238623131-1495528309-%7C1495615253");
				map.put("Accept-Encoding","gzip, deflate, br");
				map.put("Accept-Language","zh-CN,zh;q=0.8");
				map.put("Connection","keep-alive");
				map.put("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
				map.put("Host", "gupiao.nicaifu.com");
				map.put("Origin", "https://gupiao.nicaifu.com");
				map.put("Referer", "https://gupiao.nicaifu.com/app/game/trading");
				map.put("X-Requested-With","XMLHttpRequest");
				String html=post.postHtml(url,map, data, "utf8", 1);
				List<HashMap<String , Object >> list=parse(html);
				if(!list.isEmpty()){
//					mongo.upsetManyMapByTableName(list, "mm_gp360_deal_dynamic");
					for(HashMap<String, Object> one:list){
						String ttmp=JSONObject.fromObject(one).toString();
//						http://jiangfinance.chinaeast.cloudapp.chinacloudapi.cn/wf/import?type=mm_stock_json
//						http://localhost:8888/import?type=mm_stock_json
						 String su= post.postHtml("http://localhost:8888/import?type=mm_stock_json",new HashMap<String, String>(),ttmp, "utf-8", 1);
							if(su.contains("exception")){
								System.err.println("写入数据异常！！！！  < "+su+" >");
							}
					}
					mongo.upsetManyMapByTableName(list, "mm_deal_dynamic_all");
					Object id=list.get((list.size()-1)).get("next");
				    data="path=/stock/game/list_dealrecord&data[marker]=game_long&data[lastId]="+id+"&reqtoken=9f2561da1f8602889887df1860ca76ab59e59baf1c6396733d1da1cc4758f0c9";
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static List<HashMap<String, Object>> parse(String html){
		Object json=IKFunction.jsonFmt(html);
		Object data=IKFunction.keyVal(json, "data");
		Object apiRes=IKFunction.keyVal(data,"apiRes");
		Object data2=IKFunction.keyVal(apiRes, "data");
		Object list=IKFunction.keyVal(data2,"list");
		int num=IKFunction.rowsArray(list);
		List<HashMap<String, Object>> listmap=new ArrayList<HashMap<String,Object>>();
		HashMap<String ,Object> map=null;
		for(int i=1;i<=num;i++){
			Object one=IKFunction.array(list, i);
			String uid=IKFunction.keyVal(one, "memberid").toString();
			String url="https://gupiao.nicaifu.com/app/user/home?memberid="+uid;
			String time=IKFunction.keyVal(one, "business_time").toString();
			if(!IKFunction.timeOK(time)){
				continue;
			}
			map=new HashMap<String, Object>();
			Object stockName=IKFunction.keyVal(one, "stock_name");
			String stockCode=IKFunction.regexp(IKFunction.keyVal(one, "stock_code"),"(\\d+)");
			Object price=IKFunction.keyVal(one, "business_price");
			Object name=IKFunction.keyVal(one, "nickname");
			String trade=IKFunction.keyVal(one, "entrust_bs").toString();
			String profit=IKFunction.keyVal(one, "f_totalProfit").toString();
			String option=null;//1卖出0买入
			if("2".equals(trade)){
				option="1";
			}else if("1".equals(trade)){
				option="0";
			}
			Object id=IKFunction.keyVal(one, "id");
		    map.put("next", id);
			map.put("describe", "总收益："+profit+"%");
			map.put("StockName",stockName);
			map.put("StockCode", stockCode);
			map.put("closing_cost", price);
			map.put("AddTime", time);
			map.put("option", option);
			map.put("UserName", name);
			map.put("website", "360股票");
			map.put("url", url);
			map.put("tid", name+time+stockName+option);
			map.put("id", IKFunction.md5(name+time+stockName+option));
			listmap.add(map);
		}
		return listmap;
	}
	
	
}
