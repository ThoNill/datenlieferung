package tho.nill.datenlieferung;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;

@Slf4j
@RestController
@CrossOrigin(origins = "https://localhost:4200", maxAge = 3600)
@RequestMapping("/api")
public class DatenlieferungReiheController {

	@Autowired
	private DatenlieferungVersendenService serviceVersenden;

	@Autowired
	private DatenlieferungErstellenService serviceErstellen;

	public DatenlieferungReiheController() {
		super();
	}

	@CrossOrigin
	@RequestMapping(path = "/action/datenlieferung/reihe", produces = "application/json", method = RequestMethod.POST)
	public String generateTestdaten(@RequestParam(name = "monat") int monat, @RequestParam(name = "jahr") int jahr) {

		try {
			serviceErstellen.service(new MonatJahr(monat, jahr));
			serviceVersenden.service(new MonatJahr(monat, jahr));
		} catch (Exception e) {
			log.error("Exception in " + this.getClass().getCanonicalName() + ".generateTestdaten", e);
			return "Beim Abarbeiten der Datenlieferungen trat ein Fehler auf";
		}

		return "Die Datenlieferungen wurden erfolgreich erstellt";
	}
}
