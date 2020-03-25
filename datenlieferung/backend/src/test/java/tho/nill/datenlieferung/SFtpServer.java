package tho.nill.datenlieferung;

import static java.util.Collections.singletonList;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.sshd.common.file.nativefs.NativeFileSystemFactory;
import org.apache.sshd.common.session.helpers.AbstractSession;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
//import org.apache.sshd.server.sftp.SftpSubsystem;
import org.junit.rules.ExternalResource;

public class SFtpServer extends ExternalResource {

	private String benutzername;
	private String passwort;
	private int port;
	private SshServer sftpServer;
	private NativeFileSystemFactory fileSystemFactory;

	public SFtpServer(String benutzername, String passwort, int port) {
		super();
		this.benutzername = benutzername;
		this.passwort = passwort;
		this.port = port;
	}

	public Path getRootDirectory() throws IOException {
		AbstractSession session = sftpServer.getActiveSessions().get(0);

		String userName = session.getUsername();
		String homeRoot = fileSystemFactory.getUsersHomeDir();
		return Paths.get(homeRoot, userName).normalize().toAbsolutePath();
	}

	@Override
	protected void before() throws Throwable {
		super.before();

		sftpServer = SshServer.setUpDefaultServer();
		sftpServer.setPort(port);
		sftpServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
		sftpServer.setSubsystemFactories(singletonList(new SftpSubsystemFactory()));
		fileSystemFactory = new NativeFileSystemFactory(true);
		fileSystemFactory.setCreateHome(true);
		sftpServer.setFileSystemFactory(fileSystemFactory);
		sftpServer.setPasswordAuthenticator(new PasswordAuthenticator() {
			@Override
			public boolean authenticate(String username, String password, ServerSession session) {
				return benutzername.equals(username) && passwort.equals(password);
			}
		});
		sftpServer.start();
	}

	@Override
	protected void after() {
		super.after();

		try {
			sftpServer.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}