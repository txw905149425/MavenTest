package com.test.MongoMaven.crawler1.sina;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.StringUtil;

public class ParthMethod {
	public static List<HashMap<String, Object>> parseList(String html){
		List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
		Object json=IKFunction.jsonFmt(html);
		Object block=IKFunction.keyVal(json, "data");
		Object data=IKFunction.keyVal(block, "data");
		Object array=IKFunction.arrayFmt(data);
		int num=IKFunction.rowsArray(array);
		HashMap<String, Object> map=null;
		for(int i=1;i<=num;i++){
			map=new HashMap<String, Object>();
			Object js=IKFunction.array(array, i);
			Object question=IKFunction.keyVal(js, "q_content");
			Object answer=IKFunction.keyVal(js, "a_content");
			Object time=IKFunction.keyVal(js, "u_time");
			if(!IKFunction.timeOK(time.toString())){
				continue;
			}
			Object name=IKFunction.keyVal(js, "p_name");
			if(!StringUtil.isEmpty(answer.toString())){
				map.put("ifanswer","1");
			}else{
				map.put("ifanswer","0");
			}
			map.put("answer", answer);
			map.put("id", question+""+time);
			map.put("question", question);
			map.put("time", time);
			map.put("name", name);
			map.put("website", "新浪");
			map.put("json_str", js.toString());
//			System.out.println(i+"  "+question+" < "+answer+" > "+time+"   "+name);
			list.add(map);
		}
		return list;
	}
}
