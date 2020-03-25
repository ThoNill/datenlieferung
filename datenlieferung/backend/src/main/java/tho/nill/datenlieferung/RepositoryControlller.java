package tho.nill.datenlieferung;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import entities.Datenaustausch;
import entities.EingangText;
import entities.RechnungsGruppierung;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "https://localhost:4200", maxAge = 3600)
@RequestMapping("/api/daten")
public class RepositoryControlller {

	private static final String EXCEPTION_IN = "Exception in ";
	@Autowired
	private RepositoryService service;

	public RepositoryControlller() {
		super();
	}

	@CrossOrigin
	@RequestMapping(path = "/datenlieferung", produces = "application/json", method = RequestMethod.GET)
	public List<Object> getDatenlieferungListe() {

		try {
			return service.getDatenlieferungListe();
		} catch (Exception e) {
			log.error(EXCEPTION_IN + this.getClass().getCanonicalName() + ".getDatenlieferungListe", e);
			return Collections.emptyList();
		}

	}

	@CrossOrigin
	@RequestMapping(path = "/datenaustausch", produces = "application/json", method = RequestMethod.GET)
	public List<Datenaustausch> getDatenAustauschListe() {

		try {
			return service.getDatenAustauschListe();
		} catch (Exception e) {
			log.error(EXCEPTION_IN + this.getClass().getCanonicalName() + ".getDatenaustauschListe", e);
			return Collections.emptyList();
		}

	}

	@CrossOrigin
	@RequestMapping(path = "/rechnungauftrag", produces = "application/json", method = RequestMethod.GET)
	public List<Object> getRechnungAuftragListe() {

		try {
			return service.getRechnungAuftragListe();
		} catch (Exception e) {
			log.error(EXCEPTION_IN + this.getClass().getCanonicalName() + ".getRechnungAuftragListe", e);
			return Collections.emptyList();
		}

	}

	@CrossOrigin
	@RequestMapping(path = "/rechnungsGruppierung", produces = "application/json", method = RequestMethod.GET)
	public List<RechnungsGruppierung> getRechnungsGruppierungListe() {

		try {
			return service.getRechnungsGruppierungListe();
		} catch (Exception e) {
			log.error(EXCEPTION_IN + this.getClass().getCanonicalName() + ".getRechnungsGruppierungListe", e);
			return Collections.emptyList();
		}

	}

	@CrossOrigin
	@RequestMapping(path = "/eingangtext", produces = "application/json", method = RequestMethod.GET)
	public List<EingangText> getEingangTextListe() {

		try {
			return service.getEingangTextListe();
		} catch (Exception e) {
			log.error(EXCEPTION_IN + this.getClass().getCanonicalName() + ".getRechnungsGruppierungListe", e);
			return Collections.emptyList();
		}

	}

}
