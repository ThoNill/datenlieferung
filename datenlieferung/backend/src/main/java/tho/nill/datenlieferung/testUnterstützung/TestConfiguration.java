package tho.nill.datenlieferung.testUnterstützung;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
//@PropertySource(value = "classpath:sftp.properties", encoding = "UTF-8")
public class TestConfiguration {

	@Value("${sftp.user}")
	public String BENUTZERNAME;

	public TestConfiguration() {
		super();
	}

}
