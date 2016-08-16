package storm.example;

import org.apache.commons.io.FileUtils;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class OutputBolt extends BaseRichBolt {

    private String fileLocation = "/tmp/test-storm-file";
    private File file;

    public OutputBolt() {
        init();
    }

    public OutputBolt(String fileLocation){
        this.fileLocation = fileLocation;
        init();
    }

    private void init(){
        this.file = new File(fileLocation);
        if (file.exists()){
            try {
                FileUtils.forceDeleteOnExit(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
    }

    @Override
    public void execute(Tuple tuple) {
        StringBuffer buffer = new StringBuffer()
            .append(tuple.getValueByField("word").toString())
            .append(" = ")
            .append(tuple.getValueByField("count").toString())
            .append(System.lineSeparator());
        try {
            FileUtils.writeStringToFile(new File(fileLocation) , buffer.toString() , true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {}
}
