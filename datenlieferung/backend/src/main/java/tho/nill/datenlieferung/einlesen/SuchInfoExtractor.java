package tho.nill.datenlieferung.einlesen;

import java.util.List;

public interface SuchInfoExtractor {
	List<SuchInfo> extractSuchInfos(String text);
}
