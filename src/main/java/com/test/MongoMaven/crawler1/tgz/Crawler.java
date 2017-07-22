package com.test.MongoMaven.crawler1.tgz;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;
import com.test.MongoMaven.uitil.StringUtil;

//数据量太少
public class Crawler {

	public static void main(String[] args) {
		String url="http://qa.tg.hexun.com/qa_list/index";
		String html=HttpUtil.getHtml(url, new HashMap<String, String>(), "utf8",1,new HashMap<String, String>()).get("html");
		if(!StringUtil.isEmpty(html)&&IKFunction.htmlFilter(html, ".fl.a-mod-p2>a[href]")){
			Object doc=IKFunction.JsoupDomFormat(html);
			int num=IKFunction.jsoupRowsByDoc(doc, ".fl.a-mod-p2>a[href]");
			for(int i=0;i<num;i++){
				String timeObj=IKFunction.jsoupTextByRowByDoc(doc, ".c-888",i);
				timeObj=IKFunction.regexp(timeObj, "(\\d{4}-\\d{2}-\\d{2})")+" 11:11:11";
				if(!IKFunction.timeOK(timeObj)){
					continue;
				}
				String tmp=IKFunction.jsoupListAttrByDoc(doc,".fl.a-mod-p2>a[href]", "href", i);
				String question=IKFunction.jsoupTextByRowByDoc(doc,".fl.a-mod-p2>a[href]",i);
				tmp=tmp.substring(1,tmp.length());
				String durl="http://qa.tg.hexun.com"+tmp;
				String dhtml=HttpUtil.getHtml(durl, new HashMap<String, String>(), "utf8",1,new HashMap<String, String>()).get("html");
				if(!StringUtil.isEmpty(dhtml)&&IKFunction.htmlFilter(dhtml, "#openWrap-all>.f-555")){
					Object ddoc=IKFunction.JsoupDomFormat(dhtml);
					String answer=IKFunction.jsoupTextByRowByDoc(ddoc, "#openWrap-all>.f-555", 0);
					String time=IKFunction.jsoupTextByRowByDoc(ddoc, "", 0);
				}
			}
			
		}
	}

}
