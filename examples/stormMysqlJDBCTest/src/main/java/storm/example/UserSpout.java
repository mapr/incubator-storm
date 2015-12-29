package storm.example;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class UserSpout implements IRichSpout {

    final static Random rand = new Random();
    private boolean isDistributed;
    private SpoutOutputCollector collector;
    public static final List<Values> rows = Lists.newArrayList(
            new Values(1,"Jon",System.currentTimeMillis()),
            new Values(2,"Doe",System.currentTimeMillis()),
            new Values(3,"Unknown",System.currentTimeMillis()),
            new Values(4,"Chuck", System.currentTimeMillis()));


    UserSpout(){
        this(true);
    }

    UserSpout(boolean isDistributed){
        this.isDistributed = isDistributed;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("user_id" , "user_name", "create_date"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;

    }

    @Override
    public void close() {

    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void nextTuple() {
        final Values row = rows.get(rand.nextInt(rows.size()));
        this.collector.emit(row, UUID.randomUUID());
        Thread.yield();

    }

    @Override
    public void ack(Object msgId) {

    }

    @Override
    public void fail(Object msgId) {

    }
}
