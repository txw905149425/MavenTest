package com.test.MongoMaven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Connection.KeyVal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.test.MongoMaven.uitil.HttpUtil;

public class Test {
	
	public static void main(String[] args) throws FileNotFoundException {
		String str1="00";
		String str2="0";
		PrintWriter pw=new PrintWriter(new File("d:/code2.txt"));
		for(int j=0;j<3;j++){
			String n="";
			if(j==0){
				n="000";
			}else if(j==1){
				n="001";
			}else{
				n="002";
			}
			for(int i=0;i<1000;i++){
				String num=String.valueOf(i);
				if(num.length()==1){
					num=n+str1+num;
				}else if(num.length()==2){
					num=n+str2+num;
				}else{
					num=n+num;
				}
				pw.println(num);
			}
		}
		pw.close();
		System.out.println("end!!!!");

	}
	public static String clear_blank_str(Object v) {
		return v.toString().replaceAll("\\s", "").replaceAll("&nbsp", "").replaceAll("\n", "");
	}
	public static Object jsonFmt(Object obj) {
		try {
			if (obj == null||obj.toString()=="") {
				return "";
			}
			String jsonstr = obj.toString();
			if (jsonstr.indexOf("{") != 0) {
				jsonstr = jsonstr.substring(jsonstr.indexOf("{"));
			}
			jsonstr = jsonstr.substring(0, jsonstr.lastIndexOf("}") + 1);
			return JSONObject.fromObject(jsonstr);
		} catch (Exception e) {
			return  new JSONObject();
		}

	}
	
	public static ArrayList<NameValuePair> fmtStr(String param) {
	ArrayList<NameValuePair> listParams = new ArrayList<NameValuePair>() ;
	try{
		// <expression><![CDATA["keyword@"+$KEYVAL(task,"keyword")+";searchtype@0;objectType@2;dataType@1;page@"+(page+1)]]></expression>
		if(param!=null){//write by txw
			if(param.contains(";")){
				String[] liststr=param.split(";");
				for(int i=0;i<liststr.length;i++){
					String keystr=liststr[i];
					if(keystr.contains("@")){
						listParams.add(new BasicNameValuePair(keystr.split("@")[0],keystr.split("@")[1]));
					}
				}
			}else{
				if(param.contains("@")){
					listParams.add(new BasicNameValuePair(param.split("@")[0],param.split("@")[1]));
				}
			}
		}
		}catch(Exception e){
//			e.printStackTrace();
		}
	return listParams;
	}
	public static Object jsonArrayFmt(Object obj) {
		try {
			if (obj == null||obj.toString()=="") {
				return "";
			}
			String jsonstr = obj.toString();
			if (jsonstr.indexOf("[") != 0) {
				jsonstr = jsonstr.substring(jsonstr.indexOf("["));
			}
			jsonstr = jsonstr.substring(0, jsonstr.lastIndexOf("]") + 1);
			if(jsonstr.contains("\\\"")){
				jsonstr=jsonstr.replaceAll("\\\\\"","\"");
			}
			return JSONArray.fromObject(jsonstr);
		} catch (Exception e) {
			return  new JSONArray();
		}
	}
	
	public static String regexp(Object value, Object regexp) {
		if (regexp == null || regexp.toString().isEmpty()) {
			return "";
		}
		Pattern p = Pattern.compile(regexp.toString().trim());
		Matcher m = p.matcher(value.toString().trim());
		if (m.find()) {
			return m.group(1).trim();
		} else {
			return "";
		}
	}
	
	public static String removeByReg(Object value, String regexp) {
		if (regexp == null || regexp.toString().isEmpty()) {
			return "";
		}
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(value.toString().replace("\n", ""));
		if (m.find()) {
			return m.replaceAll("");
		} else {
			return value.toString();
		}
	}
	
	// 获取数组index值(其中index从1开始计数) （淘宝店铺地区的拆分选择）
		public static Object array(Object obj, int index) {
			if (obj instanceof JSONArray) {
				JSONArray array = (JSONArray) (obj);
				if (array.size() >= index) {
					return array.get(index - 1);
				}
			} else if (obj instanceof Object[]) {
				Object[] array = (Object[]) (obj);
				if (array.length >= index) {
					return array[index - 1];
				}
			} else {
				try {
					JSONArray array = JSONArray.fromObject(obj);
					if (array.size() >= index) {
						return array.get(index - 1);
					}
				} catch (Exception e) {
				}
			}
			return "";
		}
	 
		
		public static Object keyVal(Object obj, Object key) {
			if (key.toString().contains("bOcr")) {
				JSONObject json = (JSONObject) obj;
				Object value = json.get(key.toString());
				System.out.println();
			}
			Object value = null;
			if (obj instanceof JSONObject) {
				JSONObject json = (JSONObject) obj;
				value = json.get(key.toString());
			} else {
				try {
					JSONObject json = JSONObject.fromObject(obj);
					value = json.get(key.toString());
				} catch (Exception e) {
				}
			}
			return value == null ? "" : value;
		}
		
		public static int rowsArray(Object obj) {
			if (obj instanceof JSONArray) {
				return ((JSONArray) obj).size();
			} else if (obj instanceof Object[]) {
				Object[] array = (Object[]) (obj);
				return array.length;
			} else {
				try {
					return JSONArray.fromObject(obj).size();
				} catch (Exception e) {
				}
			}
			return 0;
		}
		
//		function GenRand(){
//			
//			var util = new ActiveXObject("CAPICOM.Utilities");
//			var rand = util.BinaryToHex(util.GetRandom(128));
//			var i = 0;
//			var ansString = "";
//			var num;
//			var numString = "";
//			for(i=0;i<256;){
//				num = parseInt(rand.substr(i,2), 16);
//				numString += num + " " ;
//				ansString += String.fromCharCode(num);
//				i+=2;
//			}
//			//alert("TIME" + NowTime()+ "TIME");
//			//set the string to contentText
//			return "TIME" + NowTime()+ "TIME" + ansString;
//			
//		}
		public String GenRand(){
			
			
			return "TIME";
		}
		
}
