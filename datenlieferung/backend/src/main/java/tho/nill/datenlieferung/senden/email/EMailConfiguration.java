package tho.nill.datenlieferung.senden.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EMailConfiguration {

	@Value("${smtp.user}")
	public String BENUTZERNAME;
	@Value("${smtp.password}")
	public String PASSWORT;
	@Value("${smtp.host}")
	public String HOST;
	@Value("${smtp.port}")
	public int PORT;
	@Value("${smtp.to}")
	public String TO;
	@Value("${smtp.from}")
	public String FROM;

	public EMailConfiguration() {
		super();
	}

}
