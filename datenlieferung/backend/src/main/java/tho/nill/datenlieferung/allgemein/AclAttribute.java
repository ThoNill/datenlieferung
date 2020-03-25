package tho.nill.datenlieferung.allgemein;

import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileAttribute;
import java.util.List;

public class AclAttribute implements FileAttribute<List<AclEntry>> {
	List<AclEntry> list;

	@Override
	public String name() {
		return "acl:acl";
	}

	public AclAttribute(List<AclEntry> list) {
		super();
		this.list = list;
	}

	@Override
	public List<AclEntry> value() {
		return list;
	}

}
