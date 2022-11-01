package com.parkit.parkingsystem.model;

import java.time.LocalDateTime;

/**
 * The Ticket object indicates the fare, inTime, outTime, vehicle number, parking number, old/new client
 * 
 * @author trimok
 *
 */
public class Ticket {
	/**
	 * id
	 */
	private int id;
	/**
	 * The parkingSpot
	 * 
	 * @see com.parkit.parkingsystem.model.ParkingSpot
	 */
	private ParkingSpot parkingSpot;
	/**
	 * Vehicle number
	 */
	private String vehicleRegNumber;

	/**
	 * The price (fare)
	 */
	private double price;
	// TM 25/10/22 Use of LocalDateTime instead of Date
	/**
	 * InTime
	 */
	private LocalDateTime inTime;
	/**
	 * OutTime
	 */
	private LocalDateTime outTime;
	// TM 27/10/22
	/**
	 * An old client is a client who has already a ticket (with outTime filled) in the database
	 */
	private boolean oldClient;

	/**
	 * Get old client flag
	 * 
	 * @return : the old client flag
	 */
	public boolean isOldClient() {
		return oldClient;
	}

	/**
	 * Set oldClient
	 * 
	 * @param oldClient
	 *            : the old client flag
	 */
	public void setOldClient(boolean oldClient) {
		this.oldClient = oldClient;
	}

	/**
	 * Get id
	 * 
	 * @return : the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set id
	 * 
	 * @param id
	 *            : the id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Getting the parkingSpot
	 * 
	 * @return : the parking spot
	 */
	public ParkingSpot getParkingSpot() {
		if (parkingSpot != null) {
			return new ParkingSpot(parkingSpot.getId(), parkingSpot.getParkingType(), parkingSpot.isAvailable());
		} else {
			return null;
		}
	}

	/**
	 * Setting the parkingSpot
	 * 
	 * @param parkingSpot
	 *            : the parking spot
	 */
	public void setParkingSpot(ParkingSpot parkingSpot) {
		if (parkingSpot != null) {
			this.parkingSpot = new ParkingSpot(parkingSpot.getId(), parkingSpot.getParkingType(),
					parkingSpot.isAvailable());
		} else {
			this.parkingSpot = null;
		}
	}

	/**
	 * Getting the vehicle number
	 * 
	 * @return : the vehicle number
	 */
	public String getVehicleRegNumber() {
		return vehicleRegNumber;
	}

	/**
	 * Setting the vehicle number *
	 * 
	 * @param vehicleRegNumber
	 *            : the vehicle number
	 */
	public void setVehicleRegNumber(String vehicleRegNumber) {
		this.vehicleRegNumber = vehicleRegNumber;
	}

	/**
	 * Getting the fare
	 * 
	 * @return : the fare
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Setting the fare
	 * 
	 * @param price
	 *            : the fare
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * Getting the inTime
	 * 
	 * @return : the inTime
	 */
	public LocalDateTime getInTime() {
		return inTime;
	}

	/**
	 * Setting the inTime
	 * 
	 * @param inTime
	 *            : the inTime
	 */
	public void setInTime(LocalDateTime inTime) {
		this.inTime = inTime;
	}

	/**
	 * Getting the outTime
	 * 
	 * @return : the outTime
	 */
	public LocalDateTime getOutTime() {
		return outTime;
	}

	/**
	 * Setting the outTime
	 * 
	 * @param outTime
	 *            : the outTime
	 */
	public void setOutTime(LocalDateTime outTime) {
		this.outTime = outTime;
	}
}
