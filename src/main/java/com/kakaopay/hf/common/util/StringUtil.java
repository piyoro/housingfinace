package com.kakaopay.hf.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {

	public static boolean isEmpty(String src) {
		return StringUtils.isEmpty(src);
	}

	public static String trim(String src) {
		if (StringUtils.isEmpty(src)) {
			return "";
		}

		return src.trim();
	}

	public static String pad(int src, int size, String ch) {
		return pad(String.valueOf(src), size, true, ch);
	}

	public static String pad(String src, int size, String ch) {
		return pad(src, size, true, ch);
	}

	public static String pad(String src, int size, boolean position, String ch) {
		if (position) {
			return StringUtils.leftPad(src, size, ch);
		}
		return StringUtils.rightPad(src, size, ch);
	}

	public static String getStrToAllowNum(String src) {
		if (StringUtils.isEmpty(src)) {
			return "";
		}
		Pattern p = Pattern.compile(Matcher.quoteReplacement("\"([0-9]{1,3}(,[0-9]{3})*)\""));
		Matcher matcher = p.matcher(src);
		StringBuffer sb = new StringBuffer(src.length());
		while (matcher.find()) {
			matcher.appendReplacement(sb, StringUtil.getStrToPureNum(matcher.group(1)));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	public static String getStrToPureNum(String src) {
		if (StringUtils.isEmpty(src)) {
			return "0";
		}
		return src.replaceAll("[^0-9]", "");
	}

	public static int getStrToNum(String src) {
		try {
			return Integer.parseInt(src);
		} catch (Exception e) {
			return 0;
		}
	}

}
