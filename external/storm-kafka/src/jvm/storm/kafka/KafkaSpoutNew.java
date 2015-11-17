package storm.kafka;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import kafka.message.Message;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by dmitry-sergeev on 29.10.15.
 */
public class KafkaSpoutNew extends BaseRichSpout {
    public static class MessageAndRealOffset {
        public Message msg;
        public long offset;

        public MessageAndRealOffset(Message msg, long offset) {
            this.msg = msg;
            this.offset = offset;
        }
    }

    public static final Logger LOG = LoggerFactory.getLogger(KafkaSpoutNew.class);

    SpoutConfig _spoutConfig;
    SpoutOutputCollector _collector;
    KafkaConsumer consumer;
    ConcurrentMap<Long, Values> messages;
    long pollTimeout;
    long maxFailCount;


    public KafkaSpoutNew() {
    }

    public KafkaSpoutNew(SpoutConfig spoutConfig) {
        this._spoutConfig = spoutConfig;
    }

    @Override
    public void open(Map conf, final TopologyContext context, final SpoutOutputCollector collector) {
        _collector = collector;

        if(conf.containsKey(Config.KAFKA_POLL_TIMEOUT)) {
            pollTimeout = (Long) conf.get(Config.KAFKA_POLL_TIMEOUT);
        }else{
            pollTimeout = 100;
        }
        if(conf.containsKey(Config.KAFKA_MAX_FAIL_ATTEMPTS_COUNT)){
            maxFailCount = (Long) conf.get(Config.KAFKA_MAX_FAIL_ATTEMPTS_COUNT);
        }else{
            maxFailCount = 5;
        }
        if(consumer == null){
            consumer = new KafkaConsumer(conf);
        }
        messages = new ConcurrentHashMap<Long, Values>();
        consumer.subscribe(Collections.singletonList(_spoutConfig.topic));
    }

    @Override
    public void nextTuple() {
        ConsumerRecords records = consumer.poll(pollTimeout);
        Iterator<ConsumerRecord<String,String>>iter=records.iterator();
        while(iter.hasNext()){
            ConsumerRecord record = iter.next();
            Values message = new Values(record.key(), record.value(), record.topic(), maxFailCount);
            messages.putIfAbsent(record.offset(), message);
            _collector.emit(message, record.offset());
        }
        consumer.commitAsync(new OffsetCommitCallback() {
            @Override
            public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                if(exception != null){
                    LOG.debug("Exception raised during the commit in nextTuple() method: {}", exception.getMessage());
                }
            }
        });
    }

    @Override
    public void fail(Object msgId) {
        LOG.debug("Message with offset {} failed", msgId);
        Values message = messages.get(msgId);
        Long currentAttempt = (Long) message.get(3);
        if(currentAttempt < 1){
            LOG.debug("Message with offset {} reached maximum fail attempts. Skipping...", msgId);
        }else{
            message.set(3, currentAttempt-1);
            _collector.emit(message, msgId);
        }
    }

    @Override
    public void ack(Object msgId) {
        LOG.debug("Message with offset {} proceeded successfully", msgId);
        messages.remove(msgId);
    }

    @Override
    public void close() {
        consumer.close();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("key", "message", "topic", "attempt"));
    }



}
