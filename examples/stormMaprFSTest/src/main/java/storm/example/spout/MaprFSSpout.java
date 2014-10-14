package main.java.storm.example.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class MaprFSSpout extends BaseRichSpout {
    public static final Logger LOG = LoggerFactory.getLogger(MaprFSSpout.class);
    private SpoutOutputCollector collector;
    private BlockingQueue<String> bq = new LinkedBlockingDeque<String>();
    private BufferedReader br;
    public String fsPath;

    public MaprFSSpout(String maprfsPath) {
        this.fsPath = maprfsPath;
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        FileSystem fs = null;
        Configuration configuration = new Configuration();
        try {
            fs = FileSystem.get(configuration);
            br = new BufferedReader(new InputStreamReader(fs.open(new Path(fsPath))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread() {
            @Override
            public void run() {
                String text = null;
                try {
                    text = br.readLine();
                    while (text != null) {
                        bq.offer(text);
                        text = br.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void nextTuple() {
        while (!bq.isEmpty())
            collector.emit(new Values(bq.poll()));
        Thread.yield();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(fsPath));
    }

    @Override
    public void ack(Object msgId) {
        LOG.info("Complete processing: " + (String) msgId);
        //TODO: persist the msgId to keep track
    }

    @Override
    public void fail(Object msgId) {
        LOG.error("Fail processing: " + (String) msgId);
    }
}
