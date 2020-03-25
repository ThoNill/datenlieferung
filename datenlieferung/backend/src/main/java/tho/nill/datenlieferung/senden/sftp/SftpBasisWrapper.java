package tho.nill.datenlieferung.senden.sftp;

import java.io.File;
import java.io.IOException;

import lombok.NonNull;

public abstract class SftpBasisWrapper<FILEINFO> implements SftpConnectionWrapper<FILEINFO> {

	private String localAktualDir;
	private String remoteAktualDir;

	public SftpBasisWrapper(@NonNull String localAktualDir, @NonNull String remoteAktualDir) {
		super();
		this.localAktualDir = localAktualDir;
		this.remoteAktualDir = remoteAktualDir;
	}

	@Override
	public String getLocalActualDir() {
		return localAktualDir;
	}

	@Override
	public String getRemoteActualDir() throws IOException {
		return remoteAktualDir;
	}

	protected static @NonNull String anpasssen(String remoteAktualDir) {
		if (remoteAktualDir == null || remoteAktualDir.length() == 0) {
			return "";
		}
		char c = remoteAktualDir.charAt(remoteAktualDir.length() - 1);
		if (c != '/' && c != File.separatorChar) {
			return remoteAktualDir + "/";
		}
		return remoteAktualDir;
	}

}