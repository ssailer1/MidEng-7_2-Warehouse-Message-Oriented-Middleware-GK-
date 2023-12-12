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
