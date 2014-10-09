package storm.example;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import kafka.consumer.*;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by ko3a4ok on 08.10.14.
 */
public class KafkaSpout extends BaseRichSpout {
    private String queueName;
    private SpoutOutputCollector collector;
    static String a_zookeeper = "localhost:5181";
    static String a_groupId = "1";

    private BlockingQueue<String> bq = new LinkedBlockingDeque<String>();

    public KafkaSpout(String queueName) {
        this.queueName = queueName;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("value"));
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        new Thread() {
            @Override
            public void run() {
                kafka.javaapi.consumer.ConsumerConnector consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig(a_zookeeper, a_groupId));
                Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
                topicCountMap.put(queueName, new Integer(1));
                Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
                List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(queueName);
                for (final KafkaStream stream : streams) {
                    ConsumerIterator<byte[], byte[]> it = stream.iterator();
                    while (it.hasNext()) {
                        String value = new String(it.next().message());
                        bq.offer(value);
                    }
                }

            }
        }.start();
    }

    private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
       return new ConsumerConfig(props);
    }


    @Override
    public void nextTuple() {
        while (!bq.isEmpty())
            collector.emit(new Values(bq.poll()));
        Thread.yield();
    }
}
