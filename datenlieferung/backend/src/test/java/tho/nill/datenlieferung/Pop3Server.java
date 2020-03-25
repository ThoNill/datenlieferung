package tho.nill.datenlieferung;

import javax.mail.internet.MimeMessage;

import org.junit.rules.ExternalResource;

import com.icegreen.greenmail.util.GreenMail;

public class Pop3Server extends ExternalResource {

	private GreenMail pop3Server;
	private String email;
	private String user;
	private String password;

	public Pop3Server(String user, String password, String email) {
		super();
		this.email = email;
		this.user = user;
		this.password = password;
		// this.user = user + ":" + password + "@" + host;
	}

	@Override
	protected void before() throws Throwable {
		super.before();
		try {
			pop3Server = new GreenMail(Pop3ServerSetup.ALL);
			pop3Server.setUser(email, user, password);
//			GreenMailBean g;
			/*
			 * pop3Server = new GreenMailBean(); pop3Server.setUsers(Arrays.asList(user));
			 * pop3Server.setHostname(host); pop3Server.setPortOffset(0);
			 * pop3Server.setSmtpProtocol(true); pop3Server.setPop3Protocol(true);
			 * pop3Server.setAutostart(false); pop3Server.afterPropertiesSet();
			 */
			pop3Server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MimeMessage[] getMessages() {
		if (pop3Server != null) {
			return pop3Server.getReceivedMessages();
		}
		return new MimeMessage[] {};
	}

	@Override
	protected void after() {
		super.after();
		if (pop3Server != null) {
			try {
				pop3Server.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}