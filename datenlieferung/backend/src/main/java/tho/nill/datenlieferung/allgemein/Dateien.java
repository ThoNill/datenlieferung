package tho.nill.datenlieferung.allgemein;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import tho.nill.datenlieferung.exceptions.DatenlieferungException;

public class Dateien {
	private Dateien() {
		super();
	}

	public static Path createPath(String dateiname) {
		return FileSystems.getDefault().getPath(dateiname);
	}

	public static FileInputStream createInputStream(String dateiname) throws FileNotFoundException {
		return new FileInputStream(createFile(dateiname));
	}

	public static FileOutputStream createOutputStream(String dateiname) throws IOException {
		return createOutputStream(newFile(dateiname));
	}

	public static FileOutputStream createOutputStream(File datei) throws FileNotFoundException {
		return new FileOutputStream(datei);
	}

	public static FileWriter createWriter(String dateiname) throws IOException {
		return new FileWriter(newFile(dateiname));
	}

	public static File createFile(String dateiname) {
		File f = new File(dateiname);
		if (f.exists()) {
			if (f.canRead()) {
				return f;
			}
		} else {
			try {
				return newFile(dateiname);
			} catch (IOException e) {
				throw new DatenlieferungException("Die Datei " + dateiname + " kann nicht erzeugt werden", e);
			}
		}
		throw new DatenlieferungException("Die Datei " + dateiname + " existiert kann aber nicht gelesen werden");
	}

	private static File newFile(String dateiname) throws IOException {
		Path p = createPath(dateiname);
		if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
			FileAttribute<Set<PosixFilePermission>> attrs = createPosixAttributes();
			Files.createDirectories(p.getParent(), attrs);
			return Files.createFile(p, attrs).toFile();
		} else {
			AclAttribute attrs = generateAclAttributes();
			Files.createDirectories(p.getParent(), attrs);
			return Files.createFile(p, attrs).toFile();
		}

	}

	private static FileAttribute<Set<PosixFilePermission>> createPosixAttributes() {
		Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-------");
		return PosixFilePermissions.asFileAttribute(perms);
	}

	private static AclAttribute generateAclAttributes() throws IOException {
		String benutzer = System.getProperty("user.name");
		UserPrincipal prinzipal = FileSystems.getDefault().getUserPrincipalLookupService()
				.lookupPrincipalByName(benutzer);
		AclEntry entry = AclEntry.newBuilder().setType(AclEntryType.ALLOW).setPrincipal(prinzipal)
				.setPermissions(AclEntryPermission.READ_ACL, AclEntryPermission.READ_DATA,
						AclEntryPermission.READ_ATTRIBUTES, AclEntryPermission.READ_NAMED_ATTRS,
						AclEntryPermission.DELETE, AclEntryPermission.DELETE_CHILD, AclEntryPermission.WRITE_DATA,
						AclEntryPermission.WRITE_ACL, AclEntryPermission.WRITE_ATTRIBUTES,
						AclEntryPermission.WRITE_NAMED_ATTRS, AclEntryPermission.WRITE_OWNER,
						AclEntryPermission.APPEND_DATA, AclEntryPermission.SYNCHRONIZE)
				.setFlags(AclEntryFlag.DIRECTORY_INHERIT, AclEntryFlag.FILE_INHERIT).build();
		List<AclEntry> acl = new ArrayList();
		acl.add(0, entry);
		return new AclAttribute(acl);

	}

}
