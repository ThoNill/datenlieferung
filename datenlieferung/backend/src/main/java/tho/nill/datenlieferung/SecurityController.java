package tho.nill.datenlieferung;

import java.security.Principal;

import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "https://localhost:4200", maxAge = 3600)
public class SecurityController {

	public SecurityController() {
		super();
	}

	@RequestMapping(path = "/api/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean login(@RequestBody User user) {
		log.debug("Anmelden von User " + user.getUserName());
		boolean ok = user.getUserName().equals("user") && user.getPassword().equals("password");
		log.debug("Ok? " + ok);
		return ok;
	}

	@CrossOrigin
	@RequestMapping(path = "/api/logout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean logout() {
		log.debug("Abmelden ");
		return false;
	}

	@CrossOrigin
	@RequestMapping(path = "/api/usera", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public Principal user(Principal principal) {
		log.debug("Authorization1 " + principal.getName());
		return principal;
	}

	@CrossOrigin
	@RequestMapping(path = "/api/userb", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String user2() {
		log.debug("Authorization2 ");
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		log.debug("Prinzipal " + principal);
		if (principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername();
		} else {
			return principal.toString();
		}
	}
}
