package tho.nill.datenlieferung;

import static org.junit.Assert.assertEquals;

import java.util.function.Consumer;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import entities.Datenlieferung;
import lombok.extern.slf4j.Slf4j;
import repositories.DatenlieferungProtokollRepository;
import repositories.DatenlieferungRepository;
import repositories.RechnungAuftragRepository;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.sql.IdToDatenlieferungMap;
import tho.nill.datenlieferung.sql.SqlQueryFlux;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SqlQueryFluxTest implements Consumer {

	@Autowired
	public DatenlieferungRepository datenlieferungenRepo;

	@Autowired
	public DatenlieferungProtokollRepository datenlieferungenProtokollRepo;

	@Autowired
	public RechnungAuftragRepository rechnungAuftragRepo;

	@Autowired
	DataSource dataSource;

	private int count;

	public SqlQueryFluxTest() {
		super();
	}

	public void init() {
		System.gc();
		Check.clearDb(rechnungAuftragRepo, datenlieferungenProtokollRepo, datenlieferungenRepo);

		count = 0;
	}

	@Test
	public void query() {
		init();
		for (int i = 0; i < 10; i++) {
			erzeugeDatenlieferung(i);
		}
		SqlQueryFlux.using(dataSource, "select DatenlieferungId from datenlieferung ").subscribe(this);
		assertEquals(10, count);
	}

	@Test
	public void queryAndMap() {
		init();
		for (int i = 0; i < 10; i++) {
			erzeugeDatenlieferung(i);
		}
		SqlQueryFlux.using(dataSource, "select DatenlieferungId from datenlieferung ")
				.map(new IdToDatenlieferungMap(datenlieferungenRepo, 0)).subscribe(this);
		assertEquals(10, count);
	}

	@Test
	public void queryAndMapAktion0() {
		init();
		for (int i = 0; i < 10; i++) {
			erzeugeDatenlieferung(i);
		}
		SqlQueryFlux.using(dataSource, "select DatenlieferungId from datenlieferung where letzteAktion = 0")
				.map(new IdToDatenlieferungMap(datenlieferungenRepo, 0)).subscribe(this);
		assertEquals(0, count);
	}

	@Test
	public void queryAndMapAktionErstellt() {
		init();
		for (int i = 0; i < 10; i++) {
			erzeugeDatenlieferung(i);
		}
		SqlQueryFlux
				.using(dataSource,
						"select DatenlieferungId from datenlieferung where letzteAktion = "
								+ AktionsArt.ERSTELLT.ordinal())
				.map(new IdToDatenlieferungMap(datenlieferungenRepo, 0)).subscribe(this);
		assertEquals(10, count);
	}

	private void erzeugeDatenlieferung(int i) {
		Datenlieferung d = new Datenlieferung();
		d.setVersenderIK(new IK(100000000 + i));
		d.setLetzteAktion(AktionsArt.ERSTELLT);
		datenlieferungenRepo.saveAndFlush(d);
	}

	@Override
	public void accept(Object t) {
		count++;
		if (t instanceof Datenlieferung) {
			log.info("Datenlieferung: " + ((Datenlieferung) t).getDatenlieferungId());
		}
		if (t instanceof Object[]) {
			log.info("DatenlieferungsId: " + ((Object[]) t)[0]);
		}
	}
}
