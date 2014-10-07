package storm.example;

import backtype.storm.spout.Scheme;
import backtype.storm.tuple.Fields;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 22.09.14.
 */
public class SimpleScheme implements Scheme {

    @Override
    public List<Object> deserialize(byte[] ser) {
        String str = "ERROR";
        try {
            str = new String(ser, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("MyScheme deserialize fail : " + e.getMessage());
        }

        List<Object> outList = new ArrayList<>(1);
        outList.add(str);

        return outList;
    }

    @Override
    public Fields getOutputFields() {
        return new Fields("bytes");
    }
}
