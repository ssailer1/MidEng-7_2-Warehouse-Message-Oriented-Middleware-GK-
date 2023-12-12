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
