package tho.nill.datenlieferung.einlesen;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SuchInfo {
	private String dateiname;
	private long größe;
	private LocalDateTime erstellt;
}
