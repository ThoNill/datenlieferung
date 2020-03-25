package tho.nill.datenlieferung.sql;

import java.util.function.Consumer;

import reactor.core.publisher.SynchronousSink;

public class ResultSetSinkConsumer implements Consumer<SynchronousSink<Object[]>> {
	ResultSetEnvelope reader;

	public ResultSetSinkConsumer(ResultSetEnvelope reader) {
		super();
		this.reader = reader;
	}

	@Override
	public void accept(SynchronousSink<Object[]> client) {
		try {
			if (reader.next()) {
				client.next(reader.getArray());
			} else {
				client.complete();
			}
		} catch (Exception e) {
			client.error(e);
		}
	}

}
