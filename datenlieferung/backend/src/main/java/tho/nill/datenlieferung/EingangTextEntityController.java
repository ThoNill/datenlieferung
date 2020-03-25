package tho.nill.datenlieferung;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import entities.EingangText;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;
import tho.nill.datenlieferung.simpleAttributes.Bewertung;

@RestController
@CrossOrigin(origins = "https://localhost:4200", maxAge = 3600)
@RequestMapping("/api")
public class EingangTextEntityController {

	private static final String DER_EINGANTEXT_DATENSATZES_MIT_DER = "Der EingangText Datensatzes mit der ";
	@Autowired
	private EingangTextEntityService service;

	public EingangTextEntityController() {
		super();
	}

	@CrossOrigin
	@RequestMapping(path = "/entity/eingangtext/insert", produces = "application/json", method = RequestMethod.POST)
	public String create(@RequestParam(name = "regexp") String regexp,
			@RequestParam(name = "bewertung") String bewertung) {

		try {
			EingangText d = service.create(regexp, Bewertung.valueOf(bewertung));
			return DER_EINGANTEXT_DATENSATZES_MIT_DER + d.getEingangTextId() + " wurden erfolgreich angelegt";
		} catch (Exception e) {
			return "Beim Anlegen eines EingangText Datensatzes trat ein Fehler auf";
		}
	}

	@CrossOrigin
	@RequestMapping(path = "/entity/eingangtext/update/{id}", produces = "application/json", method = RequestMethod.PUT)
	public String update(@PathVariable(name = "id") long id, @RequestParam(name = "regexp") String regexp,
			@RequestParam(name = "bewertung") String bewertung) {

		try {
			service.update(id, regexp, Bewertung.valueOf(bewertung));
			return DER_EINGANTEXT_DATENSATZES_MIT_DER + id + " wurden erfolgreich geändert";
		} catch (Exception e) {
			return "Beim Ändern des EingangText Datensatzes mit der " + id + " trat ein Fehler auf";
		}
	}

	@CrossOrigin
	@RequestMapping(path = "/entity/eingangtext/delete/{id}", produces = "application/json", method = RequestMethod.DELETE)
	public String delete(@PathVariable(name = "id") long id) {

		try {
			service.delete(id);
			return DER_EINGANTEXT_DATENSATZES_MIT_DER + id + " wurden erfolgreich entfernt";
		} catch (Exception e) {
			return "Beim Entfernen des EingangText Datensatzes mit der " + id + " trat ein Fehler auf";
		}
	}

	@CrossOrigin
	@RequestMapping(path = "/entity/eingangtext/get/{id}", produces = "application/json", method = RequestMethod.GET)
	public EingangText get(@PathVariable(name = "id") long id) {

		try {
			Optional<EingangText> o = service.get(id);
			if (o.isPresent()) {
				return o.get();
			}
		} catch (Exception e) {
			throw new DatenlieferungException("Exception bei get ", e);
		}
		return null;
	}

}
