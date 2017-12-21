package com.test.MongoMaven.wx.thread;

import java.util.ArrayList;
import java.util.HashMap;
import com.test.MongoMaven.uitil.DataUtil;
import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.MongoDbUtil;
import com.test.MongoMaven.uitil.StringUtil;

public class Actions implements Runnable{
	private DataUtil util;
	private MongoDbUtil mongo;
	public Actions(DataUtil util,MongoDbUtil mongo){
		this.util=util;
		this.mongo=mongo;
	}
	
	public void run() {
		HashMap<String, String> map1=new HashMap<String, String>();
		map1.put("need", "true");
		map1.put("ip", "http-dyn.abuyun.com");
		map1.put("port", "9020");
		map1.put("user", "H1799393Q8VD2D1D");
		map1.put("pwd", "089AB8F589925559");
		
//		HashMap<String, String> map2=new HashMap<String, String>();
//		map2.put("need", "true");
//		map2.put("ip", "http-pro.abuyun.com");
//		map2.put("port", "9010");
//		map2.put("user", "HI2E0G3899HC411P");
//		map2.put("pwd", "120BA229EEF1CA79");
		
	    String url=util.getUrl();
	    String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8", 1, map1).get("html");
		String durl=getDurl(html);
		if(durl.length()>10){
//			System.out.println(">>>>>>>>>>>>>>>>>>"+durl);
			String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, map1).get("html");
			if(!StringUtil.isEmpty(dhtml)){
				if(dhtml.contains("验证码")){
					dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8", 1, map1).get("html");
				}
				if(!StringUtil.isEmpty(dhtml)){
					ArrayList<HashMap<String, Object>> ulist=WxUtil.parseWeixinGZH(dhtml,map1);
					if(!ulist.isEmpty()){
						try {
							mongo.upsetManyMapByTableName(ulist, "jg_wx_gzh");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						ulist=WxUtil.parseWeixinGZH(dhtml,map1);
						if(!ulist.isEmpty()){
							try {
								mongo.upsetManyMapByTableName(ulist, "jg_wx_gzh");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
						}
					}
			
				}
			}		
			
		}
		
	}
	
	public static String getDurl(String html){
		if(StringUtil.isEmpty(html)){
			return "";
		}
		Object doc=IKFunction.JsoupDomFormat(html);
		String durl=IKFunction.jsoupListAttrByDoc(doc, ".tit>a","href",0);
		return durl;
	}	
}
