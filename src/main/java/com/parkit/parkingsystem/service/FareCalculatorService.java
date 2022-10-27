package com.parkit.parkingsystem.service;

import java.time.temporal.ChronoUnit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	private static final double SIXTY = 60.0;

	public void calculateFare(Ticket ticket) {
		// TM 26/10/22 add a second to outTime, in case inTime = outTime in tests
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		// TM 25/10/22 Difference in Hours using LocalDateTime
		double differenceInHours = ChronoUnit.MINUTES.between(ticket.getInTime(), ticket.getOutTime()) / SIXTY;

		// TM 27/10/22 reduction
		double factorRate = ticket.isOldClient() ? Fare.OLD_CLIENT_RATE_FACTOR : 1.0;

		// Base rate
		ParkingType parkingType = ticket.getParkingSpot().getParkingType();
		double rate_per_hour = 0;
		switch (parkingType) {
			case CAR : {
				rate_per_hour = Fare.CAR_RATE_PER_HOUR;
				break;
			}
			case BIKE : {
				rate_per_hour = Fare.BIKE_RATE_PER_HOUR;
				break;
			}
			default :
				throw new IllegalArgumentException("Unkown Parking Type");
		}

		// Price calculus
		double price = differenceInHours * rate_per_hour * factorRate;
		ticket.setPrice(price);
	}
}