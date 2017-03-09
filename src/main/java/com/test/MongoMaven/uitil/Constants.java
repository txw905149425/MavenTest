package com.test.MongoMaven.uitil;

import java.net.InetAddress;


public class Constants {
	static public String GaodigJsoupRowStr = "GaodigJsoupRow";
	public static String LOG_FLAG = "====================";
	// 日期的格式现实
	public static String JAVA_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static String DATE_FORMAT_YMD = "yyyyMMdd";
	public static String NOYEAR_DATE_FORMAT = "MM-dd HH:mm:ss";
	public static String CRAWL_FAILED = "crawlFailed";

	public static String apiIsUse = "apiIsUse";
	public static String apils = "apils";
	public static String WEIBO_API_USEDATE = "weiboApiUseDate";
	public static String WEIBO_API_CURRENT = "weiboApiCurrent";
	public static String HOME_WEIBO_API_LIST_TYPE = "sinaApih";
	public static String HOME_WEIBO_API_USEDATE = "homeWeiboApiUseDate";
	public static String HOME_WEIBO_API_CURRENT = "homeWeiboApiCurrent";
	public static String CLIENT_ID = "client_ID";
	public static String OPEN_ID = "open_ID";
	public static String TOKEN = "token";
	public static String WEIBO_API_USE_TIMESUP = "150";
	public static String ACCESS_URL = "access_URL";
	public static String COOKIE = "cookie";

	// redis 全局信息标识
	public static String POOL_GLOBAL = "poolGlobal";
	public static String CONFIG_GLOBAL = "config";
	private static String phantomjspath = "/home/cjl/phantomjs/bin/phantomjs";
	private static String default_firefoxpath = "/home/cjl/firefox/firefox/firefox";

	// HBase rowid &rowkey
	public static String ROW_ID = "rowId";

	// 日志信息级别，在RedisApi里面输出时分别对应debug,info和error三个等级的日志信息
	public static String LOG_LEVEL = "logLevel";
	public static int DEBUG = 1;
	public static int INFO = 2;
	public static int ERROR = 3;

	// quartz job 字段
	public static String JOB_TASK_QUENE = "tasklsid";
	public static String JOB_TASK_PRIORITY = "priority";
	// api 信息
	public static String API_KEY = "apiKey";
	public static String API_SECRET = "apiSecret";
	public static String API_MAX = "apiMax";
	public static String API_USE = "apiUse";
	public static String API_NAME = "name";
	// 参数设置共通字段
	public static String MINDUE_TIME = "mindueTime";
	public static String MAXDUE_TIME = "maxdueTime";


	// ////////////////////////////////////////////////////////////////////
	// REDIS
	public static String REDIS_MASTER_IP() {
		return "172.16.0.122";
	}

	public static int REDIS_MASTER_PORT() {
		// return RedisApi.getIntConfig(Constants.POOL_GLOBAL,
		// Constants.CONFIG_GLOBAL, "redisMasterPort", 6379);
		return 6379;
	}

	// 默认的全局Redis服务器连接IP和对应的端口
	private static String redis_global_ip = "172.16.0.112";
	private static int redis_global_port = 6379;
	private static String redis_global_passwd = "bjgdFristMan112";

	// "182.254.149.66"
	public static void SET_REDIS_GLOBAL_IP(String ip) {
		redis_global_ip = ip;
	}

	public static void SET_REDIS_GLOBAL_PORT(int port) {
		redis_global_port = port;
	}

	public static void SET_REDIS_GLOBAL_PASSWD(String passwd) {
		redis_global_passwd = passwd;
	}

	public static String REDIS_GLOBAL_IP = "globalRedisIp";

	public static String REDIS_GLOBAL_IP() {
		return redis_global_ip;
	}

	public static String REDIS_GLOBAL_PORT = "globalRedisPort";

	public static int REDIS_GLOBAL_PORT() {
		return redis_global_port;
	}

	public static String REDIS_GLOBAL_PASSWORD = "globalRedisPasswd";

	public static String REDIS_GLOBAL_PASSWORD() {
		return redis_global_passwd;
	}

	public static int REDIS_MAX_ACTIVE() {
		// return RedisApi.getIntConfig(Constants.POOL_GLOBAL,
		// Constants.CONFIG_GLOBAL, "redisMaxActive", 500);
		return -1;
	}

	public static int REDIS_MAX_IDLE() {
		// return RedisApi.getIntConfig(Constants.POOL_GLOBAL,
		// Constants.CONFIG_GLOBAL, "redisMaxIdle", 5);
		return 8000;
	}

	public static int REDIS_MAX_WAIT() {
		// return RedisApi.getIntConfig(Constants.POOL_GLOBAL,
		// Constants.CONFIG_GLOBAL, "redisMaxWait", 1000 * 10);
		// return 1000 * 150;
		return -1;
	}

	public static boolean REDIS_TEST_BORROW() {
		// return RedisApi.getBooleanConfig(Constants.POOL_GLOBAL,
		// Constants.CONFIG_GLOBAL, "redisTestBorrow", true);
		return true;
	}
}
