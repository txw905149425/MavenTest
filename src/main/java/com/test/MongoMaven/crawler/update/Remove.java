package com.test.MongoMaven.crawler.update;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.model.Filters;
import com.test.MongoMaven.uitil.MongoDbUtil;

public class Remove {
	public static void main(String[] args) {
		 MongoDbUtil mongo=new MongoDbUtil();
		 mongo.getShardConn("stock_code").deleteMany(Filters.exists("name",false));
	}
}
