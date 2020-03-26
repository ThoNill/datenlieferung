package tho.nill.datenlieferung;

import java.util.function.Consumer;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.thymeleaf.TemplateEngine;

import entities.Datenlieferung;
import lombok.extern.slf4j.Slf4j;
import repositories.AdresseRepository;
import repositories.DateiNummerRepository;
import repositories.DatenaustauschRepository;
import repositories.DatenlieferungProtokollRepository;
import repositories.DatenlieferungRepository;
import repositories.RechnungAuftragRepository;
import repositories.VersenderKeyRepository;
import repositories.ZertifikatRepository;
import tho.nill.datenlieferung.allgemein.DatenlieferungAktion;
import tho.nill.datenlieferung.allgemein.PathConfiguration;
import tho.nill.datenlieferung.auftragsdatei.ErzeugeAuftragsDatei;
import tho.nill.datenlieferung.nummernvergabe.Nummervergabe;
import tho.nill.datenlieferung.originaldatei.ErzeugeTestDatendatei;
import tho.nill.datenlieferung.senden.cd.AlleCDBelegeAusgeben;
import tho.nill.datenlieferung.senden.email.EMailSender;
import tho.nill.datenlieferung.senden.sftp.JschWrapperFabric;
import tho.nill.datenlieferung.senden.sftp.SftpSender;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;
import tho.nill.datenlieferung.sql.IdToDatenlieferungMap;
import tho.nill.datenlieferung.sql.SqlQueryFlux;
import tho.nill.datenlieferung.zertifikate.VerschlüsselnSigneren;

@Slf4j
@Service
public class DatenlieferungVersendenService extends BasisService<MonatJahr> implements Consumer<Datenlieferung> {

	@Autowired
	TemplateEngine templateEngine;

	@Autowired
	DataSource dataSource;

	@Autowired
	public AdresseRepository adresseRepo;

	@Autowired
	public DatenaustauschRepository datenaustauschRepo;

	@Autowired
	public DateiNummerRepository nummern;

	@Autowired
	public DatenlieferungRepository datenlieferungenRepo;

	@Autowired
	public DatenlieferungProtokollRepository datenlieferungenProtokollRepo;

	@Autowired
	public RechnungAuftragRepository rechnungAuftragRepo;

	private ZertifikatRepository zertifikatRepo;

	private VersenderKeyRepository keyRepo;

	@Autowired
	private PathConfiguration pathConfiguration;

	public DatenlieferungVersendenService(PlatformTransactionManager transactionManager,
			ZertifikatRepository zertifikatRepo, VersenderKeyRepository keyRepo) {
		super(transactionManager);
		this.zertifikatRepo = zertifikatRepo;
		this.keyRepo = keyRepo;
	}

	@Override
	public void performService(MonatJahr d) {
		try {
			String originalPath = pathConfiguration.dataPathOriginal;
			String verschlPath = pathConfiguration.dataPathVerschl;

			Nummervergabe nummernVergabe = new Nummervergabe(nummern);
			ErzeugeTestDatendatei erzeugen = new ErzeugeTestDatendatei(originalPath, verschlPath);
			VerschlüsselnSigneren verschlüsseln = new VerschlüsselnSigneren(keyRepo, zertifikatRepo, originalPath,
					verschlPath);
			ErzeugeAuftragsDatei auftragsdatei = new ErzeugeAuftragsDatei(originalPath, verschlPath);

			JschWrapperFabric wrapperFabric = new JschWrapperFabric();
			SftpSender sftpVersand = new SftpSender(datenaustauschRepo, wrapperFabric, originalPath, verschlPath);

			AlleCDBelegeAusgeben cdVersand = new AlleCDBelegeAusgeben(templateEngine, datenaustauschRepo, adresseRepo,
					datenlieferungenRepo, originalPath, verschlPath);

			EMailSender emailVersand = new EMailSender(templateEngine, datenaustauschRepo, adresseRepo, originalPath,
					verschlPath);

			VerteilerVersand sender = new VerteilerVersand(datenaustauschRepo, cdVersand, emailVersand, sftpVersand);

			SqlQueryFlux
					.using(dataSource,
							"select DatenlieferungId from datenlieferung where letzteAktion = "
									+ AktionsArt.ANGELEGT.ordinal() + " and mj = " + d.getMJ())
					.map(new IdToDatenlieferungMap(datenlieferungenRepo, 0))
					.map(new DatenlieferungAktion(AktionsArt.ANGELEGT, AktionsArt.NUMMERIERT, datenlieferungenRepo,
							datenlieferungenProtokollRepo, nummernVergabe))

					.map(new DatenlieferungAktion(AktionsArt.NUMMERIERT, AktionsArt.ERSTELLT, datenlieferungenRepo,
							datenlieferungenProtokollRepo, erzeugen))
					.map(new DatenlieferungAktion(AktionsArt.ERSTELLT, AktionsArt.VERSCHLÜSSELT, datenlieferungenRepo,
							datenlieferungenProtokollRepo, verschlüsseln))
					.map(new DatenlieferungAktion(AktionsArt.VERSCHLÜSSELT, AktionsArt.AUFTRAGSDATEI,
							datenlieferungenRepo, datenlieferungenProtokollRepo, auftragsdatei))
					.map(new DatenlieferungAktion(AktionsArt.AUFTRAGSDATEI, AktionsArt.GESENDET, datenlieferungenRepo,
							datenlieferungenProtokollRepo, sender))
					.subscribe(this);

		} catch (Exception e) {
			log.error("Exception in " + this.getClass().getCanonicalName() + ".performService", e);
		}
	}

	@Override
	/**
	 * braucht nicht implementiert werden
	 */
	public void accept(Datenlieferung datenlieferung) {
	}
}
