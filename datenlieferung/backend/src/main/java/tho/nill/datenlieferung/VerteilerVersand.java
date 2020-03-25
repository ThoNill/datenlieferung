package tho.nill.datenlieferung;

import java.util.List;
import java.util.Optional;

import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import repositories.DatenaustauschRepository;
import tho.nill.datenlieferung.allgemein.Action;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

public class VerteilerVersand implements Action {

	private DatenaustauschRepository datenaustauschRepo;
	private Action cdVersand;
	private Action emailVersand;
	private Action sftpVersand;

	public VerteilerVersand(DatenaustauschRepository datenaustauschRepo, Action cdVersand, Action emailVersand,
			Action sftpVersand) {
		super();
		this.datenaustauschRepo = datenaustauschRepo;
		this.cdVersand = cdVersand;
		this.emailVersand = emailVersand;
		this.sftpVersand = sftpVersand;
	}

	@Override
	public Optional<DatenlieferungProtokoll> action(Datenlieferung datenlieferung) {
		List<Verbindungsart> da = datenaustauschRepo.sucheVerbindung(datenlieferung.getVersenderIK(),
				datenlieferung.getDatenAnnahmeIK(), datenlieferung.getDatenPrüfungsIK(), Richtung.AUSGANG,
				datenlieferung.getDatenArt());
		if (da.size() == 1) {
			Verbindungsart art = da.get(0);
			switch (art) {
			case CD:
				return cdVersand.action(datenlieferung);
			case EMAIL:
				return emailVersand.action(datenlieferung);
			case SFTP:
				return sftpVersand.action(datenlieferung);
			default:
				break;

			}
		}
		return Optional.empty();
	}

}
