/**
 * Copyright (c) 2014-2015 OGN, All Rights Reserved.
 */

package org.ogn.commons.beacon.impl.aprs;

import org.ogn.commons.beacon.OgnBeacon;
import org.ogn.commons.beacon.ReceiverBeaconType;
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
		LOG.trace(aprsLine);
		OgnBeacon result = null;

		final Matcher statusMatcher = aprsStatusPattern.matcher(aprsLine);
		final Matcher positionMatcher = aprsPositionPattern.matcher(aprsLine);

		// Check if we have a APRS status
		if (statusMatcher.matches()) {
			final String comment = statusMatcher.group("comment");
			final Matcher receiverMatcher = ognReceiverPattern.matcher(comment);
			if (receiverMatcher.matches()) {
				LOG.trace("Receiver status beacon: {}", aprsLine);
				result = new AprsReceiverBeacon(statusMatcher, ReceiverBeaconType.RECEIVER_STATUS)
						.update(receiverMatcher);
			}
			// Check if we have a APRS position
		} else if (positionMatcher.matches()) {
			final String comment = positionMatcher.group("comment");
			if (comment == null) {
				LOG.trace("Receiver position beacon without comment: {}", aprsLine);
				result = new AprsReceiverBeacon(positionMatcher, ReceiverBeaconType.RECEIVER_POSITION);
			} else {
				final Matcher aircraftMatcher = ognAircraftPattern.matcher(comment);
				if (aircraftMatcher.matches()) {
					LOG.trace("Aircraft position beacon: {}", aprsLine);
					final boolean isRelayed = false;
					result = new AprsAircraftBeacon(positionMatcher, isRelayed).update(aircraftMatcher);
				}

				final Matcher receiverMatcher = ognReceiverPattern.matcher(comment);
				if (receiverMatcher.matches()) {
					LOG.trace("Receiver position beacon: {}", aprsLine);
					result = new AprsReceiverBeacon(positionMatcher, ReceiverBeaconType.RECEIVER_POSITION)
							.update(receiverMatcher);
				}
			}
		}

		return result;
	}
}