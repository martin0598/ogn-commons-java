/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.commons.aprs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.ogn.commons.beacon.AircraftBeacon;
import org.ogn.commons.beacon.OgnBeacon;
import org.ogn.commons.beacon.ReceiverBeacon;
import org.ogn.commons.beacon.impl.aprs.AprsLineParser;
import org.ogn.commons.utils.JsonUtils;

public class AprsLineParserTest {

	@Test
	public void test() {

		// "SKRZYCZNE>APRS,TCPIP*,qAC,GLIDERN1:/165321h4941.08NI01901.86E&/A=001345";
		final String acBeacon1 = "PH-844>APRS,qAS,Veendam:/102529h5244.42N/00632.07E'089/077/A=000876 id06DD82AC -474fpm +0.1rot 7.8dB 1e +0.7kHz gps2x3 hear8222";
		final String brBeacon1 = "EBZW>APRS,TCPIP*,qAC,GLIDERN1:/102546h5100.86NI00531.43E&/A=000298 CPU:0.9 RAM:968.2/1056.5MB NTP:1.5ms/-20.0ppm RF:+127-2.9ppm/+4.3dB";

		final AprsLineParser parser = AprsLineParser.get();

		final AprsLineParser parser2 = AprsLineParser.get();

		// parser is expected to be a singleton
		assertSame(parser, parser2);

		OgnBeacon beacon = parser.parse(acBeacon1);

		assertNotNull(beacon);
		assertTrue(beacon instanceof AircraftBeacon);

		// ignore aircraft beacons
		beacon = parser.parse(acBeacon1, false, true);
		assertNull(beacon);

		beacon = parser.parse(brBeacon1);

		assertNotNull(beacon);
		assertTrue(beacon instanceof ReceiverBeacon);

		// ignore receiver beacons
		beacon = parser.parse(brBeacon1, true, false);
		assertNull(beacon);

		String acBeacon2 = "OGNFFDE83>APRS,RELAY*,qAS,Aue:/053527h9140.49S/06801.07Ez073/173/A=071668 !W15! id03FFDE83 -8592fpm -0.3rot FL708.76 gps32x47";
		beacon = parser.parse(acBeacon2);
		assertNotNull(beacon);

		acBeacon2 = "FLRDDDDD6>APRS,qAS,Aue:/053615h5138.02N/01015.55E'000/000/A=000266 !W30! id06DDDDD6 -157fpm +0.0rot 28.8dB 0e +4.9kHz gps18x21 s6.09 h0A";

		beacon = parser.parse(acBeacon2);
		assertEquals(6.09, ((AircraftBeacon) beacon).getFirmwareVersion(), 0.01);
	}

	@Test
	public void test2() {
		final String brBeacon = "FLRDDDBBC>APRS,qAS,UKGRF:/140044h5227.15N/00108.34E'286/023/A=001200 id06DDDBBC +653fpm +0.7rot 9.0dB 0e +1.8kHz gps2x3 hearE61E";

		final String recBeacon = "EBZW>APRS,TCPIP*,qAC,GLIDERN1:/102546h5100.86NI00531.43E&/A=000298 v1.0.4 CPU:0.9 RAM:968.2/1056.5MB NTP:1.5ms/-20.0ppm RF:+127-2.9ppm/+4.3dB";

		final AprsLineParser parser = AprsLineParser.get();

		OgnBeacon beacon = parser.parse(brBeacon);

		assertNotNull(beacon);
		assertTrue(beacon instanceof AircraftBeacon);

		beacon = parser.parse(recBeacon);

		assertNotNull(beacon);
		assertTrue(beacon instanceof ReceiverBeacon);
	}

	@Test
	public void test_0_2_6_RecBeaconFormat() {
		final String rawRecPosBeacon = "SKRZYCZNE>APRS,TCPIP*,qAC,GLIDERN1:/165321h4941.08NI01901.86E&/A=001345";
		final AprsLineParser parser = AprsLineParser.get();
		final OgnBeacon beacon1 = parser.parse(rawRecPosBeacon);
		assertNotNull(beacon1);
		assertTrue(beacon1 instanceof ReceiverBeacon);
		System.out.println(JsonUtils.toJson(beacon1));

		final String rawRecMetricsBeacon = "SKRZYCZNE>APRS,TCPIP*,qAC,GLIDERN1:>165321h v0.2.6.RPI-GPU CPU:0.4 RAM:693.9/970.5MB "
				+ "NTP:0.6ms/+0.4ppm +52.6C 2/2Acfts[1h] RF:+48+0.6ppm/+4.23dB/+12.7dB@10km[28932]/+22.2dB@10km[7/14]";

		final OgnBeacon beacon2 = parser.parse(rawRecMetricsBeacon);
		assertNotNull(beacon2);
		assertTrue(beacon2 instanceof ReceiverBeacon);
		System.out.println(JsonUtils.toJson(beacon2));
	}

}