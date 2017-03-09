package com.test.MongoMaven.uitil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * @author wangyz
 *
 */
public class StringUtil {
	public static String HTML_META_PATTERN = "<\\s*meta\\s*([^>]*)\\s*>";
	public static String[] HTML_CHARSET_PATTERNS = { "charset\\s*=\\s*([^/\"]*)", "charset=\"([^\"]*)\"" };

	/**
	 * 空串判断
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * 非空串判断
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return str != null && str.trim().length() > 0;
	}

	/**
	 * 字符串转换为列表
	 * @param value
	 * @return
	 */
	public static List<String> String2List(String value) {
		List<String> ret = new ArrayList<String>();
		if (value == null || "".equals(value)) {
			return ret;
		}
		value = value.replaceAll("，", ",");
		String[] t = value.split(",");
		for (int i = 0; i < t.length; i++) {
			ret.add(t[i]);
		}
		return ret;
	}

	/**
	 * 列表转换为字符串
	 * @param value
	 * @return
	 */
	public static String List2String(List<String> value) {
		if (value.size() == 0) {
			return "";
		}
		if (value.size() == 1) {
			return value.get(0);
		}
		String ret = value.get(0);
		for (int i = 1; i < value.size(); i++) {
			ret = ret + "," + value.get(i);
		}
		return ret;
	}

	/**
	 * 计算字符串MD5值
	 * @param content
	 * @return
	 */
	public static String getMD5Data(String content) {
		try {
			byte[] src = content.getBytes("GBK");
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(src);
			return byte2hex(md5.digest()).toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 新的md5签名，首尾放secret。 param 分配给您的APP_SECRET	
	 * @param params
	 * @param secret
	 * @return
	 */
	public static String md5Signature(TreeMap<String, String> params, String secret) {
		String result = null;
		StringBuffer orgin = getBeforeSign(params, new StringBuffer(secret));
		if (orgin == null) {
			return result;
		}
		orgin.append(secret);
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			result = byte2hex(md.digest(orgin.toString().getBytes("utf-8")));
		} catch (Exception e) {
			throw new java.lang.RuntimeException("sign error !");
		}
		return result;
	}

	/**
	 * 添加参数的封装方法
	 * @param params
	 * @param orgin
	 * @return
	 */
	private static StringBuffer getBeforeSign(TreeMap<String, String> params, StringBuffer orgin) {
		if (params == null) {
			return null;
		}
		for (String key : params.keySet()) {
			orgin.append(key).append(params.get(key));
		}
		return orgin;
	}

	/**
	 * 二进制转字符串
	 * @param b
	 * @return
	 */
	private static String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer();
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs.append("0").append(stmp);
			} else {
				hs.append(stmp);
			}
		}
		return hs.toString().toUpperCase();
	}

	/**
	 * 拼接URL
	 * @param map
	 * @return
	 */
	public static String buildUrl(TreeMap<String, String> map) {
		StringBuilder param = new StringBuilder();
		for (String key : map.keySet()) {
			String value = map.get(key);
			String v = null;
			try {
				v = URLEncoder.encode(value, "utf-8");
			} catch (UnsupportedEncodingException ex) {
			}
			param.append("&").append(key).append("=").append(v);
		}
		return param.toString().substring(1);
	}

	/**
	 * 类型转换
	 * @param value
	 * @return
	 */
	public static Object changeType(String value) {
		try {
			int i = Integer.parseInt(value);
			return i;
		} catch (Exception e) {

		}
		try {
			long l = Long.parseLong(value);
			return l;
		} catch (Exception e) {

		}
		try {
			double d = Double.parseDouble(value);
			return d;
		} catch (Exception e) {

		}
		return value;
	}

	/**
	 * 获取编码
	 * @param content
	 * @return
	 */
	public static String getCharSet(String content) {
		String charset = "";
		Pattern[] cps = new Pattern[HTML_CHARSET_PATTERNS.length];
		for (int i = 0; i < HTML_CHARSET_PATTERNS.length; i++) {
			cps[i] = Pattern.compile(HTML_CHARSET_PATTERNS[i], Pattern.CASE_INSENSITIVE);
		}
		Pattern mp = Pattern.compile(HTML_META_PATTERN, Pattern.CASE_INSENSITIVE);
		Matcher mm = mp.matcher(content);
		while (mm.find()) {
			String attribs = mm.group(1);
			for (int i = 0; i < HTML_CHARSET_PATTERNS.length; i++) {
				Matcher cm = cps[i].matcher(attribs);
				if (cm.find()) {
					charset = cm.group(1);
				}
			}
		}
		return charset.replace("'", "");
	}

	/**
	 * 获取编码
	 * @param content
	 * @return
	 */
	public static String getCharSet2(String content) {
		String charset = "";
		Pattern mp = Pattern.compile("charset\\b\\s*=\\s*[\"|']?([^\"\"|^'']*)", Pattern.CASE_INSENSITIVE);
		Matcher mm = mp.matcher(content);
		if (mm.find()) {
			charset = mm.group(1);
		}
		return charset;
	}

	/**
	 * 是否数字
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}

	/**
	 * 获取详细异常信息
	 * @param e
	 * @return
	 */
	
	public static String getError(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			e.printStackTrace(pw);
			return sw.toString();
		} finally {
			pw.close();
			try {
				sw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * makes MD5 hash of string for use with RouterOS API
	 * @param s
	 * @return
	 */
	static public String hashMD5(String s) {
		String md5val = "";
		MessageDigest algorithm = null;
		try {
			algorithm = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException nsae) {
			System.out.println("Cannot find digest algorithm");
			System.exit(1);
		}
		byte[] defaultBytes = new byte[s.length()];
		for (int i = 0; i < s.length(); i++) {
			defaultBytes[i] = (byte) (0xFF & s.charAt(i));
		}
		algorithm.reset();
		algorithm.update(defaultBytes);
		byte messageDigest[] = algorithm.digest();
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < messageDigest.length; i++) {
			String hex = Integer.toHexString(0xFF & messageDigest[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	/**
	 * converts hex value string to normal string for use with RouterOS API
	 * @param s
	 * @return
	 */
	static public String hexStrToStr(String s) {
		String ret = "";
		for (int i = 0; i < s.length(); i += 2) {
			ret += (char) Integer.parseInt(s.substring(i, i + 2), 16);
		}
		return ret;
	}

	/**
	 * 过滤html相关标签
	 * @param inputString
	 * @return
	 */
	public static String HtmlText(String inputString) {
		String htmlStr = inputString; //含html标签的字符串 
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;
		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> } 
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> } 
			String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式 

			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); //过滤script标签 

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); //过滤style标签 

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); //过滤html标签 

			/* 空格 —— */
			// p_html = Pattern.compile("\\ ", Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = htmlStr.replaceAll(" ", " ");

			textStr = htmlStr;

		} catch (Exception e) {
		}
		return textStr;
	}

	public static void main(String[] args) throws MalformedURLException {
		String str = null;
		String err_str = null;
		try{
			str.indexOf("a");
		}catch(Exception e){
			e.printStackTrace();
		 err_str =	e.getMessage();
		}
		System.err.println(err_str);
	}

}
