package storm.example;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.SubmitOptions;
import backtype.storm.generated.TopologyInitialStatus;
import backtype.storm.task.ShellBolt;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.HashMap;
import java.util.Map;

public class IsolationWordCountTopology {
    public static class SplitSentence extends ShellBolt implements IRichBolt {

        public SplitSentence() {
            super("python", "splitsentence.py");
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("word"));
        }

        @Override
        public Map<String, Object> getComponentConfiguration() {
            return null;
        }
    }

    public static class WordCount extends BaseBasicBolt {
        Map<String, Integer> counts = new HashMap<String, Integer>();

        @Override
        public void execute(Tuple tuple, BasicOutputCollector collector) {
            String word = tuple.getString(0);
            Integer count = counts.get(word);
            if (count == null)
                count = 0;
            count++;
            counts.put(word, count);
            collector.emit(new Values(word, count));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("word", "count"));
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 3 || args.length < 2) {
            System.out.println("Usage: " + "<topology-name> "
                    + "<userName> " + " <file location>");
            System.exit(-1);
        }

        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("spout", new RandomSentenceSpout(), 5);

        builder.setBolt("split", new SplitSentence(), 8).shuffleGrouping("spout");
        builder.setBolt("count", new WordCount(), 12).fieldsGrouping("split", new Fields("word"));
        builder.setBolt("writer", new OutputBolt(args[2]), 1).fieldsGrouping("count" , new Fields("word", "count"));

        Config conf = new Config();
        conf.setDebug(true);
        conf.setNumWorkers(3);
        StormSubmitter.submitTopologyAs(args[0], conf, builder.createTopology(), new SubmitOptions(TopologyInitialStatus.ACTIVE), null, args[1]);

    }
}
