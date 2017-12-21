package com.test.MongoMaven.wx.thread;

import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Delete {
	public static void main(String[] args) {
		MongoDbUtil mongo=new MongoDbUtil();
		mongo.getShardConn("jg_wx_gzh").deleteMany(Filters.exists("id"));
	}
}
