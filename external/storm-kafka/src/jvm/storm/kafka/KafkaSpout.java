package storm.kafka;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by dmitry-sergeev on 09.12.15.
 */
public class KafkaSpout extends BaseRichSpout {

    public static final org.slf4j.Logger LOG = LoggerFactory.getLogger(KafkaSpout.class);

    private BaseRichSpout implemenation;
    private SpoutConfig _spoutConfig;

    public KafkaSpout(SpoutConfig spoutConfig){
        _spoutConfig = spoutConfig;
        if(_spoutConfig.kafkaAPIv != null && 0.9 >= Double.parseDouble(_spoutConfig.kafkaAPIv)){
            implemenation = new KafkaJavaApiSpout(_spoutConfig);
            LOG.debug("Will use 0.9 Kafka spout implementation");
        }else{
            implemenation = new KafkaSpoutOld(_spoutConfig);
            LOG.debug("Will use 0.8 Kafka spout implementation");
        }
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
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

}
