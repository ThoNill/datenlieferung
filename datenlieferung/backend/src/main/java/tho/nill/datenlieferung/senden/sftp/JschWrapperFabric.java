package tho.nill.datenlieferung.senden.sftp;

import java.io.IOException;

import tho.nill.datenlieferung.senden.sftp.JSchWrapper.FileData;

public class JschWrapperFabric implements SftpConnectionWrapperFabric<FileData> {

	@Override
	public JSchWrapper create(String benutzername, String passwort, String host, int port, String localAktualDir,
			String remoteAktualDir) throws IOException {
		return new JSchWrapper(benutzername, passwort, host, port, localAktualDir, remoteAktualDir);
	}

}
