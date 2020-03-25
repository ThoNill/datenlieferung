package tho.nill.datenlieferung;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import entities.Datenlieferung;
import entities.RechnungAuftrag;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import repositories.DateiNummerRepository;
import repositories.DatenlieferungRepository;
import repositories.RechnungAuftragRepository;
import tho.nill.datenlieferung.anlegen.Nähmaschine;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;
import tho.nill.datenlieferung.sql.SqlQueryFlux;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class NähmaschineTest implements Consumer<Datenlieferung> {

	@Autowired
	public PlatformTransactionManager transactionManager;

	@Autowired
	public DatenlieferungRepository datenlieferungenRepo;

	@Autowired
	public RechnungAuftragRepository rechnungsAuftragRepo;

	@Autowired
	public DateiNummerRepository dateiNummerRepo;

	@Autowired
	public DataSource dataSource;

	public int rechnr = 1;
	public DatenArt datenart = DatenArt.PAR300ABRP;
	public int datenannahmeIk = 111111111;
	public int prüfungsIk = 222222222;
	public int versenderIk = 333333333;

	public NähmaschineTest() {
		super();
	}

	@Before
	public void init() {
		Check.clearDb(dateiNummerRepo, rechnungsAuftragRepo, datenlieferungenRepo);
	}

	@Test
	public void nähen300Abrp() {
		rechnungsAufträgeGenerieren(DatenArt.PAR300ABRP);
		assertEquals(50, rechnungsAuftragRepo.count());
		datenlieferungenErzeugen(50);
		assertEquals(5, datenlieferungenRepo.count());
		datenlieferungenErzeugen(0);
		assertEquals(5, datenlieferungenRepo.count());
		rechnungsAufträgeGenerieren(DatenArt.PAR300ABRP);
		assertEquals(100, rechnungsAuftragRepo.count());
		datenlieferungenErzeugen(50);
		assertEquals(10, datenlieferungenRepo.count());
	}

	@Test
	public void nähen3000Daten() {
		rechnungsAufträgeGenerieren(DatenArt.PAR300DATEN);
		assertEquals(50, rechnungsAuftragRepo.count());
		datenlieferungenErzeugen(50);
		assertEquals(100, rechnungsAuftragRepo.count());
		assertEquals(10, datenlieferungenRepo.count());
		datenlieferungenErzeugen(0);
		assertEquals(10, datenlieferungenRepo.count());
		rechnungsAufträgeGenerieren(DatenArt.PAR300DATEN);
		assertEquals(150, rechnungsAuftragRepo.count());
		datenlieferungenErzeugen(50);
		assertEquals(20, datenlieferungenRepo.count());
		assertEquals(200, rechnungsAuftragRepo.count());
	}

	public void datenlieferungenErzeugen(int erwarteteAnzahl) {
		TransactionTemplate template = new TransactionTemplate(transactionManager);
		final NähmaschineTest test = this;
		template.execute(new TransactionCallbackWithoutResult() {
			// the code in this method executes in a transactional context
			@Override
			public void doInTransactionWithoutResult(TransactionStatus status) {
				Nähmaschine n = new Nähmaschine(datenlieferungenRepo, rechnungsAuftragRepo, dateiNummerRepo);
				SqlQueryFlux.using(dataSource,
						"select rechnungAuftragId, versenderIK,datenart,DatenAnnahmeIK,DatenPrüfungsIK,mj from rechnungauftrag where datenlieferung_id is null order by versenderIK,datenart,DatenAnnahmeIK,DatenPrüfungsIK, mj ")
						.map(n).filter(x -> !((List<Datenlieferung>) x).isEmpty())
						.flatMap(x -> Flux.fromIterable((List<Datenlieferung>) x)).subscribe(test);
				assertEquals(erwarteteAnzahl, n.getAnzahl());
			}
		});
	}

	public void rechnungsAufträgeGenerieren(DatenArt datenart) {
		this.datenart = datenart;
		TransactionTemplate template = new TransactionTemplate(transactionManager);
		template.execute(new TransactionCallbackWithoutResult() {
			// the code in this method executes in a transactional context
			@Override
			public void doInTransactionWithoutResult(TransactionStatus status) {
				datenannahmeIk = 111111111;
				prüfungsIk = 222222222;
				versenderIk = 333333333;
				rechnungsAufträgeGenerieren(10);
				setVersenderIk(333333334);
				rechnungsAufträgeGenerieren(10);
				setDatenannahmeIk(111111112);
				rechnungsAufträgeGenerieren(20);
				setPrüfungsIk(222222221);
				rechnungsAufträgeGenerieren(5);
				setPrüfungsIk(222222228);
				rechnungsAufträgeGenerieren(5);
			}
		});

	}

	private void rechnungsAufträgeGenerieren(int anzahl) {
		for (int i = 0; i < anzahl; i++) {
			auftragGenerieren();
		}
	}

	private void auftragGenerieren() {
		RechnungAuftrag a = new RechnungAuftrag();
		a.setRechnungsNummer(rechnr);
		rechnr++;
		a.setDatenArt(datenart);
		a.setDatenAnnahmeIK(new IK(datenannahmeIk));
		a.setDatenPrüfungsIK(new IK(prüfungsIk));
		a.setVersenderIK(new IK(versenderIk));
		a.setMj(new MonatJahr(1, 2019));
		rechnungsAuftragRepo.saveAndFlush(a);
	}

	@Override
	public void accept(Datenlieferung t) {
		log.info("Datenlieferung: " + t.getDatenlieferungId());

	}

	public void setDatenart(DatenArt datenart) {
		this.datenart = datenart;
	}

	public void setDatenannahmeIk(int datenannahmeIk) {
		this.datenannahmeIk = datenannahmeIk;
	}

	public void setPrüfungsIk(int prüfungsIk) {
		this.prüfungsIk = prüfungsIk;
	}

	public void setVersenderIk(int versenderIk) {
		this.versenderIk = versenderIk;
	}

}
