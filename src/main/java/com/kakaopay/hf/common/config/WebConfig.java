package com.kakaopay.hf.common.config;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kakaopay.hf.common.interceptor.JwtInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

	@Bean
	public FilterRegistrationBean<CharacterEncodingFilter> getEncodingFilterRegistrationBean() {
		logger.debug("getEncodingFilterRegistrationBean() - start");
		FilterRegistrationBean<CharacterEncodingFilter> registrationBean = new FilterRegistrationBean<>();
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setForceEncoding(true);
		characterEncodingFilter.setEncoding("UTF-8");
		registrationBean.setFilter(characterEncodingFilter);
		registrationBean.setOrder(0);
		registrationBean.addUrlPatterns("/*");
		logger.debug("getEncodingFilterRegistrationBean() - end");
		return registrationBean;
	}

	@Bean
	public HttpMessageConverter<String> responseBodyConverter() {
		return new StringHttpMessageConverter(Charset.forName("UTF-8"));
	}

	@Bean
	public JwtInterceptor jwtInterceptor() {
		return new JwtInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 인터셉터는 추후 추가
		registry.addInterceptor(jwtInterceptor()).addPathPatterns("/**");
	}

}
