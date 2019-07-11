package com.esolutions.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

	private String message;
	private String operation;
	private int code;
	private long countFailure;
	private long countSuccess;

	public Response(String operation, String message, int code) {
		this.operation = operation;
		this.message = message;
		this.code = code;
	}

	public Response(String operation, String message, int code, long countFailure, long countSuccess) {
		this.operation = operation;
		this.message = message;
		this.code = code;
		this.countFailure = countFailure;
		this.countSuccess = countSuccess;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return the countFailure
	 */
	public long getCountFailure() {
		return countFailure;
	}

	/**
	 * @param countFailure the countFailure to set
	 */
	public void setCountFailure(long countFailure) {
		this.countFailure = countFailure;
	}

	/**
	 * @return the countSuccess
	 */
	public long getCountSuccess() {
		return countSuccess;
	}

	/**
	 * @param countSuccess the countSuccess to set
	 */
	public void setCountSuccess(long countSuccess) {
		this.countSuccess = countSuccess;
	}

}
