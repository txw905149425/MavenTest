package com.test.MongoMaven;

import java.io.IOException;
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
	
	public static void main(String[] args) {
		 HttpUtil ht=new HttpUtil();
		 String url="http://www.creditchina.gov.cn/publicity_info_search?t=1481250169448";
		url="http://www.lnzc.gov.cn/SitePages/AfficheListAll1.aspx";
		 String html=null;
		 HashMap<String, String> map=new HashMap<String, String>();
		 map.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		 map.put("Accept-Encoding","gzip, deflate");
		 map.put("Connection","keep-alive");
		 map.put("Content-Type","application/x-www-form-urlencoded");
//		 map.put("Cookie","ASP.NET_SessionId=1vcfet55px2axhediwmmoeez; __CSRFCOOKIE=85f7a30a-ecab-4b13-921c-d7e2a86c6bcb");
		 map.put("X-Requested-With","XMLHttpRequest");
//		 try {
////				html=ht.getHtml(url, map, 1, 10000);
//			} catch (ClientProtocolException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		 Document doc=Jsoup.parse(html);
//		 __EVENTTARGET:ctl00$ctl00$ContentPlaceHolderMain$ContentPlaceHolderMain$g_8ac3be76_5de3_4bbf_a034_d924bd6995c4
//		 __EVENTARGUMENT:dvt_firstrow={26};dvt_startposition={Paged=TRUE&p_publishDate=20161219 16:00:00&p_ID=3540}
//		 __REQUESTDIGEST:0x8176EF9F7DAF5EF6C7B766179DC3F892B293CDC63A80BEC426258E0E41EAD90D87CDF63CD8DB7EB0D72D3374DDBC22D4BDAF2315882176699E19BDE0CB5CB639,18 Jan 2017 01:55:36 -0000
//		 __VIEWSTATE:/wEPBSpWU0tleTo1MmQ0NTY0MS1iN2I2LTRkMzAtOThmNi1jODk3MDA5YzhlNDVkTxrvdMwdwvDTpkIzGj5uggB65KioGm4odJnEQNFdZJQ=
		 String qaz=doc.select(".ms-paging>a").get(0).attr("href");
		 String wsx=regexp(qaz,"\\((.*?)\\)").replaceAll("'","");
		 String[] arr=wsx.split(",");
		 System.out.println(arr[0]);
		 System.out.println(arr[1]);
		 String __REQUESTDIGEST=doc.select("input[name=__REQUESTDIGEST]").get(0).attr("value");
		 String __VIEWSTATE=doc.select("input[name=__VIEWSTATE]").get(0).attr("value");
		 System.out.println(__REQUESTDIGEST);
		 System.out.println(__VIEWSTATE);
		 ArrayList<NameValuePair> list=new ArrayList<NameValuePair>();
		 list.add(new BasicNameValuePair("__EVENTTARGET",arr[0]));
		 list.add(new BasicNameValuePair("__EVENTARGUMENT",arr[1]));
		 list.add(new BasicNameValuePair("__REQUESTDIGEST",__REQUESTDIGEST));
		 list.add(new BasicNameValuePair("__VIEWSTATE",__VIEWSTATE));
//		 keyword=贺兰&searchtype=0&objectType=2&page=1
//		 keyword=%E8%B4%BA%E5%85%B0&searchtype=0&objectType=2&dataType=1&exact=0&page=1
//		 list.add(new BasicNameValuePair("searchtype","0"));
//		 list.add(new BasicNameValuePair("objectType","2"));
		 try {
			html=ht.postHtml(url, map, list, 1000, 1);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 System.out.println(html);
		 

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
