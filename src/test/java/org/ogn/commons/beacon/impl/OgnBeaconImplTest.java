/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.commons.beacon.impl;

import static org.ogn.commons.utils.AprsUtils.dmsToDeg;
import static org.ogn.commons.utils.AprsUtils.feetsToMetres;
import static org.ogn.commons.utils.AprsUtils.kntToKmh;
import static org.ogn.commons.utils.AprsUtils.toUtcTimestamp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;
import org.ogn.commons.beacon.AddressType;
import org.ogn.commons.beacon.AircraftBeacon;
import org.ogn.commons.beacon.AircraftType;
import org.ogn.commons.beacon.OgnBeacon;
import org.ogn.commons.beacon.ReceiverBeacon;
import org.ogn.commons.beacon.impl.aprs.AprsLineParser;

public class OgnBeaconImplTest {
	AprsLineParser parser = AprsLineParser.get();
	
	@Test
	public void test_ReceiverBeacon() {
		// APRS POSITION
		ReceiverBeacon beacon1 = (ReceiverBeacon)parser.parse("VITACURA2>APRS,TCPIP*,qAC,GLIDERN3:/042136h3322.81SI07034.95W&/A=002345 v0.2.5.ARM CPU:0.3 RAM:695.0/970.5MB NTP:0.6ms/-5.7ppm +51.5C RF:+0-0.0ppm/+1.32dB");
		Assert.assertEquals("VITACURA2", beacon1.getId());
		Assert.assertEquals("GLIDERN3", beacon1.getServerName());
		Assert.assertEquals(toUtcTimestamp(4, 21, 36), beacon1.getTimestamp());
		Assert.assertEquals(dmsToDeg(33.2281) * -1.0, beacon1.getLat(), 0.01);
		Assert.assertEquals(dmsToDeg(70.3495) * -1.0, beacon1.getLon(), 0.01);
		Assert.assertEquals(kntToKmh(0), beacon1.getGroundSpeed(), 0.1);	// default value
		Assert.assertEquals(feetsToMetres(2345), beacon1.getAlt(), 0.01);
		Assert.assertEquals("0.2.5", beacon1.getVersion());
		Assert.assertEquals("ARM", beacon1.getPlatform());
		Assert.assertEquals(0.3, beacon1.getCpuLoad(), 0.01);
		Assert.assertEquals(695.0, beacon1.getFreeRam(), 0.01);
		Assert.assertEquals(970.5, beacon1.getTotalRam(), 0.01);
		Assert.assertEquals(51.5, beacon1.getCpuTemp(), 0.01);
		Assert.assertEquals(0, beacon1.getRecCrystalCorrection());
		Assert.assertEquals(0.0, beacon1.getRecCrystalCorrectionFine(), 0.01);
		Assert.assertEquals(1.32, beacon1.getRecInputNoise(), 0.01);
				
		// APRS STATUS
		ReceiverBeacon beacon2 = (ReceiverBeacon)parser.parse("VITACURA2>APRS,TCPIP*,qAC,GLIDERN3:>042136h v0.2.5.ARM CPU:0.3 RAM:695.0/970.5MB NTP:0.6ms/-5.7ppm +52.1C 0/0Acfts[1h] RF:+0-0.0ppm/+1.32dB/+2.1dB@10km[193897]/+9.0dB@10km[10/20]");
		Assert.assertEquals(52.1, beacon2.getCpuTemp(), 0.01);
	}
	
	@Test
	public void test_AircraftBeacon() {
		AircraftBeacon beacon = (AircraftBeacon)parser.parse("ZK-GSC>APRS,qAS,Omarama:/165202h4429.25S/16959.33E'/A=001407 id05C821EA +020fpm +0.0rot 16.8dB 0e -3.1kHz gps1x3 hear1084 hearB597 hearB598");
		Assert.assertEquals("ZK-GSC", beacon.getId());
		Assert.assertEquals("Omarama", beacon.getReceiverName());
		Assert.assertEquals(toUtcTimestamp(16, 52, 02), beacon.getTimestamp());
		Assert.assertEquals(dmsToDeg(44.2925) * -1.0, beacon.getLat(), 0.01);
		Assert.assertEquals(dmsToDeg(169.5933) *  1.0, beacon.getLon(), 0.01);
		Assert.assertEquals(kntToKmh(0), beacon.getGroundSpeed(), 0.1);	// default value
		Assert.assertEquals(feetsToMetres(1407), beacon.getAlt(), 0.01);
		
		Assert.assertEquals(AddressType.ICAO, beacon.getAddressType());
		Assert.assertEquals(AircraftType.GLIDER, beacon.getAircraftType());
		Assert.assertFalse(beacon.isStealth());
		Assert.assertEquals("C821EA", beacon.getAddress());
		Assert.assertEquals(feetsToMetres(20) / 60.0, beacon.getClimbRate(), 0.01);
		Assert.assertEquals(0.0, beacon.getTurnRate(), 0.01);
		Assert.assertEquals(16.8, beacon.getSignalStrength(), 0.01);
		Assert.assertEquals(0, beacon.getErrorCount());
		Assert.assertEquals(-3.1, beacon.getFrequencyOffset(), 0.01);
		Assert.assertEquals(3, beacon.getHeardAircraftIds().length);
		Assert.assertEquals("1084",  beacon.getHeardAircraftIds()[0]);
		Assert.assertEquals("B597",  beacon.getHeardAircraftIds()[1]);
		Assert.assertEquals("B598",  beacon.getHeardAircraftIds()[2]);
	}
	
	@Test
	public void test_ValidBeacons() {
		Path path = Paths.get("src/test/java/org/ogn/commons/beacon/impl/valid_beacons.txt");
		File f = path.toFile();
		
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			for(String line; (line = br.readLine()) != null; ) {
		        OgnBeacon beacon = parser.parse(line);
		        if (!line.startsWith("#"))
		        	Assert.assertNotNull(beacon);
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
