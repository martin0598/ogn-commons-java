package org.ogn.commons.aprs;

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

import org.junit.Assert;
import org.junit.Test;
import org.ogn.commons.beacon.AddressType;
import org.ogn.commons.beacon.AircraftBeacon;
import org.ogn.commons.beacon.AircraftType;
import org.ogn.commons.beacon.OgnBeacon;
import org.ogn.commons.beacon.ReceiverBeacon;
import org.ogn.commons.beacon.impl.aprs.AprsLineParser;

public class OgnBeaconParsing {
	AprsLineParser parser = AprsLineParser.get();
	
	@Test
	public void test_ReceiverBeacon() {
		// APRS POSITION
		ReceiverBeacon beacon1 = (ReceiverBeacon)parser.parse("VITACURA2>APRS,TCPIP*,qAC,GLIDERN3:/042136h3322.81SI07034.95W&/A=002345 v0.2.5.ARM CPU:0.3 RAM:695.0/970.5MB NTP:0.6ms/-5.7ppm +51.5C RF:+0-0.0ppm/+1.32dB");
		Assert.assertEquals(beacon1.getId(), "VITACURA2");
		Assert.assertEquals(beacon1.getServerName(), "GLIDERN3");
		Assert.assertEquals(beacon1.getTimestamp(), toUtcTimestamp(4, 21, 36));
		Assert.assertEquals(beacon1.getLat(), dmsToDeg(33.2281) * -1.0, 0.01);
		Assert.assertEquals(beacon1.getLon(), dmsToDeg(70.3495) * -1.0, 0.01);
		Assert.assertEquals(beacon1.getGroundSpeed(), kntToKmh(0), 0.1);	// default value
		Assert.assertEquals(beacon1.getAlt(), feetsToMetres(2345), 0.01);
		Assert.assertEquals(beacon1.getVersion(), "0.2.5");
		Assert.assertEquals(beacon1.getPlatform(), "ARM");
		Assert.assertEquals(beacon1.getCpuLoad(), 0.3, 0.01);
		Assert.assertEquals(beacon1.getFreeRam(), 695.0, 0.01);
		Assert.assertEquals(beacon1.getTotalRam(), 970.5, 0.01);
		Assert.assertEquals(beacon1.getCpuTemp(), 51.5, 0.01);
		Assert.assertEquals(beacon1.getRecCrystalCorrection(), 0);
		Assert.assertEquals(beacon1.getRecCrystalCorrectionFine(), 0.0, 0.01);
		Assert.assertEquals(beacon1.getRecInputNoise(), 1.32, 0.01);
				
		// APRS STATUS
		ReceiverBeacon beacon2 = (ReceiverBeacon)parser.parse("VITACURA2>APRS,TCPIP*,qAC,GLIDERN3:>042136h v0.2.5.ARM CPU:0.3 RAM:695.0/970.5MB NTP:0.6ms/-5.7ppm +52.1C 0/0Acfts[1h] RF:+0-0.0ppm/+1.32dB/+2.1dB@10km[193897]/+9.0dB@10km[10/20]");
		Assert.assertEquals(beacon2.getCpuTemp(), 52.1, 0.01);
	}
	
	@Test
	public void test_AircraftBeacon() {
		AircraftBeacon beacon = (AircraftBeacon)parser.parse("ZK-GSC>APRS,qAS,Omarama:/165202h4429.25S/16959.33E'/A=001407 id05C821EA +020fpm +0.0rot 16.8dB 0e -3.1kHz gps1x3");// hear1084 hearB597 hearB598");
		Assert.assertEquals(beacon.getId(), "ZK-GSC");
		Assert.assertEquals(beacon.getReceiverName(), "Omarama");
		Assert.assertEquals(beacon.getTimestamp(), toUtcTimestamp(16, 52, 02));
		Assert.assertEquals(beacon.getLat(), dmsToDeg(44.2925) * -1.0, 0.01);
		Assert.assertEquals(beacon.getLon(), dmsToDeg(169.5933) *  1.0, 0.01);
		Assert.assertEquals(beacon.getGroundSpeed(), kntToKmh(0), 0.1);	// default value
		Assert.assertEquals(beacon.getAlt(), feetsToMetres(1407), 0.01);
		
		Assert.assertEquals(beacon.getAddressType(), AddressType.forValue(1));
		Assert.assertEquals(beacon.getAircraftType(), AircraftType.forValue(1));
		Assert.assertEquals(beacon.isStealth(), false);
		Assert.assertEquals(beacon.getAddress(), "C821EA");
		Assert.assertEquals(beacon.getClimbRate(), feetsToMetres(20) / 60.0, 0.01);
		Assert.assertEquals(beacon.getTurnRate(), 0.0, 0.01);
		Assert.assertEquals(beacon.getSignalStrength(), 16.8, 0.01);
		Assert.assertEquals(beacon.getErrorCount(), 0);
		Assert.assertEquals(beacon.getFrequencyOffset(), -3.1, 0.01);
	}
	
	@Test
	public void test_ValidBeacons() {
		URL path = OgnBeaconParsing.class.getResource("valid_beacons.txt");
		File f = null;
		try {
			f = new File(path.toURI());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			for(String line; (line = br.readLine()) != null; ) {
		        OgnBeacon beacon = parser.parse(line);
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