package storm.kafka;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;

import java.util.Map;

/**
 * Created by dmitry-sergeev on 09.12.15.
 */
public class KafkaSpout extends BaseRichSpout {

    private BaseRichSpout implemenation;
    private SpoutConfig _spoutConfig;

    public KafkaSpout(SpoutConfig spoutConfig){
        _spoutConfig = spoutConfig;
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        if(conf.containsKey(Config.KAFKA_USE_09_API) && (Boolean)conf.get(Config.KAFKA_USE_09_API)){
            implemenation = new KafkaJavaApiSpout(_spoutConfig);
        }else{
            implemenation = new KafkaSpoutOld(_spoutConfig);
        }
        implemenation.open(conf, context, collector);
    }

    @Override
    public void nextTuple() {
        implemenation.nextTuple();
    }

    @Override
    public void ack(Object msgId) {
        implemenation.ack(msgId);
    }

    @Override
    public void fail(Object msgId) {
        implemenation.fail(msgId);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        implemenation.declareOutputFields(declarer);
    }

    @Override
    public void close() {
        implemenation.close();
    }

    @Override
    public void activate() {
        implemenation.activate();
    }

    @Override
    public void deactivate() {
        implemenation.deactivate();
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return implemenation.getComponentConfiguration();
    }
}
