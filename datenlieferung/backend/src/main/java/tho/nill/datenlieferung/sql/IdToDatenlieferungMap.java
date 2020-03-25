package tho.nill.datenlieferung.sql;

import java.util.function.Function;

import entities.Datenlieferung;
import repositories.DatenlieferungRepository;

public class IdToDatenlieferungMap implements Function<Object[], Datenlieferung> {

	private DatenlieferungRepository datenlieferungenRepo;
	int index = 0;

	public IdToDatenlieferungMap(DatenlieferungRepository datenlieferungenRepo, int index) {
		super();
		this.datenlieferungenRepo = datenlieferungenRepo;
		this.index = index;
	}

	@Override
	public Datenlieferung apply(Object[] t) {
		return datenlieferungenRepo.getOne((Long) t[index]);
	}
}
