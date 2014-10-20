package main.java.storm.example;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import main.java.storm.example.bolt.OutputBolt;
import main.java.storm.example.spout.MaprFSSpout;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class MaprFSTopology {
    public static final Logger LOG = LoggerFactory.getLogger(MaprFSTopology.class);
    private static final String FILE_NAME = "test_10000.txt";
    private static final String DEFAULT_MAPRFS_PATH = "/" + FILE_NAME;

    public static void main(String[] args) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();
        MaprFSSpout spout;
        OutputBolt bolt;
        Config conf = new Config();

        conf.setDebug(true);
        // never timeout
        conf.setMessageTimeoutSecs(Integer.MAX_VALUE);
        prepareForTesting();
        spout = new MaprFSSpout(DEFAULT_MAPRFS_PATH);
        bolt = new OutputBolt();

        builder.setSpout("maprfs", spout, 1);
        builder.setBolt("import", bolt, 1).shuffleGrouping("maprfs");

        if(args!=null && args.length >= 1) {
            conf.setNumWorkers(3);
            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
        } else {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("test", conf, builder.createTopology());
            Utils.sleep(10000);
            cluster.killTopology("test");
            cluster.shutdown();
        }
    }

    private static void prepareForTesting(){
        Configuration conf = new Configuration();
        try {
            FileSystem fs = FileSystem.get(conf);
            Path confDst1 = new Path(DEFAULT_MAPRFS_PATH);
            if(!fs.exists(confDst1)){
                FSDataOutputStream out = fs.create(confDst1);
                OutputStreamWriter writer = new OutputStreamWriter(out);
                LOG.info("File " + FILE_NAME + " created in maprfs");
                for(int i = 0; i < 9999; i++){
                    writer.write( i + " line\n");
                }
                writer.write("END\n");
                writer.close();
                out.close();
            } else {
                LOG.info("File " + FILE_NAME + " already exists in maprfs");
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
