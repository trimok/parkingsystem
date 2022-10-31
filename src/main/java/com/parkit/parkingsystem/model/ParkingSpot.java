package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

/**
 * The ParkingSpot object indicate the type of parking spot and the availability
 * 
 * @author trimok
 *
 */
public class ParkingSpot {
	/**
	 * The key
	 */
	private int number;
	/**
	 * The parkingType
	 * 
	 * @see com.parkit.parkingsystem.constants.ParkingType
	 */
	private ParkingType parkingType;
	/**
	 * The availability
	 */
	private boolean isAvailable;

	// TM 31/10/22 Skeleton constructor, only the key (parking number) is correctly filled
	/**
	 * Skeleton Constructor : only the key (parking number) is correctly filled
	 * 
	 * @param number
	 *            : the parking number
	 */
	public ParkingSpot(int number) {
		this.number = number;
		this.parkingType = ParkingType.CAR;
		this.isAvailable = false;
	}

	/**
	 * Standard constructor
	 * 
	 * @param number
	 *            : the parking number
	 * @param parkingType
	 *            : the parking type
	 * @param isAvailable
	 *            : the availability
	 */
	public ParkingSpot(int number, ParkingType parkingType, boolean isAvailable) {
		this.number = number;
		this.parkingType = parkingType;
		this.isAvailable = isAvailable;
	}

	/**
	 * Get the id
	 * 
	 * @return : the id
	 */
	public int getId() {
		return number;
	}

	/**
	 * Set the id
	 * 
	 * @param number
	 *            : the parking number
	 */
	public void setId(int number) {
		this.number = number;
	}

	/**
	 * Get the ParkingType
	 * 
	 * @return : the parking type
	 */
	public ParkingType getParkingType() {
		return parkingType;
	}

	/**
	 * Set the ParkingType
	 * 
	 * @param parkingType
	 *            : the parking type
	 */
	public void setParkingType(ParkingType parkingType) {
		this.parkingType = parkingType;
	}

	/**
	 * Get avaibility
	 * 
	 * @return : the availability
	 */
	public boolean isAvailable() {
		return isAvailable;
	}

	/**
	 * Set avaibility
	 * 
	 * @param available
	 *            : the availability
	 */
	public void setAvailable(boolean available) {
		isAvailable = available;
	}

	/**
	 * Equals method
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ParkingSpot that = (ParkingSpot) o;
		return number == that.number;
	}

	/**
	 * Hashcode method
	 */
	@Override
	public int hashCode() {
		return number;
	}
}
