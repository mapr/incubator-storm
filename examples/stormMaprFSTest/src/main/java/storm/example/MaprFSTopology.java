package storm.example;

import backtype.storm.*;
import backtype.storm.topology.*;
import backtype.storm.utils.Utils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import storm.example.bolt.OutputBolt;
import storm.example.spout.MaprFSSpout;

import java.io.*;
import java.nio.Buffer;
import java.util.Map;
import java.util.Scanner;

public class MaprFSTopology {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Please provide file name");
            return;
        }
        TopologyBuilder builder = new TopologyBuilder();
        Config conf = new Config();

        conf.setDebug(true);
        IRichSpout spout = new MaprFSSpout(args[0]);
        IRichBolt bolt = new OutputBolt();

        builder.setSpout("maprfs-reader", spout, 1);
        builder.setBolt("output", bolt, 1).shuffleGrouping("maprfs-reader");

        if(args.length >= 2) {
            conf.setNumWorkers(3);
            StormSubmitter.submitTopology(args[1], conf, builder.createTopology());
        } else {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("test", conf, builder.createTopology());
            Utils.sleep(10000);
            cluster.killTopology("test");
            cluster.shutdown();
        }
    }

}
