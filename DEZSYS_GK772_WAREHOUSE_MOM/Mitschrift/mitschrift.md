# DEZSYS_GK72_WAREHOUSE_MOM

@author: Sebastian Sailer

@version: 2023-11-28

Arbeitsumgebung: MacOS Sonoma 14.0

ActiveMQ installieren:

```bash
brew install apache-activemq
```

Danach kann man den Service gleich starten:

```bash
brew services start activemq
```

![serverstart.png](/Users/basti/Library/Application%20Support/marktext/images/124f59764e1f3c35c4fa7e30ebfaf81fed3aeded.png)

Wird die Seite [Admin](http://localhost:8161/admin/index.jsp) aufgerufen wird man nach Benutzter und Passwort gefragt

(beides: admin) und dann ist man angemeldet

![activemq.png](/Users/basti/Library/Application%20Support/marktext/images/e1398995eb068ae09b4d6e8e33a568839a3b3d13.png)

##### Änderungen an MOMApplication.java

```java
package warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

/**
 * The Application that starts the MOM sender OR receiver
 * @author Sailer Sebastian
 * @version 12.12.2023
 */
@SpringBootApplication
public class MOMApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(MOMApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
		app.run(args);
	}
}

```

Hier ändern wir den Port auf 8081, da 8080 bereits vom Warehouse der ersten Aufgabe belegt ist.

##### Änderungen an MOMController

```java
package warehouse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.MediaType;


/**
 * Controller for consuming all the contents in the message queue
 * @author Sailer Sebastian
 * @version 12.12.2023
 */
@RestController
public class MOMController {
    private StringBuilder messageQueueResultsBuilder = new StringBuilder();

    @CrossOrigin
    @RequestMapping(value = "/warehouse/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public String allWarehouseData()    {

        new MOMSender();
        formatJSONString(new MOMReceiver().getAllWarehouseData());
        return this.messageQueueResultsBuilder.toString();
    }

    /**
     * Utility method that formats a java string into a valid JSON string
     * @param newMessage
     */
    public void formatJSONString(String newMessage)    {
        if (this.messageQueueResultsBuilder.isEmpty()) {
            this.messageQueueResultsBuilder.append("[").append(newMessage).append("]");
        } else if (this.messageQueueResultsBuilder.charAt(this.messageQueueResultsBuilder.length() - 1) == ']') {
            this.messageQueueResultsBuilder.deleteCharAt(this.messageQueueResultsBuilder.length() - 1);
            this.messageQueueResultsBuilder.append(",").append(newMessage).append("]");
        }
    }
}

```

##### Änderungen an MOMSender

Jetzt muss ein Sender eingerichtet werden, um Daten in die Message Queue schicken zu können.

```java

```

##### Änderungen MOMReceiver

```java
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
    private static String queueName = "warehouse-Wien-Donaustadt";

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

```
