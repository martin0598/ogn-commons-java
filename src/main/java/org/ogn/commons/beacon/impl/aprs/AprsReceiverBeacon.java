/**
 * Copyright (c) 2014-2015 OGN, All Rights Reserved.
 */

package org.ogn.commons.beacon.impl.aprs;

import java.io.Serializable;

import org.ogn.commons.beacon.OgnBeacon;
import org.ogn.commons.beacon.ReceiverBeacon;
import org.ogn.commons.beacon.ReceiverBeaconType;
import org.ogn.commons.beacon.impl.OgnBeaconImpl;
import org.ogn.commons.utils.Version;

import com.google.code.regexp.Matcher;

public class AprsReceiverBeacon extends OgnBeaconImpl implements ReceiverBeacon, Serializable {

	private static final long serialVersionUID = 2851952572220758613L;

	protected ReceiverBeaconType type;

	/**
	 * name of the server receiving the packet
	 */
	protected String srvName;

	/**
	 * receiver's version
	 */
	protected String version;

	/**
	 * hardware platform on which the receiver runs
	 */
	protected String platform;

	/**
	 * CPU load (as indicated by the linux 'uptime' command)
	 */
	protected float cpuLoad;

	/**
	 * CPU temperature of the board (in deg C) or <code>Float.NaN</code> if not set
	 */
	protected float cpuTemp = Float.NaN;

	/**
	 * total size of RAM available in the system (in MB)
	 */
	protected float totalRam;

	/**
	 * size of free RAM (in MB)
	 */
	protected float freeRam;

	/**
	 * estimated NTP error (in ms)
	 */
	protected float ntpError;

	/**
	 * real time crystal correction(set in the configuration) (in ppm)
	 */
	protected float rtCrystalCorrection;

	/**
	 * receiver (DVB-T stick's) crystal correction (in ppm)
	 */
	protected int recCrystalCorrection;

	/**
	 * receiver correction measured taking GSM for a reference (in ppm)
	 */
	protected float recCrystalCorrectionFine;

	/**
	 * receiver's input noise (in dB)
	 */
	protected float recInputNoise;

	@Override
	public float getCpuLoad() {
		return cpuLoad;
	}

	@Override
	public float getCpuTemp() {
		return cpuTemp;
	}

	@Override
	public float getFreeRam() {
		return freeRam;
	}

	@Override
	public float getTotalRam() {
		return totalRam;
	}

	@Override
	public float getNtpError() {
		return ntpError;
	}

	@Override
	public float getRtCrystalCorrection() {
		return rtCrystalCorrection;
	}

	@Override
	public int getRecCrystalCorrection() {
		return recCrystalCorrection;
	}

	@Override
	public float getRecCrystalCorrectionFine() {
		return recCrystalCorrectionFine;
	}

	@Override
	public float getRecAbsCorrection() {
		return recCrystalCorrection + recCrystalCorrectionFine;
	}

	@Override
	public float getRecInputNoise() {
		return recInputNoise;
	}

	@Override
	public String getServerName() {
		return srvName;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getPlatform() {
		return platform;
	}

	@Override
	public int getNumericVersion() {
		return version == null ? 0 : Version.fromString(version);
	}

	// private default constructor
	// required by jackson (as it uses reflection)
	@SuppressWarnings("unused")
	private AprsReceiverBeacon() {
		// no default implementation
	}

	public AprsReceiverBeacon(Matcher matcher, ReceiverBeaconType type) {
		super(matcher);
		this.srvName = matcher.group("receiver");
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Float.floatToIntBits(cpuLoad);
		result = prime * result + Float.floatToIntBits(cpuTemp);
		result = prime * result + Float.floatToIntBits(freeRam);
		result = prime * result + Float.floatToIntBits(ntpError);
		result = prime * result + recCrystalCorrection;
		result = prime * result + Float.floatToIntBits(recCrystalCorrectionFine);
		result = prime * result + Float.floatToIntBits(recInputNoise);
		result = prime * result + Float.floatToIntBits(rtCrystalCorrection);
		result = prime * result + ((srvName == null) ? 0 : srvName.hashCode());
		result = prime * result + Float.floatToIntBits(totalRam);
		return result;
	}

	@Override
	public ReceiverBeaconType getReceiverBeaconType() {
		return this.type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AprsReceiverBeacon other = (AprsReceiverBeacon) obj;
		if (Float.floatToIntBits(cpuLoad) != Float.floatToIntBits(other.cpuLoad))
			return false;
		if (Float.floatToIntBits(cpuTemp) != Float.floatToIntBits(other.cpuTemp))
			return false;
		if (Float.floatToIntBits(freeRam) != Float.floatToIntBits(other.freeRam))
			return false;
		if (Float.floatToIntBits(ntpError) != Float.floatToIntBits(other.ntpError))
			return false;
		if (recCrystalCorrection != other.recCrystalCorrection)
			return false;
		if (Float.floatToIntBits(recCrystalCorrectionFine) != Float.floatToIntBits(other.recCrystalCorrectionFine))
			return false;
		if (Float.floatToIntBits(recInputNoise) != Float.floatToIntBits(other.recInputNoise))
			return false;
		if (Float.floatToIntBits(rtCrystalCorrection) != Float.floatToIntBits(other.rtCrystalCorrection))
			return false;
		if (srvName == null) {
			if (other.srvName != null)
				return false;
		} else if (!srvName.equals(other.srvName))
			return false;
		if (Float.floatToIntBits(totalRam) != Float.floatToIntBits(other.totalRam))
			return false;
		return true;
	}

	public OgnBeacon update(Matcher receiverMatcher) {
		this.version = receiverMatcher.group("version");
		this.platform = receiverMatcher.group("platform");
		this.cpuLoad = Float.parseFloat(receiverMatcher.group("cpuLoad"));
		this.freeRam = Float.parseFloat(receiverMatcher.group("ramFree"));
		this.totalRam = Float.parseFloat(receiverMatcher.group("ramTotal"));

		this.ntpError = receiverMatcher.group("ntpOffset") == null ? 0
				: Float.parseFloat(receiverMatcher.group("ntpOffset"));
		this.rtCrystalCorrection = receiverMatcher.group("ntpCorrection") == null ? 0
				: Float.parseFloat(receiverMatcher.group("ntpCorrection"));
		// receiverMatcher.group("voltage");
		// receiverMatcher.group("amperage");
		this.cpuTemp = receiverMatcher.group("cpuTemperature") == null ? 0
				: Float.parseFloat(receiverMatcher.group("cpuTemperature"));
		// receiverMatcher.group("visibleSenders");
		// receiverMatcher.group("senders");
		this.recCrystalCorrection = receiverMatcher.group("rfCorrectionManual") == null ? 0
				: Integer.parseInt(receiverMatcher.group("rfCorrectionManual"));
		this.recCrystalCorrectionFine = receiverMatcher.group("rfCorrectionAutomatic") == null ? 0
				: Float.parseFloat(receiverMatcher.group("rfCorrectionAutomatic"));
		this.recInputNoise = receiverMatcher.group("signalQuality") == null ? 0
				: Float.parseFloat(receiverMatcher.group("signalQuality"));
		/*
		 * receiverMatcher.group("sendersSignalQuality"); receiverMatcher.group("sendersMessages");
		 * receiverMatcher.group("goodSendersSignalQuality"); receiverMatcher.group("goodSenders");
		 * receiverMatcher.group("goodAndBadSenders");
		 */
		return this;
	}

}