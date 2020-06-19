package tho.nill.datenlieferung;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import entities.Datenaustausch;
import repositories.DatenaustauschRepository;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

@Service
public class DatenaustauschEntityService {

	@Autowired
	DatenaustauschRepository datenaustauschRepro;

	public DatenaustauschEntityService() {
		super();
	}

	public Optional<Datenaustausch> get(long id) {
		return datenaustauschRepro.findById(id);
	}

	public Datenaustausch create(IK versenderIK, IK datenAnnahmeIK, IK datenPrüfungsIK, Richtung richtung,
			DatenArt datenArt, Verbindungsart verbindung, String host, int port, String hostVerzeichnis, String emailTo,
			String emailFrom, String loginNutzer, String loginPasswort, String name, String straße, String plz,
			String annahmeClassName, String codepage) {
		Datenaustausch d = new Datenaustausch();
		felderSetzen(d, versenderIK, datenAnnahmeIK, datenPrüfungsIK,

				richtung, datenArt,

				verbindung,

				host, port, hostVerzeichnis,

				emailTo, emailFrom, loginNutzer, loginPasswort, name, straße, plz,

				annahmeClassName, codepage);
		return datenaustauschRepro.save(d);
	}

	public void update(long id, IK versenderIK, IK datenAnnahmeIK, IK datenPrüfungsIK, Richtung richtung,
			DatenArt datenArt, Verbindungsart verbindung, String host, int port, String hostVerzeichnis, String emailTo,
			String emailFrom, String loginNutzer, String loginPasswort, String name, String straße, String plz,
			String annahmeClassName, String codepage) {
		Datenaustausch d = datenaustauschRepro.getOne(id);
		felderSetzen(d, versenderIK, datenAnnahmeIK, datenPrüfungsIK,

				richtung, datenArt,

				verbindung,

				host, port, hostVerzeichnis,

				emailTo, emailFrom, loginNutzer, loginPasswort, name, straße, plz,

				annahmeClassName, codepage);
		datenaustauschRepro.save(d);
	}

	public void delete(long id) {
		datenaustauschRepro.deleteById(id);

	}

	private void felderSetzen(Datenaustausch d, IK versenderIK, IK datenAnnahmeIK, IK datenPrüfungsIK,
			Richtung richtung, DatenArt datenArt, Verbindungsart verbindung, String host, int port,
			String hostVerzeichnis, String emailTo, String emailFrom, String loginNutzer, String loginPasswort,
			String name, String straße, String plz, String annahmeClassName, String codepage) {
		d.setVersenderIK(versenderIK);
		d.setDatenAnnahmeIK(datenAnnahmeIK);
		d.setDatenPrüfungsIK(datenPrüfungsIK);

		d.setRichtung(richtung);
		d.setDatenArt(datenArt);
		d.setVerbindung(verbindung);
		d.setHost(host);
		d.setPort(port);
		d.setHostVerzeichnis(hostVerzeichnis);
		d.setEmailTo(emailTo);
		d.setEmailFrom(emailFrom);
		d.setLoginNutzer(loginNutzer);
		d.setLoginPasswort(loginPasswort);
		// TODO ByteBLOB loginCert()loginCert);
		d.setName(name);
		d.setStrasse(straße);
		d.setPlz(plz);
		d.setAnnahmeClassName(annahmeClassName);
		d.setCodepage(codepage);
	}
}
