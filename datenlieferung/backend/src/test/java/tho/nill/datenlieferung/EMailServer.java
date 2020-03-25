package tho.nill.datenlieferung;

import java.util.Arrays;

import javax.mail.internet.MimeMessage;

import org.junit.rules.ExternalResource;

import com.icegreen.greenmail.spring.GreenMailBean;

public class EMailServer extends ExternalResource {

	private GreenMailBean smtpServer;
	private int port;
	private String host;
	private String user;

	public EMailServer(String user, String password, int port, String host) {
		super();
		this.port = port;
		this.host = host;
		this.user = user + ":" + password + "@" + host;
	}

	@Override
	protected void before() throws Throwable {
		super.before();
		try {
			smtpServer = new GreenMailBean();
			smtpServer.setUsers(Arrays.asList(user));
			smtpServer.setHostname(host);
			smtpServer.setPortOffset(port - 25);
			smtpServer.setSmtpProtocol(true);
			smtpServer.setAutostart(false);
			smtpServer.afterPropertiesSet();
			smtpServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MimeMessage[] getMessages() {
		if (smtpServer != null) {
			return smtpServer.getReceivedMessages();
		}
		return new MimeMessage[] {};
	}

	@Override
	protected void after() {
		super.after();
		if (smtpServer != null) {
			try {
				smtpServer.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}