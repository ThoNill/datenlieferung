package tho.nill.datenlieferung.senden.sftp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface SftpConnectionWrapper<FILEINFO> extends AutoCloseable {

	@Override
	void close();

	String getLocalActualDir();

	String getRemoteActualDir() throws IOException;

	/** Datei-Daten zu einer einzelnen Datei */
	FILEINFO getFileData(String remoteFilePath) throws IOException;

	/** Datei-Daten zu allen Dateien in einem Verzeichnis */
	List<FILEINFO> getFileDataList(String remoteDir) throws IOException;

	void createRemoteFile(InputStream is, String remoteDstFilePath) throws IOException;

	public void uploadFile(String localSrcFilePath, String remoteDstFilePath) throws IOException;

	void downloadFile(String remoteSrcFilePath, String localDstFilePath) throws IOException;

	void deleteRemoteFile(String remoteSrcFilePath) throws IOException;

}