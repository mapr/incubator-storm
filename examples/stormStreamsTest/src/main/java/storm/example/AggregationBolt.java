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
import org.apache.log4j.Logger;
import storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import storm.kafka.bolt.mapper.TupleToKafkaMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class AggregationBolt<K,V>  implements IRichBolt {

    protected OutputCollector collector;
    protected static final Logger log = Logger.getLogger(AggregationBolt.class);
    private long periodInSecs;
    private TupleToKafkaMapper<K, V> mapper;
    private List<Tuple> batch;

    public AggregationBolt(long periodInSecs) {
        this.periodInSecs = periodInSecs;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context,
                        OutputCollector collector) {
        this.collector = collector;

        if(this.mapper == null) {
            this.mapper = new FieldNameBasedTupleToKafkaMapper();
        }
        batch = new CopyOnWriteArrayList<>();
    }

    @Override
    public void execute(Tuple tuple) {
        if(isTickTuple(tuple)){
            for(Tuple t : batch){
                collector.emit(t, new Values(t.getValueByField(DemoTopology.KEY_FIELD),
                        t.getValueByField(DemoTopology.MESSAGE_FIELD),
                        t.getValueByField(DemoTopology.TOPIC_FIELD),
                        t.getValueByField(DemoTopology.ATTEMP_FIELD)));
                collector.ack(t);
            }
            batch.clear();
        }else {
            batch.add(tuple);
        }
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(DemoTopology.KEY_FIELD, DemoTopology.MESSAGE_FIELD, DemoTopology.TOPIC_FIELD, DemoTopology.ATTEMP_FIELD));
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
