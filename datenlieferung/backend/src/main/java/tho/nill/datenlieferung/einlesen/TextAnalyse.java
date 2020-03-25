package tho.nill.datenlieferung.einlesen;

import java.util.List;

import entities.EingeleseneDatei;
import lombok.NonNull;

public interface TextAnalyse {
	List<Long> analysieren(@NonNull EingeleseneDatei datei);
}
