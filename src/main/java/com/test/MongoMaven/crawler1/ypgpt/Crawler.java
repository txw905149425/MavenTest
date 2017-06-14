package com.test.MongoMaven.crawler1.ypgpt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.http.client.ClientProtocolException;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;
import com.test.MongoMaven.uitil.StringUtil;


//优品股票通   数据量一般   抓取频率2分钟一次分钟
public class Crawler {
	static PostData post=new PostData();
	public static void main(String[] args) {
		String url="http://api.uptougu.com/live/featuredStageLiveList?deviceType=Android&appId=up&appVersion=4.4.8&platformType=app&channelNo=9510&manufacturer=Xiaomi&systemNo=4.4.4&deviceId=866401022288545&pageSize=20&pageNum=1";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		MongoDbUtil mongo=new MongoDbUtil();
		if(!StringUtil.isEmpty(html)){
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json, "resultData");
			Object rows=IKFunction.keyVal(data,"rows");
			int num =IKFunction.rowsArray(rows);
			try{
				for(int i=1;i<=num;i++){
					Object one=IKFunction.array(rows, i);
					Object name=IKFunction.keyVal(one,"userName");
					Object liveStageId=IKFunction.keyVal(one,"liveStageId");
					String userId=IKFunction.keyVal(one,"userId").toString();
					Object uid=IKFunction.keyVal(one,"liveId");
					String value="deviceType=Android&appId=up&appVersion=4.4.8&platformType=app&channelNo=9510&manufacturer=Xiaomi&systemNo=4.4.4&deviceId=866401022288545&pageSize=20&flag=1&maxLiveContentId=0&pageNum=1&liveStageId="+liveStageId+"&liveId="+uid+"&userId=";
					List<HashMap<String , Object>> list=parseDetail(value,userId);
					if(!list.isEmpty()){
						mongo.upsetManyMapByTableName(list, "ww_ask_online_all");
//						mongo.upsetManyMapByTableName(list, "ww_tzmb_ask_shares");
						for(HashMap<String, Object> two:list){
							two.remove("json_str");
							String ttmp=JSONObject.fromObject(two).toString();
							 String su= post.postHtml("http://localhost:8888/import?type=ww_stock_json",new HashMap<String, String>(),ttmp, "utf-8", 1);
								if(su.contains("exception")){
									System.err.println("写入数据异常！！！！  < "+su+" >");
								}
							}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	public static List<HashMap<String , Object>> parseDetail(String value,String userId){
		String url="http://api.uptougu.com/live/pullLiveStageMessage";
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("Content-Type", "application/x-www-form-urlencoded");
		map.put("Host", "api.uptougu.com");
		map.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.4; zh-cn; HM NOTE 1LTE Build/KTU84P) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
		List<HashMap<String , Object>> list=new ArrayList<HashMap<String , Object>>();
		HashMap<String , Object> result = null;
		try {
			String html=post.postHtml(url, map, value, "utf8", 2);
//			System.out.println(html);
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json, "resultData");
			Object rows=IKFunction.keyVal(data,"content");
			int num =IKFunction.rowsArray(rows);
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(rows, i);
				String  id=IKFunction.keyVal(one, "userId").toString();
				if(!userId.equals(id)){
					continue;
				}
				Object time=IKFunction.keyVal(one,"createTime");
				if(!IKFunction.timeOK(time.toString())){
					continue;
				}
				result=new HashMap<String, Object>();
				Object name=IKFunction.keyVal(one,"userName");
				Object answer=IKFunction.keyVal(one,"content");
				Object que=IKFunction.keyVal(one,"replyLiveMessage");
				Object question=IKFunction.keyVal(que,"content");
				if(!StringUtil.isEmpty(answer.toString())){
					map.put("ifanswer","1");
				}else{
					map.put("ifanswer","0");
				}
				result.put("id",question+""+time);
				result.put("question",question);
				result.put("name",name);
				result.put("answer",answer);
				result.put("time",time);
				result.put("json_str",one.toString());
				result.put("website","优品股票通");
				list.add(result);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
}
