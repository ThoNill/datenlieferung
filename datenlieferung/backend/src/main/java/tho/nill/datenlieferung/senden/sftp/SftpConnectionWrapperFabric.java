package tho.nill.datenlieferung.senden.sftp;

import java.io.IOException;

public interface SftpConnectionWrapperFabric<FILEINFO> {

	SftpConnectionWrapper<FILEINFO> create(String benutzername, String passwort, String host, int port,
			String localAktualDir, String remoteAktualDir) throws IOException;

}