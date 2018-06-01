/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.commons.utils;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.String.format;

import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.UUID;

import org.ogn.commons.beacon.OgnBeacon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AprsUtils {

	private static final Logger LOG = LoggerFactory.getLogger(AprsUtils.class);

	private static final int RADIUS = 6371000;

	private AprsUtils() {

	}

	/**
	 * Generates APRS login sentence, required by APRS server. Refer to
	 * <a href="http://www.aprs-is.net/Connecting.aspx">Connecting to APRS-IS</a> for details.
	 * 
	 * @param userName
	 * @param passCode
	 * @param appName
	 * @param version
	 * @param filter
	 * @return
	 */
	public static String formatAprsLoginLine(final String userName, final String passCode, final String appName,
			final String version, final String filter) {
		return filter == null ? format("user %s pass %s vers %s %s", userName, passCode, appName, version)
				: format("user %s pass %s vers %s %s filter %s", userName, passCode, appName, version, filter);
	}

	public static String formatAprsLoginLine(final String userName, final String passCode, final String appName,
			final String version) {
		return formatAprsLoginLine(userName, passCode, appName, version, null);
	}

	/**
	 * @return a unique client id(based on the host name + sequence id) which can be used as APRS user name. The max
	 *         length is 9 characters and complies with APRS
	 *         <a href="http://www.aprs-is.net/Connecting.aspx#loginrules"> Login rules </a>
	 */
	public static String generateClientId() {
		try {

			final String suffix = UUID.randomUUID().toString().split("-")[3].toUpperCase();
			final String hostName = InetAddress.getLocalHost().getHostName();
			final String res = (hostName.length() > 3 ? hostName.substring(0, 3) : hostName).toUpperCase();
			final StringBuilder bld = new StringBuilder(res.replace("-", ""));
			bld.append("-");
			bld.append(suffix);

			return bld.toString();
		} catch (final Exception e) {
			final String fallbackClientId = "CLI" + new Random(System.currentTimeMillis()).nextInt(100);
			LOG.warn("clientId generation failed, returning fallback id: {}", fallbackClientId, e);
			return fallbackClientId;
		}
	}

	// calc. password for the APRS server login
	public static int generatePass(String callSign) {
		return -1;
	}

	public static double dmsToDeg(double dms) {
		final double absDms = Math.abs(dms);
		final double d = Math.floor(absDms);
		final double m = (absDms - d) * 100 / 60;
		return (d + m);
	}

	public static double degToDms(double deg) {
		final double absDeg = Math.abs(deg);
		final double d = Math.floor(absDeg);
		final double m = (absDeg - d) * 60 / 100;
		return (d + m);
	}

	public enum Coordinate {
		LAT, LON;
	}

	public static String degToIgc(double deg, Coordinate what) {
		final StringBuilder result = new StringBuilder();

		char sign = 'S';
		switch (what) {
		case LAT:
			if (deg < 0.0f)
				sign = 'S';
			else
				sign = 'N';

			result.append(String.format("%07.0f", Math.abs(degToDms(deg) * 100 * 100 * 10)));
			break;
		case LON:
			if (deg < 0.0f)
				sign = 'W';
			else
				sign = 'E';

			result.append(String.format("%08.0f", Math.abs(degToDms(deg) * 100 * 100 * 10)));
			break;
		}

		result.append(sign);
		return result.toString();
	}

	public static double degToMeters(double deg) {
		// Converts an angle (lon or lat) to meters.
		// We assume a spherical Earth and being at the sea level.
		final double earthRadius = (double) 6371 * 1000; // in meters
		return (deg * Math.PI * earthRadius / 180);
	}

	/**
	 * Creates a unix timestamp, based on given h:m:s. Local system's date is taken as a reference
	 * 
	 * @param h
	 *            hour
	 * @param m
	 *            minutes
	 * @param s
	 *            seconds
	 * @return
	 */
	public static long toUtcTimestamp(int h, int m, int s) {
		return ZonedDateTime.of(LocalDateTime.of(LocalDate.now(), LocalTime.of(h, m, s)), ZoneOffset.UTC)
				.toEpochSecond() * 1000;
	}

	public static long toUtcTimestamp(LocalDate date, int h, int m, int s) {
		return ZonedDateTime.of(LocalDateTime.of(date, LocalTime.of(h, m, s)), ZoneOffset.UTC).toEpochSecond() * 1000;
	}

	/**
	 * @param time
	 *            time in 6 digit format provided in a APRS packet (e.g. 162334, 051202)
	 * @return
	 */
	public static long toUtcTimestamp(String time) {
		final int h = Integer.parseInt(time.substring(0, 2));
		final int m = Integer.parseInt(time.substring(2, 4));
		final int s = Integer.parseInt(time.substring(4, 6));

		return toUtcTimestamp(h, m, s);
	}

	public static long toUtcTimestamp(LocalDate date, String time) {
		final int h = Integer.parseInt(time.substring(0, 2));
		final int m = Integer.parseInt(time.substring(2, 4));
		final int s = Integer.parseInt(time.substring(4, 6));

		return toUtcTimestamp(date, h, m, s);
	}

	public static float feetsToMetres(float feets) {
		return (float) (Math.round((feets / (float) 3.2808) * 10) / 10.0);
	}

	public static float kntToKmh(float knts) {
		return knts * (float) 1.852; // kts to km/h
	}

	/**
	 * computes distance(in m) between two coordinates (in deg. format)
	 * 
	 * @param degLat1
	 * @param degLon1
	 * @param dgLat2
	 * @param degLon2
	 * @return a distance in m
	 */
	public static double calcDistance(double degLat1, double degLon1, double degLat2, double degLon2) {

		final double radLat1 = degLat1 * Math.PI / 180;
		final double radLon1 = degLon1 * Math.PI / 180;

		final double radLat2 = degLat2 * Math.PI / 180;
		final double radLon2 = degLon2 * Math.PI / 180;

		return acos(sin(radLat1) * sin(radLat2) + cos(radLat1) * cos(radLat2) * cos(radLon2 - radLon1)) * RADIUS;
	}

	public static double calcDistance(OgnBeacon beacon1, OgnBeacon beacon2) {
		return calcDistance(beacon1.getLat(), beacon1.getLon(), beacon2.getLat(), beacon2.getLon());
	}

	public static double calcDistanceInKm(double degLat1, double degLon1, double degLat2, double degLon2) {
		return round(AprsUtils.calcDistance(degLat1, degLon1, degLat2, degLon2) / 1000 * 100.0) / 100.0;
	}

	public static double calcDistanceInKm(OgnBeacon beacon1, OgnBeacon beacon2) {
		return calcDistanceInKm(beacon1.getLat(), beacon1.getLon(), beacon2.getLat(), beacon2.getLon());
	}

}