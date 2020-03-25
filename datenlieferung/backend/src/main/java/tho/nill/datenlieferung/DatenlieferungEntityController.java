package tho.nill.datenlieferung;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import tho.nill.datenlieferung.exceptions.DatenlieferungException;

@RestController
@CrossOrigin(origins = "https://localhost:4200", maxAge = 3600)
@RequestMapping("/api")
public class DatenlieferungEntityController {

	@Autowired
	private DatenlieferungEntityService service;

	public DatenlieferungEntityController() {
		super();
	}

	@CrossOrigin
	@RequestMapping(path = "/entity/datenlieferung/get/{id}", produces = "application/json", method = RequestMethod.GET)
	public Object get(@PathVariable(name = "id") long id) {

		try {
			Optional<Object> o = service.get(id);
			if (o.isPresent()) {
				return o.get();
			}
		} catch (Exception e) {
			throw new DatenlieferungException("Exception bei get ", e);
		}
		return null;
	}

}
