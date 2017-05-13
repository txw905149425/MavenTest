package com.test.MongoMaven.crawler1.gncg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.bson.Document;

import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.PostData;


/*公牛选股    对应的版块下线了！！！  2017-05-10发现*/   
public class Crawler {
	
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		PostData post=new PostData();
		HashMap<String , String> map=new HashMap<String, String>();
		map.put("Content-Type", "application/x-www-form-urlencoded");
		map.put("Host", "api.gongniuchaogu.com");
		map.put("Connection", "Keep-Alive");
		map.put("X-Session-Token","7b512fabcce224c84f27ff09406228a4");
		String url="https://api.gongniuchaogu.com/api/zb/curCList";
		List<HashMap<String , Object>> listresult=new ArrayList<HashMap<String , Object>>();
		HashMap<String , Object> result = null;
	try {
			for(int i=117;i<200;i++){
				String data="t=1493184600&token=eb717c98c40d236dbf054c1e8875387eace3021a&h_id="+i+"&machine=HM+NOTE+1LTE&os=4.4.4&platform=2&protocolVersion=2.0&qudao=1000001&resolution=720*1280&type=new&uuid=866401022288545&version=2.4.4";
					String html=post.postHtml(url, map,data, "utf8", 2);
					System.out.println(html);
					if(html.length()<100){
						continue;
					}
						Object json=IKFunction.jsonFmt(html);
						Object js=IKFunction.keyVal(json, "data");
						Object list=IKFunction.keyVal(js, "lists");
						int  num=IKFunction.rowsArray(list);
						for(int j=1;j<=num;j++){
							result=new HashMap<String, Object>();
							Object one=IKFunction.array(list,j);
							Object que=IKFunction.keyVal(one, "quotes");
							Object time=IKFunction.keyVal(one, "time");
							if(!IKFunction.timeOK(time.toString())){
								continue;
							}
							String tt=IKFunction.timeFormat(time.toString());
							if("{}".equals(que.toString())){
								Object tmp=IKFunction.keyVal(one, "user");
								Object name=IKFunction.keyVal(tmp, "name");
								Object question=IKFunction.keyVal(one, "content");
								result.put("id", question+""+time);
								result.put("question", question);
								result.put("time", tt);
								result.put("name", name);
								result.put("website", "公牛炒股");
								result.put("json_str", one);
							}else{
								Object tmp=IKFunction.keyVal(one, "user");
								Object name=IKFunction.keyVal(tmp, "name");
								Object question=IKFunction.keyVal(que, "content");
								Object answer=IKFunction.keyVal(one, "content");
								result.put("id", question+""+time);
								result.put("question", question);
								result.put("answer", answer);
								result.put("time", tt);
								result.put("name", name);
								result.put("website", "公牛炒股");
								result.put("json_str", one);
							}
							listresult.add(result);
						}
						mongo.upsertDocByTableName(new Document("id",i), "ww_gncg_genius_id");
						System.out.println("id:"+i);
					
			}
			if(!listresult.isEmpty()){
				mongo.upsetManyMapByTableName(listresult, "ww_ask_online_all");	
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
}
