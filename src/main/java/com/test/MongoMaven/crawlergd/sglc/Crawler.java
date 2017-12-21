package com.test.MongoMaven.crawlergd.sglc;

import java.util.ArrayList;
import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	public static void main(String[] args) {
		String url="http://napi.shagualicai.cn/public_kit/allteacherkitlist.shtml?page=1&index=2";
		MongoDbUtil mongo=new MongoDbUtil();
	  try{
		String html=HttpUtil.getHtml(url,new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>200){
			Object json=IKFunction.jsonFmt(html);
			Object data=IKFunction.keyVal(json, "data");
			int num=IKFunction.rowsArray(data);
			for(int i=1;i<=num;i++){
				Object one =IKFunction.array(data, i);
				Object timeobj=IKFunction.keyVal(one,"ctime");
				String time=IKFunction.timeFormat(timeobj.toString());
				if(!IKFunction.timeOK(time)){
					continue;
				}
				HashMap<String, Object> map = new HashMap<String, Object>();
				Object id=IKFunction.keyVal(one, "id");
				Object title=IKFunction.keyVal(one, "title");
				Object name=IKFunction.keyVal(one, "nickName");
				String durl="http://napi.shagualicai.cn/public_course/getcourseinfo.shtml?courseId="+id;
				String dhtml=HttpUtil.getHtml(durl,new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>()).get("html");
				if(!StringUtil.isEmpty(html)&&html.length()>200){
					Object djson=IKFunction.jsonFmt(dhtml);
					Object ddata=IKFunction.keyVal(djson, "data");
					Object xml=IKFunction.keyVal(ddata, "content");
					Object doc=IKFunction.JsoupDomFormat(xml);
					int size=IKFunction.jsoupRowsByDoc(doc, "p");
					ArrayList<HashMap<String, Object>> contList = new ArrayList<HashMap<String,Object>>();
					for(int j=0;j<size;j++){
						String cont=IKFunction.jsoupTextByRowByDoc(doc, "p",j);
						if(!StringUtil.isEmpty(cont)){
							HashMap<String, Object> map1 = new HashMap<String, Object>();
							map1.put("cont", cont);
							contList.add(map1);
						}
					}
					map.put("id", IKFunction.md5(title +""+ name));
					map.put("source", "傻瓜理财炒股");
					map.put("name", name);
					map.put("contentlist", contList);
					map.put("url", durl);
					map.put("contenthtml", xml);
					map.put("title", title);
					map.put("time", time);
					map.put("timedel", IKFunction.getTimeNowByStr("yyyy-MM-dd"));
					mongo.upsertMapByTableName(map, "lzx_viewpoint");
				}
			}
		}
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	}
}
