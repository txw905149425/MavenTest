package com.test.MongoMaven.crawler1;

import java.util.HashMap;

import com.test.MongoMaven.uitil.HttpUtil;
import com.test.MongoMaven.uitil.IKFunction;

public class WWtest {
	public static void main(String[] args) {
//		http://liven.9666.info/view.php?jsoncallback=jQuery1830931948125312255_1495785079156&un=nz_tffueq90&rid=331600&lmi=19306496&oid=2429562&cft=1495783506821&_=1495785089531
		String url="http://liven.9666.info/view.php?jsoncallback=jQuery1830931948125312255_1495785079156&un=nz_tffueq90&rid=331600";
		url="http://liven.9666.info/newBuys.php?jsoncallback=jQuery1830931948125312255_1495785079158&un=nz_tffueq90&rid=331600";
		url="http://liven.9666.info/others.php?un=zbny_fsx&rid=331600";
		long timenow=System.currentTimeMillis();
		long timeago=timenow-100*60*1000;
		url="http://api.upbaa.com/upbaa/service.jsp?op=MobileQueryChatMsgIncludeSelf&p1=%7B%22ifClear%22%3A1%2C%22targetId%22%3A-52%2C%22remindMaxTimestamp%22%3A"+timeago+"%2C%22maxTimestamp%22%3A"+timeago+"%2C%22userId%22%3A794590%7D&p2="+timenow+"%3AD8416376F15E8F2D854BA9DFE21ECD6F00000001495698531636";
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("User-Agent", "okhttp/3.2.0");
		String html=HttpUtil.getHtml(url, map, "utf8", 1, new HashMap<String, String>()).get("html");
//		System.out.println(html);
		Object json=IKFunction.keyVal(html, "returnCode");
		System.out.println(json);
	}
//	http://liven.9666.info/others.php?jsoncallback=jQuery18307346163673670338_1495782497787&un=nz_tffueq90&rid=331600&lmi=19306123&chi=37504519&lcdi=18&lsi=3408184&bid=2421657.html&oid=2429011&cft=1495782
	
}
