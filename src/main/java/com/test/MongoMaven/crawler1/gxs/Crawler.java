package com.test.MongoMaven.crawler1.gxs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;

//股先生
public class Crawler {

	public static void main(String[] args) {
		try {
			MongoDbUtil mongo=new MongoDbUtil();
			PostData post=new PostData();
			for(int i=1;i<=3;i++){
				String url="https://content.api.guxiansheng.cn/index.php?c=question&a=get&pagesize=30&stock_code=&appId=android&key=&member_id=0&curpage="+i;
				String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8",1,new HashMap<String, String>()).get("html");
				if(!StringUtil.isEmpty(html)&&html.length()>200){
					List<HashMap<String, Object>> list=parse(html);
					if(!list.isEmpty()){
						mongo.upsetManyMapByTableName(list, "ww_ask_online_all");
//						for(HashMap<String, Object> one:list){
//							String ttmp=JSONObject.fromObject(one).toString();
//							 String su= post.postHtml("http://localhost:8888/import?type=ww_stock_json",new HashMap<String, String>(),ttmp, "utf-8", 1);
//								if(su.contains("exception")){
//									System.err.println("写入数据异常！！！！  < "+su+" >");
//								}
//					     }
					}
				}
			}
		  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
	}
	
	public static List<HashMap<String, Object>> parse(String html){
		Object json=IKFunction.jsonFmt(html);
		Object data=IKFunction.keyVal(json, "data");
		Object list=IKFunction.keyVal(data, "list");
		int num=IKFunction.rowsArray(list);
		List<HashMap<String, Object>> listmap=new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> map=null;
		for(int i=1;i<=num;i++){
			map=new HashMap<String, Object>();
			Object one=IKFunction.array(list, i);
			Object stockCode=IKFunction.keyVal(one, "stock_code");
			String name=IKFunction.keyVal(one, "seller_name").toString();
//			if(name.equals("趋势为王")){
//			System.out.println("ssss");	
//			}
			String stockName=IKFunction.keyVal(one, "stock_name").toString();
			Object question=stockName+"("+stockCode+")"+IKFunction.keyVal(one, "intro");
			Object answer=IKFunction.keyVal(one, "answer_intro");
			String timestr=IKFunction.keyVal(one, "answer_time").toString();
			if("0".equals(timestr)){
				timestr=IKFunction.keyVal(one, "question_time").toString();
			}
			if(!StringUtil.isEmpty(answer.toString())){
				if(answer.toString().contains("快来跟上明日牛股操作")||answer.toString().contains("每天更新最新的资讯信息及优质股")){
					answer="";
					map.put("ifanswer","0");
				}else{
					map.put("ifanswer","1");
				}
			}else{
				map.put("ifanswer","0");
			}
			String time=IKFunction.timeFormat(timestr);
			if(!IKFunction.timeOK(time)){
				continue;
			}
			map.put("id", IKFunction.md5(question+""+answer));
			map.put("tid", question+timestr);
			map.put("question", question);
			map.put("time",time);
			map.put("name", name);
			map.put("answer", answer);
			map.put("website", "股先生");
			listmap.add(map);
		}
		return listmap;
		
	}
	
	

}
