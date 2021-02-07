package tho.nill.datenlieferung.senden.sftp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.PathComponents;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;

@Slf4j
public class SshjWrapper extends SftpBasisWrapper<RemoteResourceInfo> {
	private SSHClient ssh;
	private SFTPClient sftp;

	public SshjWrapper(String benutzername, String passwort, String host, int port, String localAktualDir,
			String remoteAktualDir) throws IOException {
		super(localAktualDir, anpasssen(remoteAktualDir));
		try {
			log.debug("host " + host);
			log.debug("port " + port);
			log.debug("user " + benutzername);
			log.debug("passwort " + passwort);
			log.debug("vor SSHClient");
			ssh = new SSHClient();
			ssh.addHostKeyVerifier(new PromiscuousVerifier());
			ssh.setConnectTimeout(2000);
			ssh.setTimeout(2000);
			log.debug("vor connect");
			ssh.connect(host, port);
			ssh.authPassword(benutzername, passwort.toCharArray());

			sftp = ssh.newSFTPClient();
			sftp.getFileTransfer().setPreserveAttributes(false);
			log.debug("ende");

		} catch (Exception ex) {
			log.error("Exception in " + this.getClass().getCanonicalName() + "performService", ex);
		}
	}

	@Override
	public void uploadFile(String localSrcFilePath, String remoteDstFilePath) throws IOException {
		sftp.put(new FileSystemFile(localSrcFilePath), getRemoteActualDir() + remoteDstFilePath);
	}

	@Override
	public void close() {
		try {
			if (sftp != null) {
				sftp.close();
				sftp = null;
			}
		} catch (IOException e) {
			throw new DatenlieferungException("Sftp Error in close", e);
		}
		try {
			if (ssh != null) {
				ssh.disconnect();
				ssh = null;
			}
		} catch (IOException e) {
			throw new DatenlieferungException("ssh Error in disconnect", e);
		}
	}

	@Override
	public RemoteResourceInfo getFileData(String remoteFilePath) throws IOException {
		PathComponents pathComponent = new PathComponents("", remoteFilePath, "/");
		return new RemoteResourceInfo(pathComponent, sftp.lstat(remoteFilePath));
	}

	@Override
	public List<RemoteResourceInfo> getFileDataList(String remoteDir) throws IOException {
		return sftp.ls(getRemoteActualDir() + remoteDir);

	}

	@Override
	public void createRemoteFile(InputStream _is, String remoteDstFilePath) throws IOException {
		try (RemoteFile f = sftp.open(remoteDstFilePath); InputStream is = _is) {
			byte[] buffer = new byte[1000];
			long fileOffset = 0;
			int len;
			while ((len = is.read(buffer)) != -1) {
				f.write(fileOffset, buffer, 0, len);
				fileOffset += len;
			}
		}
	}

	@Override
	public void downloadFile(String remoteSrcFilePath, String localDstFilePath) throws IOException {
		sftp.get(getRemoteActualDir() + remoteSrcFilePath, localDstFilePath);
	}

	@Override
	public void deleteRemoteFile(String remoteSrcFilePath) throws IOException {
		sftp.rm(getRemoteActualDir() + remoteSrcFilePath);

	}

}