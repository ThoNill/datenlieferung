package tho.nill.datenlieferung.senden.sftp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.IdentityInfo;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

import lombok.extern.slf4j.Slf4j;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;

@Slf4j
public class SftpVFSWrapper extends SftpBasisWrapper<FileObject> {
	public static final String KEYNAME = "/id_client";
	private StandardFileSystemManager fileSystemManager;
	private FileObject remoteBasisFile;
	private FileObject localBasisFile;
	private String keysDir;

	public SftpVFSWrapper(String user, String password, String host, int port, String localAktualDir,
			String remoteAktualDir, String keysDir) throws FileSystemException {
		super(localAktualDir, anpasssen(remoteAktualDir));
		this.keysDir = keysDir;
		this.fileSystemManager = new StandardFileSystemManager();
		fileSystemManager.init();
		String connectionUrl;
		if (".".equals(remoteAktualDir)) {
			connectionUrl = String.format("sftp://%s:%s@%s:%d/", user, password, host, port);
		} else {
			connectionUrl = String.format("sftp://%s:%s@%s:%d/%s", user, password, host, port, remoteAktualDir);
		}
		log.debug("url: " + connectionUrl);

		FileSystemOptions connectionOptions = new FileSystemOptions();
		try {
			configureConnection(connectionOptions);
		} catch (GeneralSecurityException | IOException e) {
			throw new DatenlieferungException(e);
		}
		this.remoteBasisFile = fileSystemManager.resolveFile(connectionUrl, connectionOptions);
		this.localBasisFile = fileSystemManager.resolveFile(localAktualDir);
	}

	private void configureConnection(FileSystemOptions connectionOptions) throws GeneralSecurityException, IOException {
		SftpFileSystemConfigBuilder sftpConfigBuilder = SftpFileSystemConfigBuilder.getInstance();
		sftpConfigBuilder.setSessionTimeoutMillis(connectionOptions, 2000);
		sftpConfigBuilder.setStrictHostKeyChecking(connectionOptions, "no");
		sftpConfigBuilder.setPreferredAuthentications(connectionOptions, "password");
		String privKey = keysDir + KEYNAME + ".key";
		String pubKey = keysDir + KEYNAME + ".pub";
		File privFile = new File(privKey);
		File pubFile = new File(pubKey);
		IdentityInfo identityInfo = new IdentityInfo(privFile, pubFile, null);
		sftpConfigBuilder.setIdentityProvider(connectionOptions, identityInfo);
	}

	@Override
	public void close() {
		try {
			remoteBasisFile.close();
			localBasisFile.close();
			fileSystemManager.close();
		} catch (FileSystemException e) {
			throw new DatenlieferungException("VFS Filsystem can not close", e);
		}

	}

	@Override
	public FileObject getFileData(String remoteFilePath) throws IOException {
		return remoteBasisFile.resolveFile(remoteFilePath);
	}

	/** Geht nicht, da vom VFS nicht implementiert!!! */
	@Override
	public List<FileObject> getFileDataList(String remoteFilePath) throws IOException {
		return Arrays.<FileObject>asList(remoteBasisFile.resolveFile(remoteFilePath).getChildren());
	}

	@Override
	public void createRemoteFile(InputStream is, String remoteDstFilePath) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void downloadFile(String remoteSrcFilePath, String localDstFilePath) throws IOException {
		FileObject local = localBasisFile.resolveFile(localDstFilePath);
		FileObject remote = remoteBasisFile.resolveFile(remoteSrcFilePath);
		local.copyFrom(remote, Selectors.SELECT_SELF);

	}

	@Override
	public void uploadFile(String localSrcFilePath, String remoteDstFilePath) throws IOException {
		FileObject local = localBasisFile.resolveFile(localSrcFilePath);
		FileObject remote = remoteBasisFile.resolveFile(remoteDstFilePath);
		remote.copyFrom(local, Selectors.SELECT_SELF);
	}

	@Override
	public void deleteRemoteFile(String remoteSrcFilePath) throws IOException {
		FileObject remote = remoteBasisFile.resolveFile(remoteSrcFilePath);
		remote.delete();
	}
}
