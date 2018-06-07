package org.ogn.commons.utils;

public class Configuration {

	private Configuration() {
		//
	}

	public static String getValue(String envVariable, String systemProperty) {
		return getValue(envVariable, systemProperty, null);
	}

	public static String getValue(String envVariable, String systemProperty, String defaultValue) {
		final String result = System.getenv(envVariable);
		return result == null ? System.getProperty(systemProperty, defaultValue) : result;
	}

	public static int getIntValue(String envVariable, String systemProperty) {
		return Integer.parseInt(getValue(envVariable, systemProperty));
	}

	public static int getIntValue(String envVariable, String systemProperty, int defaultValue) {
		int result = defaultValue;
		try {
			result = Integer.parseInt(getValue(envVariable, systemProperty));
		} catch (final Exception ex) {
			// NOSONAR
		}
		return result;
	}

	public static boolean getBooleanValue(String envVariable, String systemProperty) {
		boolean result = false;
		try {
			result = Boolean.parseBoolean(getValue(envVariable, systemProperty));
		} catch (final Exception ex) {
			// NOSONAR
		}
		return result;
	}

}
