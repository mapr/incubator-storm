package storm.example;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.storm.hive.bolt.mapper.DelimitedRecordHiveMapper;
import org.apache.storm.hive.common.HiveOptions;
import org.apache.storm.hive.trident.HiveStateFactory;
import org.apache.storm.hive.trident.HiveUpdater;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.state.StateFactory;

public class HiveTopology {

    public static void main(String[] args) {
        if (args.length != 4 && args.length != 6) {
            System.out.println("Usage: " + " <metaStoreURI> "
                    + "<databaseName> <tableName> <topologyName> "
                    + "[Kerberos keytab] [Kerberos principal]");
            System.exit(-1);
        }

        String metaStoreURI = args[0];
        HiveConf.ConfVars.METASTOREURIS.toString();
        String dbName = args[1];
        String tblName = args[2];
        Config conf = new Config();
        conf.setMaxSpoutPending(5);
        Object keytab = null, principal = null;
        if(args.length == 6){
            keytab = args[4];
            principal = args[5];
        }


        try {
            StormSubmitter.submitTopology(args[3], conf, buildTopology(metaStoreURI, dbName, tblName, keytab, principal));
        } catch(Exception e) {
            System.out.println("Failed to submit topology"+e);
        }

    }

    public static StormTopology buildTopology(String metaStoreURI, String dbName, String tblName, Object keytab, Object principal) {
        int batchSize = 100;
        FixedBatchSpout spout = new FixedBatchSpout(batchSize);
        spout.setCycle(true);
        TridentTopology topology = new TridentTopology();
        Stream stream = topology.newStream("hivespout",spout);
        String[] partNames = {"city","state"};
        String[] colNames = {"id","name","phone","street"};
        Fields hiveFields = new Fields("id","name","phone","street","city","state");
        DelimitedRecordHiveMapper mapper = new DelimitedRecordHiveMapper()
                .withColumnFields(new Fields(colNames))
                .withPartitionFields(new Fields(partNames));
        HiveOptions hiveOptions;
        if (keytab != null && principal != null) {
            hiveOptions = new HiveOptions(metaStoreURI,dbName,tblName,mapper)
                    .withTxnsPerBatch(10)
                    .withBatchSize(batchSize)
                    .withIdleTimeout(10)
                    .withCallTimeout(30000)
                    .withKerberosKeytab((String)keytab)
                    .withKerberosPrincipal((String)principal);
        } else  {
            hiveOptions = new HiveOptions(metaStoreURI,dbName,tblName,mapper)
                    .withTxnsPerBatch(10)
                    .withBatchSize(batchSize)
                    .withCallTimeout(30000)
                    .withIdleTimeout(10);
        }
        StateFactory factory = new HiveStateFactory().withOptions(hiveOptions);
        TridentState state = stream.partitionPersist(factory, hiveFields, new HiveUpdater(), new Fields());
        return topology.build();
    }

    public static void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
        }
    }
}
