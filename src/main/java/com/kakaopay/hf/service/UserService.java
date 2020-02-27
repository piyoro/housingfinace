package com.kakaopay.hf.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kakaopay.hf.common.exception.HousingFinanceException;
import com.kakaopay.hf.common.util.SecurityUtil;
import com.kakaopay.hf.component.JWTComponent;
import com.kakaopay.hf.domain.User;
import com.kakaopay.hf.repository.UserRepository;
import com.kakaopay.hf.vo.ReqUserVO;

@Service
public class UserService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Value("${jwt.time.expire}")
	private String strExpire;
	@Value("${jwt.time.refresh}")
	private String strRefresh;
	@Value("${jwt.time.test.expire}")
	private String strTestExpire;
	@Value("${jwt.time.test.refresh}")
	private String strTestRefresh;

	@Resource
	JWTComponent jwtComponent;
	@Resource
	UserRepository userRepository;

	public ResponseEntity<?> signUp(ReqUserVO vo) {

		String userId = vo.getUserId();
		Optional<User> findUser = userRepository.findById(userId);
		if (findUser.isPresent()) {
			throw new HousingFinanceException(HttpStatus.BAD_REQUEST, "이미 존재하는 id 입니다.");
		}
		String pwd = SecurityUtil.encryptSHA256(vo.getPwd());
		userRepository.save(User.builder().id(vo.getUserId()).pwd(pwd).build());

		Map<String, String> response = new HashMap<>();
		response.put("msg", "계정이 생성되었습니다.");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<?> signIn(ReqUserVO vo) {

		String userId = vo.getUserId();
		String pwd = SecurityUtil.encryptSHA256(vo.getPwd());

		Optional<User> oFindUser = userRepository.findById(userId);
		if (!oFindUser.isPresent()) {
			throw new HousingFinanceException(HttpStatus.BAD_REQUEST, "계정이 존재하지 않거나 비밀번호가 일치하지 않습니다.");
		}
		User findUser = oFindUser.get();
		String findPwd = findUser.getPwd();
		if (!pwd.equals(findPwd)) {
			throw new HousingFinanceException(HttpStatus.BAD_REQUEST, "계정이 존재하지 않거나 비밀번호가 일치하지 않습니다.");
		}
		String issueToken = jwtComponent.issueToken(userId, strExpire);
		Map<String, String> response = new HashMap<>();
		response.put("token", issueToken);
		String refreshToken = jwtComponent.issueToken(userId, strRefresh);
		response.put("refreshToken", refreshToken);
		// 테스트
		/*
		issueToken = jwtComponent.issueToken(userId, strTestExpire);
		refreshToken = jwtComponent.issueToken(userId, strTestRefresh);
		response.put("token", issueToken);
		response.put("refreshToken", refreshToken);
		*/
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<?> refresh(String jwt) {
		String userId = jwtComponent.getClaimInfo(jwt, "userId");
		Map<String, String> response = new HashMap<>();
		String issueToken = jwtComponent.issueToken(userId, strExpire);
		response.put("token", issueToken);
		String refreshToken = jwtComponent.issueToken(userId, strRefresh);
		response.put("refreshToken", refreshToken);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
