package storm.example;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;

public class KafkaTestTopology {
    private final static String KAFKA_SPOUT = "KAFKA SPOUT";
    private final static String PRINT_BOLT = "printBolt";

    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
        if (args.length < 3) {
            System.out.println("Usage: [topic name] [zookeeper host:port] [topology name]");
            return;
        }

        KafkaSpout kafkaSpout = new KafkaSpout(args[0], args[1]);

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(KAFKA_SPOUT, kafkaSpout).setMaxSpoutPending(200);
        builder.setBolt(PRINT_BOLT, new PrintBolt()).shuffleGrouping(KAFKA_SPOUT);

        Config config = new Config();
        if (args.length > 0) {
            StormSubmitter.submitTopologyWithProgressBar(args[2], config, builder.createTopology());
        } else {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("KafkaTestTopology", config, builder.createTopology());
        }
    }
}
