package tho.nill.datenlieferung;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import entities.DatenlieferungProtokoll;
import entities.EingeleseneDatei;
import repositories.DatenaustauschRepository;
import repositories.DatenlieferungProtokollRepository;
import repositories.DatenlieferungRepository;
import repositories.EingangTextRepository;
import repositories.EingeleseneDateiRepository;
import tho.nill.datenlieferung.allgemein.DummyVoid;
import tho.nill.datenlieferung.einlesen.BestätigeEingang;
import tho.nill.datenlieferung.einlesen.email.EMailEmpfänger;
import tho.nill.datenlieferung.einlesen.email.Pop3Configuration;
import tho.nill.datenlieferung.einlesen.sftp.SftpEmpfänger;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;

@Service
public class RückmeldungenService extends BasisService<DummyVoid> {

	private DatenaustauschRepository datenaustauschRepo;

	@Autowired
	public DatenlieferungProtokollRepository protokollRepo;

	@Autowired
	public EingeleseneDateiRepository eingeleseneDateiRepo;

	@Autowired
	public EingangTextRepository eingangTextRepo;

	@Autowired
	public Pop3Configuration configMail;

	@Autowired
	public DataSource dataSource;

	@Autowired
	private DatenlieferungRepository datenlieferungRepo;

	@Autowired
	private Protokollführer protokoll;

	public RückmeldungenService(PlatformTransactionManager transactionManager,
			DatenaustauschRepository datenaustauschRep) {
		super(transactionManager);
		this.datenaustauschRepo = datenaustauschRep;
	}

	@Override
	public void performService(DummyVoid d) {
		SftpEmpfänger sftpEmpfänger = new SftpEmpfänger(eingeleseneDateiRepo, datenaustauschRepo);
		EMailEmpfänger emailEmpfänger = new EMailEmpfänger(configMail, eingeleseneDateiRepo);

		Optional<DatenlieferungProtokoll> protokollSftp = sftpEmpfänger.action();
		Optional<DatenlieferungProtokoll> protokollEMail = emailEmpfänger.action();
		if (protokollEMail.isEmpty() && protokollSftp.isEmpty()) {
			BestätigeEingang bestätigeEingang = new BestätigeEingang("", "", eingeleseneDateiRepo, datenlieferungRepo,
					eingangTextRepo, protokoll);
			bestätigeEingang.init();
			List<Long> nichtBestätigt = eingeleseneDateiRepo.getNichtBestätigteDateien();
			ArrayList<Long> liste = new ArrayList<>(nichtBestätigt);
			for (Long id : liste) {
				EingeleseneDatei datei = eingeleseneDateiRepo.getOne(id);
				bestätigeEingang.action(datei);
			}
		} else {
			if (protokollEMail.isPresent()) {
				protokollRepo.saveAndFlush(protokollEMail.get());
				throw new DatenlieferungException(protokollEMail.get().getMeldung());
			}
			if (protokollSftp.isPresent()) {
				protokollRepo.saveAndFlush(protokollSftp.get());
				throw new DatenlieferungException(protokollSftp.get().getMeldung());
			}
		}

	}

}
