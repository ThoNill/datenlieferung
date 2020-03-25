package tho.nill.datenlieferung.senden.sftp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SftpConfiguration {

	@Value("${sftp.user}")
	public String BENUTZERNAME;
	@Value("${sftp.password}")
	public String PASSWORT;
	@Value("${sftp.host}")
	public String HOST;
	@Value("${sftp.port}")
	public int PORT;
//	@Value("${sftp.start.test}")
//	public boolean START;

	public SftpConfiguration() {
		super();
	}

}
