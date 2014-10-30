package storm.example;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.Scheme;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.latent.storm.rabbitmq.RabbitMQSpout;
import io.latent.storm.rabbitmq.config.ConnectionConfig;
import io.latent.storm.rabbitmq.config.ConsumerConfig;
import io.latent.storm.rabbitmq.config.ConsumerConfigBuilder;

import java.io.IOException;

/**
 * Created by nikita on 19.09.14.
 */
public class RabbitMqTestTopology {
    private final static String REMOTE_HOST = "localhost";
    private final static String RABBIT_SPOUT = "rabbitSpout";
    private final static String PRINT_BOLT = "printBolt";

    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
        String QUEUE_NAME = args[0];
        //=========Init queue=========
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(REMOTE_HOST);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            channel.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("ERROR: queue not create: " + e.getMessage());
        }
        //======================================

        ConnectionConfig connectionConfig = new ConnectionConfig(
                REMOTE_HOST,
                5672,
                "guest",
                "guest",
                ConnectionFactory.DEFAULT_VHOST, 10);

        ConsumerConfig spoutConfig = new ConsumerConfigBuilder().connection(connectionConfig)
                .queue(QUEUE_NAME)
                .prefetch(200)
                .requeueOnFail()
                .build();

        IRichSpout rabbitMQSpout = new RabbitMQSpout(new SimpleScheme());

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(RABBIT_SPOUT, rabbitMQSpout)
                .addConfigurations(spoutConfig.asMap())
                .setMaxSpoutPending(200);
        builder.setBolt(PRINT_BOLT, new PrintBolt()).shuffleGrouping(RABBIT_SPOUT);

        Config config = new Config();

        if (args != null && args.length > 1) {
            StormSubmitter.submitTopologyWithProgressBar(args[1], config, builder.createTopology());
        } else {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("RabbitMqTestTopology", config, builder.createTopology());
        }
    }
}
