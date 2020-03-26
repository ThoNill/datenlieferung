package tho.nill.datenlieferung.einlesen;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultSuchInfoExtractor implements SuchInfoExtractor {
	Pattern pattern = Pattern.compile(
			"[eE]([a-zA-Z]{3})([0-9]+) ([0-9]+) ([0-9]{4})([0-9]{2})([0-9]{2}):([0-9]{2})([0-9]{2})([0-9]{2})",
			Pattern.CASE_INSENSITIVE);

	@Override
	public List<SuchInfo> extractSuchInfos(String text) {
		List<SuchInfo> infos = new ArrayList<SuchInfo>();
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {

			String dateiname = "E" + extractGroup(text, matcher, 1) + extractGroup(text, matcher, 2);
			long größe = Long.parseLong(extractGroup(text, matcher, 3));
			int jahr = Integer.parseInt(extractGroup(text, matcher, 4));
			int monat = Integer.parseInt(extractGroup(text, matcher, 5));
			int tag = Integer.parseInt(extractGroup(text, matcher, 6));
			int stunde = Integer.parseInt(extractGroup(text, matcher, 7));
			int minute = Integer.parseInt(extractGroup(text, matcher, 8));
			int sekunde = Integer.parseInt(extractGroup(text, matcher, 9));
			LocalDateTime erstellt = LocalDateTime.of(jahr, monat, tag, stunde, minute, sekunde);
			log.debug("dateiname= " + dateiname);
			log.debug("größe " + größe);
			log.debug("erstellt= " + erstellt);
			SuchInfo info = new SuchInfo();
			info.setDateiname(dateiname);
			info.setGröße(größe);
			info.setErstellt(erstellt);
			infos.add(info);
		}
		return infos;
	}

	private String extractGroup(String text, Matcher matcher, int i) {
		return text.substring(matcher.start(i), matcher.end(i));
	}

	private String extractNumber(String text, Matcher matcher, int i) {
		String gtext = extractGroup(text, matcher, i);
		return gtext.substring(firstPositionNot0(gtext));
	}

	private int firstPositionNot0(String text) {
		int length = text.length();
		int pos = 0;
		while (pos < length) {
			if (text.charAt(pos) == '0') {
				pos++;
			} else {
				return pos;
			}
		}
		return 0;
	}

}
