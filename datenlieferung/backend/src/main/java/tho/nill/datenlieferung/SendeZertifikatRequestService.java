package tho.nill.datenlieferung;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import entities.VersenderKey;
import lombok.extern.slf4j.Slf4j;
import repositories.VersenderKeyRepository;
import tho.nill.datenlieferung.allgemein.Dateien;
import tho.nill.datenlieferung.senden.email.EMailConfiguration;
import tho.nill.datenlieferung.zertifikate.PrivPubEntities;
import tho.nill.datenlieferung.zertifikate.PrivPubKeys;

@Slf4j
@Service
public class SendeZertifikatRequestService extends BasisService<ZertifikatRequestDaten> {
	@Autowired
	private TemplateEngine templateEngine;

	private VersenderKeyRepository keyRepo;

	@Autowired
	private EMailConfiguration config;

	public SendeZertifikatRequestService(PlatformTransactionManager transactionManager, VersenderKeyRepository keyRepo,
			EMailConfiguration config) {
		super(transactionManager);
		this.keyRepo = keyRepo;
		this.config = config;
	}

	@Override
	public void performService(ZertifikatRequestDaten d) {
		try {
			String distinguishName = "CN=" + d.getName() + ",OU=IK" + d.getIk().toString()
					+ ",OU=Trust Center,O=ITSG TrustCenter fuer sonstige Leistungserbringer,C=DE";

			KeyPair keyPair = new PrivPubKeys().generateKeyPair();
			PrivPubEntities pp = new PrivPubEntities(keyRepo);
			VersenderKey k = pp.saveKeyPair(keyPair, d.getIk(), distinguishName);
			File requestFile = Dateien.createFile("target/" + d.getIk().toString() + ".p10");
			pp.saveCertificationRequest(k, requestFile);
			senden(k, requestFile);
		} catch (IOException | MessagingException | NoSuchAlgorithmException | OperatorCreationException e) {
			log.error("Exception in " + this.getClass().getCanonicalName() + ".performService", e);
		}
	}

	public void senden(VersenderKey key, File requestfile) throws MessagingException {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(config.HOST);
		sender.setUsername(config.BENUTZERNAME);
		sender.setPassword(config.PASSWORT);
		sender.setPort(config.PORT);

		String reduziertesIK = key.getVersenderIK().toString().substring(0, 7);
		MimeMessage message = sender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setTo("crq@itsg-trust.de");
		helper.setFrom(config.FROM);
		helper.setSentDate(new Date());
		helper.setSubject("Certification Request " + reduziertesIK + ".p10");

		final Context context = new Context();
		context.setVariable("ik", reduziertesIK);
		String body = templateEngine.process("emailRequest.txt", context);

		helper.setText(body);

		FileSystemResource fileRequest = new FileSystemResource(requestfile);
		helper.addAttachment(reduziertesIK + ".p10", fileRequest);

		sender.send(message);
	}

}
