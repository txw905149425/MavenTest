package com.test.MongoMaven.crawlerxg.gpdt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;


/**
 * 选股版块
 * 一个网站存一张表，每个网站中会有多个指标 多指标一样的存一张表
 * 取数据时
 * */
public class Test {
	
	public static void main(String[] args) {
		String url="https://sec.wedengta.com/getIntelliStock?action=CategoryDetail&AID=0&GUID=fbdfc7ee748c90e99d782e4ed8b15a35&DUA=SN%3DADRCJPH24_GA%26VN%3D242041111%26BN%3D0%26VC%3DXiaomi%26MO%3DHM%20NOTE%201LTE%26RL%3D720_1280%26CHID%3D10003_%26LCID%3D%26RV%3D%26OS%3DAndroid4.4.4%26DV%3DV1&IMEI=866401022288545&ticket=&dtCellphoneState=0&dtnickname=&dtheadimgurl=&dtMemberType=0&dtMemberEndTime=0&id=16&_=1493371309453";
		url="https://sec.wedengta.com/getIntelliStock?action=CategoryDetail&AID=0&GUID=fbdfc7ee748c90e99d782e4ed8b15a35&DUA=SN%3DADRCJPH24_GA%26VN%3D242041111%26BN%3D0%26VC%3DXiaomi%26MO%3DHM%20NOTE%201LTE%26RL%3D720_1280%26CHID%3D10003_10003%26LCID%3D%26RV%3D%26OS%3DAndroid4.4.4%26DV%3DV1&IMEI=866401022288545&ticket=&dtCellphoneState=0&dtnickname=&dtheadimgurl=&dtMemberType=0&dtMemberEndTime=0&id=14&_=1493631750547";
		url="https://sec.wedengta.com/getIntelliStock?action=CategoryDetail&AID=0&GUID=fbdfc7ee748c90e99d782e4ed8b15a35&DUA=SN%3DADRCJPH24_GA%26VN%3D242041111%26BN%3D0%26VC%3DXiaomi%26MO%3DHM%20NOTE%201LTE%26RL%3D720_1280%26CHID%3D10003_10003%26LCID%3D%26RV%3D%26OS%3DAndroid4.4.4%26DV%3DV1&IMEI=866401022288545&ticket=&dtCellphoneState=0&dtnickname=&dtheadimgurl=&dtMemberType=0&dtMemberEndTime=0&id=18&_=1493643379809https://sec.wedengta.com/getIntelliStock?action=CategoryDetail&AID=0&GUID=fbdfc7ee748c90e99d782e4ed8b15a35&DUA=SN%3DADRCJPH24_GA%26VN%3D242041111%26BN%3D0%26VC%3DXiaomi%26MO%3DHM%20NOTE%201LTE%26RL%3D720_1280%26CHID%3D10003_10003%26LCID%3D%26RV%3D%26OS%3DAndroid4.4.4%26DV%3DV1&IMEI=866401022288545&ticket=&dtCellphoneState=0&dtnickname=&dtheadimgurl=&dtMemberType=0&dtMemberEndTime=0&id=18&_=1493643379809";
		url="https://sec.wedengta.com/getIntelliStock?action=CategoryDetail&AID=0&GUID=fbdfc7ee748c90e99d782e4ed8b15a35&DUA=SN%3DADRCJPH24_GA%26VN%3D242041111%26BN%3D0%26VC%3DXiaomi%26MO%3DHM%20NOTE%201LTE%26RL%3D720_1280%26CHID%3D10003_10003%26LCID%3D%26RV%3D%26OS%3DAndroid4.4.4%26DV%3DV1&IMEI=866401022288545&ticket=&dtCellphoneState=0&dtnickname=&dtheadimgurl=&dtMemberType=0&dtMemberEndTime=0&id=17&_=1493644668700";
		url="https://sec.wedengta.com/getIntelliStock?action=CategoryDetail&AID=0&GUID=fbdfc7ee748c90e99d782e4ed8b15a35&DUA=SN%3DADRCJPH24_GA%26VN%3D242041111%26BN%3D0%26VC%3DXiaomi%26MO%3DHM%20NOTE%201LTE%26RL%3D720_1280%26CHID%3D10003_10003%26LCID%3D%26RV%3D%26OS%3DAndroid4.4.4%26DV%3DV1&IMEI=866401022288545&ticket=&dtCellphoneState=0&dtnickname=&dtheadimgurl=&dtMemberType=0&dtMemberEndTime=0&id=33&_=1493644924459";
		Map<String , String> result=HttpUtil.getHtml(url, new HashMap<String, String>(), "unicode", 1, new HashMap<String, String>());
		String html=result.get("html");
		MongoDbUtil mongo=new MongoDbUtil();
		HashMap<String, Object > records=null;
		HashMap<String, Object> stockList=null;
		List<HashMap<String, Object>> listMap=null;
		Object json=IKFunction.jsonFmt(html);
		Object js=IKFunction.keyVal(json, "content");
		System.out.println(js);
		Object list=IKFunction.keyVal(js, "vtDaySec");
		int num=IKFunction.rowsArray(list);
		for(int i=1;i<=num;i++){
			records=new HashMap<String, Object>();
			Object one=IKFunction.array(list, i);
			Object title=IKFunction.keyVal(one, "sTitle");
			Object time=IKFunction.keyVal(one, "sDate");
			Object vtIntelliStock=IKFunction.keyVal(one, "vtIntelliStock");
			int nums=IKFunction.rowsArray(vtIntelliStock);
			String str="";
			for(int j=1;j<=nums;j++){
				Object two=IKFunction.array(vtIntelliStock, j);
				Object sDtSecCode=IKFunction.keyVal(two, "sDtSecCode");
				if(j!=nums){
					str=str+sDtSecCode+"|";	
				}else{
					str=str+sDtSecCode;
				}
				
			}
			str=IKFunction.charEncode(str, "utf8");
			String durl="https://sec.wedengta.com/getSecInfo?AID=0&GUID=fbdfc7ee748c90e99d782e4ed8b15a35&DUA=SN%3DADRCJPH24_GA%26VN%3D242041111%26BN%3D0%26VC%3DXiaomi%26MO%3DHM%20NOTE%201LTE%26RL%3D720_1280%26CHID%3D10003_10003%26LCID%3D%26RV%3D%26OS%3DAndroid4.4.4%26DV%3DV1&IMEI=866401022288545&ticket=&dtCellphoneState=0&dtnickname=&dtheadimgurl=&dtMemberType=0&dtMemberEndTime=0&action=squote&seccode="+str+"&_="+System.currentTimeMillis();
			System.out.println(durl);
			result=HttpUtil.getHtml(durl, new HashMap<String, String>(), "unicode", 1, new HashMap<String, String>());
			html=result.get("html");
			Object djson=IKFunction.jsonFmt(html);
			Object djs=IKFunction.keyVal(djson, "content");
			Object vSecSimpleQuote=IKFunction.keyVal(djs, "vSecSimpleQuote");
			int size=IKFunction.rowsArray(vSecSimpleQuote);
			listMap=new ArrayList<HashMap<String,Object>>();
			for(int z=1;z<=size;z++){
				stockList=new HashMap<String, Object>();
				Object thread=IKFunction.array(vSecSimpleQuote, z);
				Object selecprice=IKFunction.keyVal(thread, "fClose");
				Object priceNow=IKFunction.keyVal(thread, "fNow");
				Object stockName=IKFunction.keyVal(thread, "sSecName");
				Object stockCode=IKFunction.keyVal(thread, "sDtSecCode").toString().substring(4);
				stockList.put("selecprice", selecprice);
				stockList.put("stockName", stockName);
				stockList.put("code", stockCode);
				stockList.put("priceNow", priceNow);
//				stockList.put("title", title);
//				stockList.put("time", time);
//				stockList.put("id", time+""+title+stockName);
//				mongo.upsertMapByTableName(stockList, "xg_gpdt/_stock");
				listMap.add(stockList);
			}
			records.put("title", title);
			records.put("website", "股票灯塔");
			records.put("time", time);
			records.put("id", time+""+title);
			records.put("list", listMap);
			mongo.upsertMapByTableName(records, "xg_gpdt_stock");
		}
		
	}
	
	
}
