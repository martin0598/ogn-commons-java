/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.commons.beacon.impl.aprs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.ogn.commons.utils.AprsUtils.feetsToMetres;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.ogn.commons.beacon.AddressType;
import org.ogn.commons.beacon.AircraftBeacon;
import org.ogn.commons.beacon.AircraftType;
import org.ogn.commons.utils.JsonUtils;

public class AprsAircraftBeaconTest {
	String validAprs = "ICA4B0E3A>APRS,qAS,Letzi:/165319h4711.75N\\00802.59E^327/149/A=006498";
	AprsLineParser parser = AprsLineParser.get();

	@Test
	public void testEqualsAndHashCode() {
		String acBeacon = validAprs + " id06DD82AC -474fpm +0.1rot 7.8dB 1e +0.7kHz gps2x3 hear8222";

		AircraftBeacon b1 = (AircraftBeacon) parser.parse(acBeacon);
		AircraftBeacon b2 = (AircraftBeacon) parser.parse(acBeacon);

		assertEquals(b1.hashCode(), b2.hashCode());
		assertEquals(b1, b2);
		assertNotSame(b1, b2);
	}

	@Ignore
	@Test
	public void corrupted_beacon() {
		String acBeacon = "F-PVVA>APRS,qAS,CHALLES:/130435h4534.95N/00559.83E'237/105/A=002818|$#*IL<&z#XLx|";
		AircraftBeacon b1 = (AircraftBeacon) parser.parse(acBeacon);

		assertNotNull(b1);
		System.out.println(JsonUtils.toJson(b1));

		assertEquals(AddressType.UNRECOGNIZED, b1.getAddressType());
	}

	@Ignore
	@Test
	public void corrupted_beacon2() {
		String acBeacon = validAprs + " 33sss3 XX!~@SS id06DDEAAB +020fpm -0.7rot 53.2dB 0e +0.7kHz gps3x5";
		
		AircraftBeacon b1 = (AircraftBeacon) parser.parse(acBeacon);

		assertNotNull(b1);
		assertEquals(acBeacon, b1.getRawPacket());
	}
	
	@Test
    public void test_invalid_token() {
    	AircraftBeacon aircraft_beacon = (AircraftBeacon) parser.parse(validAprs + " notAValidToken");
    	Assert.assertNull(aircraft_beacon);
    }
       
    @Test
    public void test_basic() {
        AircraftBeacon aircraft_beacon = (AircraftBeacon) parser.parse(validAprs + " id0ADDA5BA -454fpm -1.1rot 8.8dB 0e +51.2kHz gps4x5 hear1084 hearB597 hearB598");
        Assert.assertEquals(AddressType.FLARM, aircraft_beacon.getAddressType());
        Assert.assertEquals(AircraftType.TOW_PLANE, aircraft_beacon.getAircraftType());
        Assert.assertFalse(aircraft_beacon.isStealth());
        Assert.assertEquals("DDA5BA", aircraft_beacon.getAddress());
        Assert.assertEquals(feetsToMetres(-454) / 60.0, aircraft_beacon.getClimbRate(), 0.01);
        Assert.assertEquals(-1.1, aircraft_beacon.getTurnRate(), 0.01);
        Assert.assertEquals(8.8, aircraft_beacon.getSignalStrength(), 0.01);
        Assert.assertEquals(0, aircraft_beacon.getErrorCount());
        Assert.assertEquals(51.2, aircraft_beacon.getFrequencyOffset(), 0.01);
        Assert.assertEquals("4x5", aircraft_beacon.getGpsStatus());
        Assert.assertEquals(3, aircraft_beacon.getHeardAircraftIds().length);
        Assert.assertEquals("1084", aircraft_beacon.getHeardAircraftIds()[0]);
        Assert.assertEquals("B597", aircraft_beacon.getHeardAircraftIds()[1]);
        Assert.assertEquals("B598", aircraft_beacon.getHeardAircraftIds()[2]);
    }

    @Test
    public void test_stealth() {
        AircraftBeacon aircraft_beacon = (AircraftBeacon) parser.parse(validAprs + " id0ADD1234 -454fpm -1.1rot 8.8dB 0e +51.2kHz gps4x5 hear1084 hearB597 hearB598");
        Assert.assertFalse(aircraft_beacon.isStealth());

        aircraft_beacon = (AircraftBeacon) parser.parse(validAprs + " id8ADD1234 -454fpm -1.1rot 8.8dB 0e +51.2kHz gps4x5 hear1084 hearB597 hearB598");
        Assert.assertTrue(aircraft_beacon.isStealth());
    }

    @Test
    public void test_v024() {
    	AircraftBeacon aircraft_beacon = (AircraftBeacon) parser.parse(validAprs + " id21400EA9 -2454fpm +0.9rot 19.5dB 0e -6.6kHz gps1x1 s6.02 h0A rDF0C56");

        Assert.assertEquals(6.02, aircraft_beacon.getFirmwareVersion(), 0.01);
        Assert.assertEquals(10, aircraft_beacon.getHardwareVersion());
        Assert.assertEquals("DF0C56", aircraft_beacon.getOriginalAddress());
    }

    @Test
    public void test_v024_ogn_tracker() {
    	AircraftBeacon aircraft_beacon = (AircraftBeacon) parser.parse(validAprs + " id07353800 +020fpm -14.0rot FL004.43 38.5dB 0e -2.9kHz");

        Assert.assertEquals(4.43, aircraft_beacon.getFlightLevel(), 0.01);
    }

    @Test
    public void test_v025() {
    	AircraftBeacon aircraft_beacon = (AircraftBeacon) parser.parse(validAprs + " id06DDE28D +535fpm +3.8rot 11.5dB 0e -1.0kHz gps2x3 s6.01 h0C +7.4dBm");

        Assert.assertEquals(7.4, aircraft_beacon.getERP(), 0.01);
    }

    @Test
    public void test_v026() {
        // from 0.2.6 it is sufficent we have only the ID, climb and turn rate or just the ID
        AircraftBeacon aircraft_beacon_triple = (AircraftBeacon) parser.parse(validAprs + " id093D0930 +000fpm +0.0rot");
        AircraftBeacon aircraft_beacon_single = (AircraftBeacon) parser.parse(validAprs + " id093D0930");

        Assert.assertNotNull(aircraft_beacon_triple);
        Assert.assertNotNull(aircraft_beacon_single);
    }
}