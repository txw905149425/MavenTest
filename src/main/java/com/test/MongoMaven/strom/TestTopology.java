package com.test.MongoMaven.strom;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;

import clojure.main;

public class TestTopology {
	
	public static void main(String[] args) {
		try{
			//1.实例化TopologyBuilder
			TopologyBuilder topologyBuilder = new TopologyBuilder();
		 // 2.设置喷发节点并分配并发数，该并发数将会控制该对象在集群中的线程数。
			topologyBuilder.setSpout("spout1", new TestSpout(),1);
			topologyBuilder.setBolt("bolt1",new TestBolt(),1).shuffleGrouping("spout1");
			Config config = new Config();
			config.setDebug(true);
			 if (args != null && args.length > 0) {
				config.setNumWorkers(1);
				StormSubmitter.submitTopology(args[0], config,topologyBuilder.createTopology());
			} else {
				// 这里是本地模式下运行的启动代码。
				config.setMaxTaskParallelism(1);
				LocalCluster cluster = new LocalCluster();
				cluster.submitTopology("simple", config, topologyBuilder.createTopology());
				
				}
		}catch(Exception e){e.printStackTrace();}
	}
}
