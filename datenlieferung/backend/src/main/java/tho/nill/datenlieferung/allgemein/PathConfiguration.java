package tho.nill.datenlieferung.allgemein;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathConfiguration {

	@Value("${daten.path.original}")
	public String dataPathOriginal;

	@Value("${daten.path.verschl}")
	public String dataPathVerschl;

}
