package tho.nill.datenlieferung.simpleAttributes;

import org.nill.vorlagen.compiler.ConverterVerzeichnis;

public class Converters extends  ConverterVerzeichnis{

	public Converters() {
		put(MonatJahr.class, MonatJahrAdapter.class);
		put(IK.class, IKAdapter.class);
	}
}
