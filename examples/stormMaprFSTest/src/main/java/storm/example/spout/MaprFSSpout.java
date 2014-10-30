package storm.example.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class MaprFSSpout extends BaseRichSpout {
    public static final Logger LOG = LoggerFactory.getLogger(MaprFSSpout.class);
    private SpoutOutputCollector collector;
    private BlockingQueue<String> bq = new LinkedBlockingDeque<String>();
    private BufferedReader br;
    public String fsPath;
    public static final String TEXT = "text";

    public MaprFSSpout(String maprfsPath) {
        this.fsPath = maprfsPath;
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        runEmmiter();
    }

    private void runEmmiter() {
        new MaprFSTailReader().start();
    }

    class MaprFSTailReader extends Thread {
        @Override
        public void run() {
            FileSystem srcFs = null;
            try {
                srcFs = FileSystem.get(new Configuration());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Path path = new Path(fsPath);
            long offset =  0;
            while (true) {
                try {
                    FSDataInputStream in = srcFs.open(path);
                    long size = srcFs.getFileStatus(path).getLen() - offset;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream((int) size);
                    in.seek(offset);
                    IOUtils.copyBytes(in, baos, 1024, false);
                    offset = in.getPos();
                    in.close();
                    InputStream is = new ByteArrayInputStream(baos.toByteArray());
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    do {
                        line = br.readLine();
                        if (line == null) break;
                        bq.offer(line);
                    } while (true);

                    long fileSize = srcFs.getFileStatus(path).getLen();
                    offset = (fileSize > offset) ? offset: fileSize;
                    Thread.sleep(2000);
                } catch (Exception e) {
                    break;
                }
            }
        }
    }

    @Override
    public void nextTuple() {
        while (!bq.isEmpty())
            collector.emit(new Values(bq.poll()));
        Thread.yield();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(TEXT));
    }

}
