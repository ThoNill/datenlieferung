package tho.nill.datenlieferung.senden.cd;

import java.util.List;
import java.util.Optional;

import org.thymeleaf.TemplateEngine;

import entities.Datenaustausch;
import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import lombok.NonNull;
import repositories.AdresseRepository;
import repositories.DatenaustauschRepository;
import repositories.DatenlieferungRepository;
import tho.nill.datenlieferung.allgemein.Action;
import tho.nill.datenlieferung.senden.email.EMailSender;
import tho.nill.datenlieferung.senden.sftp.SftpConnectionWrapperFabric;
import tho.nill.datenlieferung.senden.sftp.SftpSender;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

public class Senden implements Action {
	private TemplateEngine templateEngine;
	private DatenlieferungRepository datenlieferungenRepo;
	private DatenaustauschRepository datenaustauschRepo;
	private AdresseRepository adresseRepo;
	private SftpConnectionWrapperFabric<?> wrapperFabrik;
	private String datenPathOriginal;
	private String datenPathVerschl;

	public Senden(@NonNull TemplateEngine templateEngine, @NonNull DatenlieferungRepository datenlieferungenRepo,
			@NonNull DatenaustauschRepository datenaustauschRepo, @NonNull AdresseRepository adresseRepo,
			@NonNull SftpConnectionWrapperFabric<?> wrapperFabrik, @NonNull String datenPathOriginal,
			@NonNull String datenPathVerschl) {
		super();
		this.templateEngine = templateEngine;
		this.datenlieferungenRepo = datenlieferungenRepo;
		this.datenaustauschRepo = datenaustauschRepo;
		this.adresseRepo = adresseRepo;
		this.wrapperFabrik = wrapperFabrik;
		this.datenPathOriginal = datenPathOriginal;
		this.datenPathVerschl = datenPathVerschl;
	}

	@Override
	public Optional<DatenlieferungProtokoll> action(Datenlieferung datenlieferung) {
		// TODO warum nur Verbindungsart.CD ?
		List<Datenaustausch> datenaustauschListe = datenaustauschRepo.sucheDatenaustausch(
				datenlieferung.getVersenderIK(), datenlieferung.getDatenAnnahmeIK(),
				datenlieferung.getDatenPrüfungsIK(), Richtung.AUSGANG, datenlieferung.getDatenArt(), Verbindungsart.CD);
		if (!datenaustauschListe.isEmpty()) {
			Datenaustausch datenaustausch = datenaustauschListe.get(0);
			switch (datenaustausch.getVerbindung()) {
			case CD:
				return new AlleCDBelegeAusgeben(templateEngine, datenaustauschRepo, adresseRepo, datenlieferungenRepo,
						datenPathOriginal, datenPathVerschl).action(datenlieferung);
			case EMAIL:
				return new EMailSender(templateEngine, datenaustauschRepo, adresseRepo, datenPathOriginal,
						datenPathVerschl).action(datenlieferung);
			case SFTP:
				return new SftpSender(datenaustauschRepo, wrapperFabrik, datenPathOriginal, datenPathVerschl)
						.action(datenlieferung);
			default:
				break;

			}
		}
		return Optional.empty();
	}

}
