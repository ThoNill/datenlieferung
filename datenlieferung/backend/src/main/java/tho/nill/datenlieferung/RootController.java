package tho.nill.datenlieferung;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import tho.nill.datenlieferung.exceptions.DatenlieferungException;

@Controller
public class RootController {
	@RequestMapping("/")
	public void handleRequest() {
		throw new DatenlieferungException("test exception");
	}

}
