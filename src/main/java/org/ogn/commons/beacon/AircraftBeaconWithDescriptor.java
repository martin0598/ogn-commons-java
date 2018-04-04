package org.ogn.commons.beacon;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A utility wrapper class for encapsulating aircraft beacon together with the aircraft descriptor
 * 
 * @author Wojtek
 *
 */
public class AircraftBeaconWithDescriptor {

	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
	AircraftBeacon beacon;

	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
	Optional<AircraftDescriptor> descriptor;

	// default empty constructor required to satisfy jackson
	@SuppressWarnings("unused")
	private AircraftBeaconWithDescriptor() {

	}

	public AircraftBeaconWithDescriptor(AircraftBeacon beacon, Optional<AircraftDescriptor> descriptor) {
		super();
		this.beacon = beacon;
		this.descriptor = descriptor;
	}

	public AircraftBeacon getBeacon() {
		return beacon;
	}

	public Optional<AircraftDescriptor> getDescriptor() {
		return descriptor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((beacon == null) ? 0 : beacon.hashCode());
		result = prime * result + ((descriptor == null) ? 0 : descriptor.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AircraftBeaconWithDescriptor other = (AircraftBeaconWithDescriptor) obj;
		if (beacon == null) {
			if (other.beacon != null)
				return false;
		} else if (!beacon.equals(other.beacon))
			return false;
		if (descriptor == null) {
			if (other.descriptor != null)
				return false;
		} else if (!descriptor.equals(other.descriptor))
			return false;
		return true;
	}
}