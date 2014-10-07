package storm.example;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.util.Map;

/**
 * Created by nikita on 22.09.14.
 */
public class PrintBolt extends BaseRichBolt {

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {

    }

    @Override
    public void execute(Tuple input) {

        System.out.println("OUT>> [" + Thread.currentThread().getId() + "] " + input.getValue(0).toString());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}