package storm.example;

import backtype.storm.Config;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixedBatchSpout implements IBatchSpout {
    int maxBatchSize;
    HashMap<Long, List<List<Object>>> batches = new HashMap<Long, List<List<Object>>>();
    private Values[] outputs = {
            new Values("1","user1","123456","street1","sunnyvale","ca"),
            new Values("2","user2","123456","street2","sunnyvale","ca"),
            new Values("3","user3","123456","street3","san jose","ca"),
            new Values("4","user4","123456","street4","san jose","ca"),
    };
    private int index = 0;
    boolean cycle = false;

    public FixedBatchSpout(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }

    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }

    @Override
    public Fields getOutputFields() {
        return new Fields("id","name","phone","street","city","state");
    }

    @Override
    public void open(Map conf, TopologyContext context) {
        index = 0;
    }


    @Override
    public void emitBatch(long batchId, TridentCollector collector) {
        List<List<Object>> batch = this.batches.get(batchId);
        if(batch == null){
            batch = new ArrayList<List<Object>>();
            if(index>=outputs.length && cycle) {
                index = 0;
            }
            for(int i=0; i < maxBatchSize; index++, i++) {
                if(index == outputs.length){
                    index=0;
                }
                batch.add(outputs[index]);
            }
            this.batches.put(batchId, batch);
        }
        for(List<Object> list : batch){
            collector.emit(list);
        }
    }

    @Override
    public void ack(long batchId) {
        this.batches.remove(batchId);
    }

    @Override
    public void close() {
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config conf = new Config();
        conf.setMaxTaskParallelism(1);
        return conf;
    }

}
