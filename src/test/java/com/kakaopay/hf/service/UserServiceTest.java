package com.kakaopay.hf.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.kakaopay.hf.vo.ReqUserVO;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

	@Resource
	UserService userService;

	@BeforeClass
	public static void init() {
		logger.debug("Starting testt...");
	}

	// @Test
	public void signUpTest() {
		ReqUserVO vo = new ReqUserVO();
		vo.setUserId("test1234");
		vo.setPwd("1234");
		ResponseEntity<?> res = userService.signUp(vo);
		Map<String, Object> t = (Map<String, Object>) res.getBody();
		assertThat(t.get("msg"), is("계정이 생성되었습니다."));
	}

	@Test
	public void signInTest() {
		ReqUserVO vo = new ReqUserVO();
		vo.setUserId("test1234");
		vo.setPwd("1234");
		ResponseEntity<?> res = userService.signIn(vo);
		Map<String, Object> t = (Map<String, Object>) res.getBody();
		assertThat(t.get("token"), is(notNullValue()));
		assertThat(t.get("refreshToken"), is(notNullValue()));
	}

	@Test
	public void refreshTest() {
		ReqUserVO vo = new ReqUserVO();
		vo.setUserId("test1234");
		vo.setPwd("1234");
		ResponseEntity<?> res = userService.signIn(vo);
		Map<String, Object> t = (Map<String, Object>) res.getBody();
		String refreshToken = (String) t.get("refreshToken");
		assertThat(refreshToken, is(notNullValue()));
		res = userService.refresh(refreshToken);
		t = (Map<String, Object>) res.getBody();
		assertThat(t.get("token"), is(notNullValue()));
		assertThat(t.get("refreshToken"), is(notNullValue()));
	}

	@AfterClass
	public static void complete() {
		logger.debug("Shut down test.");
	}
}
