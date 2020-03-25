package tho.nill.datenlieferung;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public abstract class BasisService<DATEN> {

	private PlatformTransactionManager transactionManager;

	public BasisService(PlatformTransactionManager transactionManager) {
		super();
		this.transactionManager = transactionManager;
	}

	public abstract void performService(DATEN d);

	public void service(DATEN d) {
		TransactionTemplate template = new TransactionTemplate(transactionManager);
		template.execute(new TransactionCallbackWithoutResult() {
			// the code in this method executes in a transactional context
			@Override
			public void doInTransactionWithoutResult(TransactionStatus status) {
				performService(d);
			}
		});

	}

}
