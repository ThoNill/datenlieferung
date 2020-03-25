package tho.nill.datenlieferung;

import java.util.List;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import entities.Datenlieferung;
import lombok.extern.slf4j.Slf4j;
import repositories.DateiNummerRepository;
import repositories.DatenlieferungRepository;
import repositories.RechnungAuftragRepository;
import tho.nill.datenlieferung.anlegen.Nähmaschine;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;
import tho.nill.datenlieferung.sql.SqlQueryFlux;

@Slf4j
@Service
public class DatenlieferungErstellenService extends BasisService<MonatJahr> implements Consumer<List<Datenlieferung>> {
	@Autowired
	DataSource dataSource;

	@Autowired
	public DateiNummerRepository dateiNummerRepo;

	@Autowired
	public DatenlieferungRepository datenlieferungenRepo;

	@Autowired
	public RechnungAuftragRepository rechnungsAuftragRepo;

	public DatenlieferungErstellenService(PlatformTransactionManager transactionManager) {
		super(transactionManager);
	}

	@Override
	public void performService(MonatJahr d) {
		try {
			Nähmaschine n = new Nähmaschine(datenlieferungenRepo, rechnungsAuftragRepo, dateiNummerRepo);
			SqlQueryFlux.using(dataSource,
					"select rechnungAuftragId, versenderIK,datenart,DatenAnnahmeIK,DatenPrüfungsIK,mj from rechnungauftrag where datenlieferung_id is null order by versenderIK,datenart,DatenAnnahmeIK,DatenPrüfungsIK, mj ")
					.map(n).subscribe(this);

		} catch (Exception e) {
			log.error("Exception in " + this.getClass().getCanonicalName() + "performService", e);
		}
	}

	@Override
	public void accept(List<Datenlieferung> datenlieferung) {
		// TODO Auto-generated method stub

	}

}
