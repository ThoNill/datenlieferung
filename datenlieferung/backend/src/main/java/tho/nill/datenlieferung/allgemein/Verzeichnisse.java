package tho.nill.datenlieferung.allgemein;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import entities.Datenlieferung;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Verzeichnisse {
	private String datenPathOriginal = "target/original";
	private String datenPathVerschl = "target/verschl";

	public Verzeichnisse(String datenPathOriginal, String datenPathVerschl) {
		super();
		this.datenPathOriginal = datenPathOriginal;
		this.datenPathVerschl = datenPathVerschl;
		log.debug("datenPathOriginal " + datenPathOriginal);
		log.debug("datenPathVerschl " + datenPathVerschl);
	}

	public File getOrginalFile(Datenlieferung d) {
		return createDirectorys(Dateien.createFile(datenPathOriginal + File.separatorChar + d.getDatenlieferungId()));
	}

	public File getVerschlFile(Datenlieferung d) {

		return createDirectorys(Dateien.createFile(getDataVerz(d) + d.getPhysDateiname()));
	}

	public File getAuftragsFile(Datenlieferung d) {
		return createDirectorys(Dateien.createFile(getDataVerz(d) + d.getPhysDateiname() + ".AUF"));
	}

	private String getDataVerz(Datenlieferung d) {
		return datenPathVerschl + File.separatorChar + d.getCdnummer() + File.separatorChar + "data"
				+ File.separatorChar;
	}

	private String getInfoVerz(int cdnummer) {
		return datenPathVerschl + File.separatorChar + cdnummer + File.separatorChar + "info" + File.separatorChar;
	}

	public File getBegleitzettelFile(Datenlieferung d) {
		return createDirectorys(Dateien.createFile(getInfoVerz(d.getCdnummer()) + "Begleit" + d.getDatenlieferungId()));
	}

	public File getCDBriefFile(Datenlieferung d) {
		return createDirectorys(Dateien.createFile(getInfoVerz(d.getCdnummer()) + "CDBrief.txt"));
	}

	public File getBrennerFile(int cdnummer) {
		return createDirectorys(Dateien.createFile(getInfoVerz(cdnummer) + "Brenner.txt"));
	}

	public static File createDirectorys(File f) {

		try {
			Files.createDirectories(f.toPath().getParent());
		} catch (IOException e) {
			log.error("Exception in Verzeichnisse.createDirectory", e);
		}
		return f;
	}

	public static String getTestVerz(Object source, String verz) {
		String testVerzName = "target" + "/" + "daten" + "/" + source.getClass().getSimpleName() + "/" + verz;
		try {
			Files.createDirectories(new File(testVerzName).toPath());
		} catch (IOException e) {
			log.error("Exception in Verzeichnisse.createDirectory", e);
		}
		return testVerzName;
	}

	public static String getTestDatei(Object source, String verz, String name) {
		return getTestVerz(source, verz) + "/" + name;
	}

}