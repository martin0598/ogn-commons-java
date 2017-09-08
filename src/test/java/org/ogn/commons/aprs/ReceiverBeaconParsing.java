package org.ogn.commons.aprs;

import org.junit.Assert;
import org.junit.Test;
import org.ogn.commons.beacon.ReceiverBeacon;
import org.ogn.commons.beacon.impl.aprs.AprsLineParser;

public class ReceiverBeaconParsing {
	String validAprs = "Ulrichamn>APRS,TCPIP*,qAC,GLIDERN1:/085616h5747.30NI01324.77E&/A=001322";
	AprsLineParser parser = AprsLineParser.get();
	
	@Test
    public void test_fail_validation() {
		ReceiverBeacon receiver_beacon = (ReceiverBeacon) parser.parse(validAprs + " notAValidToken");
		Assert.assertNull(receiver_beacon); 
	}
	
    @Test
    public void test_v021() {
        ReceiverBeacon receiver_beacon = (ReceiverBeacon) parser.parse(validAprs + " v0.2.1 CPU:0.8 RAM:25.6/458.9MB NTP:0.0ms/+0.0ppm +51.9C RF:+26-1.4ppm/-0.25dB");
        Assert.assertEquals(receiver_beacon.getRecCrystalCorrection(), 26);
        Assert.assertEquals(receiver_beacon.getRecCrystalCorrectionFine(), -1.4, 0.01);
        Assert.assertEquals(receiver_beacon.getRecInputNoise(), -0.25, 0.01);
    }

    @Test
    public void test_v022() {
    	ReceiverBeacon receiver_beacon = (ReceiverBeacon) parser.parse(validAprs + " v0.2.2.x86 CPU:0.5 RAM:669.9/887.7MB NTP:1.0ms/+6.2ppm +52.0C RF:+0.06dB");
    	Assert.assertEquals(receiver_beacon.getVersion(), "0.2.2");
        Assert.assertEquals(receiver_beacon.getPlatform(), "x86");
        Assert.assertEquals(receiver_beacon.getCpuLoad(), 0.5, 0.01);
        Assert.assertEquals(receiver_beacon.getCpuTemp(), 52, 0.01);
        Assert.assertEquals(receiver_beacon.getFreeRam(), 669.9, 0.01);
        Assert.assertEquals(receiver_beacon.getTotalRam(), 887.7, 0.01);
        Assert.assertEquals(receiver_beacon.getNtpError(), 1.0, 0.01);
        Assert.assertEquals(receiver_beacon.getRecInputNoise(), 0.06, 0.01);

        // parts not available set to None
        Assert.assertEquals(receiver_beacon.getRecCrystalCorrection(), 0, 0.01);
        Assert.assertEquals(receiver_beacon.getRecCrystalCorrectionFine(), 0, 0.01);
    }

    @Test
    public void test_v025() {
		ReceiverBeacon receiver_beacon = (ReceiverBeacon) parser.parse(validAprs + " v0.2.5.RPI-GPU CPU:0.8 RAM:287.3/458.7MB NTP:1.0ms/-6.4ppm 5.016V 0.534A +51.9C RF:+55+0.4ppm/-0.67dB/+10.8dB@10km[57282]");
        //self.assertEqual(receiver_beacon['voltage'], 5.016)
        //self.assertEqual(receiver_beacon['amperage'], 0.534)
        //self.assertEqual(receiver_beacon['senders_signal'], 10.8)
        //self.assertEqual(receiver_beacon['senders_messages'], 57282)

        receiver_beacon = (ReceiverBeacon) parser.parse(validAprs + " v0.2.5.ARM CPU:0.4 RAM:638.0/970.5MB NTP:0.2ms/-1.1ppm +65.5C 14/16Acfts[1h] RF:+45+0.0ppm/+3.88dB/+24.0dB@10km[143717]/+26.7dB@10km[68/135]");
        //self.assertEqual(receiver_beacon['senders_visible'], 14)
        //self.assertEqual(receiver_beacon['senders_total'], 16)
        //self.assertEqual(receiver_beacon['senders_signal'], 24.0)
        //self.assertEqual(receiver_beacon['senders_messages'], 143717)
        //self.assertEqual(receiver_beacon['good_senders_signal'], 26.7)
        //self.assertEqual(receiver_beacon['good_senders'], 68)
        //self.assertEqual(receiver_beacon['good_and_bad_senders'], 135)
    }

    @Test
    public void test_v026() {
		ReceiverBeacon receiver_beacon = (ReceiverBeacon) parser.parse(validAprs);
		Assert.assertNotNull(receiver_beacon);
    }
}