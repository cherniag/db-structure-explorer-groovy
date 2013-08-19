package com.musicqubed.selenium;

import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.DigestUtils;

public class RememberMeUtils {
	private static long TWO_WEEKS = 14L * 24L * 60L * 60L * 1000L;

	public static void main(String[] args) {

		String username = "+447408831838";
		String userToken = "c7213cd89f51597428f6bce9cf9d6cac";
		
		String rememberMeToken = generateRememberMeToken(username, userToken);
		System.out.println("Token " + rememberMeToken);

	}

	public static String generateRememberMeToken(String username, String userToken) {
		String expiredMillis = getExpiredMillis();
		String signature = makeTokenSignature(username, Long.parseLong(expiredMillis), userToken);

		String rememberMeToken = encodeCookie(new String[] { username, expiredMillis, signature });
		System.out.println("_REMEMBER_ME=" + rememberMeToken);
		return rememberMeToken;
	}

	private static String getExpiredMillis() {
		String expiredMillis = Long.toString(System.currentTimeMillis() + TWO_WEEKS);
		return expiredMillis;
	}

	private static String encodeCookie(String[] cookieTokens) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cookieTokens.length; i++) {
			sb.append(cookieTokens[i]);

			if (i < cookieTokens.length - 1) {
				sb.append(":");
			}
		}

		String value = sb.toString();

		sb = new StringBuilder(new String(Base64.encode(value.getBytes())));

		while (sb.charAt(sb.length() - 1) == '=') {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	public static String makeTokenSignature(String username, long tokenExpiryTime, String password) {
		return DigestUtils.md5DigestAsHex((username + ":" + tokenExpiryTime + ":" + password + ":" + "web").getBytes());
	}
}
