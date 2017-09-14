/**
 * Copyright (c) 2014-2015 OGN, All Rights Reserved.
 */

package org.ogn.commons.beacon.impl.aprs;

import static org.ogn.commons.utils.AprsUtils.dmsToDeg;
import static org.ogn.commons.utils.AprsUtils.feetsToMetres;
import static org.ogn.commons.utils.AprsUtils.kntToKmh;
import static org.ogn.commons.utils.AprsUtils.toUtcTimestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.ogn.commons.beacon.AddressType;
import org.ogn.commons.beacon.AircraftBeacon;
import org.ogn.commons.beacon.AircraftType;
import org.ogn.commons.beacon.OgnBeacon;
import org.ogn.commons.beacon.impl.OgnBeaconImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;

public class AprsAircraftBeacon extends OgnBeaconImpl implements AircraftBeacon {

	private static final long serialVersionUID = -7640993719847348787L;

	private static final Logger LOG = LoggerFactory.getLogger(AprsAircraftBeacon.class);

	/**
	 * Name of the receiver which received this message
	 */
	protected String receiverName;

	/**
	 * ICAO/FLARM/OGN tracker ID
	 */
	protected String address;

	/**
	 * Original (FLARM) address. If one sets ICAO address this one will still point to the original FLARM device id
	 */
	protected String originalAddress;

	/**
	 * id can be either ICAO, FLARM or OGN
	 */
	protected AddressType addressType = AddressType.UNRECOGNIZED;

	/**
	 * type of an aircraft (Glider, tow plane, helicopter, etc..)
	 */
	protected AircraftType aircraftType;

	/**
	 * stealth mode active or not
	 */
	protected boolean stealth;

	/**
	 * climb rate in m/s
	 */
	protected float climbRate;

	/**
	 * turn rate in deg/s
	 */
	protected float turnRate;

	/**
	 * reception signal strength measured in dB
	 */
	protected float signalStrength;

	/**
	 * estimated effective radiated power of the transmitter
	 */
	protected float erp = Float.NaN;

	/**
	 * frequency offset measured in KHz
	 */
	protected float frequencyOffset; // in KHz

	/**
	 * GPS status (GPS accuracy in meters, horizontal and vertical)
	 */
	protected String gpsStatus;

	/**
	 * number of errors corrected by the receiver
	 */
	protected int errorCount;

	/**
	 * 8-bit hardware version (hex)
	 */
	protected int hardwareVersion;

	/**
	 * version of the transmitter's firmware
	 */
	protected float firmwareVersion = Float.NaN;

	/**
	 * id of another aircraft received by this aircraft
	 */
	protected Set<String> heardAircraftIds = new HashSet<>();

	/**
	 * flight level as computed by the device (barometric)
	 */
	protected float flightLevel = Float.NaN;


	@Override
	public String getReceiverName() {
		return receiverName;
	}

	@Override
	public int getTrack() {
		return track;
	}

	@Override
	public float getGroundSpeed() {
		return groundSpeed;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public String getOriginalAddress() {
		return originalAddress;
	}

	@Override
	public AddressType getAddressType() {
		return addressType;
	}

	@Override
	public AircraftType getAircraftType() {
		return aircraftType;
	}

	@Override
	public boolean isStealth() {
		return stealth;
	}

	@Override
	public float getClimbRate() {
		return climbRate;
	}

	@Override
	public float getTurnRate() {
		return turnRate;
	}

	@Override
	public float getSignalStrength() {
		return signalStrength;
	}

	@Override
	public float getFrequencyOffset() {
		return frequencyOffset;
	}

	@Override
	public String getGpsStatus() {
		return gpsStatus;
	}

	@Override
	public int getErrorCount() {
		return errorCount;
	}

	@Override
	public String[] getHeardAircraftIds() {
		return heardAircraftIds.toArray(new String[0]);
	}

	@Override
	public float getFirmwareVersion() {
		return firmwareVersion;
	}

	@Override
	public float getERP() {
		return erp;
	}

	@Override
	public float getFlightLevel() {
		return flightLevel;
	}

	@Override
	public int getHardwareVersion() {
		return hardwareVersion;
	}

	// private default constructor
	// required by jackson (as it uses reflection)
	@SuppressWarnings("unused")
	private AprsAircraftBeacon() {
		// no default implementation
	}

	public AprsAircraftBeacon(Matcher positionMatcher) {
		super(positionMatcher);
		this.receiverName = positionMatcher.group("receiver");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((addressType == null) ? 0 : addressType.hashCode());
		result = prime * result + ((aircraftType == null) ? 0 : aircraftType.hashCode());
		result = prime * result + Float.floatToIntBits(climbRate);
		result = prime * result + Float.floatToIntBits(erp);
		result = prime * result + errorCount;
		result = prime * result + Float.floatToIntBits(firmwareVersion);
		result = prime * result + Float.floatToIntBits(frequencyOffset);
		result = prime * result + ((gpsStatus == null) ? 0 : gpsStatus.hashCode());
		result = prime * result + hardwareVersion;
		result = prime * result + ((heardAircraftIds == null) ? 0 : heardAircraftIds.hashCode());
		result = prime * result + ((originalAddress == null) ? 0 : originalAddress.hashCode());
		result = prime * result + ((receiverName == null) ? 0 : receiverName.hashCode());
		result = prime * result + Float.floatToIntBits(signalStrength);
		result = prime * result + (stealth ? 1231 : 1237);
		result = prime * result + Float.floatToIntBits(turnRate);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AprsAircraftBeacon other = (AprsAircraftBeacon) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (addressType != other.addressType)
			return false;
		if (aircraftType != other.aircraftType)
			return false;
		if (Float.floatToIntBits(climbRate) != Float.floatToIntBits(other.climbRate))
			return false;
		if (Float.floatToIntBits(erp) != Float.floatToIntBits(other.erp))
			return false;
		if (errorCount != other.errorCount)
			return false;
		if (Float.floatToIntBits(firmwareVersion) != Float.floatToIntBits(other.firmwareVersion))
			return false;
		if (Float.floatToIntBits(frequencyOffset) != Float.floatToIntBits(other.frequencyOffset))
			return false;
		if (gpsStatus == null) {
			if (other.gpsStatus != null)
				return false;
		} else if (!gpsStatus.equals(other.gpsStatus))
			return false;
		if (hardwareVersion != other.hardwareVersion)
			return false;
		if (heardAircraftIds == null) {
			if (other.heardAircraftIds != null)
				return false;
		} else if (!heardAircraftIds.equals(other.heardAircraftIds))
			return false;
		if (originalAddress == null) {
			if (other.originalAddress != null)
				return false;
		} else if (!originalAddress.equals(other.originalAddress))
			return false;
		if (receiverName == null) {
			if (other.receiverName != null)
				return false;
		} else if (!receiverName.equals(other.receiverName))
			return false;
		if (Float.floatToIntBits(signalStrength) != Float.floatToIntBits(other.signalStrength))
			return false;
		if (stealth != other.stealth)
			return false;
		if (Float.floatToIntBits(turnRate) != Float.floatToIntBits(other.turnRate))
			return false;
		return true;
	}

	public OgnBeacon update(Matcher aircraftMatcher) {        
		int details = Integer.parseInt(aircraftMatcher.group("details"), 16);
		this.addressType  = AddressType.forValue(details & 0b00000011);
		this.aircraftType = AircraftType.forValue((details & 0b01111100) >>> 2);
		this.stealth 	  = ((details & 0b10000000) >>> 7) == 1;
		
		this.address = aircraftMatcher.group("id");
		this.climbRate = aircraftMatcher.group("climbRate") == null ? 0 : feetsToMetres(Float.parseFloat(aircraftMatcher.group("climbRate"))) / 60.0f;
		this.turnRate = aircraftMatcher.group("turnRate") == null ? 0 : Float.parseFloat(aircraftMatcher.group("turnRate"));
		this.flightLevel = aircraftMatcher.group("flightLevel") == null ? 0 : Float.parseFloat(aircraftMatcher.group("flightLevel"));
		this.signalStrength = aircraftMatcher.group("signalQuality") == null ? 0 : Float.parseFloat(aircraftMatcher.group("signalQuality"));
		this.errorCount = aircraftMatcher.group("errors") == null ? 0 : Integer.parseInt(aircraftMatcher.group("errors"));
		this.frequencyOffset = aircraftMatcher.group("frequencyOffset") == null ? 0 : Float.parseFloat(aircraftMatcher.group("frequencyOffset"));
		this.gpsStatus = aircraftMatcher.group("gpsAccuracy") == null ? "" : aircraftMatcher.group("gpsAccuracy");
		this.firmwareVersion = aircraftMatcher.group("flarmSoftwareVersion") == null ? 0 : Float.parseFloat(aircraftMatcher.group("flarmSoftwareVersion"));
		this.hardwareVersion = aircraftMatcher.group("flarmHardwareVersion") == null ? 0 : Integer.parseInt(aircraftMatcher.group("flarmHardwareVersion"), 16);
		this.originalAddress = aircraftMatcher.group("flarmId") == null ? "" : aircraftMatcher.group("flarmId");
		this.erp = aircraftMatcher.group("signalPower") == null ? 0 : Float.parseFloat(aircraftMatcher.group("signalPower"));
		this.heardAircraftIds = aircraftMatcher.group("proximity") == null ? new TreeSet<String>() : new TreeSet<String>(Arrays.asList(aircraftMatcher.group("proximity").substring(4).split(" hear")));
		return this;
	}
}