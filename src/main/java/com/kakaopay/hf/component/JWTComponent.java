package com.kakaopay.hf.component;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.kakaopay.hf.common.exception.HousingFinanceException;
import com.kakaopay.hf.common.util.StringUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTComponent {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(JWTComponent.class);

	@Value("${jwt.secretkey}")
	private String secretKey;

	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	public String issueToken(String userId, String strExpire) {
		long current = System.currentTimeMillis();
		return issueToken(current, userId, strExpire);
	}

	public String issueToken(long current, String userId, String strExpire) {
		Date now = new Date(current);

		int expireTime = StringUtil.getStrToNum(strExpire);
		Date expDate = new Date(current + (1000 * 60 * expireTime));
		// 테스트
		/*
		expDate = new Date();
		expDate.setTime(now.getTime() + (1000 * expireTime));
		*/
		logger.debug("current {} expireTime {}", new Object[] { current, expireTime });
		String jwt = Jwts.builder().setHeaderParam("typ", "JWT").setSubject("hf.kakaopay.com").claim("userId", userId).setIssuedAt(now)
				.setExpiration(expDate).signWith(getJwtSignKey(secretKey), signatureAlgorithm).compact();

		return jwt;
	}

	public boolean isExpToken(String jwt) {
		try {
			Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(getJwtSignKey(secretKey)).build().parseClaimsJws(jwt);
			Date exp = claims.getBody().getExpiration();
			Date now = new Date();
			logger.debug("exp {} now {}", new Object[] { exp, now });

			if (now.after(exp)) {
				return true;
			}

		} catch (ExpiredJwtException eje) {
			return true;
		} catch (Exception e) {
			throw new HousingFinanceException(HttpStatus.BAD_REQUEST, "토큰 검증 실패");
		}
		return false;
	}

	public String getClaimInfo(String jwt, String valueKey) {
		try {
			Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(getJwtSignKey(secretKey)).build().parseClaimsJws(jwt);
			return String.valueOf(claims.getBody().get(valueKey));
		} catch (ExpiredJwtException eje) {
			throw new HousingFinanceException(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다.");
		} catch (Exception e) {
			throw new HousingFinanceException(HttpStatus.BAD_REQUEST, "토큰 검증 실패");
		}
	}

	public Key getJwtSignKey(String secretKey) {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}
}
