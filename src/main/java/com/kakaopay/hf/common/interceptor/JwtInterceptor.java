package com.kakaopay.hf.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kakaopay.hf.common.exception.HousingFinanceException;
import com.kakaopay.hf.common.util.StringUtil;
import com.kakaopay.hf.component.JWTComponent;
import com.kakaopay.hf.controller.CommonController;

/**
 * jwt token 검증 인터셉터
 */
public class JwtInterceptor extends HandlerInterceptorAdapter {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);

	@Autowired
	JWTComponent jwtComponenet;

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object object) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("preHandle(HttpServletRequest, HttpServletResponse, Object) - start");
		}

		String uri = req.getRequestURI();
		logger.debug("preHandle(HttpServletRequest, HttpServletResponse, Object) - uri {}", new Object[] { uri });

		// user api 아닐 경우 jwt 검증
		if (!StringUtil.isEmpty(uri) && !uri.startsWith("/user/")) {
			String jwt = CommonController.getAccessToken(req);
			logger.debug("preHandle(HttpServletRequest, HttpServletResponse, Object) - jwtComponenet {} token {}",
					new Object[] { jwtComponenet, jwt });
			if (jwtComponenet.isExpToken(jwt)) {
				throw new HousingFinanceException(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다.");
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("preHandle(HttpServletRequest, HttpServletResponse, Object) - end");
		}
		return true;
	}
}
