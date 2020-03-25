package tho.nill.datenlieferung;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "https://localhost:4200", maxAge = 3600)
@RequestMapping("/api")
public class KorrekturErstellenController {

	@Autowired
	private KorrekturErstellenService service;

	public KorrekturErstellenController() {
		super();
	}

	@CrossOrigin
	@RequestMapping(path = "/action/datenlieferung/korrektur", produces = "application/json", method = RequestMethod.POST)
	public String generateTestdaten(@RequestParam(name = "id") long id) {

		try {
			service.service(Long.valueOf(id));
		} catch (Exception e) {
			return "Beim Abarbeiten der Korrektur trat ein Fehler auf";
		}

		return "Die Korrektur wurden erfolgreich erstellt";
	}
}
