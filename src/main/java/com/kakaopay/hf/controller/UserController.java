package com.kakaopay.hf.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakaopay.hf.service.UserService;
import com.kakaopay.hf.vo.ReqUserVO;

@RestController()
@RequestMapping("/user")
public class UserController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Resource
	UserService userService;

	@PostMapping(value = "/signup")
	public ResponseEntity<?> signup(@Valid @RequestBody ReqUserVO userVO, HttpServletRequest req, HttpServletResponse res, Errors errors) {
		return userService.signUp(userVO);
	}

	@PostMapping(value = "/signin")
	public ResponseEntity<?> signin(@Valid @RequestBody ReqUserVO userVO, HttpServletRequest req, HttpServletResponse res, Errors errors) {
		return userService.signIn(userVO);
	}

	@GetMapping(value = "/refresh")
	public ResponseEntity<?> refresh(HttpServletRequest req, HttpServletResponse res) {
		String jwt = CommonController.getAccessToken(req);
		return userService.refresh(jwt);
	}
}
