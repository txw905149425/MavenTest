package com.test.MongoMaven.crawlergd.zncj;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.StringUtil;

public class Crawler {
	 public static void main(String[] args) {
		String url="http://appdata.zhiniu8.com/v2/weibo/mobile/assembleCommunityIndex?appId=1002&sign=null&__extInfo=%7B%22uuid%22%3A%221a5410e1-df8d-3882-beb1-1c2ae5b07a88%22%2C%22version%22%3A%223.18.1%22%2C%22pushId%22%3A%22ctd6q8jpljusa3d64p7c1go0pp%22%2C%22marketChannel%22%3A%22huawei%22%7D&data=%7B%22pageReq%22%3A%7B%22startIndex%22%3A0%2C%22pageSize%22%3A20%7D%2C%22showZones%22%3A%5B%7B%22sz%22%3A%22adver_shequ%22%7D%5D%2C%22platform%22%3A%22android%22%2C%22version%22%3A%223.18.1%22%7D";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8",1, new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&html.length()>300){
			Object json=IKFunction.jsonFmt(html);
			Object query=IKFunction.keyVal(json, "queryCommunityIndexRecommends");
			Object resp=IKFunction.keyVal(query,"pageResp");
			Object records=IKFunction.keyVal(resp,"records");
			int num=IKFunction.rowsArray(records);
			for(int i=1;i<=num;i++){
				Object one=IKFunction.array(records, i);
				Object timeObj=IKFunction.keyVal(one,"lastUpdateTime");
				String time=IKFunction.timeFormat(timeObj.toString());
				if(!IKFunction.timeOK(time)){
					continue;
				}
				Object expandObj=IKFunction.keyVal(one,"expandObj");
				Object title=IKFunction.keyVal(expandObj,"title");
				Object content=IKFunction.keyVal(one,"content");
				Object financeUser=IKFunction.keyVal(one,"financeUser");
				Object name=IKFunction.keyVal(financeUser,"yyNickName");
				
			}
		}
		
		
	 }
}
