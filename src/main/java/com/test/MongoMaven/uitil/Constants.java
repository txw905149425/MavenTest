package com.test.MongoMaven.uitil;

import java.net.InetAddress;


public class Constants {

	
	// 默认的全局Redis服务器连接IP和对应的端口
	private static String redis_global_ip = "468456456";
	private static int redis_global_port = 4554;
	private static String redis_global_passwd = "545645456456";

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

}
