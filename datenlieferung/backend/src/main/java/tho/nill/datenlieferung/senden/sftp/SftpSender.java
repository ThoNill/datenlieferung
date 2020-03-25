package tho.nill.datenlieferung.senden.sftp;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import entities.Datenaustausch;
import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import lombok.NonNull;
import repositories.DatenaustauschRepository;
import tho.nill.datenlieferung.allgemein.Action;
import tho.nill.datenlieferung.allgemein.CreateProtokoll;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

public class SftpSender extends Verzeichnisse implements Action {

	private DatenaustauschRepository datenaustauschRepo;
	private SftpConnectionWrapperFabric<?> wrapperFabrik;

	public SftpSender(@NonNull DatenaustauschRepository datenaustauschRepo,
			@NonNull SftpConnectionWrapperFabric<?> wrapperFabrik, @NonNull String datenPathOriginal,
			@NonNull String datenPathVerschl) {
		super(datenPathOriginal, datenPathVerschl);
		this.datenaustauschRepo = datenaustauschRepo;
		this.wrapperFabrik = wrapperFabrik;
	}

	@Override
	public Optional<DatenlieferungProtokoll> action(@NonNull Datenlieferung datenlieferung) {
		try {
			datenaustauschBestimmenUndSenden(datenlieferung);
		} catch (Exception e) {
			return CreateProtokoll.create(AktionsArt.GESENDET, FehlerMeldung.FILE_SFTP, e,
					datenlieferung.getDatenlieferungId());
		}
		return Optional.empty();
	}

	private void datenaustauschBestimmenUndSenden(Datenlieferung datenlieferung) throws IOException {
		List<Datenaustausch> da = datenaustauschRepo.sucheDatenaustausch(datenlieferung.getVersenderIK(),
				datenlieferung.getDatenAnnahmeIK(), datenlieferung.getDatenPrüfungsIK(), Richtung.AUSGANG,
				datenlieferung.getDatenArt(), Verbindungsart.SFTP);
		if (!da.isEmpty()) {
			senden(datenlieferung, da.get(0));
		} else {
			throw new DatenlieferungException(
					"Keine Angabe zum Datenaustausch für " + datenlieferung.getDatenlieferungId());
		}
	}

	public void senden(@NonNull Datenlieferung datenlieferung, @NonNull Datenaustausch austausch) throws IOException {
		checkDatenAustausch(austausch);

		try (SftpConnectionWrapper<?> wrapper = wrapperFabrik.create(austausch.getLoginNutzer(),
				austausch.getLoginPasswort(), austausch.getHost(), austausch.getPort(), "",
				austausch.getHostVerzeichnis())) {
			wrapper.uploadFile(getAuftragsFile(datenlieferung).getAbsolutePath(),
					datenlieferung.getPhysDateiname() + ".AUF");
			wrapper.uploadFile(getVerschlFile(datenlieferung).getAbsolutePath(), datenlieferung.getPhysDateiname());
			wrapper.close();
		}
	}

	private void checkDatenAustausch(Datenaustausch austausch) {
		if (austausch.getLoginNutzer() == null) {
			throw new DatenlieferungException(
					"Keine Angabe zum Datenaustausch für getLoginNutzer ID= " + austausch.getDatenaustauschId());

		}
		if (austausch.getLoginPasswort() == null) {
			throw new DatenlieferungException(
					"Keine Angabe zum Datenaustausch für getLoginPasswort ID= " + austausch.getDatenaustauschId());

		}
		if (austausch.getHost() == null) {
			throw new DatenlieferungException(
					"Keine Angabe zum Datenaustausch für getHost ID= " + austausch.getDatenaustauschId());

		}
		if (austausch.getHostVerzeichnis() == null) {
			throw new DatenlieferungException(
					"Keine Angabe zum Datenaustausch für getHostVerzeichnis ID= " + austausch.getDatenaustauschId());

		}
		if (austausch.getPort() == 0) {
			throw new DatenlieferungException(
					"Keine Angabe zum Datenaustausch für getPort ID= " + austausch.getDatenaustauschId());

		}
	}

}