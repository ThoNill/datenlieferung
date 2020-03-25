package tho.nill.datenlieferung.senden.sftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import tho.nill.datenlieferung.allgemein.Dateien;
import tho.nill.datenlieferung.senden.sftp.JSchWrapper.FileData;;

/**
 * Wrapper-Klasse zur Dateiuebertragung von und zu einem SFTP-Server sowie zum
 * Entpacken von .zip und .gz. Die Wrapper-Klasse dient zur Entkopplung von der
 * SFTP-Implementierung.
 */
@Slf4j
public class JSchWrapper extends SftpBasisWrapper<FileData> {
	private static final String FEHLER_BEIM_UNZIP_VON = "Fehler beim Unzip von ";
	private static final String AN = "' an '";
	private Session session;
	private ChannelSftp channel;

	public JSchWrapper(String benutzername, String passwort, String host, int port, String localAktualDir,
			String remoteAktualDir) throws IOException {
		super(localAktualDir, remoteAktualDir);
		log.debug("host " + host);
		log.debug("port " + port);
		log.debug("user " + benutzername);
		log.debug("passwort " + passwort);

		log.debug("start client  connect");
		JSch.setConfig("ssh-rsa", JSch.getConfig("signature.rsa"));
		JSch.setConfig("ssh-dss", JSch.getConfig("signature.dss"));

		JSch.setLogger(new Logger() {

			@Override
			public boolean isEnabled(int level) {
				return true;
			}

			@Override
			public void log(int level, String message) {
				log.debug("JSched " + message);

			}

		});
		try {

			session = (new JSch()).getSession(benutzername, host, port);
			session.setPassword(passwort);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setConfig("ssh-rsa", JSch.getConfig("signature.rsa"));
			session.setConfig("ssh-dss", JSch.getConfig("signature.dss"));
			session.connect();
		} catch (JSchException ex) {
			log.error("Exception in " + this.getClass().getCanonicalName() + ".constructor1 ", ex);
			throw new IOException("Fehler beim SFTP-Connect mit '" + benutzername + AN + host + "': ", ex);
		}
		log.debug("nach client connect");
		try {
			Files.createDirectories(new File(remoteAktualDir).toPath());
			channel = (ChannelSftp) session.openChannel("sftp");
			if (channel == null) {
				close();
				throw new IOException("Fehler beim Oeffnen des SFTP-Channel zur SFTP-Session mit '"
						+ session.getUserName() + AN + session.getHost() + "'. ");
			}
			channel.connect();
			channel.cd(remoteAktualDir);
		} catch (JSchException | SftpException ex) {
			ex.printStackTrace();
			log.error("Exception in " + this.getClass().getCanonicalName() + ".constructor 2 ", ex);
			close();
			throw new IOException("Fehler beim Oeffnen des SFTP-Channel zur SFTP-Session mit '" + session.getUserName()
					+ AN + session.getHost() + "': ", ex);
		}
	}

	@Override
	public void close() {
		try {
			if (channel != null) {
				channel.disconnect();
				channel = null;
			}
		} finally {
			if (session != null) {
				session.disconnect();
				session = null;
			}
		}
	}

	@Override
	public String getLocalActualDir() {
		return channel.lpwd();
	}

	@Override
	public String getRemoteActualDir() throws IOException {
		try {
			return channel.pwd();
		} catch (SftpException ex) {
			throw new IOException(ex);
		}
	}

	/** Datei-Daten zu einer einzelnen Datei */
	@Override
	public FileData getFileData(String remoteFilePath) throws IOException {
		try {
			@SuppressWarnings("unchecked")
			List<ChannelSftp.LsEntry> lsEntryLst = channel.ls(remoteFilePath);
			if (lsEntryLst == null || lsEntryLst.size() != 1) {
				return null;
			}
			LsEntry lsEntry = lsEntryLst.get(0);
			FileData fd = new FileData();
			int i = remoteFilePath.lastIndexOf('/');
			fd.parentPath = (i < 0) ? "" : remoteFilePath.substring(0, i);
			fd.isFile = !lsEntry.getAttrs().isDir() && !lsEntry.getAttrs().isLink();
			fd.name = lsEntry.getFilename();
			fd.size = lsEntry.getAttrs().getSize();
			fd.timestamp = Calendar.getInstance();
			fd.timestamp.setTimeInMillis(1000L * lsEntry.getAttrs().getMTime());
			return fd;
		} catch (SftpException ex) {
			throw new IOException(ex);
		}
	}

	/** Datei-Daten zu allen Dateien in einem Verzeichnis */
	@Override
	public List<FileData> getFileDataList(String remoteDir) throws IOException {
		try {
			List<FileData> fileDataLst = new ArrayList<FileData>();
			@SuppressWarnings("unchecked")
			List<ChannelSftp.LsEntry> lsEntryLst = channel.ls(remoteDir);
			for (LsEntry lsEntry : lsEntryLst) {
				FileData fd = new FileData();
				fd.parentPath = remoteDir;
				fd.isFile = !lsEntry.getAttrs().isDir(); // && !lsEntry.getAttrs().isLink();
				fd.name = lsEntry.getFilename();
				fd.size = lsEntry.getAttrs().getSize();
				fd.timestamp = Calendar.getInstance();
				fd.timestamp.setTimeInMillis(1000L * lsEntry.getAttrs().getMTime());
				log.debug("Datei: " + fd.name);
				if (fd.isFile) {
					fileDataLst.add(fd);
				}
			}
			log.debug("Dateianzahl: " + fileDataLst.size());
			return fileDataLst;
		} catch (SftpException ex) {
			ex.printStackTrace();
			throw new IOException(ex);
		}
	}

	@Override
	public void createRemoteFile(InputStream is, String remoteDstFilePath) throws IOException {
		try {
			channel.put(is, remoteDstFilePath);
		} catch (SftpException ex) {
			throw new IOException(ex);
		}
	}

	@Override
	public void uploadFile(String localSrcFilePath, String remoteDstFilePath) throws IOException {
		try {
			log.debug("uploadFile local " + localSrcFilePath);
			log.debug("uploadFile remote " + remoteDstFilePath);
			channel.put(localSrcFilePath, remoteDstFilePath);
			log.debug("Die Datei local " + localSrcFilePath + " wurde hochgeladen");
		} catch (SftpException ex) {
			ex.printStackTrace();
			throw new IOException(ex);
		}
	}

	@Override
	public void downloadFile(String remoteSrcFilePath, String localDstFilePath) throws IOException {
		try {
			log.debug("downloadFile local " + localDstFilePath);
			log.debug("downloadFile remote " + new File(remoteSrcFilePath).getName());

			channel.get(remoteSrcFilePath, localDstFilePath);
			log.debug("Die Datei local " + remoteSrcFilePath + " wurde heruntergeladen");
		} catch (SftpException ex) {
			throw new IOException(ex);
		}
	}

	/** Entpacken von remote .gz */
	public void ungzipRemote(String remoteSourceZipFile, String localDestFilePath) throws IOException {
		try (InputStream instreamZipped = channel.get(remoteSourceZipFile)) {
			ungzipStream(instreamZipped, localDestFilePath);
		} catch (Exception ex) {
			throw new IOException(FEHLER_BEIM_UNZIP_VON + remoteSourceZipFile + ",", ex);
		}
	}

	/** Entpacken von lokalem .gz */
	public static void ungzipLocal(String localSourceZipFile, String localDestFilePath) throws IOException {
		try (InputStream instreamZipped = Dateien.createInputStream(localSourceZipFile)) {
			ungzipStream(instreamZipped, localDestFilePath);
		} catch (Exception ex) {
			throw new IOException(FEHLER_BEIM_UNZIP_VON + localSourceZipFile + ",", ex);
		}
	}

	/** Entpacken von .gz */
	public static void ungzipStream(InputStream instreamZipped, String localDestFilePath) throws IOException {
		try (GZIPInputStream zin = new GZIPInputStream(new BufferedInputStream(instreamZipped))) {
			try (BufferedOutputStream os = new BufferedOutputStream(Dateien.createOutputStream(localDestFilePath))) {
				int size;
				byte[] buffer = new byte[64 * 1024];
				while ((size = zin.read(buffer, 0, buffer.length)) > 0) {
					os.write(buffer, 0, size);
				}
			}
		}
	}

	/** Entpacken von remote .zip */
	public long unzipRemote(String remoteSourceZipFile, String localDestDir) throws IOException {
		try (InputStream instreamZipped = channel.get(remoteSourceZipFile)) {
			return unzipStream(instreamZipped, localDestDir);
		} catch (Exception ex) {
			throw new IOException(FEHLER_BEIM_UNZIP_VON + remoteSourceZipFile + ",", ex);
		}
	}

	/** Entpacken von lokalem .zip */
	public static long unzipLocal(String localSourceZipFile, String localDestDir) throws IOException {
		try (InputStream instreamZipped = Dateien.createInputStream(localSourceZipFile)) {
			return unzipStream(instreamZipped, localDestDir);
		} catch (Exception ex) {
			throw new IOException(FEHLER_BEIM_UNZIP_VON + localSourceZipFile + ",", ex);
		}
	}

	/** Entpacken von .zip */
	public static long unzipStream(InputStream instreamZipped, String localDestDir) throws IOException {
		long anzahlEntries = 0;
		String remoteResultFilename = null;
		String destDir = (localDestDir == null) ? "" : localDestDir.trim();
		destDir = (destDir.endsWith("/") || destDir.endsWith("\\")) ? destDir : (destDir + File.separator);
		try (ZipInputStream zin = new ZipInputStream(new BufferedInputStream(instreamZipped))) {
			ZipEntry zipEntry;
			while ((zipEntry = zin.getNextEntry()) != null) {
				remoteResultFilename = zipEntry.getName();
				if (remoteResultFilename != null && remoteResultFilename.startsWith("/")
						&& remoteResultFilename.length() > 1) {
					remoteResultFilename = remoteResultFilename.substring(1);
				}
				try (BufferedOutputStream os = new BufferedOutputStream(
						Dateien.createOutputStream(destDir + remoteResultFilename))) {
					int size;
					byte[] buffer = new byte[64 * 1024];
					while ((size = zin.read(buffer, 0, buffer.length)) > 0) {
						os.write(buffer, 0, size);
					}
				}
				zin.closeEntry();
				anzahlEntries++;
			}
		} catch (Exception ex) {
			throw new IOException("Fehler beim Unzip, letzter Zip-Entry " + remoteResultFilename + ",", ex);
		}
		return anzahlEntries;
	}

	/** Downloaden sowie Entpacken von .zip und .gz */
	public void downloadAndUnzip(String remoteSrcDir, String localDstDir, String filenameMustContain,
			int maxAlterInTagen) throws IOException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -1 * maxAlterInTagen);
		(Dateien.createFile(localDstDir)).mkdirs();
		List<FileData> fds = getFileDataList(remoteSrcDir);
		for (FileData fd : fds) {
			if (fd.isFile && fd.name.contains(filenameMustContain) && fd.timestamp.after(cal)) {
				String remoteSrcFilePath = fd.parentPath + "/" + fd.name;
				String localDstFilePath = localDstDir + File.separator + fd.name;
				if (fd.name.toLowerCase().endsWith(".zip")) {
					unzipRemote(remoteSrcFilePath, localDstDir);
				} else if (fd.name.toLowerCase().endsWith(".gz")) {
					ungzipRemote(remoteSrcFilePath, localDstFilePath.substring(0, localDstFilePath.length() - 3));
				} else {
					downloadFile(remoteSrcFilePath, localDstFilePath);
				}
			}
		}
	}

	/** Downloaden sowie Entpacken von .zip und .gz */
	public static void downloadAndUnzip(String remoteSrcDir, String localDstDir, String filenameMustContain,
			String maxAlterInTagen, String benutzername, String passwort, String host, String port) throws IOException {
		try (JSchWrapper sftpWrapper = new JSchWrapper(benutzername, passwort, host, Integer.parseInt(port),
				localDstDir, remoteSrcDir)) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			log.debug("Remote in " + remoteSrcDir + ":");
			List<FileData> fds = sftpWrapper.getFileDataList(remoteSrcDir);
			for (FileData fd : fds) {
				if (fd.isFile) {
					log.debug(df.format(Long.valueOf(fd.timestamp.getTimeInMillis())) + ", " + fd.size + " Bytes, "
							+ fd.name);
				}
			}
			sftpWrapper.downloadAndUnzip(remoteSrcDir, localDstDir, filenameMustContain,
					Integer.parseInt(maxAlterInTagen));
			log.debug("Lokal in " + localDstDir + ":");
			File[] fls = (Dateien.createFile(localDstDir)).listFiles();
			for (File fl : fls) {
				log.debug(df.format(Long.valueOf(fl.lastModified())) + ", " + fl.length() + " Bytes, " + fl.getName());
			}
		}
	}

	/** Datei-Daten */
	@Data
	public static class FileData {
		private boolean isFile;
		private String parentPath;
		private String name;
		private long size;
		private Calendar timestamp;
	}

	@Override
	public void deleteRemoteFile(String remoteSrcFilePath) throws IOException {
		try {
			channel.rm(remoteSrcFilePath);
		} catch (SftpException ex) {
			throw new IOException(ex);
		}

	}
}