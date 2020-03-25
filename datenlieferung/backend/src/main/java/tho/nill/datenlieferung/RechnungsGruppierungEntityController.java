package tho.nill.datenlieferung;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import entities.RechnungsGruppierung;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;

@RestController
@CrossOrigin(origins = "https://localhost:4200", maxAge = 3600)
@RequestMapping("/api")
public class RechnungsGruppierungEntityController {

	private static final String DER_RECHNUNGS_GRUPPIERUNG_DATENSATZES_MIT_DER = "Der RechnungsGruppierung Datensatzes mit der ";
	@Autowired
	private RechnungsGruppierungEntityService service;

	public RechnungsGruppierungEntityController() {
		super();
	}

	@CrossOrigin
	@RequestMapping(path = "/entity/rechnungsGruppierung/insert", produces = "application/json", method = RequestMethod.POST)
	public String create(@RequestParam(name = "versenderIK") String versenderIK,

			@RequestParam(name = "datenPrüfungsIK") String datenPrüfungsIK,
			@RequestParam(name = "datenArt") String datenArt,
			@RequestParam(name = "maxRechnungsAnzahl") int maxRechnungsAnzahl) {

		try {
			RechnungsGruppierung d = service.create(new IK(Integer.parseInt(versenderIK)),
					new IK(Integer.parseInt(datenPrüfungsIK)), DatenArt.valueOf(datenArt), maxRechnungsAnzahl);
			return DER_RECHNUNGS_GRUPPIERUNG_DATENSATZES_MIT_DER + d.getRechnungsGruppierungId()
					+ " wurden erfolgreich angelegt";
		} catch (Exception e) {
			return "Beim Anlegen eines RechnungsGruppierung Datensatzes trat ein Fehler auf";
		}
	}

	@CrossOrigin
	@RequestMapping(path = "/entity/rechnungsGruppierung/update/{id}", produces = "application/json", method = RequestMethod.PUT)
	public String update(@PathVariable(name = "id") long id, @RequestParam(name = "versenderIK") String versenderIK,
			@RequestParam(name = "datenPrüfungsIK") String datenPrüfungsIK,
			@RequestParam(name = "datenArt") String datenArt,
			@RequestParam(name = "maxRechnungsAnzahl") int maxRechnungsAnzahl) {

		try {
			service.update(id, new IK(Integer.parseInt(versenderIK)), new IK(Integer.parseInt(datenPrüfungsIK)),
					DatenArt.valueOf(datenArt), maxRechnungsAnzahl);
			return DER_RECHNUNGS_GRUPPIERUNG_DATENSATZES_MIT_DER + id + " wurden erfolgreich geändert";
		} catch (Exception e) {
			return "Beim Ändern des RechnungsGruppierung Datensatzes mit der " + id + " trat ein Fehler auf";
		}
	}

	@CrossOrigin
	@RequestMapping(path = "/entity/rechnungsGruppierung/delete/{id}", produces = "application/json", method = RequestMethod.DELETE)
	public String delet(@PathVariable(name = "id") long id) {

		try {
			service.delete(id);
			return DER_RECHNUNGS_GRUPPIERUNG_DATENSATZES_MIT_DER + id + " wurden erfolgreich entfernt";
		} catch (Exception e) {
			return "Beim Entfernen des RechnungsGruppierung Datensatzes mit der " + id + " trat ein Fehler auf";
		}
	}

	@CrossOrigin
	@RequestMapping(path = "/entity/rechnungsGruppierung/get/{id}", produces = "application/json", method = RequestMethod.GET)
	public RechnungsGruppierung get(@PathVariable(name = "id") long id) {

		try {
			Optional<RechnungsGruppierung> o = service.get(id);
			if (o.isPresent()) {
				return o.get();
			}
		} catch (Exception e) {
			throw new DatenlieferungException("Exception bei get ", e);
		}
		return null;
	}

}
