/**
 * Copyright (c) 2014-2018 OGN, All Rights Reserved.
 */

package org.ogn.commons.beacon.impl;

import static org.ogn.commons.utils.AprsUtils.dmsToDeg;
import static org.ogn.commons.utils.AprsUtils.feetsToMetres;
import static org.ogn.commons.utils.AprsUtils.kntToKmh;
import static org.ogn.commons.utils.AprsUtils.toUtcTimestamp;

import java.io.Serializable;

import org.ogn.commons.beacon.OgnBeacon;

import com.google.code.regexp.Matcher;

/**
 * Generic class representing GPS position fix
 * 
 * @author wbuczak
 */
public class OgnBeaconImpl implements OgnBeacon, Serializable {

	private static final long serialVersionUID = 7387914213815737388L;

	protected String id;
	protected long timestamp;
	protected double lat;
	protected double lon;
	protected float alt;

	/**
	 * deg
	 */
	protected int track;

	/**
	 * km/h
	 */
	protected float groundSpeed;

	protected String rawPacket;

	protected OgnBeaconImpl() {
	}

	public OgnBeaconImpl(Matcher matcher) {
		this.rawPacket = matcher.group(0);

		// APRS status and position fields
		this.id = matcher.group("callsign");
		// this.dstcall = matcher.group("dstcall");
		// this.srvName = matcher.group("receiver");
		this.timestamp = toUtcTimestamp(matcher.group("time"));

		// if we have a APRS status, then we have just 5 groups
		if (matcher.groupCount() == 5) {
			return;
		}

		// APRS position fields
		this.lat = dmsToDeg(Double.parseDouble(matcher.group("latitude")) / 100);
		this.lat += matcher.group("posExtension") == null ? 0
				: Double.parseDouble(matcher.group("latitudeEnhancement")) / 1000 / 60;
		if (matcher.group("latitudeSign").equals("S")) {
			this.lat *= -1;
		}
		// matcher.group("symboltable");
		this.lon = dmsToDeg(Double.parseDouble(matcher.group("longitude")) / 100);
		this.lon += matcher.group("posExtension") == null ? 0
				: Double.parseDouble(matcher.group("longitudeEnhancement")) / 1000 / 60;
		if (matcher.group("longitudeSign").equals("W")) {
			this.lon *= -1;
		}
		// matcher.group("symbol");
		matcher.group("courseExtension");
		this.track = matcher.group("course") == null ? 0 : Integer.parseInt(matcher.group("course"));
		this.groundSpeed = matcher.group("groundSpeed") == null ? 0
				: kntToKmh(Float.parseFloat(matcher.group("groundSpeed")));
		this.alt = matcher.group("altitude") == null ? 0 : feetsToMetres(Float.parseFloat(matcher.group("altitude")));
		// matcher.group("comment");
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public double getLat() {
		return lat;
	}

	@Override
	public double getLon() {
		return lon;
	}

	@Override
	public float getAlt() {
		return alt;
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
	public String getRawPacket() {
		return rawPacket;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(alt);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lon);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((rawPacket == null) ? 0 : rawPacket.hashCode());
		result = prime * result + Float.floatToIntBits(groundSpeed);
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + track;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OgnBeaconImpl other = (OgnBeaconImpl) obj;
		if (Float.floatToIntBits(alt) != Float.floatToIntBits(other.alt))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (Double.doubleToLongBits(lon) != Double.doubleToLongBits(other.lon))
			return false;
		if (rawPacket == null) {
			if (other.rawPacket != null)
				return false;
		} else if (!rawPacket.equals(other.rawPacket))
			return false;
		if (Float.floatToIntBits(groundSpeed) != Float.floatToIntBits(other.groundSpeed))
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (track != other.track)
			return false;
		return true;
	}
}