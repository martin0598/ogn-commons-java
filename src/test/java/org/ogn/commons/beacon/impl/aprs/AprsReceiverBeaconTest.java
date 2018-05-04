/**
 * Copyright (c) 2014-2018 OGN, All Rights Reserved.
 */

package org.ogn.commons.beacon.impl.aprs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.ogn.commons.beacon.ReceiverBeacon;

public class AprsReceiverBeaconTest {
	String validAprs = "Ulrichamn>APRS,TCPIP*,qAC,GLIDERN1:/085616h5747.30NI01324.77E&/A=001322";
	AprsLineParser parser = AprsLineParser.get();

	@Test
	public void testEqualsAndHashCode() {
		final String recBeacon = "EBZW>APRS,TCPIP*,qAC,GLIDERN1:/102546h5100.86NI00531.43E&/A=000298 CPU:0.9 RAM:968.2/1056.5MB NTP:1.5ms/-20.0ppm RF:+127-2.9ppm/+4.3dB";

		final ReceiverBeacon b1 = (ReceiverBeacon) parser.parse(recBeacon);
		final ReceiverBeacon b2 = (ReceiverBeacon) parser.parse(recBeacon);

		assertEquals(b1.hashCode(), b2.hashCode());
		assertEquals(b1, b2);
		assertNotSame(b1, b2);
	}

	@Ignore
	@Test
	public void corrupted_beacon() {
		final String recBeacon = "incorrect > ! Cdd blah blah blah xxx beacon $$ format";

		final ReceiverBeacon b1 = (ReceiverBeacon) parser.parse(recBeacon);

		assertEquals(recBeacon, b1.getRawPacket());

		// still, the object should be created (although its attributes will not
		// be initialized)
		assertNotNull(b1);
	}

	@Test
	public void test_fail_validation() {
		final ReceiverBeacon receiver_beacon = (ReceiverBeacon) parser.parse(validAprs + " notAValidToken");
		Assert.assertNull(receiver_beacon);
	}

	@Test
	public void test_v021() {
		final ReceiverBeacon receiver_beacon = (ReceiverBeacon) parser
				.parse(validAprs + " v0.2.1 CPU:0.8 RAM:25.6/458.9MB NTP:0.1ms/+2.3ppm +51.9C RF:+26-1.4ppm/-0.25dB");
		Assert.assertEquals("0.2.1", receiver_beacon.getVersion());
		Assert.assertEquals(0.8, receiver_beacon.getCpuLoad(), 0.01);
		Assert.assertEquals(25.6, receiver_beacon.getFreeRam(), 0.01);
		Assert.assertEquals(458.9, receiver_beacon.getTotalRam(), 0.01);
		Assert.assertEquals(0.1, receiver_beacon.getNtpError(), 0.01);
		Assert.assertEquals(2.3, receiver_beacon.getRtCrystalCorrection(), 0.01);
		Assert.assertEquals(51.9, receiver_beacon.getCpuTemp(), 0.01);

		Assert.assertEquals(26, receiver_beacon.getRecCrystalCorrection());
		Assert.assertEquals(-1.4, receiver_beacon.getRecCrystalCorrectionFine(), 0.01);
		Assert.assertEquals(-0.25, receiver_beacon.getRecInputNoise(), 0.01);
	}

	@Test
	public void test_v022() {
		final ReceiverBeacon receiver_beacon = (ReceiverBeacon) parser
				.parse(validAprs + " v0.2.2.x86 CPU:0.5 RAM:669.9/887.7MB NTP:1.0ms/+6.2ppm +52.0C RF:+0.06dB");
		Assert.assertEquals("x86", receiver_beacon.getPlatform());
	}

	@Ignore
	@Test
	public void test_v025() {
		ReceiverBeacon receiver_beacon = (ReceiverBeacon) parser.parse(validAprs
				+ " v0.2.5.RPI-GPU CPU:0.8 RAM:287.3/458.7MB NTP:1.0ms/-6.4ppm 5.016V 0.534A +51.9C RF:+55+0.4ppm/-0.67dB/+10.8dB@10km[57282]");
		// Assert.assertEquals(5.016, receiver_beacon.getVoltage(), 0.01);
		// Assert.assertEquals(0.534, receiver_beacon.getAmperage(), 0.01);
		// Assert.assertEquals(10.8, receiver_beacon.getSendersSignal(), 0.01);
		// Assert.assertEquals(57282, receiver_beacon.getSendersMessages();

		receiver_beacon = (ReceiverBeacon) parser.parse(validAprs
				+ " v0.2.5.ARM CPU:0.4 RAM:638.0/970.5MB NTP:0.2ms/-1.1ppm +65.5C 14/16Acfts[1h] RF:+45+0.0ppm/+3.88dB/+24.0dB@10km[143717]/+26.7dB@10km[68/135]");
		// Assert.assertEquals(14, receiver_beacon.getSendersVisible());
		// Assert.assertEquals(16, receiver_beacon.getSendersTotal());
		// Assert.assertEquals(24.0, receiver_beacon.getSendersSignal());
		// Assert.assertEquals(143717, receiver_beacon.getSendersMessages());
		// Assert.assertEquals(26.7, receiver_beacon.getGoodSendersSignal());
		// Assert.assertEquals(68, receiver_beacon.getGoodSenders());
		// Assert.assertEquals(135, receiver_beacon.getGoodAndBadSenders());
	}

	@Test
	public void test_v026() {
		final ReceiverBeacon receiver_beacon = (ReceiverBeacon) parser.parse(validAprs);
		Assert.assertNotNull(receiver_beacon);

		// Test default values
		Assert.assertEquals(0, receiver_beacon.getRecCrystalCorrection(), 0.01);
		Assert.assertEquals(0, receiver_beacon.getRecCrystalCorrectionFine(), 0.01);
	}

}