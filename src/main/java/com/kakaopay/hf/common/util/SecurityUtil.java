package com.kakaopay.hf.common.util;

import java.security.MessageDigest;

public class SecurityUtil {

	public static final String SHA_256 = "SHA-256";
	public static final String HMAC_SHA256 = "HmacSHA256";

	public static String encryptSHA256(String str) {
		String sha = "";
		try {
			MessageDigest sh = MessageDigest.getInstance(SHA_256);
			sh.update(str.getBytes());
			byte[] byteData = sh.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toHexString((byteData[i] & 0xff) + 0x100).substring(1));
			}
			sha = sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sha;
	}
}
