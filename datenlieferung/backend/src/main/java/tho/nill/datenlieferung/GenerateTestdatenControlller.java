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
public class GenerateTestdatenControlller {

	@Autowired
	private GenerateTestdatenService service;

	public GenerateTestdatenControlller() {
		super();
	}

	@CrossOrigin
	@RequestMapping(path = "/testdaten/generate", produces = "application/json", method = RequestMethod.GET)
	public String generateTestdaten() {

		try {
			service.service(DummyVoid.VOID);
		} catch (Exception e) {
			return "Bei der Erzeugung von Testdaten, trat ein Fehler auf";
		}

		return "Testdaten wurden generiert";
	}
}
