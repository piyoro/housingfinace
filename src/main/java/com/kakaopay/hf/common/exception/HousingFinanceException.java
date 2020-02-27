package com.kakaopay.hf.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class HousingFinanceException extends RuntimeException {

	public HttpStatus httpStatus;
	public String message;

	public HousingFinanceException(HttpStatus httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
