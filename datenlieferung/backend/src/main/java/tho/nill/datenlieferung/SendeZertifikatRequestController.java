package tho.nill.datenlieferung;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tho.nill.datenlieferung.simpleAttributes.IK;

@RestController
@CrossOrigin(origins = "https://localhost:4200", maxAge = 3600)
@RequestMapping("/api")
public class SendeZertifikatRequestController {

	@Autowired
	private SendeZertifikatRequestService service;

	public SendeZertifikatRequestController() {
		super();
	}

	@CrossOrigin
	@RequestMapping(path = "/action/itsg/request", produces = "application/json", method = RequestMethod.POST)
	public String generateTestdaten(@RequestParam(name = "ik") int ik, @RequestParam(name = "name") String name) {

		try {
			service.service(new ZertifikatRequestDaten(new IK(ik), name));
		} catch (Exception e) {
			return "Beim Erstellen des Requests trat ein Fehler auf";
		}

		return "Neuer Zertifikats Request wurde erstellt und versendet";
	}
}
