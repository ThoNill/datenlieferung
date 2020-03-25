package tho.nill.datenlieferung;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RootController {
	@RequestMapping("/")
	public void handleRequest() {
		throw new RuntimeException("test exception");
	}

}
