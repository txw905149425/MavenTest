//package com.test.MongoMaven.uitil;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.text.NumberFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Random;
//import java.util.WeakHashMap;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//
//import javax.xml.xpath.XPath;
//import javax.xml.xpath.XPathConstants;
//import javax.xml.xpath.XPathExpression;
//import javax.xml.xpath.XPathFactory;
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//
//import org.apache.commons.lang.StringEscapeUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Attributes;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.nodes.TextNode;
//import org.jsoup.safety.Whitelist;
//import org.jsoup.select.Elements;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//
//
//public class IKFunction {
//	private static Map<String, XPathExpression> xPathExpressMap = new WeakHashMap<String, XPathExpression>();
//	private static XPath xpathInstance = null;
//	public static Map<String, Pattern> patterns = new WeakHashMap<String, Pattern>();
//	static {
//		XPathFactory factory = XPathFactory.newInstance();
//		xpathInstance = factory.newXPath();
//	}
//
//	public static String removeUtf8space(Object str, int removeUtf8) {
//		if (StringUtil.isEmpty(str.toString())) {
//			return str.toString();
//		}
//		String result = "";
//		result = str.toString().replaceAll("^[　*| *| *|//s*]*", "").replaceAll("[　*| *| *|//s*]*$", "");
//		if (removeUtf8 == 1) {
//			byte bytes[] = { (byte) 0xC2, (byte) 0xA0 };
//			String UTFSpace = null;
//			try {
//				UTFSpace = new String(bytes, "utf-8");
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//			result = result.replaceAll(UTFSpace, "&nbsp;").replaceAll("&nbsp;", "");
//		}
//		return result;
//	}
//
//	public static String strNull(Object str) {
//		if(str == null){
//			return "";
//		}
//		if (str.toString().trim().equals("null")) {
//			return  "";
//		}
//		return str.toString().trim();
//	}
//
//	public static String regexpGroup(Object value, Object regexp, int index) {
//		if (value == null) {
//			return "";
//		}
//		try {
//			if (regexp == null || regexp.toString().isEmpty()) {
//				return "";
//			}
//			Pattern p = Pattern.compile(regexp.toString().trim());
//			Matcher m = p.matcher(value.toString().trim());
//			int count = m.groupCount();
//			String result = "";
//			if (count >= index && m.find()) {
//				result = m.group(index);
//			}
//			return result;
//		} catch (Exception es) {
//			return "";
//		}
//	}
//
//	public static String tmpGetDateBeforeSomeMouth(int interval) {
//		SimpleDateFormat sdf = null;
//		String time = null;
//		sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Calendar cal = Calendar.getInstance();
//		String today = sdf.format(cal.getTime());
//		int mouth = Integer.valueOf(today.substring(5, 7));
//		int year = Integer.valueOf(today.substring(0, 4));
//		int beforeMouth = 0;
//		if (mouth - interval <= 0) {
//			beforeMouth = mouth + 12 - interval;
//			year = year - 1;
//		} else {
//			beforeMouth = mouth - interval;
//		}
//		if (String.valueOf(beforeMouth).length() == 1) {
//			time = String.valueOf(year) + "0" + String.valueOf(beforeMouth) + today.substring(8, 10);
//		} else {
//			time = String.valueOf(year) + String.valueOf(beforeMouth) + today.substring(8, 10);
//		}
//		return time;
//
//	}
//
//	public static int tmp_getPageNum(Object totle) {
//		int page = 1;
//		if (StringUtil.isEmpty(totle.toString())) {
//			return page;
//		}
//		// num1=50; totle大于50 翻页 。翻页条件按num2算
//		int all = Integer.parseInt(totle.toString());
//		if (all > 50) {
//			if (all % 10 == 0) {
//				page = all / 10 - 1;
//			} else {
//				page = all / 10;
//			}
//		}
//		return page;
//	}
//
//	public static String tmp_jsoupMap2Js(Object html, Object selectorBlock, Object selectorClean, Object selectorHref, Object attr, Object add_url, Object regex) {
//		Document doc = (Document) html;
//		HashMap<String, String> map = new HashMap<String, String>();
//		Elements es2 = doc.select(selectorBlock.toString());
//		for (Element e : es2) {
//			Elements rs1 = e.select(selectorClean.toString());
//
//			Elements href_element = e.select(selectorHref.toString());
//			String url = "";
//			String href = "";
//			String key = "";
//			if (href_element.size() > 0) {
//				if (selectorHref.equals("script")) {
//					href = regexp(e.toString(), regex);
//					key = regexp(e.toString(), attr);
//				} else {
//					href = e.select(selectorHref.toString()).get(0).attr(attr.toString());
//					if (StringUtil.isNotEmpty(regex.toString())) {
//						href = regexp(href, regex);
//					} else {
//						href = regexp(href, "'([\\W\\w]*?)'");
//					}
//				}
//				if (!href.isEmpty()) {
//					if (add_url.toString().contains("REGEX")) {
//						url = add_url.toString().replace("REGEX", href);
//					} else {
//						url = add_url + href;
//					}
//				}
//			} else {
//				url = e.text();
//			}
//			if (add_url.toString().contains("REPLACE")) {
//				url = url.replace("REPLACE", "");
//				map.put(url, rs1.text().replaceAll(":", ""));
//			} else {
//				if (href != "") {
//					if (selectorHref.equals("script")) {
//						map.put(key, url);
//					} else {
//						map.put(rs1.text().replaceAll(":", ""), url);
//					}
//				}
//			}
//		}
//		map.remove("");
//		JSONObject json = JSONObject.fromObject(map);
//		return json.toString();
//	}
//
//	public static String getTimeStamp(Object obj) {
//		String flag = obj == null ? "" : obj.toString().trim();
//		if (StringUtil.isEmpty(flag)) {
//			return System.currentTimeMillis() + "";
//		} else {
//			if (flag.equalsIgnoreCase("ms")) {
//				return System.currentTimeMillis() + "";
//			}
//			if (flag.equalsIgnoreCase("s")) {
//				String tmp = System.currentTimeMillis() + "";
//				return tmp.substring(0, tmp.length() - 3);
//			}
//			return System.currentTimeMillis() + "";
//		}
//	}
//
//	public static String getTimeWithPre(Object html, Object regexpPre) {
//		String timePattern = "20\\d{2}[/\\-年](1[012]|0?[1-9])[\\-/月]([12][0-9]|3[01]|0?[1-9])日?\\s*(([1-5]|0?)[0-9][:点]([1-5]|0?)[0-9])?";
//		String StrReg = regexpPre.toString().trim();
//		if (StrReg != null && StrReg.length() > 0) {
//			int l = StrReg.length();
//			timePattern = StrReg.substring(l - 3, l) + ".*?" + timePattern;
//		}
//		Pattern pattern = Pattern.compile(timePattern);
//		Matcher matcher = pattern.matcher(html.toString());
//		ArrayList<String> times = new ArrayList<String>();
//		while (matcher.find()) {
//			String time = matcher.group();
//			String newTime = time.replaceAll("[/年月]", "-").replaceAll("日", "").replaceAll("\\s+", " ").replaceAll("\\-(?=[^0][^\\d])", "-0");
//			times.add(newTime);
//		}
//		String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//		String result = "0";
//		for (int i = 0; i < times.size(); i++) {
//			String time = times.get(i);
//			if ((time.compareTo(currentTime) < 0) && (time.compareTo(result) > 0))
//				result = time;
//		}
//		if (result == "0")
//			result = currentTime;
//		return result;
//	}
//
//
//	public static Object arrayFmt(Object obj) {
//		try {
//			if (obj == null || obj.toString() == "") {
//				return "";
//			}
//			String jsonstr = obj.toString();
//			if (jsonstr.startsWith("R")) {
//				jsonstr = jsonstr.substring(1);
//				if (jsonstr.contains("\\\"")) {
//					jsonstr = jsonstr.replaceAll("\\\\\"", "\"");
//				}
//			}
//			if (jsonstr.indexOf("[") != 0) {
//				jsonstr = jsonstr.substring(jsonstr.indexOf("["));
//			}
//			jsonstr = jsonstr.substring(0, jsonstr.lastIndexOf("]") + 1);
//			return JSONArray.fromObject(jsonstr);
//		} catch (Exception e) {
//			return new JSONArray();
//		}
//	}
//
//
//
//	// 5.通过相关编码格式化为网页浏览器Client可以识别的url
//	public static String urlFmt(Object value) {
//		try {
//			value = value.toString().replaceAll("\\s", "");
//			String urltmp = URLEncoder.encode(value.toString(), "GBK");
//			return urltmp;
//		} catch (Exception e) {
//			return "";
//		}
//	}
//
//	public static String urlEncode(Object url) {
//		try {
//			String value = url.toString();
//			String regex = "[\\u4E00-\\u9FA5]+";
//			Pattern p = Pattern.compile(regex);
//			String str = null;
//			Matcher m = p.matcher(value);
//			while (m.find()) {
//				str = m.group();
//				value = value.replaceAll(str, URLEncoder.encode(str, "UTF-8"));
//			}
//			return value;
//		} catch (Exception e) {
//			return "";
//		}
//	}
//
//	// 6. 排序格式化
//	public static String rankFmt(Object value, String type, int places) {
//		String vstr = value.toString();
//		String ret = "";
//		if (type.equals("0")) {
//			vstr = Double.valueOf(0.01 / Double.valueOf(vstr)).toString().replace(".", "");
//			if (vstr.contains("E")) {
//				int count = Integer.parseInt(vstr.substring(vstr.indexOf("-") + 1));
//				for (int i = 0; i < count; i++) {
//					vstr = "0" + vstr;
//				}
//				vstr = vstr.substring(0, vstr.indexOf("E"));
//			}
//			if (vstr.length() > places) {
//				vstr = vstr.substring(0, places);
//			}
//			ret = vstr;
//			for (int i = 0; i < places - vstr.length(); i++) {
//				ret += "0";
//			}
//		} else {
//			if (vstr.indexOf(".") != -1) {
//				vstr = vstr.substring(0, vstr.indexOf("."));
//			}
//			if (vstr.length() > places) {
//				for (int i = 0; i < places; i++) {
//					ret += "9";
//				}
//			} else {
//				ret = vstr;
//				for (int i = 0; i < places - vstr.length(); i++) {
//					ret = "0" + ret;
//				}
//			}
//		}
//
//		return ret;
//	}
//
//	// 7.格式化时间(yyyy-MM-dd HH:mm:ss)
//
//	public static String dateFmt(Object obj, String format) {
//		if (obj == null || obj.toString().isEmpty()) {
//			return "";
//		}
//		SimpleDateFormat sdf = null;
//		String today = "";
//		if (obj.equals("format")) {
//			sdf = new SimpleDateFormat(format);
//			Calendar cal = Calendar.getInstance();
//			today = sdf.format(cal.getTime());
//			return today;
//		}
//		String value = obj.toString();
//		value = value.toString().trim();
//		format = format.trim();
//		String[] formats = format.split("\\|");
//
//		sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Calendar cal = Calendar.getInstance();
//		today = sdf.format(cal.getTime());
//		cal.add(Calendar.DATE, -1);
//		String yesterday = sdf.format(cal.getTime());
//		cal.add(Calendar.DATE, -1);
//		String byesterday = sdf.format(cal.getTime());
//		String year = cal.get(Calendar.YEAR) + "";
//
//		long sysTime = System.currentTimeMillis();
//		if (value.indexOf("秒") != -1) {
//			long time = sysTime - Integer.parseInt(regexp(value, "(\\d*)")) * 1000;
//			SimpleDateFormat sdft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			java.util.Date dt = new Date(time);
//			return sdft.format(dt);
//
//		}
//		if (value.indexOf("分") != -1) {
//			long time = sysTime - Integer.parseInt(regexp(value, "(\\d*)")) * 60000;
//			SimpleDateFormat sdft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			java.util.Date dt = new Date(time);
//			return sdft.format(dt);
//		}
//		if (value.indexOf("半小时") != -1) {
//			long time = sysTime - 60000 * 30;
//			SimpleDateFormat sdft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			java.util.Date dt = new Date(time);
//			return sdft.format(dt);
//		}
//		if (value.indexOf("小时") != -1) {
//			long time = sysTime - Integer.parseInt(regexp(value, "(\\d*)")) * 60000 * 60;
//			SimpleDateFormat sdft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			java.util.Date dt = new Date(time);
//			return sdft.format(dt);
//		}
//		if (value.indexOf("天前") != -1) {
//			long time = sysTime - Integer.parseInt(regexp(value, "(\\d*)")) * 60000 * 60 * 24;
//			SimpleDateFormat sdft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			java.util.Date dt = new Date(time);
//			return sdft.format(dt);
//		}
//
//		if (value.indexOf("今天") != -1) {
//			return today + " " + regexp(value, "(\\d*:\\d*)") + ":00";
//		}
//		if (value.indexOf("昨天") != -1) {
//			return yesterday + " " + regexp(value, "(\\d*:\\d*)") + ":00";
//		}
//		if (value.indexOf("前天") != -1) {
//			return byesterday + " " + regexp(value, "(\\d*:\\d*)") + ":00";
//		}
//
//		for (String str : formats) {
//			try {
//				if (str.equals("EEE MMM dd HH:mm:ss Z yyyy")) {
//					SimpleDateFormat in = new SimpleDateFormat(str, Locale.US);
//					SimpleDateFormat out = new SimpleDateFormat(Constants.JAVA_DATE_FORMAT);
//					return out.format(in.parse(value));
//				}
//				SimpleDateFormat sdft = new SimpleDateFormat(str);
//				Date d = sdft.parse(value);
//				if (str.equals("MM月dd日HH:mm")) {
//					sdft.applyPattern(Constants.NOYEAR_DATE_FORMAT);
//					return year + "-" + sdft.format(d);
//				} else if (str.equals("MM-dd")) {
//					sdft.applyPattern(str);
//					return year + "-" + sdft.format(d) + " 00:00:00";
//				} else if (str.equals("MM-dd HH:mm")) {
//					sdft.applyPattern(str);
//					return year + "-" + sdft.format(d) + ":00";
//				} else {
//					sdft.applyPattern(Constants.JAVA_DATE_FORMAT);
//					return sdft.format(d);
//				}
//			} catch (ParseException e) {
//				continue;
//			}
//		}
//
//		return "";
//	}
//
//
//
//	// xpath列解析获取第row个匹配项（去除注释）
//	public String xPathCols(Object tidyDom, String xPath, int row) {
//		List<String> results = new ArrayList<String>();
//		try {
//			XPathExpression expr = xPathExpressMap.get(xPath);
//			if (expr == null) {
//				expr = xpathInstance.compile(xPath);
//				xPathExpressMap.put(xPath, expr);
//			}
//			NodeList pn = (NodeList) expr.evaluate(tidyDom, XPathConstants.NODESET);
//			for (int j = 0; j < pn.getLength(); j++) {
//				String s = getNodeText(pn.item(j), true).trim();
//				results.add(s);
//			}
//		} catch (Exception e) {
//		}
//		return results.size() > (row - 1) ? results.get(row - 1) : "";
//	}
//
//	// xpath列解析获取第row个匹配项（去除注释）
//	public String xPathRowInList(Object tidyDom, String xPath, int row) {
//
//		try {
//			XPathExpression expr = xPathExpressMap.get(xPath);
//			if (expr == null) {
//				expr = xpathInstance.compile(xPath);
//				xPathExpressMap.put(xPath, expr);
//			}
//			NodeList pn = (NodeList) expr.evaluate(tidyDom, XPathConstants.NODESET);
//			String s = getNodeText(pn.item(row), true).trim();
//			return s;
//		} catch (Exception e) {
//		}
//		// return results.size() > (row - 1) ? results.get(row - 1) : "";
//		return "";
//	}
//
//	// xpath行解析获取第一个匹配项（保留注释）
//	public String xPathCommentRows(Object tidyDom, String xPath) {
//		List<String> results = new ArrayList<String>();
//		try {
//			XPathExpression expr = xPathExpressMap.get(xPath);
//			if (expr == null) {
//				expr = xpathInstance.compile(xPath);
//				xPathExpressMap.put(xPath, expr);
//			}
//			NodeList pn = (NodeList) expr.evaluate(tidyDom, XPathConstants.NODESET);
//			for (int j = 0; j < pn.getLength(); j++) {
//				String s = getNodeText(pn.item(j), false).trim();
//				results.add(s);
//			}
//		} catch (Exception e) {
//		}
//		return results.size() > 0 ? results.get(0) : "";
//	}
//
//	// xpath列解析获取第row个匹配项（保留注释）
//	public String xPathCommentCols(Object tidyDom, String xPath, int row) {
//		List<String> results = new ArrayList<String>();
//		try {
//			XPathExpression expr = xPathExpressMap.get(xPath);
//			if (expr == null) {
//				expr = xpathInstance.compile(xPath);
//				xPathExpressMap.put(xPath, expr);
//			}
//			NodeList pn = (NodeList) expr.evaluate(tidyDom, XPathConstants.NODESET);
//			for (int j = 0; j < pn.getLength(); j++) {
//				String s = getNodeText(pn.item(j), false).trim();
//				results.add(s);
//			}
//		} catch (Exception e) {
//		}
//		return results.size() > (row - 1) ? results.get(row - 1) : "";
//	}
//
//	public Boolean isTheSameWebsite(Object xcurUrl, Object xwebSiteUrl) {
//		String curUrl = (String) xcurUrl;
//		String webSiteUrl = (String) xwebSiteUrl;
//		curUrl = curUrl.toLowerCase();
//		if (curUrl == "" || curUrl.contains("+") || curUrl.contains("javascript"))
//			return false;
//		if (curUrl.contains(webSiteUrl))
//			return true;
//		return false;
//	}
//
//	public Boolean isContentTitle(Object xtitle) {
//		String title = (String) xtitle;
//		if (title.trim().length() <= 5) {
//			return false;
//		}
//		return true;
//	}
//
//	// 获取内容
//	private static String getNodeText(Node node, boolean b) {
//		if (node.getLocalName().equals("#comment") && b) {
//			return "";
//		}
//		String value = node.getNodeValue().replaceAll(" ", " ");
//		NodeList nodes = node.getChildNodes();
//		if (nodes != null) {
//			for (int i = 0; i < nodes.getLength(); i++) {
//				value += getNodeText(nodes.item(i), b);
//			}
//		}
//		return value;
//	}
//
//	// 获取内容
//	private static String getNodeTextKeepTagSplit(Node node, boolean b) {
//		if (node.getLocalName().equals("#comment") && b) {
//			return "";
//		}
//		String value = node.getNodeValue().replaceAll(" ", " ");
//		;
//		NodeList nodes = node.getChildNodes();
//		if (nodes != null) {
//			for (int i = 0; i < nodes.getLength(); i++) {
//				value += ("\n" + getNodeTextKeepTagSplit(nodes.item(i), b));
//			}
//		}
//		return value;
//	}
//
//	public static String jsoupText(Object obj1, Object obj2) {
//		if (obj1 == null) {
//			return "";
//		}
//		return jsoupTextByDoc(Jsoup.parse(obj1.toString()), obj2.toString());
//	}
//
//	public static String jsoupTextByDoc(Object obj1, Object obj2) {
//		if (obj1 == null) {
//			return "";
//		}
//		String selector = obj2.toString().trim();
//		org.jsoup.nodes.Document soup = (org.jsoup.nodes.Document) obj1;
//		org.jsoup.select.Elements es = soup.select(selector);
//		if (es.size() > 0) {
//			String rs = es.text() == null ? "" : es.text().trim();
//			return rs;
//		} else {
//			return "";
//		}
//	}
//
//	public static int jsoupRows(Object html, String jsoup) {
//		org.jsoup.nodes.Document soup = Jsoup.parse(html.toString());
//		return jsoupRowsByDoc(soup, jsoup);
//
//	}
//
//	public static int jsoupRowsByDoc(Object html, String jsoup) {
//		if (html == null) {
//			return 0;
//		}
//		org.jsoup.nodes.Document soup = (Document) html;
//		org.jsoup.select.Elements es = soup.select(jsoup);
//		if (es.size() > 0) {
//			return es.size();
//		} else {
//			return 0;
//		}
//	}
//
//	// jsoup过滤标签解析
//	public String jsoupHtml(Object html, String jsoup) {
//		org.jsoup.nodes.Document soup = Jsoup.parse(html.toString());
//		org.jsoup.select.Elements newHtml = soup.select(jsoup);
//		if (newHtml != null) {
//			// Whitelist tags = new Whitelist();
//			// tags.addTags("div", "table", "tbody", "tr", "td", "p", "br",
//			// "ul",
//			// "li", "h1", "h2", "h3", "h4", "h5");
//			// tags.addAttributes("tag", "attr");
//			// String ret = Jsoup.clean(newHtml.html(), tags);
//			String ret = Jsoup.clean(newHtml.html(), Whitelist.simpleText());
//			return ret;
//		} else {
//			return "";
//		}
//	}
//
//	public static Object JsoupDomFormat(Object html) {
//		return Jsoup.parse(html.toString().trim());
//	}
//
//	// 抽取网页正文内容形式的网站，例如 新闻网页
//	public String jsoupContent(Object html) {
//		Document doc = Jsoup.parse(html.toString());
//		return jsoupContentByDoc(doc);
//	}
//
//	public String jsoupContentByDoc(Object obj) {
//		Document doc = (Document) obj;
//		Elements titleElement = doc.select("title");
//		String title = null;
//		if (titleElement != null) {
//			title = titleElement.get(0).text().trim().toLowerCase();
//		}
//		Elements bodys = doc.select("body");
//		if (bodys == null) {
//			return "";
//		}
//		Element just = bodys.get(0);
//		// Element just = body;
//		if (just != null) {
//			Elements ahref = just.select("a[href]");
//			// if (ahref != null)
//			// ahref.remove();
//			Elements css = just.select("style");
//			if (css != null)
//				css.remove();
//			Elements script = just.select("script");
//			if (script != null)
//				script.remove();
//			Elements iframe = just.select("iframe");
//			if (iframe != null)
//				iframe.remove();
//			// Elements img = just.select("img");
//			// if (img != null)
//			// img.remove();
//			ArrayList<Element> contents = new ArrayList<Element>();
//			Elements els = just.getAllElements();
//			// just.get
//
//			/*
//			 * for (int i = 0; i < els.size(); i++) { Element content =
//			 * els.get(i); Element current = content; if (isLeaves(content) &&
//			 * (countPunctuation(content.ownText()) >= 3)) { while
//			 * (isPassedTagName(content.tagName())){ current = content; content
//			 * = content.parent(); } content = current; if
//			 * ((contents.indexOf(content) == -1) &&
//			 * isValidContent(content.html())) { // System.out.println("addd");
//			 * contents.add(content); } }
//			 * 
//			 * }
//			 */
//
//			// getBlock(contents, just, title);
//
//			Elements children = just.children();
//			for (int i = 0; i < children.size(); i++) {
//				Element child = children.get(i);
//				title = getBlock(contents, child, title);
//			}
//			if (contents.size() == 0)
//				return "";
//			String result = "";
//			Element parent = contents.get(0).parent();
//			for (int i = 0; i < (contents.size()); i++) {
//				Element add = contents.get(i);
//				// if(add.parent().equals(parent))
//				result += add.text();// Jsoup.clean(add.html(), tags);
//				result += "\r\n";
//			}
//			// System.out.println(result);
//			return result.replaceAll("&.{2,5};|&#.{2,5};", " ");
//		}
//		return "";
//
//	}
//
//	static Whitelist tags = new Whitelist();
//	static {
//		tags.addTags("div", "table", "tbody", "tr", "td", "p", "br", "ul", "li", "h1", "h2", "h3", "h4", "h5", "img", "a");
//		tags.addAttributes("img", "src");
//		tags.addAttributes("a", "href");
//	}
//
//	public String jsoupContentWithLabel(Object html) {
//		Document doc = Jsoup.parse(html.toString());
//		return jsoupContentWithLabelByDoc(doc);
//	}
//
//	public String jsoupContentWithLabelByDoc(Object obj) {
//		Document doc = (Document) obj;
//		Elements titleElement = doc.select("title");
//		String title = null;
//		if (titleElement != null) {
//			title = titleElement.get(0).text().trim().toLowerCase();
//		}
//		Elements bodys = doc.select("body");
//		if (bodys == null) {
//			return "";
//		}
//		Element just = bodys.get(0);
//		// Element just = body;
//		if (just != null) {
//			Elements ahref = just.select("a[href]");
//			// if (ahref != null)
//			// ahref.remove();
//			Elements css = just.select("style");
//			if (css != null)
//				css.remove();
//			Elements script = just.select("script");
//			if (script != null)
//				script.remove();
//			Elements iframe = just.select("iframe");
//			if (iframe != null)
//				iframe.remove();
//			// Elements img = just.select("img");
//			// if (img != null)
//			// img.remove();
//			ArrayList<Element> contents = new ArrayList<Element>();
//			Elements els = just.getAllElements();
//
//			// just.get
//			/*
//			 * for (int i = 0; i < els.size(); i++) { Element content =
//			 * els.get(i); Element current = content; if (isLeaves(content) &&
//			 * (countPunctuation(content.ownText()) >= 3)) { while
//			 * (isPassedTagName(content.tagName())){ current = content; content
//			 * = content.parent(); } content = current; if
//			 * ((contents.indexOf(content) == -1) &&
//			 * isValidContent(content.html())) { // System.out.println("addd");
//			 * contents.add(content); } }
//			 * 
//			 * }
//			 */
//			// getBlock(contents, just, title);
//
//			Elements children = just.children();
//			for (int i = 0; i < children.size(); i++) {
//				Element child = children.get(i);
//				title = getBlock(contents, child, title);
//			}
//
//			if (contents.size() == 0)
//				return "";
//
//			String result = "";
//			Element parent = contents.get(0).parent();
//			for (int i = 0; i < (contents.size()); i++) {
//				Element add = contents.get(i);
//				// if(add.parent().equals(parent))
//				result += add.toString();// Jsoup.clean(add.html(), tags);
//				// result += "<br>";
//			}
//			// System.out.println(result);
//			return result;// .replaceAll("&.{2,5};|&#.{2,5};", " ");
//		}
//		return "";
//	}
//
//	private String getBlock(ArrayList<Element> contents, Element mine, String title) {
//		boolean hasMeetTitle = false;
//		if (title != null && mine.childNodeSize() > 0) {
//
//			String clear = mine.text().trim().toLowerCase();
//			if (title.startsWith(clear) && clear.length() > 1/*
//															 * &&text.length()>=
//															 * title
//															 * .length()*0.6
//															 */) {
//				title = null;
//				hasMeetTitle = true;
//			}
//		} else {
//			hasMeetTitle = true;
//		}
//		if (hasMeetTitle) {
//			if (contents.size() == 0) {
//				if (mine.tagName().equalsIgnoreCase("img")) {
//					contents.add(mine);
//					return title;
//				}
//			}
//		}
//		if (hasMeetTitle && isValidText(mine)) {
//			contents.add(mine);
//			return title;
//		} else {
//			Elements children = mine.children();
//			for (int i = 0; i < children.size(); i++) {
//				Element child = children.get(i);
//				title = getBlock(contents, child, title);
//			}
//		}
//		return title;
//	}
//
//	static Whitelist tagsForJudge = new Whitelist();
//	static {
//		tagsForJudge.addTags("div", "table", "tbody", "tr", "td", "p", "br", "ul", "li", "h1", "h2", "h3", "h4", "h5", "a");
//		tagsForJudge.addAttributes("a", "href");
//	}
//
//	private boolean isValidText(Element ele) {
//
//		String text = ele.toString();
//
//		// Elements e_forJudge = ele.getAllElements();
//
//		// tagsForJudge.addAttributes("a", "href");
//
//		text = Jsoup.clean(ele.toString(), tagsForJudge);
//		// System.err.println(xx);
//
//		double threshold = 0.5;
//		// double threshold = 0.2;
//		// Whitelist clearTag = new Whitelist();
//		String clear = ele.text();
//
//		if (clear.length() > threshold * text.length() && (countPunctuation(clear.toString()) > 2) && isValidContent(clear.toString())) {
//			return true;
//		}
//		return false;
//	}
//
//	private boolean isLeaves(Element el) {
//		// if(el.childNodeSize() > 0) return false;
//		Element own = el;
//		String[] validTags = { "br", "hr", "p" };
//		// Elements els = own.
//		for (int i = 0; i < validTags.length; i++) {
//			own.removeAttr(validTags[i]);
//		}
//		if (own.childNodeSize() > 0) {
//			for (int i = 0; i < own.childNodeSize(); i++)
//				System.out.println(i + ":" + own.childNode(i).nodeName());
//			return false;
//		}
//		return true;
//	}
//
//	private int countPunctuation(String content) {
//		String punctuations = "[。.，？！：、,?!]";
//		Pattern pattern = Pattern.compile(punctuations);
//		Matcher matcher = pattern.matcher(content);
//		int result = 0;
//		while (matcher.find()) {
//			result++;
//		}
//		// if(result >= 3) System.out.println(content);
//		return result;
//	}
//
//	private boolean isValidContent(String content) {
//		String clearRegex = "<[^>]+>";
//		String clear = content.replaceAll(clearRegex, "");
//		String notValidList = "论坛声明：|(可直接登录)|(帖子已删除)|(&copy;)|(指定的主题不存在)|(页面可能已被删除)|(浏览其他精彩内容)|(帖子不存在)|(404)";
//		Pattern pattern = Pattern.compile(notValidList);
//		Matcher matcher = pattern.matcher(clear);
//		if (matcher.find()) {
//			// System.out.println("matched:;::::" + matcher.group());
//			// System.out.println("matched:;::::" + clear);
//			return false;
//		}
//
//		if (clear.length() < 50) {
//			return false;
//		}
//		// System.out.println(clear);
//		return true;
//	}
//
//	private boolean isPassedTagName(String tagName) {
//		String tagNameList = "br,hr,table,tr,td,p,font,center,strong,tbody,h1,h2,h3,h4,h5,li,wbr,span";
//		if (tagNameList.contains(tagName))
//			return true;
//		return false;
//	}
//
//	private boolean isValidTagName(String tagName) {
//		String validList = "br,hr";
//		if (validList.contains(tagName)) {
//			return true;
//		}
//		return false;
//	}
//
//	// jsoup解析
//	public String jsoup(Object html, String jsoup) {
//		org.jsoup.nodes.Document soup = Jsoup.parse(html.toString());
//		org.jsoup.select.Elements newHtml = soup.select(jsoup);
//		return newHtml.toString();
//	}
//
//	// 获取数组index值(其中index从1开始计数) （淘宝店铺地区的拆分选择）
//	public static Object array(Object obj, int index) {
//		if (obj instanceof JSONArray) {
//			JSONArray array = (JSONArray) (obj);
//			if (array.size() >= index) {
//				return array.get(index - 1);
//			}
//		} else if (obj instanceof Object[]) {
//			Object[] array = (Object[]) (obj);
//			if (array.length >= index) {
//				return array[index - 1];
//			}
//		} else {
//			try {
//				JSONArray array = (JSONArray) JSONArray.fromObject(obj);
//				if (array.size() >= index) {
//					return array.get(index - 1);
//				}
//			} catch (Exception e) {
//			}
//		}
//		return "";
//	}
//
//	// 可以获取ajax 获取get 请求中的一些返回数据
//	public static String getFieldValue(Object obj, String pat, String split) {
//		// /////////////////////
//		/*
//		 * if (pat.startsWith("apiItemInfo")) { System.out.println("pat"); }
//		 */
//		// //////////////////////
//		if (obj == null || pat == null)
//			return "";
//		String str = obj.toString();
//		int pos = str.indexOf(pat);
//		if (pos < 0)
//			return "";
//		str = str.substring(pos + pat.length());
//		String[] ret = str.split(split);
//		if (ret != null && ret.length > 0) {
//			// 自定义处理url拼接函数--Jouenx
//			if (ret[0].startsWith("//detailskip")) {
//				ret[0] = "https:" + ret[0];
//			}
//			return ret[0];
//		}
//		return "";
//	}
//
//	// jounex--临时自定义处理从Tidydom读出的URL错误的函数（http://shop35466390.taobao.com////item.taobao.com/item.htm?id=44546936035/）
//	public Object formatTidydomUrl(Object obj) {
//		String strObj = obj.toString();
//		if (strObj.contains("com////")) {
//			int indexTmp = strObj.indexOf("com////") + 5;
//			strObj = "http:" + strObj.substring(indexTmp);
//			if (strObj.endsWith("/")) {
//				indexTmp = strObj.length();
//				strObj = strObj.substring(0, indexTmp - 1);
//			}
//			obj = strObj;
//		}
//		return obj;
//	}
//
//	// 注意此函数的修改导致了tb_items中的数据溢出，所以新增了Long可以单独针对处理
//	public static Integer ConvertString2Int(String v) {
//		v = v.replaceAll("\\D", "");// 去掉非数字字符
//		if (v.equals("")) {
//			return 0;
//		}
//		v = v.trim();
//		v = v.replaceAll(",", "");
//		Integer n = null;
//		try {
//			n = Integer.valueOf(v);
//		} catch (Exception e) {
//			e.printStackTrace();
//			if (Long.valueOf(v) > 2147483647) {
//				n = 2147483647;
//			}
//		}
//		return n;
//	}
//
//	// 注意此函数的修改导致了tb_items中的数据溢出，所以对函数进行重命名
//	public static Long ConvertString2Long(String v) {
//		v = v.replaceAll("\\D", "");
//		if (v.equals(""))
//			return 0l;
//		v = v.trim();
//		v = v.replaceAll(",", "");
//		return Long.valueOf(v);
//	}
//
//	public static Double ConvertString2Double(String v) {
//		v = v.replaceAll("\\D", "");
//		if (v.equals(""))
//			return 0.0;
//		v = v.trim();
//		v = v.replaceAll(",", "");
//		return Double.valueOf(v);
//	}
//
//	public int str2int(Object s) {
//		return Integer.valueOf(s.toString().trim());
//	}
//
//	public String obj2str(Object o) {
//		return o.toString();
//	}
//
//	public static String incUrlPageWithNum(Object url, String pagefield, Object incnum) {
//		Integer incNum = (Integer) incnum;
//		if (url == null || pagefield == null)
//			return "";
//		String str = url.toString();
//		String[] t;
//		int start = str.indexOf("?");
//		if (start > 0)
//			str = str.substring(start + 1);
//		t = str.split("&");
//		String newPageStr = null;
//		String oldPageStr = null;
//
//		for (String x : t) {
//			String[] y = x.split("=");
//			if (y != null && y[0].equalsIgnoreCase(pagefield)) {
//
//				if (y.length > 1) {
//					int page = Integer.valueOf(y[1]);
//					page += incNum;
//					newPageStr = y[0] + "=" + page;
//					oldPageStr = x;
//					break;
//				} else
//					return "";
//			}
//		}
//		if (newPageStr != null && oldPageStr != null) {
//			str = url.toString().replace(oldPageStr, newPageStr);
//			return str;
//		} else if (newPageStr == null) {
//			str = url + "&" + pagefield + "=2";
//		}
//		return "";
//	}
//
//	public static String incUrlPage(Object url, String pagefield) {
//		if (url == null || pagefield == null)
//			return "";
//		String str = url.toString();
//		String[] t;
//		int start = str.indexOf("?");
//		if (start > 0)
//			str = str.substring(start + 1);
//		t = str.split("&");
//		String newPageStr = null;
//		String oldPageStr = null;
//
//		for (String x : t) {
//			String[] y = x.split("=");
//			if (y != null && y[0].equalsIgnoreCase(pagefield)) {
//				if (y.length > 1) {
//					int page = Integer.valueOf(y[1]);
//					page++;
//					newPageStr = y[0] + "=" + page;
//					oldPageStr = x;
//					break;
//				} else
//					return "";
//			}
//		}
//		if (newPageStr != null && oldPageStr != null) {
//			str = url.toString().replace(oldPageStr, newPageStr);
//			return str;
//		} else if (newPageStr == null) {
//			str = url + "&" + pagefield + "=2";
//		}
//		return "";
//	}
//
//	// 从Url中抽出相关的id标识，店铺id或者宝贝id
//	public static String getUrlField(Object url, String field) {
//		if (url == null || field == null)
//			return "";
//		String str = url.toString();
//		String[] t;
//		int start = str.indexOf("?");
//		if (start > 0)
//			str = str.substring(start + 1);
//		t = str.split("&");
//		for (String x : t) {
//			String[] y = x.split("=");
//			if (y != null && y[0].equalsIgnoreCase(field)) {
//				if (y.length > 1)
//					return y[1];
//				else
//					return "";
//			}
//		}
//		return "";
//	}
//
//	// 正则解析
//	public static String regexp(Object value, Object regexp) {
//		if (value == null) {
//			return "";
//		}
//		try {
//			if (regexp == null || regexp.toString().isEmpty()) {
//				return "";
//			}
//			Pattern p = patterns.get(regexp.toString());
//			if (p == null) {
//				p = Pattern.compile(regexp.toString().trim());
//				patterns.put(regexp.toString().trim(), p);
//			}
//			Matcher m = p.matcher(value.toString().trim());
//			if (m.find()) {
//				return m.group(1).trim();
//			} else {
//				return "";
//			}
//		} catch (Exception es) {
//			return "";
//		}
//	}
//
//	public static String removeByReg(Object value, String regexp) {
//		if (regexp == null || regexp.toString().isEmpty()) {
//			return "";
//		}
//		Pattern p = patterns.get(regexp);
//		if (p == null) {
//			p = Pattern.compile(regexp);
//			patterns.put(regexp, p);
//		}
//		Matcher m = p.matcher(value.toString().replace("\n", ""));
//		if (m.find()) {
//			return m.replaceAll("");
//		} else {
//			return value.toString();
//		}
//	}
//
//	// key-value值获取，从Json形式的数据中取出需要的值
//	public Object keyVal(Object obj, Object key) {
//		if (key.toString().contains("bOcr")) {
//			JSONObject json = (JSONObject) obj;
//			Object value = json.get(key.toString());
//			// System.out.println();
//		}
//		Object value = null;
//		if (obj instanceof JSONObject) {
//			JSONObject json = (JSONObject) obj;
//			if (!json.containsKey(key.toString())) {
//				return "";
//			}
//			value = json.get(key.toString());
//		} else {
//			try {
//				JSONObject json = JSONObject.fromObject(obj);
//				if (!json.containsKey(key.toString())) {
//					return "";
//				}
//				value = json.get(key.toString());
//			} catch (Exception e) {
//			}
//		}
//		return value == null ? "" : value;
//	}
//
//	// md5处理
//	public static String md5(Object value) {
//		if (value == null || value.toString().isEmpty()) {
//			return "";
//		}
//		return StringUtil.getMD5Data(value.toString());
//	}
//
//	// split分割(淘宝店铺的拆分)
//	public static Object split(Object obj, String s) {
//		if (obj == null) {
//			return new ArrayList();
//		}
//		return obj.toString().split(s);
//	}
//
//	public static String replace_str(Object v1, Object x1, Object y1) {
//		String v = v1.toString().trim();
//		String x = x1.toString();
//		String y = y1.toString();
//		// String strTmp = v.replaceAll(x, y).trim();
//		// System.out.println(strTmp);
//		return v.replaceAll(x, y).trim();
//	}
//
//	public static String clear_blank_str(Object v) {
//		return v.toString().replaceAll("\\s", "").replaceAll("&nbsp", "");
//	}
//
//	// 字符串截取
//	public static String subStr(Object obj, int start, int end) {
//		if (obj == null) {
//			return "";
//		}
//		String str = obj.toString();
//		if (start < 0 && end <= 0 && start <= end && str.length() > -start) {
//			return str.substring(str.length() + start, str.length() + end);
//		}
//		if (start >= 0 && end >= 0 && start <= end) {
//			if (str.length() > end)
//				return str.substring(start, end);
//			else
//				return str.substring(start);
//		}
//		return str;
//	}
//
//	public static String gzip(Object primobj) throws UnsupportedEncodingException {
//		String str = primobj.toString();
//		if (str == null)
//			return null;
//		byte[] compressed;
//		ByteArrayOutputStream out = null;
//		ZipOutputStream zout = null;
//		String compressedStr = null;
//		try {
//			out = new ByteArrayOutputStream();
//			zout = new ZipOutputStream(out);
//			zout.putNextEntry(new ZipEntry("0"));
//			zout.write(str.getBytes());
//			zout.closeEntry();
//			compressed = out.toByteArray();
//			compressedStr = new sun.misc.BASE64Encoder().encodeBuffer(compressed);
//		} catch (IOException e) {
//			compressed = null;
//		} finally {
//			if (zout != null) {
//				try {
//					zout.close();
//				} catch (IOException e) {
//				}
//			}
//			if (out != null) {
//				try {
//					out.close();
//				} catch (IOException e) {
//				}
//			}
//		}
//		return compressedStr;
//	}
//
//	public static int strLength(Object str) {
//		return str.toString().trim().length();
//	}
//
//	public static String firstWord(Object obj) {
//		if (obj == null) {
//			return "";
//		}
//		String str = obj.toString();
//		String[] strList = str.split(" ");
//		if (strList.length > 1)
//			return strList[0];
//		return "";
//	}
//
//	// 向上取整
//	public static int divided(Object val1, Object val2) {
//		int page = 0;
//		try {
//			page = (int) Math.ceil(Double.parseDouble(val1.toString()) / Double.parseDouble(val2.toString()));
//		} catch (Exception e) {
//		}
//		return page;
//	}
//
//	// 默认值
//	public Object defval(Object obj, String defval) {
//		if (obj == null || obj.toString().isEmpty()) {
//			return defval;
//		} else {
//			return obj;
//		}
//	}
//
//	// 系统时间字符串 yyyy-MM-dd HH:mm:ss
//	public static String sysDateStr() {
//		return new SimpleDateFormat(Constants.JAVA_DATE_FORMAT).format(new Date());
//	}
//
//	public static Long sysDate() {
//		return System.currentTimeMillis();
//	}
//
//	public String getTime(Object html) {
//		String timePattern = "20\\d{2}[/\\-年](1[012]|0?[1-9])[\\-/月]([12][0-9]|3[01]|0?[1-9])日?\\s*(([1-5]|0?)[0-9][:点]([1-5]|0?)[0-9])?";
//		Pattern pattern = Pattern.compile(timePattern);
//		Matcher matcher = pattern.matcher(html.toString());
//		ArrayList<String> times = new ArrayList<String>();
//		while (matcher.find()) {
//			String time = matcher.group();
//			String newTime = time.replaceAll("[/年月]", "-").replaceAll("日", "").replaceAll("\\s+", " ").replaceAll("\\-(?=[^0][^\\d])", "-0");
//
//			times.add(newTime);
//			// System.out.println(newTime);
//		}
//		String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//		String result = "0";
//		for (int i = 0; i < times.size(); i++) {
//			String time = times.get(i);
//			if ((time.compareTo(currentTime) < 0) && (time.compareTo(result) > 0))
//				result = time;
//		}
//		if (result == "0")
//			result = currentTime;
//		return result;
//	}
//
//	public static String loadText(Class<?> _class, String path) {
//		System.out.println(_class.getPackage().getName());
//		String abs_path = _class.getPackage().getName().replaceAll("\\.", "/") + "/" + path;
//		System.out.println(abs_path);
//		InputStream in = _class.getClassLoader().getResourceAsStream(abs_path);
//		if (in == null) {
//			throw new RuntimeException("File not find");
//		}
//		BufferedReader br = new BufferedReader(new InputStreamReader(in));
//		StringBuilder text = new StringBuilder();
//		String line = null;
//		try {
//			while ((line = br.readLine()) != null) {
//				text.append(line + "\n");
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return text.toString();
//	}
//
//	public String getTime_bbs(Object html) {
//		String timePattern = "20\\d{2}[/\\-年](1[012]|0?[1-9])[\\-/月]([12][0-9]|3[01]|0?[1-9])日?\\s*(([1-5]|0?)[0-9][:点]([1-5]|0?)[0-9])?";
//		Pattern pattern = Pattern.compile(timePattern);
//		Matcher matcher = pattern.matcher(html.toString());
//		ArrayList<String> times = new ArrayList<String>();
//		while (matcher.find()) {
//			String time = matcher.group();
//
//			String newTime = time.replaceAll("[/年月]", "-").replaceAll("日", "").replaceAll("\\s+", " ").replaceAll("\\-(?=[^0][^\\d])", "-0");
//
//			times.add(newTime);
//			// System.out.println(newTime);
//		}
//		String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//		String result = currentTime;
//		for (int i = 0; i < times.size(); i++) {
//			String time = times.get(i);
//			if ((time.compareTo(currentTime) < 0) && (time.compareTo(result) < 0))
//				result = time;
//		}
//		// if(result == "0") result = currentTime;
//		return result;
//	}
//
//	// 系统时间年月日 yyyyMMdd
//	public static String sysDateYmd() {
//		return new SimpleDateFormat(Constants.DATE_FORMAT_YMD).format(new Date());
//	}
//
//	// 系统时间long
//	public String sysDateLong() {
//		return String.valueOf(System.currentTimeMillis());
//	}
//
//	// 数组字段串接字符串
//	public String array2Str(Object obj, String field, String separator, int row) {
//		String ret = "";
//		if (obj instanceof JSONArray) {
//			JSONArray jsonArray = (JSONArray) obj;
//			for (int i = (row - 1) * 20; i < jsonArray.size() && i < row * 20; i++) {
//				if (ret.length() > 0) {
//					ret += separator;
//				}
//				JSONObject json = JSONObject.parseObject(JSON.toJSONString(jsonArray.get(i)));
//				if (json.containsKey(field)) {
//					ret += json.getString(field);
//				}
//			}
//		} else if (obj instanceof String) {
//			try {
//				JSONArray jsonArray = (JSONArray) JSONArray.parse(JSON.toJSONString(obj));
//				for (int i = (row - 1) * 20; i < jsonArray.size() && i < row * 20; i++) {
//					if (ret.length() > 0) {
//						ret += separator;
//					}
//					JSONObject json = JSONObject.parseObject(JSON.toJSONString(jsonArray.get(i)));
//					if (json.containsKey(field)) {
//						ret += json.getString(field);
//					}
//				}
//			} catch (Exception e) {
//			}
//		}
//		return ret;
//	}
//
//	// 时间戳转时间格式
//	public String Long2FormatDate(String StrDate, String format) {
//		Long longDate = new Long(StrDate);
//		GregorianCalendar gc = new GregorianCalendar();
//		SimpleDateFormat df = new SimpleDateFormat(format);
//		gc.setTimeInMillis(longDate.longValue());
//		return df.format(gc.getTime());
//	}
//
//	// 计算时间差
//	public static long subTime(Object oneDate, Object twoDate, String dateFormat) {
//		Long subTime = 0L;
//		try {
//			Long oneDateLong = new SimpleDateFormat(dateFormat).parse(oneDate.toString()).getTime();
//			Long twoDateLong = new SimpleDateFormat(dateFormat).parse(twoDate.toString()).getTime();
//			subTime = oneDateLong - twoDateLong;
//		} catch (ParseException e) {
//			return 0;
//		}
//		return subTime;
//	}
//
//	// 数字格式化
//	public static String numFormat(Object value) {
//		if (value == null) {
//			return "";
//		}
//		return value.toString().replace(",", "").replace("￥", "").trim();
//	}
//
//	// 字符串包含
//	public static boolean myContains(Object value, String str) {
//		try {
//			return value.toString().contains(str);
//		} catch (Exception e) {
//			return false;
//		}
//	}
//
//	public static boolean judegObjIsNull(Object obj) {
//		if (obj == null) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	public static String objSubStr(Object obj, String val) {
//		try {
//			String str = obj.toString();
//			String[] valarray = val.split("-");
//			for (String ext : valarray) {
//				str = str.replace(ext, "");
//			}
//			return str;
//		} catch (Exception e) {
//			return "";
//		}
//	}
//
//	// words 用+分割多个关键词,看src 是否包含Words关键词
//	public static boolean isContainsMultipleWords(Object obj, Object words) {
//		if (words == null || obj == null) {
//			return false;
//		}
//		if (words.toString().contains("+")) {
//			String[] ws = words.toString().split("\\+");
//			String src = obj.toString().trim().toLowerCase();
//			for (String w : ws) {
//				if (!src.contains(w.trim().toLowerCase())) {
//					// if (src.length()>100){
//					// System.out.println(words+" "+w);
//					// System.out.println(src);
//					// }
//					return false;
//				}
//			}
//		}
//		if (words.toString().contains("|")) {
//			String[] ws = words.toString().split("\\|");
//			String src = obj.toString().trim().toLowerCase();
//			for (String w : ws) {
//				if (src.contains(w.trim().toLowerCase())) {
//					return true;
//				}
//			}
//		}
//		if (obj.toString().contains(words.toString().trim())) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	// 从URL连接中获取主链接
//	public String getHostFromURL(Object url) throws MalformedURLException {
//		String urlStr = url.toString();
//		if (!urlStr.startsWith("http")) {// 拼接请求头为标准URL格式，那样才能使得Gethost方法能正常获取
//			urlStr = "http://" + urlStr;
//		}
//		URL u = new URL(urlStr);
//		String x = u.getHost();
//		return x;
//	}
//
//	public String extractCssSrc(Object html, int row) throws Exception {// by
//		// chenjunliang
//		Document doc = Jsoup.parse((String) html, "UTF-8");
//		Pattern patternForStyle = Pattern.compile("\\.([^{}]*)\\{([^{}]*)\\}"); // Style
//		// regxp
//		final String DISPLAYNONE = "display:none";
//		int index = row;
//		String ip = "";
//		Elements ipElements = doc.select("#listable > tbody > tr:eq(" + index + ") > td:eq(1) > span");// html
//		// contains
//		// ip
//		for (Element ipElement : ipElements) {
//			HashMap<String, String> hideCss = new HashMap<String, String>(); // 不显示的
//			// CSS类集合标记
//			hideCss.put(DISPLAYNONE, DISPLAYNONE);
//			Element styleElement = ipElement.select("style").first();
//			Matcher m = patternForStyle.matcher(styleElement.html());
//			while (m.find()) {
//				String cssName = m.group(1).trim();
//				String cssStyle = m.group(2).trim();
//				if (cssStyle.equalsIgnoreCase(DISPLAYNONE)) {
//					hideCss.put(cssName, cssStyle);
//				}
//			}
//			for (org.jsoup.nodes.Node node : ipElement.childNodes()) {
//				if (node instanceof Element) {
//					Element ele = (Element) node;
//					String text = ele.text();
//					Attributes attrs = ele.attributes();
//					for (org.jsoup.nodes.Attribute attr : attrs) {
//						if (!hideCss.containsKey(attr.getValue())) {
//							ip += text;
//							break;
//						}
//					}
//				} else if (node instanceof TextNode) {
//					ip += ((TextNode) node).text();
//				}
//			}
//		}
//		return ip;
//	}
//
//	public String recognCountry(Object country) {
//		if (country.toString().equalsIgnoreCase("China")) {
//			return "internal";
//		}
//		return "foreign";
//	}
//
//	public static int static_rows(Object tidyDom, String xPath) {
//		if (tidyDom instanceof TagNode) {
//			TagNode node = (TagNode) tidyDom;
//			Object[] info_nodes;
//			try {
//				info_nodes = node.evaluateXPath(xPath);
//
//				if (info_nodes != null)
//					return info_nodes.length;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} else {
//			try {
//				XPathExpression expr = xPathExpressMap.get(xPath);
//				if (expr == null) {
//					expr = xpathInstance.compile(xPath);
//					xPathExpressMap.put(xPath, expr);
//				}
//				NodeList pn = (NodeList) expr.evaluate(tidyDom, XPathConstants.NODESET);
//				return pn != null ? pn.getLength() : 0;
//			} catch (Exception e) {
//			}
//		}
//		return 0;
//	}
//
//	public static String xPathByKeyword(Object tidyDom, String rowsXpath, String xPathSuffix, String xpathForAttrSuffix, String keywords) {
//		int rows = static_rows(tidyDom, rowsXpath);
//		String matcher = null;
//		if (rows == 0) {
//			return "";
//		}
//		try {
//			for (int i = 1; i <= rows; ++i) {
//				if (tidyDom instanceof TagNode) {
//					TagNode node = (TagNode) tidyDom;
//					Object[] info_nodes;
//					if (rows == 1) {
//						info_nodes = node.evaluateXPath(rowsXpath);
//					} else {
//						info_nodes = node.evaluateXPath(rowsXpath + "[" + i + "]" + xPathSuffix);
//					}
//					if (info_nodes != null) {
//						for (Object info_node : info_nodes) {
//							if (info_node instanceof TagNode) {
//								TagNode mynode = (TagNode) info_node;
//								String s = mynode.getText().toString().trim();
//								if (keywords.contains(";")) {
//									String[] matchers = keywords.split(";");
//									for (int j = 0; j < matchers.length; ++j) {
//										if (s.startsWith(matchers[j])) {
//											matcher = s;
//											if (xpathForAttrSuffix.length() > 1) {
//												info_nodes = node.evaluateXPath(rowsXpath + "[" + i + "]" + xPathSuffix + xpathForAttrSuffix);
//												matcher = info_nodes[0].toString();
//											}
//											break;
//										}
//									}
//								} else {
//									if (s.startsWith(keywords)) {
//										matcher = s;
//										if (xpathForAttrSuffix.length() > 1) {
//											info_nodes = node.evaluateXPath(rowsXpath + "[" + i + "]" + xPathSuffix + xpathForAttrSuffix);
//											matcher = info_nodes[0].toString();
//										}
//										break;
//									}
//								}
//							} else {
//								String s = info_node.toString().replaceAll("&amp;", "&").trim();
//								if (keywords.contains(";")) {
//									String[] matchers = keywords.split(";");
//									for (int j = 0; j < matchers.length; ++j) {
//										if (s.startsWith(matchers[j])) {
//											matcher = s;
//											if (xpathForAttrSuffix.length() > 1) {
//												info_nodes = node.evaluateXPath(rowsXpath + "[" + i + "]" + xPathSuffix + xpathForAttrSuffix);
//												matcher = info_nodes[0].toString();
//											}
//											break;
//										}
//									}
//								} else {
//									if (s.startsWith(keywords)) {
//										matcher = s;
//										if (xpathForAttrSuffix.length() > 1) {
//											info_nodes = node.evaluateXPath(rowsXpath + "[" + i + "]" + xPathSuffix + xpathForAttrSuffix);
//											matcher = info_nodes[0].toString();
//										}
//										break;
//									}
//								}
//							}
//						}
//					}
//				} else {
//					XPathExpression expr = null;
//					if (rows == 1) {
//						expr = xPathExpressMap.get(rowsXpath);
//					} else {
//						expr = xPathExpressMap.get(rowsXpath + "[" + i + "]" + xPathSuffix);
//					}
//					if (expr == null) {
//						if (rows == 1) {
//							expr = xpathInstance.compile(rowsXpath);
//							xPathExpressMap.put(rowsXpath, expr);
//						} else {
//							expr = xpathInstance.compile(rowsXpath + "[" + i + "]" + xPathSuffix);
//							xPathExpressMap.put(rowsXpath + "[" + i + "]" + xPathSuffix, expr);
//						}
//					}
//					NodeList pn = (NodeList) expr.evaluate(tidyDom, XPathConstants.NODESET);
//					for (int j = 0; j < pn.getLength(); j++) {
//						String s = getNodeText(pn.item(j), true).trim();
//						if (keywords.contains(";")) {
//							String[] matchers = keywords.split(";");
//							for (int k = 0; k < matchers.length; ++k) {
//								if (s.startsWith(matchers[k])) {
//									matcher = s;
//									if (xpathForAttrSuffix.length() > 1) {
//										expr = xpathInstance.compile(rowsXpath + "[" + i + "]" + xPathSuffix + xpathForAttrSuffix);
//										xPathExpressMap.put(rowsXpath + "[" + i + "]" + xPathSuffix + xpathForAttrSuffix, expr);
//										matcher = expr.evaluate(tidyDom, XPathConstants.NODESET).toString();
//									}
//									break;
//								}
//							}
//						} else {
//							if (s.startsWith(keywords)) {
//								matcher = s;
//								if (xpathForAttrSuffix.length() > 1) {
//									expr = xpathInstance.compile(rowsXpath + "[" + i + "]" + xPathSuffix + xpathForAttrSuffix);
//									xPathExpressMap.put(rowsXpath + "[" + i + "]" + xPathSuffix + xpathForAttrSuffix, expr);
//									matcher = expr.evaluate(tidyDom, XPathConstants.NODESET).toString();
//								}
//								break;
//							}
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return matcher == null ? "" : matcher.trim();
//	}
//
//	public static Integer countPages(Object tidyDom, String xpath) {
//		String rs = StaticxPathRows(tidyDom, xpath);
//		Pattern p = Pattern.compile("\\[[\\d]{1,3}\\]");
//		Matcher m = p.matcher(rs);
//		int i = 0;
//		while (m.find()) {
//			i++;
//		}
//		return i;
//	}
//
//	public static String jsoupKeepLabelWithClean(Object html, String selector, String cleanSelector) {
//		if (html == null) {
//			return null;
//		}
//		Document doc = Jsoup.parse(html.toString());
//		return jsoupKeepLabelWithCleanByDoc(doc, selector, cleanSelector);
//	}
//
//	public static String jsoupKeepLabelWithCleanByDoc(Object html, String selector, String cleanSelector) {
//		if (html == null) {
//			return null;
//		}
//		Document doc = (Document) (html);
//		if (StringUtil.isNotEmpty(cleanSelector)) {
//			if (cleanSelector.contains("::") && cleanSelector != null) {
//				String[] singleSelector = cleanSelector.split("::");
//				if (singleSelector.length >= 2) {
//					for (int i = 0; i < singleSelector.length; ++i) {
//						try {
//							doc.select(singleSelector[i]).remove();
//						} catch (Exception e) {
//						}
//					}
//				}
//			} else {
//				try {
//					doc.select(cleanSelector).remove();
//				} catch (Exception e) {
//				}
//			}
//		}
//		String result = doc.select(selector).toString().trim();
//		return result;
//	}
//
//	public static String imgUrlFill(Object html, Object refer) {
//		String referUrl = refer.toString();
//		char[] urlchars = referUrl.toCharArray();
//		for (int i = referUrl.length() - 1; i >= 0; --i) {
//			if (urlchars[i] == '/') {
//				referUrl = referUrl.substring(0, i + 1);
//			}
//		}
//		Document doc = Jsoup.parse(html.toString());
//		Elements es = doc.children();
//		for (int i = 0; i < es.size(); ++i) {
//			for (int j = 0; j < es.get(i).select("img").size(); ++j) {
//				String mergedUrl = null;
//				String imgAttr = es.get(i).select("img").get(j).attr("src").trim();
//				if (!imgAttr.startsWith("http")) {
//					mergedUrl = referUrl + imgAttr;
//					es.get(i).select("img").get(j).attr("src", mergedUrl);
//				}
//			}
//		}
//		return es.toString().trim();
//	}
//
//	public String xPathRowsByChildPath(Object tidyDom, String xPath, int level) {
//		List<String> results = new ArrayList<String>();
//		if (tidyDom instanceof TagNode) {
//			TagNode node = (TagNode) tidyDom;
//			Object[] info_nodes;
//			try {
//				info_nodes = node.evaluateXPath(xPath);
//
//				if (info_nodes != null) {
//					// CASTED TO A TAGNODE
//					for (Object info_node : info_nodes) {
//						if (info_node instanceof TagNode) {
//							TagNode mynode = (TagNode) info_node;
//							String s = mynode.getText().toString();
//							results.add(s);
//						} else {
//							// HOW TO RETRIEVE THE CONTENTS AS A STRING
//							String s = info_node.toString().replaceAll("&amp;", "&");// .getChildren().iterator().next().toString().trim();
//							results.add(s);
//						}
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} else {
//			try {
//				XPathExpression expr = xPathExpressMap.get(xPath);
//				if (expr == null) {
//					expr = xpathInstance.compile(xPath);
//					xPathExpressMap.put(xPath, expr);
//				}
//
//				NodeList pn = (NodeList) expr.evaluate(tidyDom, XPathConstants.NODESET);
//				for (int j = 0; j < pn.getLength(); j++) {
//					String s = getNodeText(pn.item(j), true).trim();
//					results.add(s);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return results.size() > 0 ? results.get(0).trim() : "";
//	}
//
//	public static String xPathRowsWithLengthLimit(Object tidyDom, String rowsXpath, String xPathSuffix, String minLengthStr, String maxLengthStr) {
//		int maxLength = Integer.valueOf(maxLengthStr);
//		int minLength = Integer.valueOf(minLengthStr);
//		int rows = static_rows(tidyDom, rowsXpath);
//		if (rows == 0) {
//			return "";
//		}
//		try {
//			for (int i = 1; i <= rows; ++i) {
//				if (tidyDom instanceof TagNode) {
//					TagNode node = (TagNode) tidyDom;
//					Object[] info_nodes;
//					if (rows == 1) {
//						info_nodes = node.evaluateXPath(rowsXpath);
//					} else {
//						info_nodes = node.evaluateXPath(rowsXpath + "[" + i + "]" + xPathSuffix);
//					}
//					if (info_nodes != null) {
//						for (Object info_node : info_nodes) {
//							if (info_node instanceof TagNode) {
//								TagNode mynode = (TagNode) info_node;
//								String s = mynode.getText().toString().trim();
//								s = s.toString().replaceAll("\\s", "").replaceAll("&nbsp", "").replaceAll(";", "");
//								if (!s.isEmpty() && s.length() < maxLength && s.length() > minLength) {
//									return s;
//								}
//							} else {
//								String s = info_node.toString().replaceAll("&amp;", "&").trim();
//								s = s.toString().replaceAll("\\s", "").replaceAll("&nbsp", "").replaceAll(";", "");
//								if (!s.isEmpty() && s.length() < maxLength && s.length() > minLength) {
//									return s;
//								}
//							}
//						}
//					}
//				} else {
//					XPathExpression expr = null;
//					if (rows == 1) {
//						expr = xPathExpressMap.get(rowsXpath);
//					} else {
//						expr = xPathExpressMap.get(rowsXpath + "[" + i + "]" + xPathSuffix);
//					}
//					if (expr == null) {
//						if (rows == 1) {
//							expr = xpathInstance.compile(rowsXpath);
//							xPathExpressMap.put(rowsXpath, expr);
//						} else {
//							expr = xpathInstance.compile(rowsXpath + "[" + i + "]" + xPathSuffix);
//							xPathExpressMap.put(rowsXpath + "[" + i + "]" + xPathSuffix, expr);
//						}
//					}
//					NodeList pn = (NodeList) expr.evaluate(tidyDom, XPathConstants.NODESET);
//					for (int j = 0; j < pn.getLength(); j++) {
//						String s = getNodeText(pn.item(j), true).trim();
//						s = s.toString().replaceAll("\\s", "").replaceAll("&nbsp", "").replaceAll(";", "");
//						if (s.isEmpty() && s.length() < maxLength && s.length() > minLength) {
//							return s;
//						}
//					}
//				}
//
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return "";
//	}
//
//	public static Float ConvertPercentage2Float(Object v) throws ParseException {
//		String str = v.toString();
//		str = str.trim().replaceAll("[\\s]*", "");
//		NumberFormat nf = NumberFormat.getPercentInstance();
//		Pattern p = Pattern.compile("([\\d|\\.|%]+)");
//		Matcher matcher = p.matcher(str);
//		if (matcher.find()) {
//			str = matcher.group(1);
//		}
//		if (str.equalsIgnoreCase("") || str == null) {
//			return null;
//		}
//		Number m = nf.parse(str);
//		return m.floatValue();
//	}
//
//	public static String jsoupWhitelistClean(Object obj_html, Object obj_WhiteAttr) {
//		String html = obj_html.toString();
//		String WhiteAttr = obj_WhiteAttr.toString();
//		Whitelist tags = new Whitelist();
//		if (WhiteAttr.contains(",")) {
//
//		}
//		String[] kv = WhiteAttr.split("-");
//		if (kv.length > 1) {
//			tags.addAttributes(kv[0], kv[1]);
//		}
//		tags.addTags("div", "table", "tbody", "tr", "td", "p", "br", "ul", "li", "h1", "h2", "h3", "h4", "h5", "h6", "strong", "u", "b", "em", "i");
//		String ret = Jsoup.clean(html, tags);
//
//		return ret;
//	}
//
//	public static String mergeNextPage(Object curUrl, Object character, Object pageNum, Object suffix) {
//		String nextPageUrl = curUrl.toString().trim() + character.toString() + pageNum.toString() + "." + suffix.toString();
//		return nextPageUrl.trim();
//	}
//
//	public static String mergeUrl(Object refer, Object suffix) {
//		if (StringUtil.isEmpty(suffix.toString()) || (StringUtil.isEmpty(refer.toString()) && !suffix.toString().startsWith("http"))) {
//			return null;
//		} else if (suffix.toString().startsWith("http")) {
//			return suffix.toString().trim();
//		}
//		String url = refer.toString().trim() + suffix.toString().trim();
//		return url.trim();
//	}
//
//	public static Integer randomInt(Object bound) {
//		bound = bound.toString().replaceAll("\\D*", "");
//		Random rd = new Random(System.currentTimeMillis());
//		if (bound == null || bound.toString().equals("")) {
//			bound = "100";
//		}
//		int num = rd.nextInt(Integer.valueOf(bound.toString()));
//		return num;
//	}
//
//	public static Integer numPlus(Object obj1, Object obj2) {
//		Double num1 = Double.valueOf(obj1.toString());
//		Double num2 = Double.valueOf(obj2.toString());
//		num2 = num1 + num2;
//		String results = num2.toString();
//		if (num2.toString().contains(".")) {
//			char[] rs = num2.toString().toCharArray();
//			int index = 0;
//			for (int i = 0; i < rs.length; ++i) {
//				if (rs[i] == '.') {
//					index = i;
//				}
//			}
//			results = num2.toString().substring(0, index);
//		}
//		return Integer.valueOf(results);
//	}
//
//	public static Integer numMultiply(Object obj1, Object obj2) {
//		Double num1 = Double.valueOf(obj1.toString());
//		Double num2 = Double.valueOf(obj2.toString());
//		num2 = num1 * num2;
//		String results = num2.toString();
//		if (num2.toString().contains(".")) {
//			char[] rs = num2.toString().toCharArray();
//			int index = 0;
//			for (int i = 0; i < rs.length; ++i) {
//				if (rs[i] == '.') {
//					index = i;
//				}
//			}
//			results = num2.toString().substring(0, index);
//		}
//		return Integer.valueOf(results);
//	}
//
//	public static String charEncode(Object char_obj, Object code_obj) {
//		String char_str = char_obj.toString().trim();
//		try {
//			char_str = URLEncoder.encode(char_str, code_obj.toString().trim());
//		} catch (UnsupportedEncodingException e) {
//
//		}
//		return char_str;
//	}
//
//	public static String judgeInValidContent(Object content, Object inValidMark) {
//		if (inValidMark == null) {
//			return content == null ? "" : content.toString().trim();
//		}
//		if (content == null) {
//			return "";
//		}
//		String str_content = content.toString().trim();
//		String str_mark = inValidMark.toString().trim();
//		if (str_mark.contains(";")) {
//			String[] str_marks = str_mark.split(";");
//			for (int i = 0; i < str_marks.length; ++i) {
//				String curMark = str_marks[i];
//				if (curMark.startsWith("=")) {
//					curMark = curMark.substring(1);
//					if (str_content.equalsIgnoreCase(curMark)) {
//						return "";
//					} else {
//						return str_content;
//					}
//				} else if (str_content.contains(curMark)) {
//					return "";
//				}
//			}
//		} else {
//			if (str_mark.startsWith("=")) {
//				str_mark = str_mark.substring(1);
//				if (str_content.equalsIgnoreCase(str_mark)) {
//					return "";
//				}
//			} else if (str_content.contains(str_mark)) {
//				return "";
//			}
//		}
//		return str_content;
//	}
//
//	public static String textarea2html(Object content) {
//		return StringEscapeUtils.unescapeHtml(content.toString().trim()).trim();
//	}
//
//	public static String cleanTransChar(Object obj) {
//		String content = obj.toString().trim();
//		content = content.replaceAll("&quot;", "\"").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&nbsp;", " ");
//		return content.trim();
//	}
//
//	public static String xPathListToBson(Object tidyDom, String rowsXpath, String xPathSuffix, int index) {
//		int rows = static_rows(tidyDom, rowsXpath);
//		String matcher = null;
//		org.bson.Document doc = new org.bson.Document();
//		if (rows == 0) {
//			return "";
//		}
//		try {
//			for (int i = 1; i <= rows; ++i) {
//				if (tidyDom instanceof TagNode) {
//					TagNode node = (TagNode) tidyDom;
//					Object[] info_nodes_key = {};
//					Object[] info_nodes_val = {};
//					String key = "";
//					String val = "";
//					if (rows == 1) {
//						info_nodes_key = node.evaluateXPath(rowsXpath);
//					} else {
//						info_nodes_key = node.evaluateXPath(rowsXpath + "[" + i + "]" + xPathSuffix + "[" + index + "]");
//						info_nodes_val = node.evaluateXPath(rowsXpath + "[" + i + "]" + xPathSuffix + "[" + (index + 1) + "]");
//					}
//					if (info_nodes_key != null) {
//						for (Object info_node : info_nodes_key) {
//							if (info_node instanceof TagNode) {
//								TagNode mynode = (TagNode) info_node;
//								key = mynode.getText().toString().trim();
//							} else {
//								String s = info_node.toString().replaceAll("&amp;", "&").trim();
//							}
//						}
//					}
//					if (info_nodes_val != null) {
//						for (Object info_node : info_nodes_val) {
//							if (info_node instanceof TagNode) {
//								TagNode mynode = (TagNode) info_node;
//								val = mynode.getText().toString().trim();
//							} else {
//								String s = info_node.toString().replaceAll("&amp;", "&").trim();
//							}
//						}
//					}
//					doc.append(key, val);
//				} else {
//					XPathExpression expr_key = null;
//					XPathExpression expr_val = null;
//					String key = "";
//					String val = "";
//					if (rows == 1) {
//						expr_key = xPathExpressMap.get(rowsXpath);
//						expr_val = xPathExpressMap.get(rowsXpath);
//					} else {
//						expr_key = xPathExpressMap.get(rowsXpath + "[" + i + "]" + xPathSuffix + "[" + (index) + "]");
//						expr_val = xPathExpressMap.get(rowsXpath + "[" + i + "]" + xPathSuffix + "[" + (index + 1) + "]");
//					}
//					if (expr_key == null) {
//						if (rows == 1) {
//							expr_key = xpathInstance.compile(rowsXpath);
//							xPathExpressMap.put(rowsXpath, expr_key);
//						} else {
//							expr_key = xpathInstance.compile(rowsXpath + "[" + i + "]" + xPathSuffix + "[" + (index) + "]");
//							xPathExpressMap.put(rowsXpath + "[" + i + "]" + xPathSuffix + "[" + (index) + "]", expr_key);
//
//							expr_val = xpathInstance.compile(rowsXpath + "[" + i + "]" + xPathSuffix + "[" + (index + 1) + "]");
//							xPathExpressMap.put(rowsXpath + "[" + i + "]" + xPathSuffix + "[" + (index + 1) + "]", expr_val);
//						}
//					}
//					NodeList pn_key = (NodeList) expr_key.evaluate(tidyDom, XPathConstants.NODESET);
//					NodeList pn_val = (NodeList) expr_val.evaluate(tidyDom, XPathConstants.NODESET);
//					key = getNodeText(pn_key.item(0), true).trim();
//					val = getNodeText(pn_val.item(0), true).trim();
//					doc.append(key, val);
//
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		matcher = doc.size() < 1 ? "" : doc.toJson();
//		return matcher == null ? "" : matcher.trim();
//	}
//
//	public static String replaceAllmaohao2Chinesemaohao(Object obj) {
//		if (obj != null && obj.toString().length() > 0) {
//			String rs = obj.toString().trim().replaceAll("\"", "“");
//			return rs;
//		} else {
//			return "";
//		}
//	}
//
//	public static String ExtrJosnListDefName(Object src, String listKey, String key_val) {
//		Object jsonarray = arrayFmt(extractJson(jsonFmt(src), listKey));// $ARRAYFMT()
//		int jsonRows = rowsArray(jsonarray);// $ROWSARRAY()
//		String names[] = key_val.split(";");
//		JSONArray jsonArray = new JSONArray();
//		for (int i = 1; i <= jsonRows; i++) {
//			if (names.length > 0) {
//				JSONObject inner_json = new JSONObject();
//				for (int j = 0; j < names.length; ++j) {
//					String name_value[] = names[j].split("-");
//					inner_json.put(name_value[0], extractJson(array(jsonarray, i), name_value[1]));
//
//				}
//				jsonArray.add(inner_json);
//			}
//		}
//		// System.err.println(jsonArray.toString());
//		return jsonArray.toString();
//	}
//
//	public static String ChinadateStrFormat(Object obj) {
//		String res = obj.toString().replaceAll("[\\u4E00-\\u9FA5]+", "-");
//		char[] temp = obj.toString().replaceAll("[\\u4E00-\\u9FA5]+", "-").toCharArray();
//		if (temp[temp.length - 1] == '-') {
//			res = res.substring(0, temp.length - 1);
//		}
//		return res;
//	}
//
//	public static String BaiduzhidaoImg2Text(Object html) {
//		if (html == null) {
//			return "";
//		}
//		TransImage2Text i2t = TransImage2Text.getTransImg2TxtIntance();
//		Document doc = Jsoup.parse(html.toString());
//		Elements elements = doc.select("img");
//		String ret = html.toString();
//		if (elements.size() > 0) {
//			ret = i2t.BaiduZhidaoTrans(html.toString()).trim();
//		}
//		return ret;
//	}
//
//	public static boolean obj2Boolean(Object obj) {
//		Object obj_ins = new Object();
//		obj_ins = obj;
//		if (obj_ins instanceof Integer) {
//			Integer i = Integer.valueOf(obj_ins.toString());
//			if (i != 0) {
//				return true;
//			} else {
//				return false;
//			}
//		}
//		if (obj_ins instanceof Boolean) {
//			if (Boolean.valueOf((boolean) obj_ins) == false) {
//				return false;
//			} else {
//				return true;
//			}
//		}
//		if (obj_ins instanceof String) {
//			boolean b = true;
//			try {
//				b = Boolean.valueOf(obj_ins.toString().trim());
//			} catch (Exception ex) {
//				return b;
//			}
//			return b;
//
//		}
//		return true;
//	}
//
//	public static String jsoupWhiteAttrOnly(Object html, Object WhiteAttr) {
//		Whitelist tags = new Whitelist();
//		if (WhiteAttr.toString().contains("-")) {
//			String[] kv = WhiteAttr.toString().split("-");
//			if (kv.length > 1) {
//				tags.addAttributes(kv[0], kv[1]);
//			} else if (kv.length == 1) {
//				tags.addTags(kv[0]);
//			}
//		}
//		String ret = Jsoup.clean(html.toString(), tags);
//		return ret;
//	}
//
//	public static boolean isEmptyString(Object str) {
//		if (str == null || StringUtil.isEmpty(str.toString()))
//			return true;
//		return false;
//	}
//
//	public static String jsoupKeepLabelByRow(Object html, Object selector, int row) {
//		if (html == null) {
//			return null;
//		}
//		Document doc = (Document) html;
//		if (selector.toString().contains(Constants.GaodigJsoupRowStr)) {
//			selector = selector.toString().replaceAll(Constants.GaodigJsoupRowStr, (row + 1) + "");
//			Elements es = doc.select(selector.toString());
//
//			if (es.size() > 0) {
//				return es.toString().trim();
//			} else if (es.size() < 1) {
//				return "";
//			}
//		}
//
//		Elements es = doc.select(selector.toString());
//		if (es.size() > row) {
//			Element el = es.get(row);
//			return el.toString().trim();
//		} else {
//			return "";
//		}
//	}
//
//	public static String jsoupTextByRow(Object html, Object selector, int row) {
//		if (html == null) {
//			return null;
//		}
//		Document doc = Jsoup.parse(html.toString());
//		return jsoupTextByRowByDoc(doc, selector, row);
//	}
//
//	public static String jsoupTextByRowByDoc(Object html, Object selector, int row) {
//		if (html == null) {
//			return null;
//		}
//		Document doc = (Document) html;
//		Element e = null;
//		if (selector.toString().contains(Constants.GaodigJsoupRowStr)) {
//			selector = selector.toString().replaceAll(Constants.GaodigJsoupRowStr, (row + 1) + "");
//			Elements es = doc.select(selector.toString());
//			// System.out.println(html.toString());
//			// System.out.println(selector);
//			if (es.size() > 0 && es.hasText()) {
//				return es.text().toString().trim();
//			} else if (es.size() > 0 && es.html().contains("script")) {
//				return es.html().toString().trim();
//			} else if (es.size() < 1) {
//				return "";
//			}
//		}
//		// System.out.println();
//		// System.out.println();
//		// System.out.println(html.toString());
//		// System.out.println(selector);
//		Elements es = doc.select(selector.toString());
//		if (es.size() > row) {
//			e = es.get(row);
//			if (e.hasText()) {
//				return e.text();
//			} else if (e.hasAttr("type")) {
//				if (e.select("[type]").attr("type").contains("text")) {
//					return e.html();
//				}
//			} else if (e.select("script").size() > 0) {
//				Element e_s = e.select("script").get(0);
//				return e.html();
//			} else {
//				return "";
//			}
//		}
//		if (es.size() == 1) {
//			return es.get(0).text();
//		}
//		return "";
//	}
//
//	public static String clickByRow(Object html, Object selector, int row) {
//		if (html == null) {
//			return null;
//		}
//		if (selector.toString().trim().startsWith("XPATH")) {
//			return "click:" + selector;
//		}
//		Document doc = (Document) html;
//		if (selector.toString().contains(Constants.GaodigJsoupRowStr)) {
//			selector = selector.toString().replaceAll(Constants.GaodigJsoupRowStr, (row + 1) + "");
//			Elements es = doc.select(selector.toString());
//			if (es.size() > 0) {
//				return "click:" + selector;
//			} else if (es.size() < 1) {
//				return "";
//			}
//		}
//		Elements es = doc.select(selector.toString());
//		if (es.size() >= row) {
//			return "click:" + "row:" + row + ":" + selector;
//		}
//		return "";
//	}
//
//	public static String read(String filename) {
//		try {
//			BufferedReader in = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(filename)), "utf-8"));
//			String s;
//			StringBuilder sb = new StringBuilder();
//			while ((s = in.readLine()) != null) {
//				sb.append(s + "\n");
//			}
//			in.close();
//			return sb.toString();
//		} catch (Exception ec) {
//			return "";
//		}
//	}
//
//	public static String jsoupListAttr(Object html, Object selector, Object attrName, int row) {
//		Document doc = Jsoup.parse(html.toString());
//		return jsoupListAttrByDoc(doc, selector, attrName, row);
//
//	}
//
//	public static String jsoupListAttrByDoc(Object html, Object selector, Object attrName, int row) {
//		Document doc = (Document) html;
//		String href = "";
//		if (selector.toString().contains(Constants.GaodigJsoupRowStr)) {
//			selector = selector.toString().replaceAll(Constants.GaodigJsoupRowStr, (row + 1) + "");
//			Elements es = doc.select(selector.toString());
//			// System.out.println(html.toString());
//			// System.out.println(selector);
//
//			if (es.size() > 0 && es.hasText()) {
//				String attrGet = es.get(0).attr(attrName.toString().trim());
//				if (StringUtil.isEmpty(attrGet)) {
//					attrGet = es.parents().get(0).attr(attrName.toString().trim());
//				}
//				return attrGet;
//			} else if (es.size() < 1) {
//				return "";
//			}
//		}
//		try {
//			href = doc.select(selector.toString().trim()).get(row).attr(attrName.toString().trim());
//		} catch (Exception e) {
//
//		}
//		return href;
//	}
//
//	public static Object yahuPageCount(Object page) {
//		return 20 * Integer.valueOf(page.toString()) - 19;
//	}
//
//	public static String splitByIndex(Object obj, String chars, int index) {
//		String rs = "";
//		if (chars.trim().contains(":")) {
//			String[] arr = chars.split(":");
//			for (int i = 0; i < arr.length; ++i) {
//				if (obj.toString().contains(arr[i])) {
//					int index_i = obj.toString().lastIndexOf(arr[i]);
//					if (index == 0) {
//						rs = obj.toString().substring(0, index_i);
//					} else {
//						int l = obj.toString().length();
//						rs = obj.toString().substring(index_i + 1, l);
//					}
//				} else {
//					continue;
//				}
//			}
//		} else {
//			int index_i = obj.toString().lastIndexOf(chars);
//			if (index == 0) {
//				rs = obj.toString().substring(0, index_i);
//			} else {
//				int l = obj.toString().length();
//				rs = obj.toString().substring(index_i + 1, l);
//			}
//			return rs;
//		}
//		return rs;
//	}
//
//	public static String joinJsonArrayByJsoupHexun(Object html) {
//		Object tidyDom = JsoupDomFormat(html.toString().trim());// JSOUPDOC
//		int rows = jsoupRowsByDoc(tidyDom, "#gkcTable>table>tbody>tr[class]"); // JSOUPROWSDOC
//		System.out.println("rows: " + rows);
//		JSONArray jsArr = new JSONArray();
//		if (rows > 0) {
//			for (int i = 1; i <= rows; ++i) {
//				JSONObject innerJs = new JSONObject();
//				String company_name = jsoupTextByRowByDoc(tidyDom, "#gkcTable>table>tbody>tr[class]:eq(" + i + ")>td:nth-child(1)", 0);// JSOUPTXTLISTDOC
//																																		// data-ya-type="thumbsUpclass="D-ib
//																																		// Mstart-23
//																																		// count
//				String job = jsoupTextByRowByDoc(tidyDom, "#gkcTable>table>tbody>tr[class]:eq(" + i + ")>td:nth-child(2)", 0);// JSOUPTXTLISTDOC
//																																// data-ya-type="thumbsUpclass="D-ib
//																																// Mstart-23
//																																// count
//				String take_office_time = jsoupTextByRowByDoc(tidyDom, "#gkcTable>table>tbody>tr[class]:eq(" + i + ")>td:nth-child(3)", 0);// JSOUPTXTLISTDOC
//																																			// data-ya-type="thumbsUpclass="D-ib
//																																			// Mstart-23
//																																			// count
//				String leave_office_time = jsoupTextByRowByDoc(tidyDom, "#gkcTable>table>tbody>tr[class]:eq(" + i + ")>td:nth-child(4)", 0);// JSOUPTXTLISTDOC
//																																			// data-ya-type="thumbsUpclass="D-ib
//																																			// Mstart-23
//																																			// count
//				String annual_pay = jsoupTextByRowByDoc(tidyDom, "#gkcTable>table>tbody>tr[class]:eq(" + i + ")>td:nth-child(5)", 0);// JSOUPTXTLISTDOC
//																																		// data-ya-type="thumbsUpclass="D-ib
//																																		// Mstart-23
//																																		// count
//				innerJs.put("公司名称", company_name.trim());
//				innerJs.put("职务", job.trim());
//				innerJs.put("任职日期", take_office_time.trim());
//				innerJs.put("离职日期", leave_office_time.trim());
//				innerJs.put("年薪(万元)", annual_pay.trim());
//				jsArr.add(innerJs);
//			}
//		}
//		return jsArr.toString().trim();
//	}
//
//	public static String handldTycCompanyName(Object companyName) {
//		String rs = companyName.toString();
//		if (rs.contains("已更名为:")) {
//			rs = rs.split("已更名为:")[0].trim();
//		}
//		if (rs.contains("曾用名:")) {
//			rs = rs.split("曾用名:")[0].trim();
//		}
//		if (rs.contains("已更名为：")) {
//			rs = rs.split("已更名为：")[0].trim();
//		}
//		if (rs.contains("曾用名：")) {
//			rs = rs.split("曾用名：")[0].trim();
//		}
//		return rs;
//	}
//
//	static void testJsoup() throws IOException {
//		String html = read("." + File.separator + "test");
//		System.out.println(html);
//		int row = 1; // div[@class='line content']/pre
//		int rows = 0;
//		ArrayList<String> arr_rs = new ArrayList<String>();
//		Object tidyDom = JsoupDomFormat(html);// JSOUPDOC
//		// rows = jsoupRowsByDoc(tidyDom, "#gkcTable>table>tbody>tr[class]");
//		// //JSOUPROWSDOC
//		System.out.println("rows: " + rows);
//		String rs = jsoupTextByRowByDoc(tidyDom, "div[class=ng-scope][ng-if=company.annuRepYearList.length>0]", 0);// JSOUPTXTLISTDOC
//																													// data-ya-type="thumbsUpclass="D-ib
//																													// Mstart-23
//																													// count
//		// System.out.println("rs1:"+rs);
//		// rs = jsoupTextByRowByDoc(tidyDom,
//		// "#ng-view > div.ng-scope > div > div > div > div.col-9.company-main > div > div:nth-child(1) > div.company_name_box.in-block.vertical-top > div > div.company_info_text > p > span",
//		// 0);//JSOUPTXTLISTDOC data-ya-type="thumbsUpclass="D-ib Mstart-23
//		// count
//		// rs= "张家口市盛唐药房医药有限公司怀来福泽园店";
//		System.out.println("rs2:" + rs);
//		System.exit(0);
//		rs = regexp(rs, "曾用名.(.*)");//
//		rs = handldTycCompanyName(rs);
//		// #gkcTable > table > tbody > tr:nth-child(2) > td
//		System.out.println("rs3:" + rs);
//		Object obj = jsonFmt("{" + rs + "}");// $JSONFMT
//		Object dateurl = extractJson(obj, "dateurl");// $EXTRACT_JSON()
//		Object dateArr = extractJson(obj, "dateArr");// $EXTRACT_JSON()
//		Object datearr = arrayFmt(dateArr); // $ARRAYFMT()
//		int jsrows = rowsArray(datearr); // $ROWSARRAY()
//		System.out.println("rw:" + jsrows);
//		Object a = array(datearr, 1);// $ARRAY(array,index)
//		Object brr = arrayFmt(a);// $ARRAYFMT()
//
//		Object f_date = array(brr, 1);// $ARRAY(array,index)
//		System.out.println(a);
//		System.out.println(f_date);
//		// System.out.println(obj);
//		// arr_rs.add("dateUrl"+": "+rs);
//
//		/*
//		 * rs = jsoupTextByRowByDoc(tidyDom,
//		 * "div[data-ya-type*=thumbsUp]>div[class*=D-ib Mstart-23 count]",
//		 * row-row);//JSOUPTXTLISTDOC data-ya-type="thumbsUpclass="D-ib
//		 * Mstart-23 count arr_rs.add("support"+": "+rs);
//		 * 
//		 * rs = regexp(rs,"(\\d+.*)"); arr_rs.add("time"+":"+rs);
//		 */
//
//		for (String s : arr_rs) {
//			System.out.println(s);
//		}
//		// partHtml= jsoupWhitelistClean(partHtml,"");
//		// partHtml= jsoupText(html,"div[id=articleText]>p");
//		// html = StringEscapeUtils.unescapeHtml(partHtml);
//		// partHtml = regexp(partHtml,"data-evaluate.*?(\\d+).*?");
//		// Object tidyDom = htmlCleanerFmt(html);
//		// System.out.println("Title:"+xPathRows(tidyDom, "//li[2]/h4/a[1]"));
//		// System.out.println("ShopId:"+xPathRows(tidyDom,
//		// "//li[2]/p[1]/span[2]/span/@data-item"));
//		// System.out.println("Title:"+xPathRows(tidyDom,
//		// "//div[@id='wgt-quality']/div[3]/div[1]"));
//		// System.out.println("ShopId:"+xPathRows(tidyDom,
//		// "//li[2]/div/div/@data-dsr"));
//
//	}
//
//	static void testXpath() throws IOException {
//		boolean isJson = true;
//		// String orignalHtml = read("." + File.separator + "test.txt");
//		String orignalHtml = read("." + File.separator + "test.txt");
//		String rs = null, rs1 = null, rs2 = null, rs3 = null, rs4 = null, rs5 = null, rs6 = null, rs7 = null;
//		Object tidyDom = null;
//		int row = 1;
//		row = 1;
//		row = 2;
//		row = 3;
//		if (!isJson) {
//			System.err.println("Parse common html... ");
//			tidyDom = htmlCleanerFmt(orignalHtml);
//			// tidyDom =tidyDomFmt(orignalHtml);// 已经过时了
//			row = 1;
//			int commonRow = 2;
//			int innerRow = 1;
//			// Integer rows = static_rows(tidyDom,
//			// ".//*[@id='zaiyaocontent']/div");
//			rs = xPathRows(tidyDom, "//*[@id='zaiyaocontent']/div[1]/span/font");
//			rs = xPathRows(tidyDom, "//*[@id='zaiyaocontent']/div[" + commonRow + "]/span/font");
//			System.out.println("report_year:" + rs);
//			Integer rows = static_rows(tidyDom, ".//*[@id='zaiyaocontent']/table[" + commonRow + "]/tbody/tr[@align='center']") - 1;
//			System.out.println("Inner_rows:" + rows);
//			rs = xPathRows(tidyDom, "//*[@id='zaiyaocontent']/table[" + commonRow + "]/tbody/tr[@align='center'][" + (innerRow + 1) + "]/td[1]");
//			System.out.println("项目名称" + rs);
//			rs = xPathRows(tidyDom, "//*[@id='zaiyaocontent']/table[1]/tbody/tr[@align='center'][" + (row + 1) + "]/td[2]");
//			rs = xPathRows(tidyDom, "//*[@id='zaiyaocontent']/table[" + commonRow + "]/tbody/tr[@align='center'][" + (innerRow + 1) + "]/td[2]1");
//			System.out.println("主营业务收入:" + rs);
//			rs = xPathRows(tidyDom, "//*[@id='zaiyaocontent']/table[" + commonRow + "]/tbody/tr[@align='center'][" + (innerRow + 1) + "]/td[3]");
//			System.out.println("主营业务成本" + rs);
//			rs = xPathRows(tidyDom, "//*[@id='zaiyaocontent']/table[" + commonRow + "]/tbody/tr[@align='center'][" + (innerRow + 1) + "]/td[4]");
//			System.out.println("主营业务毛利" + rs);
//			rs = xPathRows(tidyDom, "//*[@id='zaiyaocontent']/table[" + commonRow + "]/tbody/tr[@align='center'][" + (innerRow + 1) + "]/td[5]");
//			System.out.println("毛利率" + rs);
//
//			// rs2 =
//			// xPathRows(tidyDom,"//table[@class='stocktab mt6']/tbody/tr["+row+"]/td[2]/a/@href");
//			// rs5 = regexp(rs5,"(\\d.*\\d)");
//			// System.out.println(rs);
//		} else {
//			// http://stockdata.stock.hexun.com/gszl/data/jsondata/jbgk.ashx?count=2909&callback=hxbase_json15
//			System.err.println("Parse Json... ");
//			// $ARRAYFMT($EXTRACT_JSON($JSONFMT(html),"list"))
//			// $ROWSARRAY($ARRAYFMT($EXTRACT_JSON($JSONFMT(html),"list")))
//			orignalHtml = orignalHtml.replaceAll("\n", "");
//			Object jsonobj = jsonFmt(orignalHtml);// $JSONFMT()
//			Object jr = extractJson(jsonobj, "list");// $EXTRACT_JSON()
//			Object jsonarray = arrayFmt(jr);// $ARRAYFMT()
//			int jsrs = rowsArray(jsonarray);
//			System.out.println("rows:" + jsrs);
//			System.out.println(jsonarray);
//			Object inner_json = array(jsonarray, 1); // $ARRAY(array,list)
//			System.out.println("董监高姓名" + extractJson(inner_json, "panelrate"));// $EXTRACT_JSON()
//			System.out.println("URL" + extractJson(inner_json, "StockNameLink"));// $EXTRACT_JSON()
//			System.out.println("高管职务" + extractJson(inner_json, "industry"));// $EXTRACT_JSON()
//			System.out.println("任职日期" + extractJson(inner_json, "Number"));// $EXTRACT_JSON()
//			System.out.println("离职日期" + extractJson(inner_json, "Numdate"));// $EXTRACT_JSON()
//			System.out.println("学历" + extractJson(inner_json, "Stockdisc"));// $EXTRACT_JSON()
//			System.out.println("年薪(万元)" + extractJson(inner_json, "latestprice"));// $EXTRACT_JSON()
//			System.out.println("持股总额(万元)" + extractJson(inner_json, "Rcompany"));// $EXTRACT_JSON()
//			System.out.println("持股数量(万股)" + extractJson(inner_json, "Association"));// $EXTRACT_JSON()
//			// }
//			// Object str = array(jsonarray,2);//$ARRAY(array,index)
//			// Object obj1= extractJson(str,"isTmall");//$EXTRACT_JSON()
//			// obj1= extractJson(obj1,"dsrStr");//$EXTRACT_JSON()
//			// obj1= extractJson(obj1,"mas");//$EXTRACT_JSON()
//			// obj1=
//			// regexp(obj1.toString(),"user-rate-(.*?)\\.");//user-rate-UMCv4OFgSvCIT.htm
//			// System.out.println("obj:"+obj1);
//		}
//
//	}
//
//	static void parseHexunjson() {
//		String orignalHtml = null;
//		try {
//			orignalHtml = read("." + File.separator + "test.txt");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		// http://stockdata.stock.hexun.com/gszl/data/jsondata/jbgk.ashx?count=2909&callback=hxbase_json15
//		System.err.println("Parse Json... ");
//		orignalHtml = orignalHtml.replaceAll("\n", "");
//		Object jsonobj = jsonFmt(orignalHtml);// $JSONFMT()
//		Object jr = extractJson(jsonobj, "list");
//		Object jsonarray = arrayFmt(jr);// $ARRAYFMT()
//		int jsonRows = rowsArray(jsonarray);// $ROWSARRAY()
//		for (int i = 1; i <= jsonRows; i++) {
//			Object inner_json = array(jsonarray, i);
//			String name_code = extractJson(inner_json, "Stockname");
//			String num = extractJson(inner_json, "Number");
//			String domain = extractJson(inner_json, "deviation");
//			String type = extractJson(inner_json, "maincost");
//			String region = extractJson(inner_json, "district");
//			String url = extractJson(inner_json, "StockLink");
//			String name = regexp(name_code, "(.*)\\(");
//			String code = regexp(name_code, "(\\d+)");
//			String industry = extractJson(inner_json, "deviation");
//			String concept = extractJson(inner_json, "maincost");
//			String StockLink = extractJson(inner_json, "StockLink");
//			System.out.println(i + "," + name + "," + code + "," + industry + "," + concept + "," + region + "," + StockLink);
//			DealWithText.appendMethodB("a2.txt", i + "," + name + "," + code + "," + industry + "," + concept + "," + region + "," + StockLink + "\n", true);
//			// System.out.println(num+","+name +
//			// ""+","+code+","+domain+","+type+","+region+","+url);
//			// DealWithText.appendMethodB("a2.txt", num+","+name +
//			// ""+","+code+","+domain+","+type+","+region+","+url+"\n", true);
//			// }
//			// Object str = array(jsonarray,2);//$ARRAY(array,index)
//			// Object obj1= extractJson(str,"isTmall");//$EXTRACT_JSON()
//			// obj1= extractJson(obj1,"dsrStr");//$EXTRACT_JSON()
//			// obj1= extractJson(obj1,"mas");//$EXTRACT_JSON()
//			// obj1=
//			// regexp(obj1.toString(),"user-rate-(.*?)\\.");//user-rate-UMCv4OFgSvCIT.htm
//			// System.out.println("obj:"+obj1);
//
//		}
//
//	}
//
//	public static String extractJsonHexunList(Object html) {
//		Object obj = jsonFmt(html.toString().trim());
//		obj = extractJson(obj, "list");
//		if (obj.toString().trim().length() < 5) {
//			return "";
//		}
//		return obj.toString().trim();
//	}
//
//	public static Integer extractHealthKnowledgeRows(Object list0) {
//		JSONArray jr = (JSONArray) JSONArray.parse(JSON.toJSONString(list0));
//		return jr.size();
//	}
//
//	public static String extractHealthKnowledgelist(Object html) {
//		String regexp = "<a href=.(.*?).>";
//		Pattern p = Pattern.compile(regexp);
//		Matcher m = p.matcher(html.toString().trim());
//		JSONArray arr = new JSONArray();
//		while (m.find()) {
//			String rs = m.group(1).trim();
//			if (!arr.contains(rs)) {
//				arr.add(rs);
//				System.out.println(m.group(1));
//			}
//		}
//		return arr.toString();
//	}
//
//	public static String extractListString(Object list, Object index) {
//		JSONArray jr = (JSONArray) JSONArray.parse(JSON.toJSONString(list));
//		Integer index2 = Integer.valueOf(index.toString().trim());
//		return jr.getString(index2 - 1);
//	}
//
//	public static String judgeResultValid(Object results, Object keywords) {
//		String rs = results.toString().trim();
//		String ks = keywords.toString();
//		if (ks.length() > 0) {
//			String as[] = new String[50];
//			if (ks.contains(";")) {
//				as = ks.split(";");
//			} else {
//				as[0] = ks;
//			}
//			ArrayList<Boolean> bs = new ArrayList<Boolean>();
//			for (int i = 0; i < as.length; ++i) {
//				if (as[i] != null && as[i].length() > 0) {
//					Boolean bi = rs.contains(as[i]);
//					bs.add(bi);
//				}
//			}
//			if (bs.size() > 0) {
//				for (boolean b : bs) {
//					if (b == true) {
//						return "";
//					}
//				}
//			}
//		}
//
//		/*
//		 * if(rs.contains("不好意思")&&rs.contains("系统暂无")&&rs.contains("的相关知识")){
//		 * return ""; }
//		 */
//		return rs;
//	}
//
//	/*
//	 * 传入一个Jsoup doc ，根据selector选择器选择下标为num的元素，返回Jsoup doc(类似于过滤)
//	 */
//	public static Object tmpForChina(Object html, Object selector_div, int num) {
//		if (html == null) {
//			return null;
//		}
//		Document doc = (Document) html;
//		Document d = null;
//		Elements div = doc.select(selector_div.toString());
//		if (div.size() > 0) {
//			if (div.size() > num) {
//				d = Jsoup.parse(div.get(num).toString().trim());
//			}
//		}
//		return d;
//	}
//
//	public static Object tmpToJson(Object html, Object selector_tag, Object selector, Object selector_key, int num) {
//		if (html == null) {
//			return null;
//		}
//		Document doc = (Document) html;
//		HashMap<String, String> map = new HashMap<String, String>();
//		Element ul = null;
//		Elements tag = doc.select(selector_tag.toString());
//		if (tag.size() > num) {
//			ul = tag.get(num);
//			Elements es = ul.select(selector.toString());
//			if (es.size() > 0) {
//				for (Element e : es) {
//					String key = e.select(selector_key.toString()).remove().text();
//					map.put(key.replace("：", ""), e.text());
//				}
//			}
//		} else {
//			return null;
//		}
//		JSONObject json = JSONObject.parseObject(JSON.toJSONString(map));
//		System.out.println(json);
//		return json;
//	}
//
//	public static Object jsoupMap2Js(Object html, Object selectorBlock, Object selectorClean) {
//		Document doc = (Document) html;
//		HashMap<String, String> map = new HashMap<String, String>();
//		Elements es2 = doc.select(selectorBlock.toString());
//		for (Element e : es2) {
//			Elements rs1 = e.select(selectorClean.toString()).remove();
//			if (e.text() != "" && e.text().contains("：")) {
//				map.put(e.text().split("：")[0], e.text().split("：")[1]);
//			} else {
//				map.put(rs1.text().replaceAll(":", ""), e.text());
//			}
//		}
//		map.remove("");
//		JSONObject json = JSONObject.parseObject(JSON.toJSONString(map));
//		// System.out.println(json.toString());
//		return json;
//	}
//
//	public static Object tmpNeed(Object block, Object selector, int num) {
//		Document doc = (Document) block;
//		Object js = null;
//		// Elements es2 =doc.select("tr");
//		HashMap<String, String> map = new HashMap<String, String>();
//		Elements es2 = doc.select(selector.toString());
//		Elements tth = doc.select("th");
//		Element e = null;
//		if (es2.size() > 0 && es2.size() > num) {
//			e = es2.get(num);
//		}
//		if (e.toString().contains("table")) {
//			for (int i = 0; i < tth.size(); i++) {
//				Element th = tth.get(i);
//				Element td = null;
//				if (i >= 2) {
//					td = e.select("td").get(i + 1);
//				} else {
//					td = e.select("td").get(i);
//				}
//				// System.out.println(i+"  ------------- "+th.text()+"         =>>>>>>>>       "+td.text());
//				map.put(th.text(), td.text());
//			}
//		}
//		if (!map.isEmpty()) {
//			js = JSONObject.parseObject(JSON.toJSONString(map));
//		}
//		return js;
//	}
//
//	public static String judgeResultByLenth(Object results, Object index) {
//		String rs = results.toString().trim();
//		Long size = Long.valueOf(index.toString().trim());
//		if (rs.length() < size) {
//			return "";
//		}
//		return rs;
//	}
//
//	static void testRegex() throws IOException {
//		// String txt= read("."+File.separator+"test.txt");
//		String txt = "vrTimeHandle552write('1472117504')";
//		// String regexp = "(\\d+\\-\\d+\\-\\d+.*[\\d|:]*)";
//		String regexp = "\\(.(\\d+).\\)";
//		Pattern p = Pattern.compile(regexp);
//		Matcher m = p.matcher(txt);
//		ArrayList<String> list = new ArrayList<String>();
//		while (m.find()) {
//			String rs = m.group(1).trim();
//			if (!list.contains(rs)) {
//				list.add(rs);
//				System.out.println(m.group(1));
//			}
//		}
//	}
//
//	public static String insertValueBeforeChars(Object t, Object s, Object v) {
//		String rs = "";
//		try {
//			String target = t.toString().trim();
//			String splitStr = s.toString();
//			String value = v.toString();
//			int index = target.indexOf(splitStr);
//			String start = target.substring(0, index);
//			String end = target.substring(index);
//			rs = start + value + end;
//		} catch (Exception es) {
//
//		}
//		return rs.trim();
//	}
//
//	public static String getInnerJsonVal(Object obj1, Object obj) {
//		String jsonHtml = obj1 == null ? "" : obj1.toString().trim();
//		String jsonKey = obj == null ? "" : obj.toString().trim();
//		if (StringUtil.isEmpty(jsonKey)) {
//			return "";
//		}
//		jsonKey = "$.." + jsonKey;
//		if (jsonHtml.startsWith("R")) {
//			if (jsonHtml.contains("\\\"")) {
//				jsonHtml = jsonHtml.replaceAll("\\\\\"", "\"");
//			}
//		}
//		List<Object> cnt = JsonPath.read(jsonHtml, jsonKey);
//		if (cnt.size() > 0) {
//			return cnt.get(0) == null ? "" : cnt.get(0).toString().trim();
//		} else {
//			return "";
//		}
//	}
//
//	public static void main(String[] args) throws Exception {
//		String content = "joun0jounex";
//		String keys = "=0";
//		// keys = keys.substring(1);
//		String rs = judgeInValidContent(content, keys);
//		System.out.println("rs:" + keys);
//
//	}
//}