package tho.nill.datenlieferung;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import tho.nill.datenlieferung.allgemein.DummyVoid;

@RestController
@CrossOrigin(origins = "https://localhost:4200", maxAge = 3600)
@RequestMapping("/api")
public class ZertifikateVonDerItsgEinlesenController {

	@Autowired
	private ZertifikateVonDerItsgEinlesenService service;

	public ZertifikateVonDerItsgEinlesenController() {
		super();
	}

	@CrossOrigin
	@RequestMapping(path = "/action/itsg/einlesen", produces = "application/json", method = RequestMethod.GET)
	public String generateTestdaten() {

		try {
			service.service(DummyVoid.VOID);
		} catch (Exception e) {
			return "Beim Einlesen der Daten von der Itsg trat ein Fehler auf";
		}

		return "Zertifikatsdatei von der Itsg wurde erfolgreich eingelesen";

	}
}
