/**
 * Copyright (c) 2014-2015 OGN, All Rights Reserved.
 */

package org.ogn.commons.beacon.impl.aprs;

import org.ogn.commons.beacon.OgnBeacon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;

public class AprsLineParser {
	private static Pattern aprsPositionPattern = Pattern.compile(AprsPatternConstants.PATTERN_APRS_POSITION,
			Pattern.MULTILINE);
	private static Pattern aprsStatusPattern = Pattern.compile(AprsPatternConstants.PATTERN_APRS_STATUS,
			Pattern.MULTILINE);
	private static Pattern ognAircraftPattern = Pattern.compile(AprsPatternConstants.PATTERN_AIRCRAFT_BEACON);
	private static Pattern ognReceiverPattern = Pattern.compile(AprsPatternConstants.PATTERN_RECEIVER_BEACON);

	private static final Logger LOG = LoggerFactory.getLogger(AprsLineParser.class);

	private static class AprsLineParserHolder {
		private static AprsLineParser theInstance = new AprsLineParser();

		private AprsLineParserHolder() {
		}
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

		final Matcher statusMatcher = aprsStatusPattern.matcher(aprsLine);
		final Matcher positionMatcher = aprsPositionPattern.matcher(aprsLine);

		// Check if we have a APRS status
		if (processReceiverBeacons && statusMatcher.matches()) {
			final String comment = statusMatcher.group("comment");
			final Matcher receiverMatcher = ognReceiverPattern.matcher(comment);
			if (receiverMatcher.matches()) {
				LOG.debug("Receiver status beacon: {}", aprsLine);
				result = new AprsReceiverBeacon(statusMatcher).update(receiverMatcher);
			}
			// Check if we have a APRS position
		} else if (positionMatcher.matches()) {
			final String comment = positionMatcher.group("comment");
			if (processReceiverBeacons && comment == null) {
				LOG.debug("Receiver position beacon without comment: {}", aprsLine);
				result = new AprsReceiverBeacon(positionMatcher);
			} else {
				final Matcher aircraftMatcher = ognAircraftPattern.matcher(comment);
				if (processAircraftBeacons && aircraftMatcher.matches()) {
					LOG.debug("Aircraft position beacon: {}", aprsLine);
					result = new AprsAircraftBeacon(positionMatcher).update(aircraftMatcher);
				}

				final Matcher receiverMatcher = ognReceiverPattern.matcher(comment);
				if (processReceiverBeacons && receiverMatcher.matches()) {
					LOG.debug("Receiver position beacon: {}", aprsLine);
					result = new AprsReceiverBeacon(positionMatcher).update(receiverMatcher);
				}
			}
		}

		return result;
	}
}