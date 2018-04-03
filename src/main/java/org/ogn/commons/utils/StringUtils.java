/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.commons.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class defines a set of utility methods operating on strings and commonly used in the project
 */
public class StringUtils {
	private static final Logger LOG = LoggerFactory.getLogger(StringUtils.class);

	private StringUtils() {

	}

	public static String hex2ascii(String hex) {
		final StringBuilder output = new StringBuilder();
		for (int i = 0; i < hex.length(); i += 2) {
			final String str = hex.substring(i, i + 2);
			output.append((char) Integer.parseInt(str, 16));
		}
		return output.toString();
	}

	public static String asciiToHex(String asciiValue) {
		final char[] chars = asciiValue.toCharArray();
		final StringBuilder hex = new StringBuilder();
		for (int i = 0; i < chars.length; i++) {
			hex.append(Integer.toHexString(chars[i]));
		}
		return hex.toString();
	}

	public static String md5(String s) {
		try {
			final MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes(), 0, s.length());
			final BigInteger i = new BigInteger(1, m.digest());
			return String.format("%1$032x", i);
		} catch (final NoSuchAlgorithmException e) {
			LOG.error("call to md5() failed", e);
		}
		return null;
	}
}