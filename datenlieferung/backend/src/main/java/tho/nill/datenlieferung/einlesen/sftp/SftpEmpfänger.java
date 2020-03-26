package tho.nill.datenlieferung.einlesen.sftp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import entities.Datenaustausch;
import entities.DatenlieferungProtokoll;
import entities.EingeleseneDatei;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import repositories.DatenaustauschRepository;
import repositories.EingeleseneDateiRepository;
import tho.nill.datenlieferung.allgemein.CreateProtokoll;
import tho.nill.datenlieferung.senden.sftp.JSchWrapper;
import tho.nill.datenlieferung.senden.sftp.JSchWrapper.FileData;
import tho.nill.datenlieferung.senden.sftp.JschWrapperFabric;
import tho.nill.datenlieferung.senden.sftp.SftpConnectionWrapper;
import tho.nill.datenlieferung.senden.sftp.SftpConnectionWrapperFabric;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

@Slf4j
public class SftpEmpfänger {
	private EingeleseneDateiRepository eingeleseneDateiRepo;
	private DatenaustauschRepository datenaustauschRepo;
	private SftpConnectionWrapperFabric<?> wrapperFabrik;

	public SftpEmpfänger(@NonNull EingeleseneDateiRepository eingeleseneDateiRepo,
			@NonNull DatenaustauschRepository datenaustauschRepo) {
		super();
		this.wrapperFabrik = new JschWrapperFabric();
		this.eingeleseneDateiRepo = eingeleseneDateiRepo;
		this.datenaustauschRepo = datenaustauschRepo;
	}

	public Optional<DatenlieferungProtokoll> action() {
		try {
			sftpLesen();
		} catch (IOException e) {
			log.error("Exception in " + this.getClass().getCanonicalName() + "action", e);
			return CreateProtokoll.create(AktionsArt.RÜCKMELDUNG, FehlerMeldung.EINGANG_SFTP, e);
		}
		return Optional.empty();
	}

	private void sftpLesen() throws IOException {
		List<Datenaustausch> ld = datenaustauschRepo.getVerbindung(Richtung.EINGANG, Verbindungsart.SFTP);
		for (Datenaustausch austausch : ld) {
			sftpLesen(austausch);
		}
	}

	private void sftpLesen(Datenaustausch austausch) throws IOException {
		try (SftpConnectionWrapper<?> wrapper = wrapperFabrik.create(austausch.getLoginNutzer(),
				austausch.getLoginPasswort(), austausch.getHost(), austausch.getPort(), "",
				austausch.getHostVerzeichnis())) {
			List<FileData> fileDescriptions = ((JSchWrapper) wrapper).getFileDataList(austausch.getHostVerzeichnis());
			for (FileData fileDescription : fileDescriptions) {
				sftpLesen(wrapper, austausch, fileDescription);
			}
		}

	}

	private void sftpLesen(SftpConnectionWrapper<?> wrapper, Datenaustausch austausch, FileData fileDescription)
			throws IOException {
		log.debug("Download Name " + fileDescription.getName());
		String localFile = fileDescription.getName();
		String remoteFile = fileDescription.getParentPath() + "/" + fileDescription.getName();
		wrapper.downloadFile(remoteFile, localFile);
		String text = new String(Files.readAllBytes(Paths.get(localFile)), austausch.getCodepage());
		log.debug("Text " + text);
		EingeleseneDatei datei = new EingeleseneDatei();
		datei.setArt(Verbindungsart.SFTP);
		datei.setDaten(text);
		datei.setHost(austausch.getHost());
		datei.setHost(austausch.getHostVerzeichnis());
		datei.setErstellt(LocalDateTime.now());
		eingeleseneDateiRepo.saveAndFlush(datei);
		wrapper.deleteRemoteFile(remoteFile);
	}

}