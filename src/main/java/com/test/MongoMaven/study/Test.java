package com.test.MongoMaven.study;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.test.MongoMaven.uitil.FileUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.StringUtil;

public class Test {
  public static void main(String[] args) throws FileNotFoundException {
	  ArrayList<String> list=FileUtil.readFileReturn("cookie.txt");
	  PrintWriter pw=new PrintWriter(new File("table.txt"));
	  HashMap<String, String> map=new HashMap<String, String>();
	  for(String str:list){
		  String cook=IKFunction.regexp(str, "SNUID=(.*?);").trim();
		  if(!StringUtil.isEmpty(cook)){
			  map.put(cook, "1");
		  }
	  }
	  for (Map.Entry<String, String> entry : map.entrySet()) {
			pw.println(entry.getKey());
			System.out.println(entry.getKey());
		}
	  pw.close();
  }
  
  public static  String getCookie(){
		Map<String, String> res=HttpUtil.getHtml("http://weixin.sogou.com/", new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>());
		String test=res.get("cookie");
		String hh=IKFunction.regexp(test, "SUID=(.*?);");
		System.out.println(test);
		System.out.println(hh);
		String html=res.get("html");
		Object doc=IKFunction.JsoupDomFormat(html);
		String url=IKFunction.jsoupListAttrByDoc(doc, ".txt-box>h3>a", "href", 1);
		System.out.println(url);
		res=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, new HashMap<String, String>());
		String cook=res.get("cookie");
		String ht=res.get("html");
		System.out.println(cook);
//		System.out.println(ht);
		return hh;
	}
	
  
}
