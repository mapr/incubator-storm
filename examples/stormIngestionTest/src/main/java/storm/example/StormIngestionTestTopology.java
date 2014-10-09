package storm.example;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.metric.LoggingMetricsConsumer;
import backtype.storm.topology.TopologyBuilder;

public class StormIngestionTestTopology {
    private static final String BYTE_SPOUT = "byteSpout";
    private static final String BYTE_BOLT = "byteBolt";

    public static void main(String[] args) {
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout(BYTE_SPOUT, new ByteGeneratorSpout(), 1);
        topologyBuilder.setBolt(BYTE_BOLT, new BytesReceiverBolt(), 1).shuffleGrouping(BYTE_SPOUT);

        Config conf = new Config();
        conf.setNumWorkers(2);
        conf.registerMetricsConsumer(LoggingMetricsConsumer.class, 1);

        try {
            StormSubmitter.submitTopologyWithProgressBar("StormIngestionTestTopology", conf, topologyBuilder.createTopology());
        } catch (AlreadyAliveException e) {
            e.printStackTrace();
        } catch (InvalidTopologyException e) {
            e.printStackTrace();
        }
    }
}
