package storm.example;import backtype.storm.Config;import backtype.storm.StormSubmitter;import backtype.storm.generated.AlreadyAliveException;import backtype.storm.generated.AuthorizationException;import backtype.storm.generated.InvalidTopologyException;import backtype.storm.topology.TopologyBuilder;import backtype.storm.tuple.Fields;import storm.kafka.*;import storm.kafka.bolt.KafkaBolt;import storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;import storm.kafka.bolt.selector.DefaultTopicSelector;import java.io.IOException;import java.util.Properties;import java.util.UUID;public class DemoTopology {    public static final String KEY_FIELD = "key";    public static final String MESSAGE_FIELD = "message";    public static final String TOPIC_FIELD = "topic";    public static final String ATTEMP_FIELD = "attempt";    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException, IOException {        TopologyBuilder builder = new TopologyBuilder();        if(args.length<5){            System.err.println("usage: command <zookeeperHost:port> <bootstrap server host:port> <use kafka 09 API: true/false> <topic to read from> <topic to write count> <topic to send source data>");            return;        }        Properties props = new Properties();        props.load(DemoTopology.class.getResourceAsStream("/kafka.properties"));        props.put("topology.message.timeout.secs", 60);        props.put("bootstrap.servers", args[1]);        String topicName = args[3];        String topicNameForAverage = args[4];        String topicThatReceivesOriginalData = args[5];        props.put("group.id", topicName);        String zkConnString = args[0]; //localhost:5181        BrokerHosts hosts = new ZkHosts(zkConnString);        SpoutConfig spoutConfig = new SpoutConfig(hosts, topicName, "true".equals(args[2]) ? topicName : "/" + topicName, UUID.randomUUID().toString());        spoutConfig.scheme = new KeyValueSchemeAsMultiScheme(new KafkaBoltKeyValueScheme());        if("true".equals(args[2])){            spoutConfig.kafkaAPIv="0.9";        }        KafkaSpout spout = new KafkaSpout(spoutConfig);        builder.setSpout("spout", spout, 1);        long tuplesCountPeriodInSecs = 5;        AggregationBolt averageBolt = new AggregationBolt(tuplesCountPeriodInSecs);        builder.setBolt("aggregationBolt", averageBolt, 1).shuffleGrouping("spout");        AverageCountBolt averageCountBolt = new AverageCountBolt(tuplesCountPeriodInSecs);        builder.setBolt("averageCounter", averageCountBolt, 1).globalGrouping("spout");        KafkaBolt dataToKafkaBolt = new KafkaBolt()                .withTopicSelector(new DefaultTopicSelector(topicThatReceivesOriginalData))                .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper())                .withProducerProperties(props);        builder.setBolt("aggregationToKafka", dataToKafkaBolt, 1).fieldsGrouping("aggregationBolt", new Fields("key", "message", "topic", "attempt"));        KafkaBolt averageCountToKafkaBolt = new KafkaBolt()                .withTopicSelector(new DefaultTopicSelector(topicNameForAverage))                .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper())                .withProducerProperties(props);        builder.setBolt("averageCountToKafka", averageCountToKafkaBolt, 1).globalGrouping("averageCounter");        StormSubmitter.submitTopologyAs("streamsTest", props, builder.createTopology(), null, null, "root");    }    public static class KafkaBoltKeyValueScheme extends StringKeyValueScheme {        @Override        public Fields getOutputFields() {            return new Fields("message");        }    }}