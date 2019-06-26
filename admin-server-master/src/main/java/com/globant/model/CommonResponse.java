/**
 * 
 */
package com.globant.model;

/**
 * @author mangesh.pendhare
 *
 */
public class CommonResponse {
	public static final String SUCCESS = "success";
	public static final String FAILURE = "failure";
	private String status;
	private Object data;
	private String message;

	public CommonResponse(String status, String message) {
		this.status = status;
		this.message = message;
	}

	public CommonResponse(String status, Object data) {
		this.status = status;
		this.data = data;
	}

	public CommonResponse(String status, String message, Object data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
