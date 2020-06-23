package tho.nill.datenlieferung;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories("repositories")
@EntityScan("entities")
@ComponentScan
@EnableTransactionManagement
public class DatenlieferungApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatenlieferungApplication.class, args);
	}

}
