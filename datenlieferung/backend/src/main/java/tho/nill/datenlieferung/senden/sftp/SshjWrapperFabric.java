package tho.nill.datenlieferung.senden.sftp;

import java.io.IOException;

import net.schmizz.sshj.sftp.RemoteResourceInfo;

public class SshjWrapperFabric implements SftpConnectionWrapperFabric<RemoteResourceInfo> {

	@Override
	public SshjWrapper create(String benutzername, String passwort, String host, int port, String localAktualDir,
			String remoteAktualDir) throws IOException {
		return new SshjWrapper(benutzername, passwort, host, port, localAktualDir, remoteAktualDir);
	}

}
