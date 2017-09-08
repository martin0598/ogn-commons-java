/**
 * Copyright (c) 2014-2015 OGN, All Rights Reserved.
 */

package org.ogn.commons.beacon.impl.aprs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ogn.commons.beacon.OgnBeacon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AprsLineParser {
	private static Pattern aprsPositionPattern = Pattern.compile(AprsPatternConstants.PATTERN_APRS_POSITION, Pattern.MULTILINE);
	private static Pattern aprsStatusPattern = Pattern.compile(AprsPatternConstants.PATTERN_APRS_STATUS, Pattern.MULTILINE);
	private static Pattern ognAircraftPattern = Pattern.compile(AprsPatternConstants.PATTERN_AIRCRAFT_BEACON);
	private static Pattern ognReceiverPattern = Pattern.compile(AprsPatternConstants.PATTERN_RECEIVER_BEACON);

	private static Logger LOG = LoggerFactory.getLogger(AprsLineParser.class);

	private static class AprsLineParserHolder {
		private static AprsLineParser theInstance = new AprsLineParser();
	}

	public static AprsLineParser get() {
		return AprsLineParserHolder.theInstance;
	}

	public OgnBeacon parse(String aprsLine) {
		return parse(aprsLine, true, true);
	}

	public OgnBeacon parse(String aprsLine, boolean processAircraftBeacons, boolean processReceiverBeacons) {
		LOG.trace(aprsLine);
		OgnBeacon result = null;
		
		// Check if we have a APRS status
		Matcher statusMatcher = aprsStatusPattern.matcher(aprsLine);
		if (processReceiverBeacons && statusMatcher.matches()) {
			String comment = statusMatcher.group("comment");
			Matcher receiverMatcher = ognReceiverPattern.matcher(comment);
			if (receiverMatcher.matches()) {
				LOG.debug("Receiver status beacon: {}", aprsLine);
				result = new AprsReceiverBeacon(statusMatcher).update(receiverMatcher);
				return result;
			}
		}

		// Check if we have a APRS position
		Matcher positionMatcher = aprsPositionPattern.matcher(aprsLine);
		if (positionMatcher.matches()) {
			String comment = positionMatcher.group("comment");
			if (processReceiverBeacons && comment == null) {
				LOG.debug("Receiver position beacon without comment: {}", aprsLine);
				result = new AprsReceiverBeacon(positionMatcher);
				return result;
			} else {
				Matcher aircraftMatcher = ognAircraftPattern.matcher(comment);
				if (processAircraftBeacons && aircraftMatcher.matches()) {
					LOG.debug("Aircraft position beacon: {}", aprsLine);
					result = new AprsAircraftBeacon(positionMatcher).update(aircraftMatcher);
					return result;
				}
				
				Matcher receiverMatcher = ognReceiverPattern.matcher(comment);
				if (processReceiverBeacons && receiverMatcher.matches()) {
					LOG.debug("Receiver position beacon: {}", aprsLine);
					result = new AprsReceiverBeacon(positionMatcher).update(receiverMatcher);
					return result;
				}
			}
		}

		return result;
	}
}