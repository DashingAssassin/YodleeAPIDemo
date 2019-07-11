package com.esolutions.exception;

/**
 * Runtime exception to be thrown in cases where a Dao layer operation cannot be
 * successfully performed, or as Database layer exception encapsulation.
 */
@SuppressWarnings("serial")
public class IncorrectMongoDAOException extends RuntimeException {

	/**
	 * Creates a exception for incorrect Dao layer operations.
	 *
	 * @param message   - inflicting string message that originated the error.
	 * @param exception - exception chain reference.
	 */
	public IncorrectMongoDAOException(final String message, Throwable exception) {
		super(message, exception);
	}

	/**
	 * Creates a exception for incorrect Dao layer operations.
	 *
	 * @param message - inflicting string message that originated the error.
	 */
	public IncorrectMongoDAOException(final String message) {
		super(message);
	}

}
