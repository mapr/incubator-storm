package storm.example;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AverageCountBolt<K,V>  implements IRichBolt {

    protected OutputCollector collector;
    public static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AverageCountBolt.class);
    private long periodInSecs;
    private volatile long count;

    public AverageCountBolt(long periodInSecs) {
        this.periodInSecs = periodInSecs;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context,
                        OutputCollector collector) {
        this.collector = collector;

    }

    @Override
    public void execute(Tuple tuple) {
        if(isTickTuple(tuple)){
            long cachedCount = count;
            count = 0;
            LOG.info("Got tick tuple, emitting count={}", cachedCount);
            collector.emit(new Values("", String.valueOf(cachedCount).getBytes()));

        }else{
            count++;
            LOG.info("Increasing count={}", count);
        }

        collector.ack(tuple);
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(DemoTopology.KEY_FIELD, DemoTopology.MESSAGE_FIELD));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config conf = new Config();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, periodInSecs);
        return conf;
    }

    private static boolean isTickTuple(Tuple tuple) {
        return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
                && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
    }

}
