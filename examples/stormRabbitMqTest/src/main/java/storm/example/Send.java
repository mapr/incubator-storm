package storm.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/**
 * Created by nikita on 22.09.14.
 */
public class Send {
    private final static String QUEUE_NAME = "hello";
    private final static String REMOTE_HOST = "localhost";

    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(REMOTE_HOST);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        String message = "Hello World!";
        if (argv.length == 1) {
            message = argv[0];
        }
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [>] Sent '" + message + "'");

        channel.close();
        connection.close();
    }
}
