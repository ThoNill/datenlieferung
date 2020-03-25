package tho.nill.datenlieferung;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import entities.Datenaustausch;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

@RestController
@CrossOrigin(origins = "https://localhost:4200", maxAge = 3600)
@RequestMapping("/api")
public class DatenaustauschEntityController {

	private static final String DER_DATENAUSTAUSCH_DATENSATZES_MIT_DER = "Der Datenaustausch Datensatzes mit der ";
	@Autowired
	private DatenaustauschEntityService service;

	public DatenaustauschEntityController() {
		super();
	}

	@CrossOrigin
	@RequestMapping(path = "/entity/datenaustausch/insert", produces = "application/json", method = RequestMethod.POST)
	public String create(@RequestParam(name = "versenderIK") String versenderIK,
			@RequestParam(name = "datenAnnahmeIK") String datenAnnahmeIK,
			@RequestParam(name = "datenPrüfungsIK") String datenPrüfungsIK,
			@RequestParam(name = "richtung") String richtung, @RequestParam(name = "datenArt") String datenArt,
			@RequestParam(name = "verbindung") String verbindung, @RequestParam(name = "host") String host,
			@RequestParam(name = "port") int port, @RequestParam(name = "hostVerzeichnis") String hostVerzeichnis,
			@RequestParam(name = "emailTo") String emailTo, @RequestParam(name = "emailFrom") String emailFrom,
			@RequestParam(name = "loginNutzer") String loginNutzer,
			@RequestParam(name = "loginPasswort") String loginPasswort, @RequestParam(name = "name") String name,
			@RequestParam(name = "strasse") String straße, @RequestParam(name = "plz") String plz,
			@RequestParam(name = "annahmeClassName") String annahmeClassName,
			@RequestParam(name = "codepage") String codepage) {

		try {
			Datenaustausch d = service.create(new IK(Integer.parseInt(versenderIK)),
					new IK(Integer.parseInt(datenAnnahmeIK)), new IK(Integer.parseInt(datenPrüfungsIK)),

					Richtung.valueOf(richtung), DatenArt.valueOf(datenArt),

					Verbindungsart.valueOf(verbindung),

					host, port, hostVerzeichnis, emailTo, emailFrom, loginNutzer, loginPasswort, name, straße, plz,
					annahmeClassName, codepage);
			return DER_DATENAUSTAUSCH_DATENSATZES_MIT_DER + d.getDatenaustauschId() + " wurden erfolgreich angelegt";
		} catch (Exception e) {
			return "Beim Anlegen eines Datenaustausch Datensatzes trat ein Fehler auf";
		}
	}

	@CrossOrigin
	@RequestMapping(path = "/entity/datenaustausch/update/{id}", produces = "application/json", method = RequestMethod.PUT)
	public String update(@PathVariable(name = "id") long id, @RequestParam(name = "versenderIK") String versenderIK,
			@RequestParam(name = "datenAnnahmeIK") String datenAnnahmeIK,
			@RequestParam(name = "datenPrüfungsIK") String datenPrüfungsIK,
			@RequestParam(name = "richtung") String richtung, @RequestParam(name = "datenArt") String datenArt,
			@RequestParam(name = "verbindung") String verbindung, @RequestParam(name = "host") String host,
			@RequestParam(name = "port") int port, @RequestParam(name = "hostVerzeichnis") String hostVerzeichnis,
			@RequestParam(name = "emailTo") String emailTo, @RequestParam(name = "emailFrom") String emailFrom,
			@RequestParam(name = "loginNutzer") String loginNutzer,
			@RequestParam(name = "loginPasswort") String loginPasswort, @RequestParam(name = "name") String name,
			@RequestParam(name = "strasse") String straße, @RequestParam(name = "plz") String plz,
			@RequestParam(name = "annahmeClassName") String annahmeClassName,
			@RequestParam(name = "codepage") String codepage) {

		try {
			service.update(id, new IK(Integer.parseInt(versenderIK)), new IK(Integer.parseInt(datenAnnahmeIK)),
					new IK(Integer.parseInt(datenPrüfungsIK)),

					Richtung.valueOf(richtung), DatenArt.valueOf(datenArt),

					Verbindungsart.valueOf(verbindung),

					host, port, hostVerzeichnis,

					emailTo, emailFrom, loginNutzer, loginPasswort, name, straße, plz,

					annahmeClassName, codepage);
			return DER_DATENAUSTAUSCH_DATENSATZES_MIT_DER + id + " wurden erfolgreich geändert";
		} catch (Exception e) {
			return "Beim Ändern des Datenaustausch Datensatzes mit der " + id + " trat ein Fehler auf";
		}
	}

	@CrossOrigin
	@RequestMapping(path = "/entity/datenaustausch/delete/{id}", produces = "application/json", method = RequestMethod.DELETE)
	public String delete(@PathVariable(name = "id") long id) {

		try {
			service.delete(id);
			return DER_DATENAUSTAUSCH_DATENSATZES_MIT_DER + id + " wurden erfolgreich entfernt";
		} catch (Exception e) {
			return "Beim Entfernen des Datenaustausch Datensatzes mit der " + id + " trat ein Fehler auf";
		}
	}

	@CrossOrigin
	@RequestMapping(path = "/entity/datenaustausch/get/{id}", produces = "application/json", method = RequestMethod.GET)
	public Datenaustausch get(@PathVariable(name = "id") long id) {

		try {
			Optional<Datenaustausch> o = service.get(id);
			if (o.isPresent()) {
				return o.get();
			}
		} catch (Exception e) {
			throw new DatenlieferungException("Exception bei get ", e);
		}
		return null;
	}

}
