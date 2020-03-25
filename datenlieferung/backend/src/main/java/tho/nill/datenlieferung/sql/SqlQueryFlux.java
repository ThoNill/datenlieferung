package tho.nill.datenlieferung.sql;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import reactor.core.publisher.Flux;

public class SqlQueryFlux implements Function<ResultSetEnvelope, Flux<Object[]>>, Callable<ResultSetEnvelope>,
		Consumer<ResultSetEnvelope> {
	private static Logger logger = Logger.getLogger(SqlQueryFlux.class);
	private DataSource dataSource;

	private String stmtString;

	private SqlQueryFlux(DataSource dataSource, String stmtString) {
		super();
		this.dataSource = dataSource;
		this.stmtString = stmtString;
	}

	@Override
	public void accept(ResultSetEnvelope t) {
		try {
			t.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public Flux<Object[]> apply(ResultSetEnvelope result) {
		return Flux.generate(new ResultSetSinkConsumer(result));
	}

	@Override
	public ResultSetEnvelope call() throws Exception {
		return new ResultSetEnvelope(dataSource, stmtString);
	}

	public static Flux using(DataSource dataSource, String stmtString) {
		SqlQueryFlux stream = new SqlQueryFlux(dataSource, stmtString);
		return Flux.using(stream, stream, stream);
	}

}