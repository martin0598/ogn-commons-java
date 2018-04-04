package org.ogn.commons.beacon.impl.aprs;

public interface AprsPatternConstants {
	/*
	 * The following regexp patterns are part of the python ogn-client. You can find it here:
	 * https://github.com/glidernet/python-ogn-client Difference: Python group (?P<ground_speed>\d{4}) becomes
	 * (?<groundSpeed>\\d{4}) --> backslashes must be escaped and no "P" and no underscores (I replaced them with
	 * camelCase)
	 */

	String PATTERN_APRS_POSITION = "^(?<callsign>.+?)>(?<dstcall>[A-Z0-9]+),.+,(?<receiver>.+?):/(?<time>\\d{6})+h(?<latitude>\\d{4}\\.\\d{2})(?<latitudeSign>N|S)(?<symboltable>.)(?<longitude>\\d{5}\\.\\d{2})(?<longitudeSign>E|W)(?<symbol>.)(?<courseExtension>(?<course>\\d{3})/(?<groundSpeed>\\d{3}))?/A=(?<altitude>\\d{6})(?<posExtension>\\s!W((?<latitudeEnhancement>\\d)(?<longitudeEnhancement>\\d))!)?(?:\\s(?<comment>.*))?$";
	String PATTERN_APRS_STATUS = "(?<callsign>.+?)>(?<dstcall>[A-Z0-9]+),.+,(?<receiver>.+?):>(?<time>\\d{6})+h\\s(?<comment>.*)$";

	/*
	 * The following regexp patterns are part of the ruby ogn-client. source: https://github.com/svoop/ogn_client-ruby
	 */

	String PATTERN_RECEIVER_BEACON = "" + "(?:" + "v(?<version>\\d+\\.\\d+\\.\\d+)" + "(?:\\.(?<platform>.+?))?"
			+ "\\s)?" + "CPU:(?<cpuLoad>[\\d.]+)\\s" + "RAM:(?<ramFree>[\\d.]+)/(?<ramTotal>[\\d.]+)MB\\s"
			+ "NTP:(?<ntpOffset>[\\d.]+)ms/(?<ntpCorrection>[+-][\\d.]+)ppm\\s" + "(?:(?<voltage>[\\d.]+)V\\s)?"
			+ "(?:(?<amperage>[\\d.]+)A\\s)?" + "(?:(?<cpuTemperature>[+-][\\d.]+)C\\s*)?"
			+ "(?:(?<visibleSenders>\\d+)/(?<senders>\\d+)Acfts\\[1h\\]\\s*)?" + "(?:RF:" + "(?:"
			+ "(?<rfCorrectionManual>[+-][\\d]+)" + "(?<rfCorrectionAutomatic>[+-][\\d.]+)ppm/" + ")?"
			+ "(?<signalQuality>[+-][\\d.]+)dB"
			+ "(?:/(?<sendersSignalQuality>[+-][\\d.]+)dB@10km\\[(?<sendersMessages>\\d+)\\])?"
			+ "(?:/(?<goodSendersSignalQuality>[+-][\\d.]+)dB@10km\\[(?<goodSenders>\\d+)/(?<goodAndBadSenders>\\d+)\\])?"
			+ ")?";

	String PATTERN_AIRCRAFT_BEACON = "" + "id(?<details>\\w{2})(?<id>\\w{6}?)\\s?"
			+ "(?:(?<climbRate>[+-]\\d+?)fpm\\s?)?" + "(?:(?<turnRate>[+-][\\d.]+?)rot\\s?)?"
			+ "(?:FL(?<flightLevel>[\\d.]+)\\s?)?" + "(?:(?<signalQuality>[\\d.]+?)dB\\s?)?"
			+ "(?:(?<errors>\\d+)e\\s?)?" + "(?:(?<frequencyOffset>[+-][\\d.]+?)kHz\\s?)?"
			+ "(?:gps(?<gpsAccuracy>\\d+x\\d+)\\s?)?" + "(?:s(?<flarmSoftwareVersion>[\\d.]+)\\s?)?"
			+ "(?:h(?<flarmHardwareVersion>[\\dA-F]{2})\\s?)?" + "(?:r(?<flarmId>[\\dA-F]+)\\s?)?"
			+ "(?:(?<signalPower>[+-][\\d.]+)dBm\\s?)?" + "(?:(?<proximity>(hear[\\dA-F]{4}\\s?)+))?";
}