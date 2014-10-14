package main.java.storm.example.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.io.*;
import java.util.Map;

public class OutputBolt extends BaseRichBolt {

    public OutputBolt() {
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
    }

    @Override
    public void execute(Tuple tuple) {
        System.out.println("OUT>> [" + Thread.currentThread().getId() + "] " + tuple.getValue(0).toString());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {}
}
