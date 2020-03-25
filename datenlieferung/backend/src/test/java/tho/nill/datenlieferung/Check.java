package tho.nill.datenlieferung;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.springframework.data.jpa.repository.JpaRepository;

public class Check {

	public static void fileExists(File file, long lenght) {
		assertTrue("Die Datei " + file + " existiert nicht oder ist zu klein", file.exists() && file.length() > lenght);
	}

	public static void fileExists(File file) {
		fileExists(file, 30);
	}

	public static void clearDirectory(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				for (String name : file.list()) {
					File f = new File(file.getAbsolutePath() + File.separatorChar + name);
					if (f.exists()) {
						f.delete();
					}
				}
			} else {
				file.delete();
			}
		}
	}

	public static void clearDirectory(String fileName) {
		clearDirectory(new File(fileName));
	}

	public static void clearDb(JpaRepository<?, ?>... repos) {
		for (JpaRepository<?, ?> r : repos) {
			if (r.count() > 0) {
				r.deleteAllInBatch();
			}
		}
	}

}
