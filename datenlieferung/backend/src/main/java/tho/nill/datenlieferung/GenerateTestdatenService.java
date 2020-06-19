package tho.nill.datenlieferung;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import entities.Datenaustausch;
import entities.RechnungAuftrag;
import repositories.DatenaustauschRepository;
import repositories.RechnungAuftragRepository;
import tho.nill.datenlieferung.allgemein.DummyVoid;
import tho.nill.datenlieferung.senden.email.EMailConfiguration;
import tho.nill.datenlieferung.senden.sftp.SftpConfiguration;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

@Service
public class GenerateTestdatenService extends BasisService<DummyVoid> {

	private RechnungAuftragRepository rechnungsAuftragRepo;
	private DatenaustauschRepository datenaustauschRepo;

	@Autowired
	public SftpConfiguration configSftp;

	@Autowired
	public EMailConfiguration configEMail;

	private int rechnr = 1;
	private DatenArt datenart = DatenArt.PAR300ABRP;
	private int datenannahmeIk = 111111111;
	private int prüfungsIk = 222222222;
	private int versenderIk = 333333333;

	public GenerateTestdatenService(PlatformTransactionManager transactionManager,
			RechnungAuftragRepository rechnungsAuftragRepo, DatenaustauschRepository datenaustauschRep) {
		super(transactionManager);
		this.rechnungsAuftragRepo = rechnungsAuftragRepo;
		this.datenaustauschRepo = datenaustauschRep;
	}

	@Override
	public void performService(DummyVoid d) {
		datenart = DatenArt.PAR300ABRP;
		datenannahmeIk = 111111111;
		prüfungsIk = 222222222;
		versenderIk = 333333333;
		datenaustauschGenerierenSFTP();
		rechnungsAufträgeGenerieren(10);
		setVersenderIk(333333334);
		datenaustauschGenerierenSFTP();
		rechnungsAufträgeGenerieren(10);
		setDatenannahmeIk(111111112);
		datenaustauschGenerierenEmail();
		rechnungsAufträgeGenerieren(20);
		setPrüfungsIk(222222221);
		datenaustauschGenerierenEmail();
		rechnungsAufträgeGenerieren(5);
		setPrüfungsIk(222222228);
		datenaustauschGenerierenSFTP();
		rechnungsAufträgeGenerieren(5);

	}

	private void rechnungsAufträgeGenerieren(int anzahl) {
		for (int i = 0; i < anzahl; i++) {
			auftragGenerieren();
		}
	}

	private RechnungAuftrag auftragGenerieren() {
		RechnungAuftrag a = new RechnungAuftrag();
		a.setRechnungsNummer(rechnr);
		rechnr++;
		a.setDatenArt(datenart);
		a.setDatenAnnahmeIK(new IK(datenannahmeIk));
		a.setDatenPrüfungsIK(new IK(prüfungsIk));
		a.setVersenderIK(new IK(versenderIk));
		a.setMj(new MonatJahr(1, 2019));
		return rechnungsAuftragRepo.saveAndFlush(a);
	}

	private Datenaustausch datenaustauschGenerierenSFTP() {
		Datenaustausch datenaustausch = new Datenaustausch();
		datenaustausch.setDatenAnnahmeIK(new IK(datenannahmeIk));
		datenaustausch.setDatenPrüfungsIK(new IK(prüfungsIk));
		datenaustausch.setDatenArt(DatenArt.PAR300ABRP);
		datenaustausch.setVerbindung(Verbindungsart.SFTP);
		datenaustausch.setRichtung(Richtung.AUSGANG);
		datenaustausch.setVersenderIK(new IK(versenderIk));
		datenaustausch.setHost(configSftp.HOST);
		datenaustausch.setHostVerzeichnis("");
		datenaustausch.setPort(configSftp.PORT);
		datenaustausch.setLoginNutzer(configSftp.BENUTZERNAME);
		datenaustausch.setLoginPasswort(configSftp.PASSWORT);
		return datenaustauschRepo.saveAndFlush(datenaustausch);
	}

	private Datenaustausch datenaustauschGenerierenEmail() {
		Datenaustausch datenaustausch = new Datenaustausch();
		datenaustausch.setDatenAnnahmeIK(new IK(datenannahmeIk));
		datenaustausch.setDatenPrüfungsIK(new IK(prüfungsIk));
		datenaustausch.setDatenArt(DatenArt.PAR300ABRP);
		datenaustausch.setVerbindung(Verbindungsart.EMAIL);
		datenaustausch.setRichtung(Richtung.AUSGANG);
		datenaustausch.setVersenderIK(new IK(versenderIk));
		datenaustausch.setHost(configEMail.HOST);
		datenaustausch.setHostVerzeichnis("");
		datenaustausch.setPort(configEMail.PORT);
		datenaustausch.setLoginNutzer(configEMail.BENUTZERNAME);
		datenaustausch.setLoginPasswort(configEMail.PASSWORT);
		datenaustausch.setEmailFrom(configEMail.FROM);
		datenaustausch.setEmailTo(configEMail.TO);
		return datenaustauschRepo.saveAndFlush(datenaustausch);
	}

	public void setDatenart(DatenArt datenart) {
		this.datenart = datenart;
	}

	public void setDatenannahmeIk(int datenannahmeIk) {
		this.datenannahmeIk = datenannahmeIk;
	}

	public void setPrüfungsIk(int prüfungsIk) {
		this.prüfungsIk = prüfungsIk;
	}

	public void setVersenderIk(int versenderIk) {
		this.versenderIk = versenderIk;
	}

}
