/**
 * Copyright (c) 2014-2018 OGN, All Rights Reserved.
 */

package org.ogn.commons.beacon.impl.aprs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.ogn.commons.utils.AprsUtils.feetsToMetres;

import org.junit.Assert;
import org.junit.Test;
import org.ogn.commons.beacon.AddressType;
import org.ogn.commons.beacon.AircraftBeacon;
import org.ogn.commons.beacon.AircraftType;

public class AprsAircraftBeaconTest {
	String validAprs = "ICA4B0E3A>APRS,qAS,Letzi:/165319h4711.75N\\00802.59E^327/149/A=006498";
	String validRlayedAprs = "ICA4B0E3A>APRS,RELAY*,qAS,Letzi:/165319h4711.75N\\00802.59E^327/149/A=006498";

	AprsLineParser parser = AprsLineParser.get();

	@Test
	public void testEqualsAndHashCode() {
		final String acBeacon = validAprs + " id06DD82AC -474fpm +0.1rot 7.8dB 1e +0.7kHz gps2x3 hear8222";

		final AircraftBeacon b1 = (AircraftBeacon) parser.parse(acBeacon);
		final AircraftBeacon b2 = (AircraftBeacon) parser.parse(acBeacon);

		assertEquals(b1.hashCode(), b2.hashCode());
		assertEquals(b1, b2);
		assertNotSame(b1, b2);
	}

	@Test
	public void test_invalid_token() {
		final AircraftBeacon aircraftBeacon = (AircraftBeacon) parser.parse(validAprs + " notAValidToken");
		Assert.assertNull(aircraftBeacon);
	}

	private void validateBasic(AircraftBeacon aircraftBeacon) {
		Assert.assertEquals(AddressType.FLARM, aircraftBeacon.getAddressType());
		Assert.assertEquals(AircraftType.TOW_PLANE, aircraftBeacon.getAircraftType());
		Assert.assertFalse(aircraftBeacon.isStealth());
		Assert.assertEquals("DDA5BA", aircraftBeacon.getAddress());
		Assert.assertEquals(feetsToMetres(-454) / 60.0, aircraftBeacon.getClimbRate(), 0.01);
		Assert.assertEquals(-1.1, aircraftBeacon.getTurnRate(), 0.01);
		Assert.assertEquals(8.8, aircraftBeacon.getSignalStrength(), 0.01);
		Assert.assertEquals(0, aircraftBeacon.getErrorCount());
		Assert.assertEquals(51.2, aircraftBeacon.getFrequencyOffset(), 0.01);
		Assert.assertEquals("4x5", aircraftBeacon.getGpsStatus());
		Assert.assertEquals(3, aircraftBeacon.getHeardAircraftIds().length);
		Assert.assertEquals("1084", aircraftBeacon.getHeardAircraftIds()[0]);
		Assert.assertEquals("B597", aircraftBeacon.getHeardAircraftIds()[1]);
		Assert.assertEquals("B598", aircraftBeacon.getHeardAircraftIds()[2]);
	}

	@Test
	public void test_basic() {
		final AircraftBeacon aircraftBeacon = (AircraftBeacon) parser
				.parse(validAprs + " id0ADDA5BA -454fpm -1.1rot 8.8dB 0e +51.2kHz gps4x5 hear1084 hearB597 hearB598");
		validateBasic(aircraftBeacon);
		assertFalse(aircraftBeacon.isRelayed());
	}

	@Test
	public void test_RelayedBasic() {
		final AircraftBeacon aircraftBeacon = (AircraftBeacon) parser.parse(
				validRlayedAprs + " id0ADDA5BA -454fpm -1.1rot 8.8dB 0e +51.2kHz gps4x5 hear1084 hearB597 hearB598");
		validateBasic(aircraftBeacon);
		assertTrue(aircraftBeacon.isRelayed());
	}

	@Test
	public void test_stealth() {
		AircraftBeacon aircraftBeacon = (AircraftBeacon) parser
				.parse(validAprs + " id0ADD1234 -454fpm -1.1rot 8.8dB 0e +51.2kHz gps4x5 hear1084 hearB597 hearB598");
		Assert.assertFalse(aircraftBeacon.isStealth());

		aircraftBeacon = (AircraftBeacon) parser
				.parse(validAprs + " id8ADD1234 -454fpm -1.1rot 8.8dB 0e +51.2kHz gps4x5 hear1084 hearB597 hearB598");
		Assert.assertTrue(aircraftBeacon.isStealth());
	}

	@Test
	public void test_relayed() {
		final AircraftBeacon aircraftBeacon = (AircraftBeacon) parser.parse(
				"FLRDDFA6D>APRS,OGN035E35*,qAS,TROCALAN1:/190634h3329.76S/07028.88W'341/068/A=008528 !W12! id06DDFA6D -039fpm +0.5rot 3.2dB 5e -2.2kHz gps3x5");
		Assert.assertTrue(aircraftBeacon.isRelayed());
	}

	@Test
	public void test_v024() {
		final AircraftBeacon aircraftBeacon = (AircraftBeacon) parser
				.parse(validAprs + " id21400EA9 -2454fpm +0.9rot 19.5dB 0e -6.6kHz gps1x1 s6.02 h0A rDF0C56");

		Assert.assertEquals(6.02, aircraftBeacon.getFirmwareVersion(), 0.01);
		Assert.assertEquals(10, aircraftBeacon.getHardwareVersion());
		Assert.assertEquals("DF0C56", aircraftBeacon.getOriginalAddress());
	}

	@Test
	public void test_v024_ogn_tracker() {
		final AircraftBeacon aircraftBeacon = (AircraftBeacon) parser
				.parse(validAprs + " id07353800 +020fpm -14.0rot FL004.43 38.5dB 0e -2.9kHz");

		Assert.assertEquals(4.43, aircraftBeacon.getFlightLevel(), 0.01);
	}

	@Test
	public void test_v025() {
		final AircraftBeacon aircraftBeacon = (AircraftBeacon) parser
				.parse(validAprs + " id06DDE28D +535fpm +3.8rot 11.5dB 0e -1.0kHz gps2x3 s6.01 h0C +7.4dBm");

		Assert.assertEquals(7.4, aircraftBeacon.getERP(), 0.01);
	}

	@Test
	public void test_v026() {
		// from 0.2.6 it is sufficient we have only the ID, climb and turn rate or just the ID
		final AircraftBeacon aircraftBeacon_triple = (AircraftBeacon) parser
				.parse(validAprs + " id093D0930 +000fpm +0.0rot");
		final AircraftBeacon aircraftBeacon_single = (AircraftBeacon) parser.parse(validAprs + " id093D0930");

		Assert.assertNotNull(aircraftBeacon_triple);
		Assert.assertNotNull(aircraftBeacon_single);
	}
}