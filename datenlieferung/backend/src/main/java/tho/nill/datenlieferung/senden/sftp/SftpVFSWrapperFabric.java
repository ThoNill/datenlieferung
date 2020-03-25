package tho.nill.datenlieferung.senden.sftp;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

public class SftpVFSWrapperFabric implements SftpConnectionWrapperFabric<FileObject> {
	private String keysDir;

	public SftpVFSWrapperFabric(String keysDir) {
		super();
		this.keysDir = keysDir;
	}

	@Override
	public SftpVFSWrapper create(String benutzername, String passwort, String host, int port, String localAktualDir,
			String remoteAktualDir) throws FileSystemException {
		return new SftpVFSWrapper(benutzername, passwort, host, port, localAktualDir, remoteAktualDir, keysDir);
	}

}
