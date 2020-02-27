package com.kakaopay.hf.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kakaopay.hf.common.exception.HousingFinanceException;
import com.kakaopay.hf.common.util.StringUtil;

/**
 * <pre>
 * 오류 처리 및 controller 공통 처리를 위한 공통 controller
 * </pre>
 */
@RestControllerAdvice
public class CommonController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

	public static String getAccessToken(HttpServletRequest req) {
		String jwt = "";
		String authorization = req.getHeader("Authorization");
		if (!StringUtil.isEmpty(authorization)) {
			String[] token = authorization.split(" ");
			if ("Bearer".equals(token[0])) {
				jwt = token[1];
			}
		}
		return jwt;
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handling(Exception e) {
		String errMsg = "시스템 오류";
		logger.error(errMsg, e);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		return new ResponseEntity<>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(HousingFinanceException.class)
	public ResponseEntity<?> handling(HousingFinanceException e) {
		String errMsg = e.getMessage();
		logger.error(errMsg, e);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, Object> rtnObj = new HashMap<>();
		rtnObj.put("msg", errMsg);
		return new ResponseEntity<>(rtnObj, headers, e.getHttpStatus());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handling(MethodArgumentNotValidException e) {
		String errMsg = "파라미터 검증 오류";
		BindingResult b = e.getBindingResult();
		errMsg = b.getFieldError().getDefaultMessage();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, Object> rtnObj = new HashMap<>();
		rtnObj.put("msg", errMsg);
		return new ResponseEntity<>(rtnObj, headers, HttpStatus.BAD_REQUEST);
	}

}
