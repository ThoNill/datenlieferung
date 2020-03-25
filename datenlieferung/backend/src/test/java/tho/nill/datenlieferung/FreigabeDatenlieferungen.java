package tho.nill.datenlieferung;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import entities.EingeleseneDatei;
import entities.RechnungAuftrag;
import repositories.DatenlieferungProtokollRepository;
import repositories.DatenlieferungRepository;
import repositories.EingeleseneDateiRepository;
import repositories.RechnungAuftragRepository;

public class FreigabeDatenlieferungen {
	private DatenlieferungRepository datenlieferungRepo;
	private DatenlieferungProtokollRepository datenlieferungProtokollRepo;
	private EingeleseneDateiRepository eingeleseneDateiRepo;
	private RechnungAuftragRepository rechnungAuftragRepository;
	private PlatformTransactionManager transactionManager;

	public FreigabeDatenlieferungen(PlatformTransactionManager transactionManager,
			DatenlieferungRepository datenlieferungRepo, DatenlieferungProtokollRepository datenlieferungProtokollRepo,
			EingeleseneDateiRepository eingeleseneDateiRepo, RechnungAuftragRepository rechnungAuftragRepository) {
		super();
		this.transactionManager = transactionManager;
		this.datenlieferungRepo = datenlieferungRepo;
		this.datenlieferungProtokollRepo = datenlieferungProtokollRepo;
		this.eingeleseneDateiRepo = eingeleseneDateiRepo;
		this.rechnungAuftragRepository = rechnungAuftragRepository;
	}

	public void freigeben() {
		TransactionTemplate template = new TransactionTemplate(transactionManager);
		template.execute(new TransactionCallbackWithoutResult() {
			// the code in this method executes in a transactional context
			@Override
			public void doInTransactionWithoutResult(TransactionStatus status) {
				freigebenIntern();
			}
		});

	}

	private void freigebenIntern() {
		List<Datenlieferung> list = datenlieferungRepo.findAll();
		ArrayList<Datenlieferung> zuVerarbeiten = new ArrayList<>(list);
		for (Datenlieferung d : zuVerarbeiten) {
			ArrayList<DatenlieferungProtokoll> protokolle = new ArrayList<>(d.getDatenlieferungProtokoll());
			for (DatenlieferungProtokoll p : protokolle) {
				d.removeDatenlieferungProtokoll(p);
				datenlieferungRepo.saveAndFlush(d);
				datenlieferungProtokollRepo.saveAndFlush(p);
			}
			ArrayList<EingeleseneDatei> dateien = new ArrayList<>(d.getEingeleseneDatei());
			for (EingeleseneDatei e : dateien) {
				d.removeEingeleseneDatei(e);
				datenlieferungRepo.saveAndFlush(d);
				eingeleseneDateiRepo.saveAndFlush(e);
			}
			ArrayList<RechnungAuftrag> aufträge = new ArrayList<>(d.getRechnungAuftrag());
			for (RechnungAuftrag a : aufträge) {
				d.removeRechnungAuftrag(a);
				datenlieferungRepo.saveAndFlush(d);
				rechnungAuftragRepository.saveAndFlush(a);
			}

		}
	}
}
