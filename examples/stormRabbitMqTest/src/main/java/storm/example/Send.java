package storm.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/**
 * Created by nikita on 22.09.14.
 */
public class Send {

    public static void main(String[] argv) throws Exception {
        String REMOTE_HOST = argv[0];
        String QUEUE_NAME = argv[1];

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(REMOTE_HOST);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        String message = argv[2];
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [>] Sent '" + message + "'");

        channel.close();
        connection.close();
    }
}
