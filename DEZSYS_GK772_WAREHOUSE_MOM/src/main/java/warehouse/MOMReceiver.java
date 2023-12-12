package warehouse;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Receiver MOM Class - receives the data from a Topic
 * @author Sailer Sebastian
 * @version 12.12.2023
 */

public class MOMReceiver {
    private static String user = ActiveMQConnection.DEFAULT_USER;
    private static String password = ActiveMQConnection.DEFAULT_PASSWORD;
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String queueName = "warehouse-linz";

    public String getAllWarehouseData() {
        System.out.println( "Receiver started." );

        // Create the connection.
        Session session = null;
        Connection connection = null;
        MessageConsumer consumer = null;
        Destination destination = null;
        StringBuilder receivedMessages = null;

        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
            connection = connectionFactory.createConnection();
            connection.start();

            // Create the session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue(queueName);

            // Create the consumer
            consumer = session.createConsumer(destination);

            // Start receiving
            receivedMessages = new StringBuilder();

            TextMessage message = (TextMessage) consumer.receive(1000);
            while ( message != null ) {
                receivedMessages.append(message.getText());
                message.acknowledge();
                message = (TextMessage) consumer.receive(1000);
            }
            connection.stop();
        } catch (Exception e) {
            System.out.println("[MessageConsumer] Caught: " + e);
            e.printStackTrace();

        } finally {
            try { consumer.close(); } catch ( Exception e ) {}
            try { session.close(); } catch ( Exception e ) {}
            try { connection.close(); } catch ( Exception e ) {}

        }
        System.out.println( "Receiver finished." );
        System.out.println(receivedMessages.toString());
        return receivedMessages.toString();
    }

}
