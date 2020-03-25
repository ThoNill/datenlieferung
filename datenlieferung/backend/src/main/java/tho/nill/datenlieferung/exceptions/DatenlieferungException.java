package tho.nill.datenlieferung.exceptions;

import lombok.NonNull;

public class DatenlieferungException extends RuntimeException {

	public DatenlieferungException() {
		super();
	}

	public DatenlieferungException(@NonNull String message) {
		super(message);
	}

	public DatenlieferungException(@NonNull Throwable cause) {
		super(cause);
	}

	public DatenlieferungException(@NonNull String message, @NonNull Throwable cause) {
		super(message, cause);
	}

	public DatenlieferungException(@NonNull String message, @NonNull Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
