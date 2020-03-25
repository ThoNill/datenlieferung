package tho.nill.datenlieferung.einlesen.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Pop3Configuration {

	@Value("${pop3.user}")
	public String BENUTZERNAME;
	@Value("${pop3.password}")
	public String PASSWORT;
	@Value("${pop3.host}")
	public String HOST;
	@Value("${pop3.port}")
	public int PORT;
	@Value("${pop3.to}")
	public String TO;
	@Value("${pop3.from}")
	public String FROM;

	public Pop3Configuration() {
		super();
	}

}
