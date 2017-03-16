package com.test.MongoMaven.storm;

import java.io.FileReader;
import java.util.Map;
import java.util.Random;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class TestSpout extends BaseRichSpout{
	private static final long serialVersionUID = 1L;  //？？？？？？
	 private SpoutOutputCollector collector;
	private static String[] info = new String[]{
			"comaple\t,12424,44w46,654,12424,44w46,654,",
			"lisi\t,435435,6537,12424,44w46,654,",
			"lipeng\t,45735,6757,12424,44w46,654,",
			"hujintao\t,45735,6757,12424,44w46,654,",
			"jiangmin\t,23545,6457,2455,7576,qr44453",
			"beijing\t,435435,6537,12424,44w46,654,",
			"xiaoming\t,46654,8579,w3675,85877,077998,",
			"xiaozhang\t,9789,788,97978,656,345235,09889,",
			"ceo\t,46654,8579,w3675,85877,077998,",
			"cto\t,46654,8579,w3675,85877,077998,",
			"zhansan\t,46654,8579,w3675,85877,077998," };
	    Random random=new Random();
	
	public void nextTuple() {
		// TODO Auto-generated method stub
		try {
			String msg=info[random.nextInt(11)];
			collector.emit(new Values(msg));
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void open(Map config, TopologyContext context, SpoutOutputCollector collector) {
		// TODO Auto-generated method stub
		this.collector=collector;
		
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("source"));
	}
	
	

}
