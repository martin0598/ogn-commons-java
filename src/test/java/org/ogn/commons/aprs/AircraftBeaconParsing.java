package org.ogn.commons.aprs;

import static org.ogn.commons.utils.AprsUtils.feetsToMetres;

import org.junit.Assert;
import org.junit.Test;
import org.ogn.commons.beacon.AircraftBeacon;
import org.ogn.commons.beacon.impl.aprs.AprsLineParser;

public class AircraftBeaconParsing {
	String validAprs = "ICA4B0E3A>APRS,qAS,Letzi:/165319h4711.75N\\00802.59E^327/149/A=006498";
	AprsLineParser parser = AprsLineParser.get();
	

    @Test
    public void test_invalid_token() {
    	AircraftBeacon aircraft_beacon = (AircraftBeacon) parser.parse(validAprs + " notAValidToken");
    	Assert.assertNull(aircraft_beacon);
    }
       
    @Test
    public void test_basic() {
        AircraftBeacon aircraft_beacon = (AircraftBeacon) parser.parse(validAprs + " id0ADDA5BA -454fpm -1.1rot 8.8dB 0e +51.2kHz gps4x5 hear1084 hearB597 hearB598");
        Assert.assertFalse(aircraft_beacon.isStealth());
        Assert.assertEquals(aircraft_beacon.getAddress(), "DDA5BA");
        Assert.assertEquals(aircraft_beacon.getClimbRate(), feetsToMetres(-454) / 60.0, 0.01);
        Assert.assertEquals(aircraft_beacon.getTurnRate(), -1.1, 0.01);
        //Assert.assertEquals(aircraft_beacon['signal_quality'], 8.8);
        Assert.assertEquals(aircraft_beacon.getErrorCount(), 0);
        Assert.assertEquals(aircraft_beacon.getFrequencyOffset(), 51.2, 0.01);
        Assert.assertEquals(aircraft_beacon.getGpsStatus(), "4x5");
    }

    @Test
    public void test_hear() {
        AircraftBeacon aircraft_beacon = (AircraftBeacon) parser.parse(validAprs + " id0ADDA5BA -454fpm -1.1rot 8.8dB 0e +51.2kHz gps4x5 hear1084 hearB597 hearB598");
        String[] proximity = aircraft_beacon.getHeardAircraftIds();
        Assert.assertEquals(proximity.length, 3);
        Assert.assertEquals(proximity[0], "1084");
        Assert.assertEquals(proximity[1], "B597");
        Assert.assertEquals(proximity[2], "B598");
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

        Assert.assertEquals(aircraft_beacon.getFirmwareVersion(), 6.02, 0.01);
        Assert.assertEquals(aircraft_beacon.getHardwareVersion(), 10);
        Assert.assertEquals(aircraft_beacon.getOriginalAddress(), "DF0C56");
    }

    @Test
    public void test_v024_ogn_tracker() {
    	AircraftBeacon aircraft_beacon = (AircraftBeacon) parser.parse(validAprs + " id07353800 +020fpm -14.0rot FL004.43 38.5dB 0e -2.9kHz");

        //self.assertEqual(aircraft_beacon['flightlevel'], 4.43)
    }

    @Test
    public void test_v025() {
    	AircraftBeacon aircraft_beacon = (AircraftBeacon) parser.parse(validAprs + " id06DDE28D +535fpm +3.8rot 11.5dB 0e -1.0kHz gps2x3 s6.01 h0C +7.4dBm");

        //self.assertEqual(aircraft_beacon['signal_power'], 7.4)
    }

    @Test
    public void test_v026() {
    	AprsLineParser parser = AprsLineParser.get();
    	
        // from 0.2.6 it is sufficent we have only the ID, climb and turn rate or just the ID
        AircraftBeacon aircraft_beacon_triple = (AircraftBeacon) parser.parse(validAprs + " id093D0930 +000fpm +0.0rot");
        AircraftBeacon aircraft_beacon_single = (AircraftBeacon) parser.parse(validAprs + " id093D0930");

        Assert.assertNotNull(aircraft_beacon_triple);
        Assert.assertNotNull(aircraft_beacon_single);
    }
}