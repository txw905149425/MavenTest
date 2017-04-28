package com.test.MongoMaven.crawlerxg.test;

import java.util.HashMap;
import java.util.Map;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;

public class Test {
	
	public static void main(String[] args) {
		String url="https://sec.wedengta.com/getIntelliStock?action=CategoryDetail&AID=0&GUID=fbdfc7ee748c90e99d782e4ed8b15a35&DUA=SN%3DADRCJPH24_GA%26VN%3D242041111%26BN%3D0%26VC%3DXiaomi%26MO%3DHM%20NOTE%201LTE%26RL%3D720_1280%26CHID%3D10003_%26LCID%3D%26RV%3D%26OS%3DAndroid4.4.4%26DV%3DV1&IMEI=866401022288545&ticket=&dtCellphoneState=0&dtnickname=&dtheadimgurl=&dtMemberType=0&dtMemberEndTime=0&id=16&_=1493371309453";
		url="https://sec.wedengta.com/getSecInfo?AID=0&GUID=fbdfc7ee748c90e99d782e4ed8b15a35&DUA=SN%3DADRCJPH24_GA%26VN%3D242041111%26BN%3D0%26VC%3DXiaomi%26MO%3DHM%20NOTE%201LTE%26RL%3D720_1280%26CHID%3D10003_10003%26LCID%3D%26RV%3D%26OS%3DAndroid4.4.4%26DV%3DV1&IMEI=866401022288545&ticket=&dtCellphoneState=0&dtnickname=&dtheadimgurl=&dtMemberType=0&dtMemberEndTime=0&action=squote&seccode=0001002054%7C0001002544%7C0001002859%7C0001300577%7C0001300581%7C0001300617%7C0001300630%7C0101600139%7C0101600701%7C0101603037&_=1493373151530";
		url="https://sec.wedengta.com/getSecInfo?AID=0&GUID=fbdfc7ee748c90e99d782e4ed8b15a35&DUA=SN%3DADRCJPH24_GA%26VN%3D242041111%26BN%3D0%26VC%3DXiaomi%26MO%3DHM%20NOTE%201LTE%26RL%3D720_1280%26CHID%3D10003_10003%26LCID%3D%26RV%3D%26OS%3DAndroid4.4.4%26DV%3DV1&IMEI=866401022288545&ticket=&dtCellphoneState=0&dtnickname=&dtheadimgurl=&dtMemberType=0&dtMemberEndTime=0&action=squote&seccode=0001002172%7C0001002421%7C0001002560%7C0001002755%7C0001002782%7C0001300008%7C0001300499%7C0101600178%7C0101600496%7C0101600540%7C0101600797%7C0101600868%7C0101603029&_=1493374110218";
		Map<String , String> result=HttpUtil.getHtml(url, new HashMap<String, String>(), "unicode", 1, new HashMap<String, String>());
		String html=result.get("html");
		System.out.println(html);
		Object json=IKFunction.jsonFmt(html);
		Object js=IKFunction.keyVal(json, "content");
		System.out.println(js);
	}
	
	
}
